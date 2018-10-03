package com.cadiducho.bot;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandParseException;
import com.cadiducho.bot.api.command.args.LocalDateArgumentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandContextTest {

    static List<Argument> arguments;
    static CommandContext context;

    @BeforeAll
    public static void setUp() {
        arguments = Arrays.asList(
                new TestArgument("nombre", String.class, false),
                new TestArgument("unknown", Argument.class, false),
                new TestArgument("numero", Integer.class, false),
                new TestArgument("double", Double.class, false),
                new TestArgument("long", Long.class, false),
                new TestArgument("bool", Boolean.class, false),
                new TestArgument("fecha", LocalDate.class, false),
                new TestArgument("fechaHora", LocalDateTime.class, false),
                new TestArgument("ultimo", String.class, false)
        );
        context = new CommandContext(arguments, new String[]{"primero", "", "3", "38", "10000000", "true", "01/10/2018",
                "02/02/2002/20:30", "hola", "qué", "tal"});
    }

    @Test
    public void testLastArgument() {
        assertEquals(context.getLastArguments().get(), "hola qué tal");
    }

    @Test
    public void testStringParser() throws Exception {
        assertEquals(context.get("nombre").get(), "primero");
    }

    @Test
    public void testIntParser() throws Exception {
        assertEquals(context.get("numero").get(), 3);
    }

    @Test
    public void testDoubleParser() throws Exception {
        assertEquals(context.get("double").get(), 38D);
    }

    @Test
    public void testLongParser() throws Exception {
        assertEquals(context.get("long").get(), 10000000L);
    }

    @Test
    public void testBoolParser() throws Exception {
        assertEquals(context.get("bool").get(), true);
    }

    @Test
    public void testDateParsers() throws Exception {
        assertEquals(context.get("fecha").get(), LocalDate.of(2018, 10, 1));
        assertEquals(context.get("fechaHora").get(), LocalDateTime.of(2002, 2, 2, 20, 30));

        assertThrows(CommandParseException.class, () -> new LocalDateArgumentType().parse("malFormato"));
    }

    @Test
    public void testUnknownParser() throws Exception {
        assertThrows(CommandParseException.class, () -> context.get("unknown"));
    }
}
