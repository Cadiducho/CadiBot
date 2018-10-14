package com.cadiducho.bot.api.command.args;

public class BoolArgumentType implements ArgumentType<Boolean> {

    @Override
    public Boolean parse(String str) throws CommandParseException {
        switch (str.toLowerCase()) {
            case "true":
            case "verdadero":
            case "y":
            case "t":
            case "s":
                return true;
            case "false":
            case "falso":
            case "f":
            case "n":
                return false;
            default:
                throw new CommandParseException("Not a boolean");
        }
    }
}

