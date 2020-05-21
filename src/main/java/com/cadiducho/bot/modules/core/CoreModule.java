package com.cadiducho.bot.modules.core;

import com.cadiducho.bot.modules.core.cmds.*;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;

@ModuleInfo(name = "Cadibot-Core", description = "Funcionalidades generales de Cadibot")
public class CoreModule implements ZinciteModule {

    @Override
    public void onLoad() {
        CommandManager commandManager = ZinciteBot.getInstance().getCommandManager();

        commandManager.register(new VersionCMD());
        commandManager.register(new ChangelogCMD());
        commandManager.register(new HoraCMD());
        commandManager.register(new IntentarCMD());
        commandManager.register(new BroadcastCMD());
    }
}
