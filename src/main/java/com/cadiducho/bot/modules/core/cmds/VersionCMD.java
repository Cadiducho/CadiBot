package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;

import java.time.Instant;

@CommandInfo(aliases = {"/cadibot", "/version", "/start", "/about"})
public class VersionCMD implements BotCommand {
    
    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        String aboutme = "Hola! Soy Cadibot \n" +
                "Se está ejecutando la versión _" + CadiBotServer.VERSION + "_\n" +
                "Información y novedades: @Cadibotnews";

        getBot().sendMessage(chat.getId(), aboutme, ParseMode.MARKDOWN, null, false, messageId, null);
    }
}
