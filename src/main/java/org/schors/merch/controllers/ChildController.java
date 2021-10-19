package org.schors.merch.controllers;

import lombok.SneakyThrows;
import org.schors.merch.Storage;
import org.schors.merch.data.Child;
import org.schors.merch.data.Gender;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Controller
public class ChildController {

    private final Storage storage;

    public ChildController(Storage storage) {
        this.storage = storage;
    }

    @Command(value = UpdateType.MESSAGE)
    @HasText(textTarget = TextTarget.MESSAGE_TEXT, equals = "/add")
    public ModelAndView addChild(Session session, @SessionAtr("user") User user) {
        Child child = new Child();
        child.setUsername(user.getUserName());
        session.setAttribute("child", child);
        return new StatefulModelAndView("add-power", "add-child");
    }

    @Command(value = UpdateType.MESSAGE, state = "add-power")
    public ModelAndView addPower(@SessionAtr("child") Child child, final Update update) {
        child.setPower(Integer.parseInt(update.getMessage().getText()));
        return new StatefulModelAndView("add-gender", "add-gender");
    }

    @SneakyThrows
    @Command(value = UpdateType.CALLBACK_QUERY, state = "add-gender")
    public ModelAndView addGender(final AbsSender absSender,
                                  @SessionAtr("child") Child child,
                                  @SessionAtr("user") User user,
                                  final Update update) {
        absSender.execute(AnswerCallbackQuery.builder()
                                             .callbackQueryId(update.getCallbackQuery().getId())
                                             .build());
        child.setGender(Gender.valueOf(update.getCallbackQuery().getData()));
        storage.addPlayerChild(user.getUserName(), child);
        return new StatefulModelAndView("main", "show-children");
    }
}
