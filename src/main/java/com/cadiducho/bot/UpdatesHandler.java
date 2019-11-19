package com.cadiducho.bot;

import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.handlers.LongPollingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

@Log
@RequiredArgsConstructor
public class UpdatesHandler implements LongPollingHandler {

    private final TelegramBot bot;
    private final BotServer server;

    @Override
    public void handleUpdate(Update update) {
        final Instant now = Instant.now();
        if (update.getCallbackQuery() != null) {
            server.getCommandManager().onCallbackQuery(update.getCallbackQuery());
            return; //Si la update es una callback query, no es un mensaje de texto, un comando u otra cosa.
        }

        try {
            if (update.getMessage() != null) {
                if (update.getMessage().getType().equals(Message.Type.NEW_CHAT_MEMBERS)) {
                    server.getModuleManager().getModules().forEach(m -> m.onNewChatMembers(update.getMessage().getChat(), update.getMessage().getNewChatMembers()));
                }
                if (update.getMessage().getType().equals(Message.Type.LEFT_CHAT_MEMBER)) {
                    server.getModuleManager().getModules().forEach(m -> m.onLeftChatMember(update.getMessage().getChat(), update.getMessage().getLeftChatMember()));
                }
                if (update.getMessage().getType().equals(Message.Type.TEXT)) {
                    //Si la update fue recibida hace más de 10 minutos, ignorarla
                    if (Instant.ofEpochSecond(update.getMessage().getDate().longValue()).isBefore(now.minusSeconds(10 * 60)))
                        return;

                    boolean success = server.getCommandManager().onCmd(bot, update, now);
                    server.getModuleManager().getModules().forEach(m -> m.onPostCommand(update, success));
                }
            }
        } catch (TelegramException ex) {
            log.severe("Fallo procesando una Update de la API de Telegram: " + ex.getMessage());
            if (ex.getCause() != null) log.severe("Causa: " + ex.getCause().getMessage());

            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            log.severe(writer.toString());
        } catch (ConcurrentModificationException ex) {
            log.severe("Fallo de concurrencia procesando una Update de la API de Telegram: " + ex.getMessage());
            if (ex.getCause() != null) log.severe("Causa: " + ex.getCause().getMessage());

            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            log.severe(writer.toString());
        } catch (Exception ex) {
            log.severe("Fallo no esperado procesando una Update de la API de Telegram: " + ex.getMessage());
            if (ex.getCause() != null) log.severe("Causa: " + ex.getCause().getMessage());

            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            log.severe(writer.toString());
        }
    }
}
