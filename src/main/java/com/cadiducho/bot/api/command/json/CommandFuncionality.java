package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;

/**
 * Funcionalidad (o una de las funcionalidades) de un comando creado por Json
 */
public interface CommandFuncionality {

    void execute(TelegramBot bot, Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException;
}