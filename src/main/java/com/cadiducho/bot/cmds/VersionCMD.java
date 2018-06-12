package com.cadiducho.bot.cmds;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.util.Date;

@CommandInfo(aliases = "/version")
public class VersionCMD implements BotCommand {
    
    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Date date) throws TelegramException {
        getBot().sendMessage(chat.getId(), "Ejecutando versión " + BotServer.VERSION, null, null, false, messageId, null);
    }
}
