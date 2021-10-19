package org.schors.merch.controllers;

import lombok.SneakyThrows;
import org.schors.merch.Answer;
import org.schors.merch.BotProperties;
import org.schors.merch.Storage;
import org.schors.merch.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slyrack.telegrambots.ModelAndView;
import org.slyrack.telegrambots.StatefulModelAndView;
import org.slyrack.telegrambots.annotations.Command;
import org.slyrack.telegrambots.annotations.Controller;
import org.slyrack.telegrambots.annotations.HasText;
import org.slyrack.telegrambots.annotations.SessionAtr;
import org.slyrack.telegrambots.flags.TextTarget;
import org.slyrack.telegrambots.flags.UpdateType;
import org.slyrack.telegrambots.session.Session;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InitController {

    private final BotProperties botProperties;
    private final Storage storage;
    private Logger logger = LoggerFactory.getLogger(InitController.class);

    public InitController(BotProperties botProperties, Storage storage) {
        this.botProperties = botProperties;
        this.storage = storage;
    }

    @SneakyThrows
    @Command(value = UpdateType.MESSAGE)
    public ModelAndView start(@SessionAtr("user") User user) {
        logger.debug("start: ");
        System.out.println(user);
        if (!storage.isKnownPlayer(user.getUserName())) {
            storage.addPlayer(user.getUserName());
        }
        return new StatefulModelAndView("main", "show-children");
    }

    public ModelAndView main() {
        return new ModelAndView("")
    }

    @Command(value = UpdateType.MESSAGE, state = "answer-state")
    public ModelAndView storeAnswer(Update update, Session session,
                                    @SessionAtr("current") Map question,
                                    @SessionAtr("answers") List<Answer> answers,
                                    @SessionAtr("questions") List<Map> questions) {
        logger.debug("storeAnswer");
        if (!session.isAlive()) return new ModelAndView("cancel");
        Answer answer = new Answer();
        answer.setQuestion((String) question.get("question"));
        answer.setAnswers(List.of(update.getMessage().getText()));
        answers.add(answer);
        return getModelAndView(session, questions);
    }

    @SneakyThrows
    @Command(value = UpdateType.CALLBACK_QUERY, state = "inline-answer-state")
    public ModelAndView storeInlineAnswer(Update update, Session session, final AbsSender absSender,
                                          @SessionAtr("current") Map question,
                                          @SessionAtr("answers") List<Answer> answers,
                                          @SessionAtr("questions") List<Map> questions,
                                          @SessionAtr("chat-id") final String chatId) {
        logger.debug("storeInlineAnswer");
        if (!session.isAlive()) return new ModelAndView("cancel");
        Answer answer = (Answer) session.getAttribute("currentAnswer");
        if (answer == null) {
            answer = new Answer();
            answers.add(answer);
            session.setAttribute("currentAnswer", answer);
            answer.setQuestion((String) question.get("question"));
            answer.setAnswers(new ArrayList<>());
        }
        absSender.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .build());

        if (Util.isSingle(question)) {
            answer.getAnswers().add(update.getCallbackQuery().getData());
            modifyQuestion(question, absSender, update, chatId, answer);
            session.setAttribute("currentAnswer", null);
            return getModelAndView(session, questions);
        } else {
            if ("done".equals(update.getCallbackQuery().getData())) {
                session.setAttribute("currentAnswer", null);
                return getModelAndView(session, questions);
            } else {
                if (answer.getAnswers().contains(update.getCallbackQuery().getData())) {
                    answer.getAnswers().remove(update.getCallbackQuery().getData());
                } else {
                    answer.getAnswers().add(update.getCallbackQuery().getData());
                }
                modifyQuestion(question, absSender, update, chatId, answer);
                return new StatefulModelAndView("inline-answer-state", "none");
            }
        }
    }

    @SneakyThrows
    private void modifyQuestion(Map question, AbsSender sender, Update update, String chatId, Answer answer) {
        StringBuilder newMessage = new StringBuilder();
        newMessage.append(answer.getQuestion()).append("\n<b>");
        answer.getAnswers().forEach(s -> newMessage.append(s).append(" "));
        newMessage.append("</b>");

        sender.execute(EditMessageText.builder()
                .parseMode(ParseMode.HTML)
                .text(newMessage.toString())
                .chatId(chatId)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build());
        if (!Util.isSingle(question)) {
            Map<String, String> answers = (Map) question.get("answers");

            EditMessageReplyMarkup.EditMessageReplyMarkupBuilder builder = EditMessageReplyMarkup.builder();
            builder.chatId(chatId);

            InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();
            Util.getTuples(answers.values(), 2)
                    .forEach(tuple -> inlineKeyboardMarkupBuilder
                            .keyboardRow(tuple.map(s -> InlineKeyboardButton.builder()
                                            .text(s)
                                            .callbackData(s)
                                            .build())
                                    .collect(Collectors.toList())));
            inlineKeyboardMarkupBuilder
                    .keyboardRow(List.of(InlineKeyboardButton
                            .builder()
                            .text("Done")
                            .callbackData("done")
                            .build()));
            builder.replyMarkup(inlineKeyboardMarkupBuilder.build());
            builder.messageId(update.getCallbackQuery().getMessage().getMessageId());
            sender.execute(builder.build());
        }
    }

    private ModelAndView getModelAndView(Session session, List<Map> questions) {
        if (questions.size() > 0) {
            Map nextQuestion = questions.remove(0);
            session.setAttribute("current", nextQuestion);
            return Util.isInline(nextQuestion)
                    ? Util.isSingle(nextQuestion)
                    ? new StatefulModelAndView("inline-answer-state", "showSingleInline")
                    : new StatefulModelAndView("inline-answer-state", "showMultiInline")
                    : new StatefulModelAndView("answer-state", "showQuestion");
        } else {
            session.stop();
            return new ModelAndView("done");
        }
    }

    @SneakyThrows
    @Command(value = UpdateType.MESSAGE, state = "inline-answer-state")
    public void removeMessages(final Update update,
                               final AbsSender absSender,
                               @SessionAtr("chat-id") final String chatId) {
        logger.debug("removeMessages");
        absSender.execute(DeleteMessage.builder()
                .chatId(chatId)
                .messageId(update.getMessage().getMessageId())
                .build());
    }

    @Command(value = UpdateType.MESSAGE,
            state = {"inline-answer-state", "answer-state"},
            exclusive = true
    )
    @HasText(textTarget = TextTarget.MESSAGE_TEXT, equals = "/cancel")
    public ModelAndView cancelDialog(final Session session) {
        logger.debug("cancelDialog");
        session.stop();
        return new ModelAndView("cancel");
    }

    @Command(value = UpdateType.MESSAGE,
            state = {"inline-answer-state", "answer-state",},
            exclusive = true
    )
    @HasText(textTarget = TextTarget.MESSAGE_TEXT, equals = "/help")
    public ModelAndView helpDialog() {
        logger.debug("helpDialog");
        return new ModelAndView("help");
    }

    @Command(value = UpdateType.MESSAGE,
            state = {"inline-answer-state", "answer-state"},
            exclusive = true)
    @HasText(textTarget = TextTarget.MESSAGE_TEXT, equals = "/mashinkov")
    public ModelAndView mashinkov(Session session) {
        return new StatefulModelAndView("mashinkov", "mashinkov");
    }

    @SneakyThrows
    @Command(value = {UpdateType.CALLBACK_QUERY}, state = "mashinkov", exclusive = true)
    public ModelAndView mashinkovEnd(AbsSender sender, Session session,
                                     @SessionAtr("chat-id") final String chatId) {
        sender.execute(SendMessage.builder().chatId(chatId).text("Красиивое...").build());
        List<Map> questions = new ArrayList<>();
        questions.addAll(botProperties.getQuestions());
        session.setAttribute("questions", questions);
        session.setAttribute("answers", new ArrayList<Answer>());
        return getModelAndView(session, questions);
    }
}
