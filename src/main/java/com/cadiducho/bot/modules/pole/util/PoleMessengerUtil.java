package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.bot.modules.pole.PoleUser;
import com.cadiducho.telegrambotapi.Chat;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase para reunir los métodos relativos a enviar algo acerca de las poles o su ranking
 */
public class PoleMessengerUtil {

    private static final Emoji chart = EmojiManager.getForAlias("chart_with_upwards_trend");
    private static final Emoji gold = EmojiManager.getForAlias("first_place_medal");
    private static final Emoji silver = EmojiManager.getForAlias("second_place_medal");
    private static final Emoji bronze =  EmojiManager.getForAlias("third_place_medal");

    private static final DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy");

    /**
     * Mostrar el ranking de poles de un chat determinado
     * @param chat El chat
     * @param limit Limite del ranking total que quieras
     * @param atDay El día en el que estás observando el ranking
     * @param showToday Si mostrar las poles de hoy o no
     * @return String con el ranking
     * @throws SQLException Si falla la base de datos
     */
    public static String showPoleRank(Chat chat, int limit, LocalDate atDay, boolean showToday) throws SQLException {
        StringBuilder body = new StringBuilder();
        if (showToday) {
            Map<Integer, PoleUser> poles = getPolesOfDay(atDay, Long.parseLong(chat.getId()));
            if (poles.isEmpty()) {
                if (atDay.isEqual(LocalDate.now())) {
                    body.append("Nadie ha hecho hoy la pole :(");
                } else {
                    body.append("Nadie hizo la pole el ").append(atDay.format(formatter));
                }
            } else {
                body.append("Lista de poles del día <b>").append(atDay.format(formatter)).append("</b>\n");
                for (Map.Entry<Integer, PoleUser> entry : poles.entrySet()) {
                    final String pole_user_name = entry.getValue().getName();

                    final Emoji trophy = EmojiManager.getForAlias("trophy");
                    final Emoji medal = EmojiManager.getForAlias("sports_medal");
                    final Emoji dis = EmojiManager.getForAlias("disappointed_relieved");
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

        Map<PoleUser, Integer> topPoles = getTopPoles(Long.parseLong(chat.getId()), atDay, 1, limit);
        parseTopToStringBuilder(gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<PoleUser, Integer> topSubpoles = getTopPoles(Long.parseLong(chat.getId()), atDay, 2, limit);
        parseTopToStringBuilder(silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<PoleUser, Integer> topBronces = getTopPoles(Long.parseLong(chat.getId()), atDay, 3, limit);
        parseTopToStringBuilder(bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        return body.toString();
    }

    /**
     * Mostrar ranking global individual
     * @param atDay Día para contar sólo las poles hasta ese momento
     * @param limit Limite del ranking
     * @return String con el ranking
     * @throws SQLException Si falla la base de datos
     */
    public static String showGlobalRanking(LocalDate atDay, int limit) throws SQLException {
        StringBuilder body = new StringBuilder();
        body.append("<i>Este ranking cuenta todas las poles \nde todos los grupos</i>");
        body.append("\n\n").append(chart.getUnicode()).append("Ranking global individual");
        if (!atDay.isEqual(LocalDate.now())) {
            body.append(" a día <b>")
                    .append(formatter.format(atDay)).append("</b>");
        }
        body.append(": \n");

        Map<PoleUser, Integer> topPoles = getTopPolesGlobal(atDay, 1, limit);
        parseTopToStringBuilder(gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<PoleUser, Integer> topSubpoles = getTopPolesGlobal(atDay, 2, limit);
        parseTopToStringBuilder(silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<PoleUser, Integer> topBronces = getTopPolesGlobal(atDay, 3, limit);
        parseTopToStringBuilder(bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        return body.toString();
    }

    /**
     * Mostrar ranking global individual
     * @param atDay Día para contar sólo las poles hasta ese momento
     * @param limit Limite del ranking
     * @return String con el ranking
     * @throws SQLException Si falla la base de datos
     */
    public static String showGroupalGlobalRanking(LocalDate atDay, int limit) throws SQLException {
        StringBuilder body = new StringBuilder();
        body.append("<i>Este ranking cuenta los mejores registros\nde poles por grupos</i>");
        body.append("\n\n").append(chart.getUnicode()).append("Ranking global de grupos");
        if (!atDay.isEqual(LocalDate.now())) {
            body.append(" a día <b>")
            .append(formatter.format(atDay)).append("</b>");
        }
        body.append(": \n");

        Map<PoleUser, Integer> topPoles = getTopPolesGrupal(atDay, 1, limit);
        parseTopToStringBuilder(gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

        Map<PoleUser, Integer> topSubpoles = getTopPolesGrupal(atDay, 2, limit);
        parseTopToStringBuilder(silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

        Map<PoleUser, Integer> topBronces = getTopPolesGrupal(atDay,3, limit);
        parseTopToStringBuilder(bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

        return body.toString();
    }

    /**
     * Obtener el top de poles de un día y un chat concreto
     * @param day El día
     * @param chatId El chat
     * @return Map de posición e ID del usuario que hizo una pole, plata o bronce
     * @throws SQLException Si falla la conexión a la base de datos
     */
    public static LinkedHashMap<Integer, PoleUser> getPolesOfDay(LocalDate day, long chatId) throws SQLException {
        final LinkedHashMap<Integer, PoleUser> poles = new LinkedHashMap<>();
        try (Connection connection = CadiBotServer.getInstance().getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM cadibot_poles NATURAL JOIN cadibot_users WHERE "
                            + "DATE(time)=DATE(?) AND "
                            + "groupid=?"
                            + " ORDER BY `poleType`");
            statement.setTimestamp(1, Timestamp.valueOf(day.atStartOfDay()));
            statement.setLong(2, chatId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                PoleUser user = PoleUser.builder()
                        .id(rs.getInt("userid"))
                        .name(rs.getString("name"))
                        .username(rs.getString("username"))
                        .build();
                poles.put(rs.getInt("poleType"), user);
            }
        }
        return poles;
    }

    /**
     * Obtener el top de poles de un grupo, según su tipo
     * El tipo puede ser:
     *  1. Oro/Pole
     *  2. Plata/Subpole
     *  3. Bronce
     * @param chatId El chat
     * @param type El tipo
     * @param atDay Día para contar sólo las poles hasta ese momento
     * @return Map de usuario y cantidad de poles de dicho tipo realizado en dicho grupo
     * @throws SQLException Si falla la base de datos
     */
    public static LinkedHashMap<PoleUser, Integer> getTopPoles(long chatId, LocalDate atDay, int type, int limit) throws SQLException {
        final LinkedHashMap<PoleUser, Integer> poles = new LinkedHashMap<>();
        try (Connection connection = CadiBotServer.getInstance().getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("" +
                    "SELECT count(*) AS `totales`,`userid`,`name`,`username`,`isBanned` FROM cadibot_poles NATURAL JOIN cadibot_users " +
                    "WHERE groupid=? AND `poleType`=? AND DATE(time)<=DATE(?) GROUP BY `userid` ORDER BY `totales` DESC LIMIT ?");
            statement.setLong(1, chatId);
            statement.setInt(2, type);
            statement.setTimestamp(3, Timestamp.valueOf(atDay.atStartOfDay()));
            statement.setInt(4, limit);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                PoleUser user = PoleUser.builder()
                        .id(rs.getInt("userid"))
                        .name(rs.getString("name"))
                        .username(rs.getString("username"))
                        .isBanned(rs.getBoolean("isBanned"))
                        .build();
                poles.put(user, rs.getInt("totales"));
            }
        }
        return poles;
    }

    /**
     * Obtener el top de poles global, según su tipo
     * El tipo puede ser:
     *  1. Oro/Pole
     *  2. Plata/Subpole
     *  3. Bronce
     * @param type El tipo
     * @param atDay Día para contar sólo las poles hasta ese momento
     * @return Map de usuario y cantidad de poles
     * @throws SQLException Si falla la base de datos
     */
    public static LinkedHashMap<PoleUser, Integer> getTopPolesGlobal(LocalDate atDay, int type, int limit) throws SQLException {
        final LinkedHashMap<PoleUser, Integer> poles = new LinkedHashMap<>();
        try (Connection connection = CadiBotServer.getInstance().getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("" +
                    "SELECT userid, COUNT(*) as totales, u.name, u.username " +
                    "FROM cadibot_poles p NATURAL JOIN cadibot_users u " +
                    "WHERE poleType=? AND DATE(p.time)<=DATE(?) " +
                    "GROUP BY userid " +
                    "HAVING COUNT(*) > 1 " +
                    "ORDER BY totales DESC LIMIT ?");
            statement.setInt(1, type);
            statement.setTimestamp(2, Timestamp.valueOf(atDay.atStartOfDay()));
            statement.setInt(3, limit);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                PoleUser user = PoleUser.builder()
                        .id(rs.getInt("userid"))
                        .name(rs.getString("u.name"))
                        .username(rs.getString("username"))
                        .build();
                poles.put(user, rs.getInt("totales"));
            }
        }
        return poles;
    }

    /**
     * Obtener el top de poles global clasificado por grupos, según su tipo
     * El tipo puede ser:
     *  1. Oro/Pole
     *  2. Plata/Subpole
     *  3. Bronce
     * @param type El tipo
     * @param atDay Día para contar sólo las poles hasta ese momento
     * @return Map de usuario y cantidad de poles
     * @throws SQLException Si falla la base de datos
     */
    public static LinkedHashMap<PoleUser, Integer> getTopPolesGrupal(LocalDate atDay, int type, int limit) throws SQLException {
        final LinkedHashMap<PoleUser, Integer> poles = new LinkedHashMap<>();
        try (Connection connection = CadiBotServer.getInstance().getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("" +
                    "SELECT userid, COUNT(*) as totales, u.name, u.username, g.groupid, g.name " +
                    "FROM cadibot_poles p NATURAL JOIN cadibot_users u JOIN cadibot_grupos g ON (p.groupid = g.groupid) " +
                    "WHERE poleType=? AND DATE(p.time)<=DATE(?) " +
                    "GROUP BY userid, groupid " +
                    "HAVING COUNT(*) > 1 " +
                    "ORDER BY totales DESC LIMIT ?");
            statement.setInt(1, type);
            statement.setTimestamp(2, Timestamp.valueOf(atDay.atStartOfDay()));
            statement.setInt(3, limit);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                PoleUser user = PoleUser.builder()
                        .id(rs.getInt("userid"))
                        .name(rs.getString("u.name"))
                        .username(rs.getString("username"))
                        .groupname(rs.getString("g.name"))
                        .build();
                poles.put(user, rs.getInt("totales"));
            }
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
                PoleUser user = entry.getKey();
                Integer polesCount = entry.getValue();
                String pole_user_name = EmojiParser.parseToUnicode(user.getName()); //FixMe no parsear en... 2020? Ya serán todos los nombres unicode
                String banTag = "";
                if (user.isBanned()) {
                    pole_user_name = strikeThrough(pole_user_name);
                    banTag = "  <i>[ban]</i>";
                }
                body.append(pole_user_name).append(" → ").append(polesCount).append(banTag);
                if (user.groupname().isPresent()) {
                    body.append("<i> en ").append(user.groupname().get()).append("</i>");
                }
                body.append("\n");
            }
        }
    }

    /**
     * Tachar un nombre
     */
    public static String strikeThrough(String name) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = name.toCharArray();
        for (char c : charArray) {
            sb.append(c).append('\u0336');
        }
        return sb.toString();
    }
}
