package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandInfo(module = PoleModule.class, aliases = "/updateusername")
public class UpdateUsernameCMD implements BotCommand {

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Instant instant) throws TelegramException {
        try {
            getMySQL().updateUsername(from.getId(), Long.parseLong(chat.getId()));
            if (chat.isGroupChat()) {
                getMySQL().updateGroup(chat.getId(), chat.getTitle());
            }
            getBot().sendMessage(chat.getId(), "Tu información ha sido actualizada/registrada", null, null, false, messageId, null);
        } catch (SQLException ex) {
            getBot().sendMessage(chat.getId(), "No he podido actualizar tu información", null, null, false, messageId, null);
            Logger.getLogger(UpdateUsernameCMD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
