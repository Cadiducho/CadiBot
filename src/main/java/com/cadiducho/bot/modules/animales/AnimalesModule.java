package com.cadiducho.bot.modules.animales;

import com.cadiducho.bot.modules.animales.cmds.CatCMD;
import com.cadiducho.bot.modules.animales.cmds.DuckCMD;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;

@ModuleInfo(name = "Animales", description = "Fotos y cosas relaccionadas a animales")
public class AnimalesModule implements ZinciteModule {

    @Override
    public void onLoad() {
        CommandManager commandManager = ZinciteBot.getInstance().getCommandManager();

        commandManager.register(new CatCMD());
        commandManager.register(new DuckCMD());
    }
}
