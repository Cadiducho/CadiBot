package com.cadiducho.bot;

import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.handlers.LongPollingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.time.Instant;

@Log
@RequiredArgsConstructor
public class UpdatesHandler implements LongPollingHandler {

    private final TelegramBot bot;
    private final BotServer server;

    @Override
    public void handleUpdate(Update update) {
        if (update.getCallback_query() != null) {
            server.getCommandManager().onCallbackQuery(update);
            server.getModuleManager().getModules().forEach(m -> m.onCallbackQuery(update.getCallback_query()));
        }
        
        try {
            if (update.getMessage().getType().equals(Message.Type.TEXT)) {
                //Si la update fue recibida hace más de 10 minutos, ignorarla
                if (Instant.ofEpochSecond(update.getMessage().getDate().longValue()).isBefore(Instant.now().minusSeconds(10 * 60))) return;

                boolean success = server.getCommandManager().onCmd(bot, update);
                server.getModuleManager().getModules().forEach(m -> m.onPostCommand(update, success));
            }
        } catch (TelegramException ex) {
            log.severe("Fallo procesando una Update de la API de Telegram");
            log.severe(ex.getMessage());
        }
    }
}
