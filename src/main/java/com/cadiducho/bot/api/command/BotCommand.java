package com.cadiducho.bot.api.command;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.MySQL;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
     * @param date Instante en el que el comando fue ejecutado
     * @throws TelegramException Excepción ocurrida
     */
    default void execute(Chat chat, User from, String label, String[] args, Integer messageId, Date date) throws TelegramException {
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
            return null;
        }
        return this.getClass().getAnnotation(CommandInfo.class).aliases()[0];
    }

    default List<String> getAliases() {
        if (!this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return null;
        }
        return Arrays.asList(this.getClass().getAnnotation(CommandInfo.class).aliases());
    }
    
    default TelegramBot getBot() {
        //TODO: Multi bots? via annotation
        return botServer.getCadibot();
    }
    
    default MySQL getMySQL() {
        return botServer.getMysql();
    }
}
