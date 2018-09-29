package com.cadiducho.bot.api.command.args;

public class IntegerArgumentType implements ArgumentType<Integer> {

    @Override
    public Integer parse(String str) throws CommandParseException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new CommandParseException(ex);
        }
    }
}

