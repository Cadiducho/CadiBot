package com.cadiducho.bot.api.command.args;

public class LongArgumentType implements ArgumentType<Long> {

    @Override
    public Long parse(String str) throws CommandParseException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ex) {
            throw new CommandParseException(ex);
        }
    }
}

