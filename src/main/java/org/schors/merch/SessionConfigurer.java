package org.schors.merch;

import org.slyrack.telegrambots.annotations.Controller;
import org.slyrack.telegrambots.annotations.MiddleHandler;
import org.slyrack.telegrambots.session.Session;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
public class SessionConfigurer {

    private final BotProperties botProperties;

    public SessionConfigurer(BotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @MiddleHandler
    public void configureSession(final Update update, final Session session) {
        if (session == null) return;

        if (!session.containsAttribute("chat-id"))
            Util.getChatId(update)
                    .ifPresent(chatId -> session.setAttribute("chat-id", String.valueOf(chatId)));

        if (!session.containsAttribute("user"))
            Util.getUser(update)
                    .ifPresent(user -> session.setAttribute("user", user));

    }
}
