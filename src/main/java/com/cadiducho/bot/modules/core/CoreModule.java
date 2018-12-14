package com.cadiducho.bot.modules.core;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.core.cmds.*;

@ModuleInfo(name = "Cadibot-Core", description = "Funcionalidades generales de Cadibot")
public class CoreModule implements Module {

    @Override
    public void onLoad() {
        CommandManager commandManager = BotServer.getInstance().getCommandManager();

        commandManager.register(new VersionCMD());
        commandManager.register(new ChangelogCMD());
        commandManager.register(new HoraCMD());
        commandManager.register(new IntentarCMD());
        commandManager.register(new BroadcastCMD());
    }
}
