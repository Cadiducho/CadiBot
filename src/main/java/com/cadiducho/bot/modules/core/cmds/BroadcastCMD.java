package com.cadiducho.bot.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;

@CommandInfo(aliases = "/broadcastcadibot")
public class BroadcastCMD implements BotCommand {

    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null, false, messageId, null);
            return;
        }
        if (args.length == 0) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> /broadcast <i>mensaje</i>", "html", null, false, messageId, null);
            return;
        }
        botServer.getMysql().getGroupsIds().forEach(id -> {
            try {
                getBot().sendMessage(id, String.join(" ", args));
            } catch (TelegramException ex) {
                System.out.println("Disabling group " + id);
                botServer.getMysql().disableGroup(id);
            }
        });
    }
}
