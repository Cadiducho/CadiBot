package com.cadiducho.bot.api.command;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.MySQL;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandArguments;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;
import java.util.*;

/**
 * Comando para el bot
 * @author Cadiducho
 */
public interface BotCommand {
    BotServer botServer = BotServer.getInstance();

    /**
     * Ejecutar un comando
     * @param chat Chat donde el comando fue recibido
     * @param from Usuario por el que el comando fue ejecutado
     * @param label Primera palabra del comando ejecutado
     * @param args Argumentos del comando
     * @param messageId ID del mensaje del comando
     * @param replyingTo Mensaje al que el comando respondía
     * @param instant Instante en el que el comando fue ejecutado
     * @throws TelegramException Excepción ocurrida
     */
    default void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
    }

    default void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {

    }
    
    default Module getModule() {
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return null;
        }
        Class<? extends Module> mclass = this.getClass().getAnnotation(CommandInfo.class).module();
        for (Module module : botServer.getModuleManager().getModules()) {
            if (module.getClass().equals(mclass)) {
                return module;
            }
        }
        return null;
    }
    
    //la primera aliase de la anotación
    default String getName() {
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return "";
        }
        return this.getClass().getAnnotation(CommandInfo.class).aliases()[0];
    }

    default List<String> getAliases() {
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.getClass().getAnnotation(CommandInfo.class).aliases());
    }

    default List<Argument> getArguments() {
        if (!this.getClass().isAnnotationPresent(CommandArguments.class)) {
            return new ArrayList<>();
        }
        Argument[] arguments = this.getClass().getAnnotationsByType(Argument.class);
        return Arrays.asList(arguments);
    }

    default String getDescription() {
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return "";
        }
        return this.getClass().getAnnotation(CommandInfo.class).description();
    }

    default String getUsage() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.getName());
        for (Argument argument : this.getArguments()) {
            String open = "&lt;"; // <
            String close = "&gt;"; // >
            if (!argument.required()) {
                open = "[";
                close = "]";
            }
            stringBuilder.append(' ').append(open).append(argument.name()).append(close);
        }
        stringBuilder.append(" : ").append(this.getDescription());
        for (Argument argument : this.getArguments()) {
            String open = "&lt;"; // <
            String close = "&gt;"; // >
            if (!argument.required()) {
                open = "[";
                close = "]";
            }
            stringBuilder.append("\n - ").append(open).append(argument.name()).append(close)
                    .append(" (").append(argument.type().getSimpleName()).append("): ").append(argument.description());
        }
        return stringBuilder.toString();
    }
    
    default TelegramBot getBot() {
        return botServer.getCadibot();
    }
    
    default MySQL getMySQL() {
        return botServer.getMysql();
    }
}
