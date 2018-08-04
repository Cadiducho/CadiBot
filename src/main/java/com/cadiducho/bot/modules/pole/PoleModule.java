package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.pole.cmds.PoleCMD;
import com.cadiducho.bot.modules.pole.cmds.PoleListCMD;
import com.cadiducho.bot.modules.pole.cmds.UpdateUsernameCMD;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

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
        commandManager.register(new PoleCMD());
        commandManager.register(new PoleListCMD());
        commandManager.register(new UpdateUsernameCMD());
    }

    @Override
    public void onCallbackQuery(CallbackQuery callbackQuery) {
        try {
            if (callbackQuery.getData().equals("mostrarMasPoles")) {
                botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessage_id(), callbackQuery.getInline_message_id(),
                        "Esta funcionalidad se encuentra en pruebas", null, null, null);
            }
        } catch (TelegramException ex) {
            BotServer.logger.warning(ex.getMessage());
        }
    }

    /**
     * Obtener el top de poles de un día y un chat concreto
     * @param today El día
     * @param chatId El chat
     * @return Map de posición e ID del usuario que hizo una pole, plata o bronce
     * @throws SQLException Si falla la conexión a la base de datos
     */
    public LinkedHashMap<Integer, Integer> getPolesOfToday(LocalDateTime today, long chatId) throws SQLException {
        PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement(
                "SELECT * FROM `" + PoleModule.TABLA_POLES + "` WHERE "
                        + "DATE(time)=DATE(?) AND "
                        + "`groupchat`=?"
                        + " ORDER BY `poleType`");
        statement.setTimestamp(1, Timestamp.valueOf(today));
        statement.setLong(2, chatId);
        ResultSet rs = statement.executeQuery();

        LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            poles.put(rs.getRow(), rs.getInt("userid"));
        }

        return poles;
    }

    /**
     * Obtener el top de poles global de un grupo, según su tipo
     * El tipo puede ser:
     *  1. Oro/Pole
     *  2. Plata/Subpole
     *  3. Bronce
     * @param chatId El chat
     * @param type El tipo
     * @return Map de usuario y cantidad de poles de dicho tipo realizado en dicho grupo
     * @throws SQLException
     */
    public LinkedHashMap<Integer, Integer> getTopPoles(long chatId, int type, int limit) throws SQLException {
        PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement("SELECT count(*) AS `totales`,`userid` FROM `" + PoleModule.TABLA_POLES + "`"
                + " WHERE `groupchat`=? AND `poleType`=? GROUP BY `userid` ORDER BY `totales` DESC LIMIT ?");
        statement.setLong(1, chatId);
        statement.setInt(2, type);
        statement.setInt(3, limit);
        ResultSet rs = statement.executeQuery();
        LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            poles.put(rs.getInt("userid"), rs.getInt("totales"));
        }

        return poles;
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
