package com.cadiducho.bot.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;

import java.time.Instant;

@CommandInfo(aliases = "/intentar")
public class IntentarCMD implements BotCommand {

    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (args.length == 0) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> /intentar <i>acción</i>", "html", null, false, messageId, null);
            return;
        }
        String tried = "intenta";
        String end = "y lo consigue";
        if (Math.random() < 0.5D) {
            tried = "intentó";
            end = "pero falló";
        }
        getBot().sendMessage(chat.getId(), EmojiManager.getForAlias("game_die").getUnicode() + " " + from.getFirst_name() + " " + tried + " " + String.join(" ", args) + " " + end);
    }
}
