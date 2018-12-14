package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.squareup.moshi.Json;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Builder
@EqualsAndHashCode
public class VideoFuncionality implements CommandFuncionality {

    @Json(name = "reply_to") @Builder.Default private final ReplyPattern replyPattern = ReplyPattern.TO_NONE;
    @Json(name = "video_id") private final String videoId;

    public void execute(TelegramBot bot, Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        bot.sendVideo(chat.getId(), videoId, null, null, null, null, null, null, false, messageId, null);
    }
}