package com.cadiducho.bot.api.command.args;

public class DoubleArgumentType implements ArgumentType<Double> {

    @Override
    public Double parse(String str) throws CommandParseException {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            throw new CommandParseException(ex);
        }
    }
}

