package com.cadiducho.bot.modules.core;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.command.simple.SimpleGifCMD;
import com.cadiducho.bot.api.command.simple.SimplePhotoCMD;
import com.cadiducho.bot.api.command.simple.SimpleTextCMD;
import com.cadiducho.bot.api.command.simple.SimpleVoiceCMD;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.core.cmds.*;

import java.util.Arrays;

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

        commandManager.register(new SimpleTextCMD(Arrays.asList(":("), Arrays.asList("Sonríe, princesa", "dont be sad bb", ":)", "no me calientes a ver si te voy a dar motivos para estar sad", "Sonríe, no cambiará nada pero te verás menos feo", "Yo también estaría triste si tuviese tu cara", "tampoco me llores crack")));
        commandManager.register(new SimpleTextCMD(Arrays.asList(":)"), Arrays.asList(":)", ":D", ":smile:", ":smiley:", ":grinning:", ":blush:")));
        commandManager.register(new SimpleTextCMD(Arrays.asList("haber si me muero"), "Ojalá"));
        commandManager.register(new SimpleTextCMD(Arrays.asList("ok"), Arrays.asList("OK", "No te enfades bb", "ok", "ok de k hijo de puta", "ko", "Va", "ya")));
        commandManager.register(new SimpleTextCMD(Arrays.asList("/patata"), "Soy una patata y tú no. Bienvenido a tu cinta"));
        commandManager.register(new SimplePhotoCMD(Arrays.asList("emosido engañado", "emosido engañados"), "AgADBAADJKkxGygIoVKAZOwKkgWHWn7avBkABP0h-V4_8VernoACAAEC"));
        commandManager.register(new SimpleGifCMD(Arrays.asList("what"), "CgADBAADMwMAAjAZZAenCigGk2AkogI"));
        commandManager.register(new SimpleGifCMD(Arrays.asList("aplausos"), "CgADBAADTAMAAjEZZAfPEedEXgJYPwI"));
        commandManager.register(new SimpleGifCMD(Arrays.asList("/servilleta"), "CgADBAADiwMAAt_WwFGgpX45Eh_AKAI"));
        commandManager.register(new SimpleVoiceCMD(Arrays.asList("/titanic"), "AwADBAADujYAAv4dZAcEaOHAa3eQ8wI"));
        commandManager.register(new SimpleVoiceCMD(Arrays.asList("puigdemont", "/viva"), "AwADBAAD0AIAAnhgYFAc6it_QeBppwI"));
    }
}
