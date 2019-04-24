package com.cadiducho.bot.api.command.args;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {

    /**
     * Nombre del argumento
     * @return nombre del argumento
     */
    String name();

    /**
     * Tipo del argumento. Una class
     * @return tipo del argumento
     */
    Class<?> type();

    /**
     * Si el argumento es requerido o no
     * Eso sólo afectará a la hora de calcular el uso/descripción del comando, mostrándolo como obligatorio o no
     * @return si el argumento es requerido o no
     */
    boolean required() default true;

    /**
     * La breve descripción del argumento
     * Es recomendable incluir aquí consejos sobre el formato del argumento, por ejemplo, para fechas
     * @return breve descripción del argumento
     */
    String description();
}