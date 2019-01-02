package com.cadiducho.bot.api.command.args;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeArgumentType implements ArgumentType<LocalDateTime> {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy/HH:mm");
    final DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("d/M/yy/HH:mm");

    @Override
    public LocalDateTime parse(String str) throws CommandParseException {
        try {
            return LocalDateTime.parse(str, formatter);
        } catch (DateTimeParseException ex1) {
            try {
                return LocalDateTime.parse(str, shortFormatter);
            } catch (DateTimeParseException ex2) {
                throw new CommandParseException(ex2);
            }
        }
    }
}
