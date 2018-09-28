package com.cadiducho.bot.api.command.args;


import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = CommandArguments.class)
public @interface Argument {
    String name();
    Class<?> type();
    boolean required() default true;
}