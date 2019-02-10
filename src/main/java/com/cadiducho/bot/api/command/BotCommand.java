package com.cadiducho.bot.api.command;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.args.Argument;
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
     * @param context El {@link CommandContext} en el que ha sido ejecutado el comando
     * @param messageId ID del mensaje del comando
     * @param replyingTo Mensaje al que el comando respondía
     * @param instant Instante en el que el comando fue ejecutado
     * @throws TelegramException Excepción ocurrida
     */
    void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException;
    
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
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return new ArrayList<>();
        }
        Argument[] arguments = this.getClass().getAnnotation(CommandInfo.class).arguments();
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

        stringBuilder.append("<code>")
            .append(this.getName());
        for (Argument argument : this.getArguments()) {
            String open = "&lt;"; // <
            String close = "&gt;"; // >
            if (!argument.required()) {
                open = "[";
                close = "]";
            }
            stringBuilder.append(' ').append(open).append(argument.name()).append(close);
        }
        stringBuilder.append("</code>").append(": ").append(this.getDescription());
        for (Argument argument : this.getArguments()) {
            String open = "&lt;"; // <
            String close = "&gt;"; // >
            String opcional = "";
            if (!argument.required()) {
                open = "[";
                close = "]";
                opcional = ", opcional";
            }
            stringBuilder.append("\n <b>·</b> ")
                    .append(open).append(argument.name()).append(close)
                    .append(" (<i>").append(getArgumentName(argument)).append("</i>").append(opcional)
                    .append("): ").append(argument.description());
        }
        return stringBuilder.toString();
    }

    default String getArgumentName(Argument argument) {
        switch (argument.type().getSimpleName()) {
            case "String": return "Texto";
            case "Integer":
            case "Long": return "Número";
            case "Double": return "Número con decimales";
            case "LocalDate": return "Fecha";
            case "LocalDateTime": return "Fecha y hora";
            default: return argument.type().getSimpleName();
        }
    }
    
    default TelegramBot getBot() {
        return botServer.getCadibot();
    }
}
