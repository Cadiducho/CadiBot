package com.cadiducho.bot;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandArguments;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    @Test
    public void testCommandInfo() {
        BotCommand command = new TestCommand();

        assertEquals("/comando", command.getName());
        assertIterableEquals(Arrays.asList("/comando", "/alias", "/alternativa"), command.getAliases());
        assertEquals("descripción de prueba", command.getDescription());
    }

    @Test
    public void testCommandUsage() {
        BotCommand command = new TestCommandWithArguments();

        String usoEsperado = "/commandWithArgs <nombre> [cantidad] : resumen del comando con argumentos";
        usoEsperado += "\n - <nombre> (String): Nombre del usuario";
        usoEsperado += "\n - [cantidad] (Integer): Cantidad a asignar";
        assertEquals(usoEsperado, command.getUsage());
    }

    @CommandInfo(aliases = {"/comando", "/alias", "/alternativa"}, description = "descripción de prueba")
    private class TestCommand implements BotCommand {
    }

    @CommandInfo(aliases = "/commandWithArgs", description = "resumen del comando con argumentos")
    @CommandArguments({
            @Argument(name = "nombre", type = String.class, required = true, description = "Nombre del usuario"),
            @Argument(name = "cantidad", type = Integer.class, required = false, description = "Cantidad a asignar")
    })
    private class TestCommandWithArguments implements BotCommand {
    }
}
