package com.cadiducho.bot.api.command.args;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateArgumentType implements ArgumentType<LocalDate> {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate parse(String str) throws CommandParseException {
        try {
            return LocalDate.parse(str, formatter);
        } catch (DateTimeParseException ex) {
        throw new CommandParseException(ex);
        }
    }
}
