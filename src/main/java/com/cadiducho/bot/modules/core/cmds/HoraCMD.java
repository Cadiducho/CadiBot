package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@CommandInfo(aliases = "/hora", description = "Ver la hora del servidor")
public class HoraCMD implements BotCommand {
    
    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        getBot().sendMessage(chat.getId(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(instant), null, null, false, messageId, null);
    }
}