package com.cadiducho.bot.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marcar un método como código que se ejecutará cuando se reciba una CallbackQuery nombrada por su valor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenTo {

    /**
     * Retorna el valor de a qué llamada de CallbackQuery responderá
     * @return a qué CallbackQuery responderá
     */
    String value();
}
