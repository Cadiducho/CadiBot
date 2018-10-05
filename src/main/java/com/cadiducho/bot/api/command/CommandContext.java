package com.cadiducho.bot.api.command;

import com.cadiducho.bot.api.command.args.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Clase para manejar el contexto en el que el comando es enviado
 */
public class CommandContext {

    private final Map<String, String> rawArguments;
    private final Map<String, Class<?>> rawTypes;
    private String lastArgument;

    private static final Map<Class<?>, ArgumentType> parsers = new HashMap<>();
    static {
        //primitives
        parsers.put(String.class, new StringArgumentType());
        parsers.put(Integer.class, new IntegerArgumentType());
        parsers.put(Double.class, new DoubleArgumentType());
        parsers.put(Long.class, new LongArgumentType());
        parsers.put(Boolean.class, new BoolArgumentType());

        //dates
        parsers.put(LocalDate.class, new LocalDateArgumentType());
        parsers.put(LocalDateTime.class, new LocalDateTimeArgumentType());
    }

    public CommandContext(List<Argument> desiredArguments, String[] sendedArguments) {
        this.rawArguments = new HashMap<>();
        this.rawTypes = new HashMap<>();
        int i = 0;
        for (Argument argument : desiredArguments) {
            if (sendedArguments.length > i) {
                rawArguments.put(argument.name(), sendedArguments[i]);
                rawTypes.put(argument.name(), argument.type());

                if (desiredArguments.size() == (i + 1)) { //si es el ultimo argumento esperado
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int k = i; k < sendedArguments.length; ++k) {
                        if (k > i) {
                            stringBuilder.append(" ");
                        }
                        stringBuilder.append(sendedArguments[k]);
                    }
                    this.lastArgument = stringBuilder.toString();
                }
            }
            ++i;
        }
    }

    /**
     * Obten un argumento según su nombre
     * @param argName Nombre del argumento
     * @param type El {@link ArgumentType} para transformar el argumento en T
     * @param <T> Tipo del argumento
     * @return El argumento enviado por el usuario
     * @throws CommandParseException Si el argumento falla al ser trasformado a T
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String argName, ArgumentType<T> type) throws CommandParseException {
        if (!rawArguments.containsKey(argName)) {
            return Optional.empty();
        }

        if (!parsers.containsKey(rawTypes.get(argName))) {
            throw new CommandParseException("There is no serializer defined to the type '" + rawTypes.get(argName) + "'");
        }
        ArgumentType<T> parser;
        if (type != null) {
            parser = type;
        } else {
            parser = (ArgumentType<T>) parsers.get(rawTypes.get(argName));
        }
        return Optional.of(parser.parse(rawArguments.get(argName)));
    }

    /**
     * Obten un argumento según su nombre
     * @param argName Nombre del argumento
     * @param <T> Tipo del argumento
     * @return El argumento enviado por el usuario
     * @throws CommandParseException Si el argumento falla al ser trasformado a T
     */
    public <T> Optional<T> get(String argName) throws CommandParseException {
        return get(argName, null);
    }

    public Optional<String> getLastArguments() {
        if (rawArguments.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(lastArgument);
    }
}
