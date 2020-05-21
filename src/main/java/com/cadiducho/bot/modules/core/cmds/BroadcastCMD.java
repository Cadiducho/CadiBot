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
import com.cadiducho.zincite.api.command.args.Argument;
import com.cadiducho.zincite.api.command.args.CommandParseException;

import java.time.Instant;
import java.util.Optional;

@CommandInfo(aliases = "/broadcastcadibot", arguments = @Argument(name = "mensaje", type = String.class, description = "Mensaje a retransmitir"))
public class BroadcastCMD implements BotCommand {

    private final CadiBotServer cadiBotServer = CadiBotServer.getInstance();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null, false, messageId, null);
            return;
        }
        try {
            Optional<String> mensaje = context.get("mensaje");
            if (!mensaje.isPresent()) {
                getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(), ParseMode.HTML, null, false, messageId, null);
                return;
            }
            cadiBotServer.getDatabase().getGroupsIds().forEach(id -> {
                try {
                    getBot().sendMessage(id, mensaje.get());
                } catch (TelegramException ex) {
                    System.out.println("Disabling group " + id);
                    cadiBotServer.getDatabase().disableGroup(id);
                }
            });
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null, false, messageId, null);
        }
    }
}
