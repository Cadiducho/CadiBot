package com.cadiducho.bot.api.command.args;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArguments {

    /**
     * Lista de argumentos del comando. Ver {@link Argument}
     * @return lista de argumentos
     */
    Argument[] value() default {};
}
