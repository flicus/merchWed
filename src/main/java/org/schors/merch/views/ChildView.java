package org.schors.merch.views;

import lombok.SneakyThrows;
import org.schors.merch.data.Gender;
import org.slyrack.telegrambots.annotations.SessionAtr;
import org.slyrack.telegrambots.annotations.View;
import org.slyrack.telegrambots.annotations.ViewController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@ViewController
public class ChildView {

    @SneakyThrows
    @View("add-child")
    public void childPower(final AbsSender sender,
                           @SessionAtr("chat-id") String chatId) {
        sender.execute(SendMessage.builder().text("Сила ребенка?").chatId(chatId).build());
    }

    @SneakyThrows
    @View("add-gender")
    public void childGender(final AbsSender sender,
                            @SessionAtr("chat-id") String chatId) {
        sender.execute(SendMessage.builder()
                           .text("Пол ребенка?")
                           .chatId(chatId)
                           .replyMarkup(InlineKeyboardMarkup.builder()
                                            .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                                     .text("M")
                                                                     .callbackData(Gender.MALE.name())
                                                                     .build(),
                                                                 InlineKeyboardButton.builder()
                                                                     .text("Ж")
                                                                     .callbackData(Gender.FEMALE.name())
                                                                     .build()))
                                            .build())
                           .build());

    }
}
