package com.cadiducho.bot.api.module;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.telegrambotapi.Update;

public interface Module {

    BotServer botServer = BotServer.getInstance();
    
    /**
     * Devuelve si el módulo está activado en el servidor
     * @return enabled
     */
    default boolean isEnabled() {
        return botServer.getModuleManager().getModules().contains(this);
    }

    /**
     * Retorna el nombre del módulo
     * @return nombre
     */
    default String getName() {
        if (getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);
            return moduleInfo.name();
        } else {
            return null;
        }
    }

    /**
     * Devuelve una corta descripción del módulo
     *
     * @return La descripción
     */
    default String getDescription() {
        if (getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);
            return moduleInfo.description();
        } else {
            return null;
        }
    }

    default void onLoad() {
    }

    default void onClose() {
    }
    
    //ToDo: Mejorar API de módulos, no ejecutar todos los listeners en todos los modulos
    default void onCommandExecuted(BotCommand cmd) {
    }
    
    default void onPostCommand(Update update) {
    }
}