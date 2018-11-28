package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.pole.cmds.PoleCMD;
import com.cadiducho.bot.modules.pole.cmds.PoleListCMD;
import com.cadiducho.bot.modules.pole.cmds.UpdateUsernameCMD;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;

@Log
@ModuleInfo(name = "Poles", description = "Módulo para hacer poles cada día")
public class PoleModule implements Module {

    public static final String TABLA_POLES = "cadibot_poles";

    @Getter private PoleCacheManager poleCacheManager;

    @Override
    public void onLoad() {
        log.info("Cargando módulo de poles");
        poleCacheManager = new PoleCacheManager(this);
        poleCacheManager.loadCachedGroups();

        CommandManager commandManager = BotServer.getInstance().getCommandManager();
        commandManager.register(new PoleCMD());
        commandManager.register(new PoleListCMD());
        commandManager.register(new UpdateUsernameCMD());
        log.info("Módulo de poles cargado");
    }

    @Override
    public void onNewChatMembers(Chat chat, List<User> newChatMembers) {
        try {
            Integer botId = botServer.getCadibot().getMe().getId();
            if (newChatMembers.stream().anyMatch(user -> user.getId().equals(botId))) {
                log.info("Me han añadido al grupo " + chat.getTitle());
                botServer.getDatabase().updateGroup(chat.getId(), chat.getTitle(), true);
                poleCacheManager.setGroupLastAdded(Long.parseLong(chat.getId()));
            }
        } catch (TelegramException ignored) { }
    }

    /**
     * Comprobar si un chat está autorizado a hacer poles
     * @param bot Instancia del bot para mandar los mensajes de error
     * @param chat Chat para realizar comprobaciones
     * @param user Usuario para realizar comprobaciones
     * @return True si se pueden realizar poles
     */
    public boolean isChatSafe(TelegramBot bot, Chat chat, User user) throws TelegramException {
        if (chat.isUser()) {
            bot.sendMessage(chat.getId(), "No se vale hacer poles por privado loko");
            return false;
        }
        if (bot.getChatMembersCount(chat.getId()) < 3) {
            bot.sendMessage(chat.getId(), "Este grupo no tiene el mínimo de usuarios para hacer poles " + EmojiManager.getForAlias("sweat_smile").getUnicode());
            return false;
        }
        return true;
    }
}
