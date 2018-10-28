package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandArguments;
import com.cadiducho.bot.api.command.args.CommandParseException;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;
import java.util.Optional;

@CommandInfo(aliases = "/broadcastcadibot")
@CommandArguments(@Argument(name = "mensaje", type = String.class, description = "Mensaje a retransmitir"))
public class BroadcastCMD implements BotCommand {

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null, false, messageId, null);
            return;
        }
        try {
            Optional<String> mensaje = context.get("mensaje");
            if (!mensaje.isPresent()) {
                getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(), "html", null, false, messageId, null);
                return;
            }
            botServer.getMysql().getGroupsIds().forEach(id -> {
                try {
                    getBot().sendMessage(id, mensaje.get());
                } catch (TelegramException ex) {
                    System.out.println("Disabling group " + id);
                    botServer.getMysql().disableGroup(id);
                }
            });
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(), "html", null, false, messageId, null);
        }
    }
}
