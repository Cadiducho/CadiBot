package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase para reunir los métodos relativos a enviar algo acerca de las poles o su ranking
 */
public class PoleMessengerUtil {

    public static String showPoleRank(Chat chat, PoleCacheManager manager, int limit) throws SQLException {
        StringBuilder body = new StringBuilder();
        body.append("\n\nRanking total: \n");
        Emoji gold = EmojiManager.getForAlias("first_place_medal");
        Emoji silver = EmojiManager.getForAlias("second_place_medal");
        Emoji bronze = EmojiManager.getForAlias("third_place_medal");
        Map<Integer, Integer> topPoles = getTopPoles(Long.parseLong(chat.getId()), 1, limit);
        parseTopToStringBuilder(manager, gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<Integer, Integer> topSubpoles = getTopPoles(Long.parseLong(chat.getId()), 2, limit);
        parseTopToStringBuilder(manager, silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<Integer, Integer> topBronces = getTopPoles(Long.parseLong(chat.getId()), 3, limit);
        parseTopToStringBuilder(manager, bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        InlineKeyboardButton showLess = new InlineKeyboardButton();
        showLess.setText("Mostrar menos");
        showLess.setCallback_data("mostrarMenosPoles");
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(showLess)));

        return body.toString();
    }

    /**
     * Obtener el top de poles de un día y un chat concreto
     * @param today El día
     * @param chatId El chat
     * @return Map de posición e ID del usuario que hizo una pole, plata o bronce
     * @throws SQLException Si falla la conexión a la base de datos
     */
    public static LinkedHashMap<Integer, Integer> getPolesOfToday(LocalDateTime today, long chatId) throws SQLException {
        PreparedStatement statement = BotServer.getInstance().getMysql().openConnection().prepareStatement(
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
    public static LinkedHashMap<Integer, Integer> getTopPoles(long chatId, int type, int limit) throws SQLException {
        PreparedStatement statement = BotServer.getInstance().getMysql().openConnection().prepareStatement("SELECT count(*) AS `totales`,`userid` FROM `" + PoleModule.TABLA_POLES + "`"
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

    public static void parseTopToStringBuilder(PoleCacheManager manager, String title, StringBuilder body, Map<Integer, Integer> top) throws SQLException {
        if (!top.isEmpty()) {
            body.append("\n").append(title).append("\n");
            for (Map.Entry<Integer, Integer> entry : top.entrySet()) {
                String pole_username = EmojiParser.parseToUnicode(manager.getUsername(entry.getKey()));
                body.append(pole_username).append(" => ").append(entry.getValue()).append("\n");
            }
        }
    }
}
