package org.schors.merch;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slyrack.telegrambots.core.UpdateHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotProperties properties;
    private final UpdateHandler updateHandler;

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.handleUpdate(update, this);
    }

}
