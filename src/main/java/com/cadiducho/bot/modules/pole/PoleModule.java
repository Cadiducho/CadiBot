package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.pole.cmds.PoleCMD;
import com.cadiducho.bot.modules.pole.cmds.PoleListCMD;
import com.cadiducho.bot.modules.pole.cmds.UpdateUsernameCMD;
import com.cadiducho.bot.modules.pole.cmds.admin.AnalyzeUserCMD;
import com.cadiducho.bot.modules.pole.cmds.admin.MigrateGroupCMD;
import com.cadiducho.bot.modules.pole.util.BanUserListener;
import com.cadiducho.bot.modules.pole.util.PoleAntiCheat;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;
import lombok.Getter;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Log
@ModuleInfo(name = "Poles", description = "Módulo para hacer poles cada día")
public class PoleModule implements Module {

    public static final String TABLA_POLES = "cadibot_poles";

    @Getter private PoleCacheManager poleCacheManager;
    @Getter private PoleAntiCheat poleAntiCheat;

    @Override
    public void onLoad() {
        log.info("Cargando módulo de poles");
        poleCacheManager = new PoleCacheManager(this);
        poleCacheManager.loadCachedGroups();
        poleAntiCheat = new PoleAntiCheat(this);
        poleAntiCheat.loadBannedUsers();

        CommandManager commandManager = BotServer.getInstance().getCommandManager();
        commandManager.register(new PoleCMD());
        commandManager.register(new PoleListCMD());
        commandManager.register(new UpdateUsernameCMD());
        commandManager.register(new MigrateGroupCMD());
        commandManager.register(new AnalyzeUserCMD());
        commandManager.registerCallbackQueryListener(new BanUserListener(this));

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
     * @return True si se pueden realizar poles
     */
    public boolean isChatUnsafe(TelegramBot bot, Chat chat) throws TelegramException {
        if (chat.isUser()) {
            bot.sendMessage(chat.getId(), "No se vale hacer poles por privado loko");
            return true;
        }
        if (bot.getChatMembersCount(chat.getId()) < 3) {
            bot.sendMessage(chat.getId(), "Este grupo no tiene el mínimo de usuarios para hacer poles " + EmojiManager.getForAlias("sweat_smile").getUnicode());
            return true;
        }
        return false;
    }

    /**
     * Obten el nombre de un grupo de la base de datos a partir de su ID
     * @param groupId La id del grupo
     * @return el nombre del grupo
     */
    public Optional<String> getGroupName(Long groupId) {
        Optional<String> name = Optional.empty();
        try (Connection connection = botServer.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM cadibot_grupos WHERE groupid=?");
            statement.setLong(1, groupId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = Optional.of(rs.getString("name"));
            }
        } catch (SQLException exception) {
            log.severe("Error obteniendo un grupo para su migración");
            log.severe(exception.toString());
        }
        return name;
    }

    /**
     * Obten el nombre de un usuario desde la base de datos
     * La posición 0 representará el nombre y la 1 el @username
     * @param userid La id del usuario
     * @return El nombre usuario
     */
    public String[] getUsername(Integer userid) {
        String name = "";
        String username = "";
        try (Connection connection = botServer.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name, username FROM cadibot_users WHERE userid=?");
            statement.setInt(1, userid);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
                username = rs.getString("username");
            }
        } catch (SQLException exception) {
            log.severe("Error obteniendo un grupo para su migración");
            log.severe(exception.toString());
        }
        return new String[] {name, username};
    }
}
