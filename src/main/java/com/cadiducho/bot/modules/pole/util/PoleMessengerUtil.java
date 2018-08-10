package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase para reunir los métodos relativos a enviar algo acerca de las poles o su ranking
 */
public class PoleMessengerUtil {

    /**
     * Mostrar el ranking de poles de un chat determinado
     * @param chat El chat
     * @param manager Manager del caché
     * @param limit Limite del ranking total que quieras
     * @param showToday Si mostrar las poles de hoy o no
     * @return String con el ranking
     * @throws SQLException Si falla la base de datos
     */
    public static String showPoleRank(Chat chat, PoleCacheManager manager, int limit, boolean showToday) throws SQLException {
        StringBuilder body = new StringBuilder();
        if (showToday) {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            Map<Integer, Integer> poles =  getPolesOfToday(today.atStartOfDay(), Long.parseLong(chat.getId()));
            if (poles.isEmpty()) {
                body.append("Nadie ha hecho hoy la pole :(");
            } else {
                body.append("Lista de poles del día <b>").append(today.getDayOfMonth()).append("/").append(today.getMonthValue()).append("/").append(today.getYear()).append("</b>\n");
                for (Map.Entry<Integer, Integer> entry : poles.entrySet()) {
                    String pole_username = manager.getUsername(entry.getValue());

                    Emoji trophy = EmojiManager.getForAlias("trophy");
                    Emoji medal = EmojiManager.getForAlias("sports_medal");
                    Emoji dis = EmojiManager.getForAlias("disappointed_relieved");
                    String puesto;
                    switch (entry.getKey()) {
                        case 1:
                            puesto = trophy.getUnicode() + "<b>Pole</b>";
                            break;
                        case 2:
                            puesto = medal.getUnicode() + "<b>Subpole</b>";
                            break;
                        default:
                            puesto = dis.getUnicode() + "<b>Bronce</b>";
                            break;
                    }
                    body.append(puesto).append(": ").append(EmojiParser.parseToUnicode(pole_username)).append("\n");
                }
            }
        }
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

    /**
     * Montar sobre un StringBuilder un Map con el top de Poles
     * @param manager Manager del cache
     * @param title Si son Poles, Subpoles o Bronces
     * @param body El StringBuilder
     * @param top El map
     * @throws SQLException Si falla la base de datos
     */
    public static void parseTopToStringBuilder(PoleCacheManager manager, String title, StringBuilder body, Map<Integer, Integer> top) throws SQLException {
        if (!top.isEmpty()) {
            body.append("\n").append(title).append("\n");
            for (Map.Entry<Integer, Integer> entry : top.entrySet()) {
                String pole_username = EmojiParser.parseToUnicode(manager.getUsername(entry.getKey()));
                body.append(pole_username).append(" → ").append(entry.getValue()).append("\n");
            }
        }
    }
}
