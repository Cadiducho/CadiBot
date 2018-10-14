package com.cadiducho.bot.api.command;

import com.cadiducho.bot.api.module.Module;

import java.lang.annotation.*;

/**
 * Anotación para construir los parámetros de un {@link BotCommand}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    /**
     * {@link com.cadiducho.bot.api.module.Module} al que pertenece el comando
     * @return Módulo al que pertenece el comando
     */
    Class<? extends Module> module() default Module.class;

    /**
     * Lista de alias por las que ese comando se puede ejecutar
     * @return lista de alias
     */
    String[] aliases();

    /**
     * Descripción breve de lo que hace el comando
     * @return descripción deol comando
     */
    String description() default "";
}