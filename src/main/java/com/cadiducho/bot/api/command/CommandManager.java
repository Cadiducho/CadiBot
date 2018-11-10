package com.cadiducho.bot.api.command;

import com.cadiducho.bot.BotServer;
import com.cadiducho.telegrambotapi.*;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class to handle and add all his commands
 *
 * @author Cadiducho
 */
@Log
@RequiredArgsConstructor
public class CommandManager {

    private final Map<String, BotCommand> commandMap = new HashMap<>();
    private final Map<String, CallbackListenerInstance> callbackListenersMap = new HashMap<>();
    private final BotServer server;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * Registrar un comando y, si contiene, sus listener de CallbackQuery
     * @param cmd El comando a registrar
     */
    public void register(BotCommand cmd) {
        cmd.getAliases().forEach(alias -> commandMap.put(alias, cmd));

        //Comprobar si tiene Listeners en su interior, y registrarlos
        if (cmd.getClass().isAnnotationPresent(CallbackListener.class)) {
            for (Method method : cmd.getClass().getMethods()) {
                if (method.isAnnotationPresent(ListenTo.class)) {
                    ListenTo listenTo = method.getAnnotation(ListenTo.class);
                    CallbackListenerInstance instance = new CallbackListenerInstance(cmd, method); //necesitamos guardar el método y su instancia de clase para ejecutarla mediante reflection
                    callbackListenersMap.put(listenTo.value(), instance);
                }
            }
        }
    }

    public Optional<BotCommand> getCommand(String alias) {
        return Optional.ofNullable(commandMap.get(alias));
    }

    public Optional<CallbackListenerInstance> getCallbackListener(String query) {
        return Optional.ofNullable(callbackListenersMap.get(query));
    }

    /**
     * Ejecutar un comando
     *
     * @param bot Bot que recibe la update
     * @param update Update del comando
     * @return Verdadero si se ha ejecutado, falso si no
     * @throws com.cadiducho.telegrambotapi.exception.TelegramException Excepcion
     */
    public boolean onCmd(TelegramBot bot, Update update) throws TelegramException {
        Instant now = Instant.now();
        Message message = update.getMessage();
        User from = update.getMessage().getFrom();

        log.info(dateTimeFormatter.format(now) + " " +
                        (from.getUsername() == null ? from.getFirstName() : ("@" + from.getUsername())) +
                        "#" + message.getChat().getId() +
                        ": " + message.getText());

        String[] rawcmd = message.getText().split(" ");
        if (rawcmd.length == 0) {
            return false;
        }

        String sentLabel = rawcmd[0].toLowerCase().replace("@" + bot.getMe().getUsername(), "");
        Optional<BotCommand> target = getCommand(sentLabel);
        if (!target.isPresent()) {
            // no encontrado por primer alias, buscar frase entera
            target = getCommand(message.getText().toLowerCase());
            if (!target.isPresent()) {
                return false; // ni alias ni frase entera
            }
        }

        CommandContext context = new CommandContext(target.get().getArguments(), Arrays.copyOfRange(rawcmd, 1, rawcmd.length));

        log.info(" # Ejecutando '" + target.get().getName() + "'");
        target.get().execute(message.getChat(), from, context, message.getMessageId(), message.getReplyToMessage(), now);

        return true;
    }

    public void onCallbackQuery(CallbackQuery callbackQuery) {
        Instant now = Instant.now();
        User from = callbackQuery.getFrom();

        log.info(dateTimeFormatter.format(now) + " InlineCallbackQuery: " +
                (from.getUsername() == null ? from.getFirstName() : ("@" + from.getUsername())) +
                "#" + (callbackQuery.getMessage() != null ? callbackQuery.getMessage().getChat().getId() : "") +
                ": " + callbackQuery.getData());

        Optional<CallbackListenerInstance> target = getCallbackListener(callbackQuery.getData());
        if (target.isPresent()) {
            CallbackListenerInstance instance = target.get();
            try {
                log.info(" # Ejecutando callback listener para '" + callbackQuery.getData() + "'");
                instance.method.invoke(instance.commandInstance, callbackQuery);
            } catch (InvocationTargetException invocationException) {
                if (invocationException.getCause() instanceof TelegramException) { // los métodos de listener pueden lanzar TelegramException
                    log.severe("Error respondiendo a un CallbackQuery en la API de Telegram: ");
                    log.severe(invocationException.getCause().getMessage());
                }
            } catch (IllegalAccessException e) {
                log.severe("Error accediendo a un CallbackListener: ");
                log.severe(e.getMessage());
            }
        }
    }

    @RequiredArgsConstructor
    private class CallbackListenerInstance {
        private final BotCommand commandInstance;
        private final Method method;
    }
}
