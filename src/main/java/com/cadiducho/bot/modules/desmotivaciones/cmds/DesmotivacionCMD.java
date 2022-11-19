package com.cadiducho.bot.modules.desmotivaciones.cmds;

import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import lombok.extern.java.Log;

import java.time.Instant;

@Log
@CommandInfo(
    aliases = {"/desmotivacion", "/desmotivaciones", "/desmotiva"},
    description = "Obtiene un cartel aleatorio de desmotivaciones.es"
)
public class DesmotivacionCMD implements BotCommand {
    @Override
    public void execute(Chat chat, User user, CommandContext commandContext, Integer integer, Message message, Instant instant) throws TelegramException {
        if (user.getId() != 124618317L) {
            String cartel = DesmotivacionesModule.getAPost();
            getBot().sendPhoto(chat.getId(), cartel);
        }
    }
}