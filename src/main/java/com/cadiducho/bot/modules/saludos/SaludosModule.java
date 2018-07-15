package com.cadiducho.bot.modules.saludos;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.saludos.cmds.HolaCMD;

@ModuleInfo(name = "Saludos", description = "Conjunto de saludos")
public class SaludosModule implements Module {

    @Override
    public void onLoad() {
        BotServer.getInstance().getCommandManager().register(new HolaCMD());
    }

}
