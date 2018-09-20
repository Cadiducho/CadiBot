package com.cadiducho.bot.modules.animales;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.animales.cmds.CatCMD;

@ModuleInfo(name = "Animales", description = "Fotos y cosas relaccionadas a animales")
public class AnimalesModule implements Module {

    @Override
    public void onLoad() {
        CommandManager commandManager = BotServer.getInstance().getCommandManager();

        commandManager.register(new CatCMD());
    }
}
