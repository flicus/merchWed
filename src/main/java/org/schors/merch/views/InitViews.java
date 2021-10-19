package org.schors.merch.views;

import lombok.SneakyThrows;
import org.schors.merch.BotProperties;
import org.schors.merch.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slyrack.telegrambots.annotations.SessionAtr;
import org.slyrack.telegrambots.annotations.View;
import org.slyrack.telegrambots.annotations.ViewController;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@ViewController
public class InitViews {
    private final Logger logger = LoggerFactory.getLogger(InitViews.class);

    private final Storage storage;
    private final BotProperties botProperties;


    public InitViews(Storage storage, BotProperties botProperties) {
        this.storage = storage;
        this.botProperties = botProperties;
    }

    @SneakyThrows
    @View("show-children")
    public void showChildren(final AbsSender sender,
                             @SessionAtr("chat-id") String chatId,
                             @SessionAtr("user") User user) {
        //        storage.getPlayerChildren(user.getUserName()).stream().

    }

    @View("none")
    public void none() {

    }

}
