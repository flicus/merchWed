package org.schors.merch.controllers;

import lombok.SneakyThrows;
import org.schors.merch.BotProperties;
import org.schors.merch.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slyrack.telegrambots.ModelAndView;
import org.slyrack.telegrambots.StatefulModelAndView;
import org.slyrack.telegrambots.annotations.Command;
import org.slyrack.telegrambots.annotations.Controller;
import org.slyrack.telegrambots.annotations.SessionAtr;
import org.slyrack.telegrambots.flags.UpdateType;
import org.telegram.telegrambots.meta.api.objects.User;

@Controller
public class InitController {

    private final BotProperties botProperties;
    private final Storage storage;
    private final Logger logger = LoggerFactory.getLogger(InitController.class);

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

    //    public ModelAndView main() {
    //        return new ModelAndView("")
    //    }


}
