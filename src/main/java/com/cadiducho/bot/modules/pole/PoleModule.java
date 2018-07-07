package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.pole.cmds.PoleCMD;
import com.cadiducho.bot.modules.pole.cmds.PoleListCMD;
import com.cadiducho.bot.modules.pole.cmds.UpdateUsernameCMD;
import lombok.Getter;

@ModuleInfo(name = "Poles", description = "Módulo para hacer poles cada día")
public class PoleModule implements Module {

    public static final String TABLA_POLES = "cadibot_poles";

    @Getter private PoleCacheManager poleCacheManager;

    @Override
    public void onLoad() {
        poleCacheManager = new PoleCacheManager(this);
        poleCacheManager.initializeDirectory();
        poleCacheManager.loadCachedGroups();

        CommandManager commandManager = BotServer.getInstance().getCommandManager();
        commandManager.register(new UpdateUsernameCMD());
    }
}
