package com.cadiducho.bot.api.command;

import com.cadiducho.bot.api.command.args.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Clase para manejar el contexto en el que el comando es enviado
 */
public class CommandContext {

    final Map<String, String> rawArguments;
    final Map<String, Class<?>> rawTypes;
    private static final Map<Class<?>, ArgumentType> parsers = new HashMap<>();
    static {
        parsers.put(String.class, new StringArgumentType());
        parsers.put(Integer.class, new IntegerArgumentType());
    }

    public CommandContext(List<Argument> desiredArguments, String[] sendedArguments) {
        this.rawArguments = new HashMap<>();
        this.rawTypes = new HashMap<>();
        int i = 0;
        for (Argument argument : desiredArguments) {
            if (sendedArguments.length > i) {
                rawArguments.put(argument.name(), sendedArguments[i]);
                rawTypes.put(argument.name(), argument.type());
            }
            ++i;
        }
    }

    /**
     * Obten un argumento según su nombre
     * @param argName Nombre del argumento
     * @param <T> Tipo del argumento
     * @return El argumento enviado por el usuario
     * @throws CommandParseException Si el argumento falla al ser trasformado a T
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String argName) throws CommandParseException {
        if (!rawArguments.containsKey(argName)) {
            return Optional.empty();
        }

        ArgumentType<T> parser = (ArgumentType<T>) parsers.get(rawTypes.get(argName));
        return Optional.of(parser.parse(rawArguments.get(argName)));
    }
}
