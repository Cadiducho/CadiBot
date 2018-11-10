package com.cadiducho.bot;

import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.handlers.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Clase para gestionar las excepciones de la API al obtener las updates
 */
@RequiredArgsConstructor
@Log
public class TelegramExceptionHandler implements ExceptionHandler {

    private final TelegramBot bot;
    private final Long ownerId;

    @Override
    public void handle(TelegramException exception) {
        if (exception.getCause() != null && exception.getCause().getMessage().equals("Read timed out")) {
            //ignore
            return;
        }
        log.severe(DateTimeFormatter.ofPattern("HH:mm:ss:S").withZone(ZoneId.systemDefault()).format(Instant.now()) + " An exception occurred while polling Telegram:");
        log.severe(exception.getMessage());
        send("An exception occurred while polling Telegram: <code>" + exception + "</code>");
        if (exception.getCause() != null) {
            log.severe("Cause: " + exception.getCause().getMessage());
            send("Cause: <code>" + exception.getCause().getMessage() + "</code>");
        }
    }

    /**
     * Enviar un mensaje al ownerId por Telegram para informar del error.
     * Si se produce otro error al enviar el mensaje, este último error es ignorado
     * @param msg El mensaje a enviar
     */
    private void send(String msg) {
        try {
            bot.sendMessage(ownerId, msg, "html", null, null, null, null);
        } catch (TelegramException ignored) { }
    }
}
