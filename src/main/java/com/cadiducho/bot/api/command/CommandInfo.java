package com.cadiducho.bot.api.command;

import com.cadiducho.bot.api.module.Module;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    Class<? extends Module> module() default Module.class;
    String[] aliases();
}