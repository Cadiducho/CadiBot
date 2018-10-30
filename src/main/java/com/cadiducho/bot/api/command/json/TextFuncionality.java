package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.squareup.moshi.Json;
import com.vdurmont.emoji.EmojiParser;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Builder
@EqualsAndHashCode
public class TextFuncionality implements CommandFuncionality {

    @Json(name = "reply_to") @Builder.Default private final ReplyPattern replyPattern = ReplyPattern.TO_NONE;
    private final String text;

    public void execute(TelegramBot bot, Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        String reply = EmojiParser.parseToUnicode(text);

        bot.sendMessage(chat.getId(), reply, null, null, false, messageId, null);
    }
}