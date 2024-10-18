package com.cadiducho.bot.modules.treintaytres;

import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.Instant;

@Log
@CommandInfo(module = TreintaYTres.class, aliases = "reload33", description = "Recarga las frases de 33")
public class Reload33Cmd implements BotCommand {
    @Override
    public void execute(Chat chat, User user, CommandContext commandContext, Integer integer, Message message, Instant instant) throws TelegramException {
        if (chat.getId() != -1001696570609L) 
            getBot().sendMessage(chat.getId(), "No tienes permisos para ejecutar este comando");
        else {
            try {
                Respuestas.getInstance().refrescar();
                getBot().sendMessage(chat.getId(), "Frases recargadas");
            } catch (IOException e) {
                getBot().sendMessage(chat.getId(), "Error al recargar las frases:"+e.getMessage());
            }
        }
    }
}
