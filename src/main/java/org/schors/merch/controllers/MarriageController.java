package org.schors.merch.controllers;

import lombok.SneakyThrows;
import org.schors.merch.Storage;
import org.schors.merch.data.Child;
import org.schors.merch.data.Pair;
import org.slyrack.telegrambots.ModelAndView;
import org.slyrack.telegrambots.annotations.Command;
import org.slyrack.telegrambots.annotations.Controller;
import org.slyrack.telegrambots.annotations.HasText;
import org.slyrack.telegrambots.annotations.SessionAtr;
import org.slyrack.telegrambots.flags.TextTarget;
import org.slyrack.telegrambots.flags.UpdateType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Controller
public class MarriageController {

    private final Storage storage;


    public MarriageController(Storage storage) {
        this.storage = storage;
    }

    @SneakyThrows
    @Command(value = UpdateType.MESSAGE)
    @HasText(textTarget = TextTarget.MESSAGE_TEXT, equals = "/marry")
    public ModelAndView marry(@SessionAtr("user") User user, final AbsSender sender,
                              @SessionAtr("chat-id") String chatId
    ) {

        List<Pair> pairs = storage.getPlayerChildren(user.getUserName()).stream()
                                  .map(child -> {
                                      Child second = storage
                                          .getAllOtherChildren(user.getUserName())
                                          .filter(pair -> pair.getGender() != child.getGender())
                                          .findAny().orElse(null);
                                      Pair pair = new Pair();
                                      pair.setFirst(child);
                                      pair.setSecond(second);
                                      return pair;
                                  })
                                  .filter(pair -> pair.getSecond() != null)
                                  .collect(Collectors.toList());
        storage.removePairs(pairs);

        StringBuilder sb = new StringBuilder();
        sb.append("Нашлось пар: ").append(pairs.size()).append("\n");
        pairs.forEach(pair -> {
            sb.append("@").append(pair.getSecond().getUsername()).append(", ")
              .append(pair.getSecond().getInGameName()).append(" : ")
              .append(pair.getSecond().getPower()).append(" <- ")
              .append(pair.getFirst().getGender()).append(", ").append(pair.getFirst().getPower())
              .append("\n");
        });

        Future a = Future.


            sender.execute(SendMessage.builder()
                                      .parseMode(ParseMode.HTML)
                                      .text(sb.toString())
                                      .chatId(chatId)
                                      .build());

        return new ModelAndView("none");
    }
}
