package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;

import java.time.Instant;

@CommandInfo(aliases = {"/cadibot", "/version", "/start", "/about"})
public class VersionCMD implements BotCommand {
    
    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        String aboutme = "Hola! Soy Cadibot \n" +
                "Se está ejecutando la versión _" + BotServer.VERSION + "_ - Navideño edition " + EmojiManager.getForAlias("christmas_tree").getUnicode() + " \n" +
                "Información y novedades: @Cadibotnews";

        getBot().sendMessage(chat.getId(), aboutme, "markdown", null, false, messageId, null);
    }
}
