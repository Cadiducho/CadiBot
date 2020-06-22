package com.cadiducho.bot.modules.insultos;

import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Insultos", description = "Insultas y el bot te insulta")
public class InsultosModule implements ZinciteModule {

    private final List<String> insultos = Arrays.asList("tus muertos", "cuerpoescombro", "canica", "anormal", "retrasado", "parguelas", "jueputa",
            "malparido", "gonorrea", "cabezabuque", "gilipollas", "cabezón", "cabezon", "subnormal", "joputa", "me cago en tus ancestros", "desgraciado",
            "desgraciao", "tonto", "mortadelo", "lonchas", "lammer", "cabezahuevo");

    @Override
    public void onLoad() {
        CommandManager commandManager = ZinciteBot.getInstance().getCommandManager();

        commandManager.register(new InsultosAbstractCMD(insultos, insultos)); //todos los insultos de alias y te responde aleatoriamente uno de esos
    }
}
