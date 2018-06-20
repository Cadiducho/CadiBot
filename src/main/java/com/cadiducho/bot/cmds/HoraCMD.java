package com.cadiducho.bot.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@CommandInfo(aliases = "/hora")
public class HoraCMD implements BotCommand {
    
    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Instant instant) throws TelegramException {
        getBot().sendMessage(chat.getId(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(instant), null, null, false, messageId, null);
    }
}