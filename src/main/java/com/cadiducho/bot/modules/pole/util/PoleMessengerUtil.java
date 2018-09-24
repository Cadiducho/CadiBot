package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.modules.pole.PoleUser;
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

    private static Emoji chart = EmojiManager.getForAlias("chart_with_upwards_trend");
    private static Emoji gold = EmojiManager.getForAlias("first_place_medal");
    private static Emoji silver = EmojiManager.getForAlias("second_place_medal");
    private static Emoji bronze =  EmojiManager.getForAlias("third_place_medal");

    /**
     * Mostrar el ranking de poles de un chat determinado
     * @param chat El chat
     * @param limit Limite del ranking total que quieras
     * @param showToday Si mostrar las poles de hoy o no
     * @return String con el ranking
     * @throws SQLException Si falla la base de datos
     */
    public static String showPoleRank(Chat chat, int limit, boolean showToday) throws SQLException {
        StringBuilder body = new StringBuilder();
        if (showToday) {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            Map<Integer, PoleUser> poles = getPolesOfToday(today.atStartOfDay(), Long.parseLong(chat.getId()));
            if (poles.isEmpty()) {
                body.append("Nadie ha hecho hoy la pole :(");
            } else {
                body.append("Lista de poles del día <b>").append(today.getDayOfMonth()).append("/").append(today.getMonthValue()).append("/").append(today.getYear()).append("</b>\n");
                for (Map.Entry<Integer, PoleUser> entry : poles.entrySet()) {
                    String pole_user_name = entry.getValue().getName();

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
                    body.append(puesto).append(": ").append(EmojiParser.parseToUnicode(pole_user_name)).append("\n");
                }
            }
        }

        body.append("\n\n").append(chart.getUnicode()).append("Ranking: \n");

        Map<PoleUser, Integer> topPoles = getTopPoles(Long.parseLong(chat.getId()), 1, limit);
        parseTopToStringBuilder(gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<PoleUser, Integer> topSubpoles = getTopPoles(Long.parseLong(chat.getId()), 2, limit);
        parseTopToStringBuilder(silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<PoleUser, Integer> topBronces = getTopPoles(Long.parseLong(chat.getId()), 3, limit);
        parseTopToStringBuilder(bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        return body.toString();
    }

    public static String showGlobalRank(int limit) throws SQLException {
        StringBuilder body = new StringBuilder();
        body.append("<i>Este ranking cuenta todas las poles \nde todos los grupos</i>");
        body.append("\n\n").append(chart.getUnicode()).append("Ranking global individual: \n");

        Map<PoleUser, Integer> topPoles = getTopPolesGlobal(1, limit);
        parseTopToStringBuilder(gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<PoleUser, Integer> topSubpoles = getTopPolesGlobal(2, limit);
        parseTopToStringBuilder(silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<PoleUser, Integer> topBronces = getTopPolesGlobal(3, limit);
        parseTopToStringBuilder(bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        return body.toString();
    }

    /**
     * Obtener el top de poles de un día y un chat concreto
     * @param today El día
     * @param chatId El chat
     * @return Map de posición e ID del usuario que hizo una pole, plata o bronce
     * @throws SQLException Si falla la conexión a la base de datos
     */
    public static LinkedHashMap<Integer, PoleUser> getPolesOfToday(LocalDateTime today, long chatId) throws SQLException {
        PreparedStatement statement = BotServer.getInstance().getMysql().openConnection().prepareStatement(
                "SELECT * FROM cadibot_poles NATURAL JOIN cadibot_users WHERE "
                        + "DATE(time)=DATE(?) AND "
                        + "`groupchat`=?"
                        + " ORDER BY `poleType`");
        statement.setTimestamp(1, Timestamp.valueOf(today));
        statement.setLong(2, chatId);
        ResultSet rs = statement.executeQuery();

        LinkedHashMap<Integer, PoleUser> poles = new LinkedHashMap<>();
        while (rs.next()) {
            PoleUser user = PoleUser.builder()
                    .id(rs.getInt("userid"))
                    .name(rs.getString("name"))
                    .username(rs.getString("username"))
                    .build();
            poles.put(rs.getRow(), user);
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
     * @throws SQLException Si falla la base de datos
     */
    public static LinkedHashMap<PoleUser, Integer> getTopPoles(long chatId, int type, int limit) throws SQLException {
        PreparedStatement statement = BotServer.getInstance().getMysql().openConnection().prepareStatement("" +
                "SELECT count(*) AS `totales`,`userid`,`name`,`username` FROM cadibot_poles NATURAL JOIN cadibot_users " +
                "WHERE `groupchat`=? AND `poleType`=? GROUP BY `userid` ORDER BY `totales` DESC LIMIT ?");
        statement.setLong(1, chatId);
        statement.setInt(2, type);
        statement.setInt(3, limit);
        ResultSet rs = statement.executeQuery();
        LinkedHashMap<PoleUser, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            PoleUser user = PoleUser.builder()
                    .id(rs.getInt("userid"))
                    .name(rs.getString("name"))
                    .username(rs.getString("username"))
                    .build();
            poles.put(user, rs.getInt("totales"));
        }
        return poles;
    }

    /**
     *
     * @param type
     * @param limit
     * @return
     * @throws SQLException
     */
    public static LinkedHashMap<PoleUser, Integer> getTopPolesGlobal(int type, int limit) throws SQLException {
        PreparedStatement statement = BotServer.getInstance().getMysql().openConnection().prepareStatement("" +
                "SELECT userid, COUNT(*) as totales, u.name, u.username " +
                "FROM cadibot_poles p NATURAL JOIN cadibot_users u " +
                "WHERE poleType=? " +
                "GROUP BY userid " +
                "HAVING COUNT(*) > 1 " +
                "ORDER BY totales DESC LIMIT ?");
        statement.setInt(1, type);
        statement.setInt(2, limit);

        ResultSet rs = statement.executeQuery();
        LinkedHashMap<PoleUser, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            PoleUser user = PoleUser.builder()
                    .id(rs.getInt("userid"))
                    .name(rs.getString("u.name"))
                    .username(rs.getString("username"))
                    .build();
            poles.put(user, rs.getInt("totales"));
        }
        return poles;
    }

    /**
     * Montar sobre un StringBuilder un Map con el top de Poles
     * @param title Si son Poles, Subpoles o Bronces
     * @param body El StringBuilder
     * @param top Un map de Usuarios y sus poles
     */
    public static void parseTopToStringBuilder(String title, StringBuilder body, Map<PoleUser, Integer> top) {
        if (!top.isEmpty()) {
            body.append("\n").append(title).append("\n");
            for (Map.Entry<PoleUser, Integer> entry : top.entrySet()) {
                String pole_user_name = EmojiParser.parseToUnicode(entry.getKey().getName());
                body.append(pole_user_name).append(" → ").append(entry.getValue()).append("\n");
            }
        }
    }
}
