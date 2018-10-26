package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode
@RequiredArgsConstructor
public class ImageFuncionality implements CommandFuncionality {

    private final String imageId;

    public void execute(TelegramBot bot, Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException  {
        bot.sendPhoto(chat.getId(), imageId, null, false, messageId, null);
    }
}