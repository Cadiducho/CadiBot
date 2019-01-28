package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.squareup.moshi.Json;
import lombok.Builder;

import java.time.Instant;

/**
 * Funcionalidad (o una de las funcionalidades) de un comando creado por Json
 */
public interface CommandFuncionality {

    void execute(TelegramBot bot, Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException;

    default Integer replyTheCommandTo(ReplyPattern pattern, Integer originalMessageId, Message replyTo) {
        Integer replyTheCommandTo = null;
        switch (pattern) {
            case TO_ANSWERED:
                if (replyTo != null) replyTheCommandTo = replyTo.getMessageId();
                break;
            case TO_ORIGINAL:
                replyTheCommandTo = originalMessageId;
                break;
        }
        return replyTheCommandTo;
    }

}