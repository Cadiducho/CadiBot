package com.cadiducho.bot.api.command.args;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArguments {
    Argument[] value() default {};
}
