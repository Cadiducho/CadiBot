package com.cadiducho.bot;

import com.cadiducho.bot.api.command.args.Argument;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class TestArgument implements Argument {

    private String name;
    private Class<?> type;
    private boolean required;

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public boolean required() {
        return required;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}