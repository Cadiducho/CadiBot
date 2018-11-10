package com.cadiducho.bot.modules.insultos;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Insultos", description = "Insultas y el bot te insulta")
public class InsultosModule implements Module {

    private final List<String> insultos = Arrays.asList("tus muertos", "cuerpoescombro", "canica", "anormal", "retrasado", "parguelas", "jueputa",
            "malparido", "gonorrea", "cabezabuque", "gilipollas", "cabezón", "cabezon", "subnormal", "joputa", "me cago en tus ancestros", "desgraciado",
            "desgraciao", "tonto", "mortadelo", "lonchas", "lammer");

    @Override
    public void onLoad() {
        CommandManager commandManager = BotServer.getInstance().getCommandManager();

        commandManager.register(new InsultosAbstractCMD(insultos, insultos)); //todos los insultos de alias y te responde aleatoriamente uno de esos
    }
}
