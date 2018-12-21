package com.cadiducho.bot.api.command.args;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateArgumentType implements ArgumentType<LocalDate> {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    final DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("d/M/yy");

    @Override
    public LocalDate parse(String str) throws CommandParseException {
        try {
            return LocalDate.parse(str, formatter);
        } catch (DateTimeParseException ex1) {
            try {
                return LocalDate.parse(str, shortFormatter);
            } catch (DateTimeParseException ex2) {
                throw new CommandParseException(ex2);
            }
        }
    }
}
