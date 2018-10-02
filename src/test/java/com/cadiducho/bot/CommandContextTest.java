package com.cadiducho.bot;

import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.args.Argument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandContextTest {

    static List<Argument> arguments;
    static CommandContext context;

    @BeforeAll
    public static void setUp() {
        arguments = Arrays.asList(
                new TestArgument("nombre", String.class, false),
                new TestArgument("numero", Integer.class, false),
                new TestArgument("ultimo", String.class, false)
        );
        context = new CommandContext(arguments, new String[]{"primero", "3", "hola", "qué", "tal"});
    }

    @Test
    public void testLastArgument() {
        assertEquals(context.getLastArguments().get(), "hola qué tal");
    }

    @Test
    public void testStringParser() {
        assertEquals(context.get("nombre").get(), "primero");
    }

    @Test
    public void testIntParser() {
        assertEquals(context.get("numero").get(), 3);
    }
}
