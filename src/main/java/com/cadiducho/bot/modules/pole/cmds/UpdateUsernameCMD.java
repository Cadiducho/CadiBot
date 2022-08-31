package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;

import java.time.Instant;

@CommandInfo(module = PoleModule.class, aliases = "/updateusername", description = "Actualiza los datos de un usuario en el servidor")
public class UpdateUsernameCMD implements BotCommand {

    private final CadiBotServer cadiBotServer = CadiBotServer.getInstance();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        boolean updated = cadiBotServer.getDatabase().updateUsername(from.getId(), chat.getId());
        if (chat.isGroupChat()) {
            cadiBotServer.getDatabase().updateGroup(chat.getId(), chat.getTitle(), false);
        }
        if (updated) {
            getBot().sendMessage(chat.getId(), "Tu información ha sido actualizada/registrada", null, null,false, null, messageId, null);
        } else {
            getBot().sendMessage(chat.getId(), "No he podido actualizar tu información", null, null,false, null, messageId, null);
        }
    }
}
