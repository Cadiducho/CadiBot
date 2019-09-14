package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Conjunto de funciones y estructuras de datos para asegurar el "fair-play" de las poles
 */
@Log
@RequiredArgsConstructor
public class PoleAntiCheat {

    private final PoleModule module;
    private static final BotServer botServer = BotServer.getInstance();
    private final HashMap<UserInGroup, AntiFloodData> antiFloodDataMap = new HashMap<>();

    private final List<Integer> bannedUsers = new ArrayList<>();

    /**
     * Analizar ultimos comportamientos de un usuario para determinar si son sospechosos o no
     * @param groupId Id del grupo donde se está analizando
     * @param userId Id del usuario a analizar
     * @param days Número de días a analizar
     * @return Verdadero si es sospechoso
     */
    public boolean checkSuspiciousBehaviour(Long groupId, Integer userId, int days) {
        try (Connection connection = botServer.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `time` FROM cadibot_poles " +
                            "WHERE userid=? " +
                            "AND groupid=? " +
                            "AND `time` >= DATE_SUB(NOW(), INTERVAL " + days + " DAY) " +
                            "GROUP BY `time` ORDER BY `time` DESC;");
            statement.setInt(1, userId);
            statement.setLong(2, groupId);
            ResultSet rs = statement.executeQuery();
            ArrayList<LocalDateTime> timestamps = new ArrayList<>();
            while (rs.next()) {
                timestamps.add(rs.getTimestamp("time").toLocalDateTime());
            }

            if (checkSuspiciousBehaviour(timestamps, days)) {
                try {
                    @SuppressWarnings("OptionalGetWithoutIsPresent") String groupName = module.getGroupName(groupId).get();
                    String[] names = module.getUsername(userId);
                    String name = names[0];
                    String username = names[1];

                    log.info("Comportamiento sospechoso de " + name + "@" + username + "#" + userId + " en " + groupName + "#" + groupId);
                    TelegramBot bot = botServer.getCadibot();
                    Long ownerId = botServer.getOwnerId();
                    StringBuilder sb = new StringBuilder();

                    sb.append("Posible uso de mensajes automáticos por ")
                            .append(name).append("@").append(username).append("#").append(userId)
                            .append(" en ")
                            .append(groupName).append("#").append(groupId)
                            .append("\n\n");
                    timestamps.forEach(t -> sb.append(t.format(DateTimeFormatter.ofPattern("d/M → HH:mm:ss.SSS"))).append('\n'));

                    final InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                    final InlineKeyboardButton banear = new InlineKeyboardButton();
                    banear.setText("Banear usuario");
                    banear.setCallbackData("askBanUser#" + userId + "#" + groupId);
                    inlineKeyboard.setInlineKeyboard(Collections.singletonList(Collections.singletonList(banear)));

                    bot.sendMessage(ownerId, sb.toString(), "html", null, null, null, inlineKeyboard);
                    return true;
                } catch (TelegramException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            log.severe("Error analizando comportamiento sospechoso: ");
            log.severe(ex.getMessage());
        }
        return false;
    }

    /**
     * Analizar una lista de fechas para determinar si son sospechosos o no
     * @param timestamps Lista de timestamps a analizar
     * @param days Número de días a analizar
     * @return Verdadero si el comportamiento es sopechoso
     */
    public boolean checkSuspiciousBehaviour(List<LocalDateTime> timestamps, int days) {
        // Si ha hecho pole los <days> días seguidos
        if (timestamps.size() >= days) {
            float avgMinutes = 0;
            float avgSeconds = 0;
            for (LocalDateTime ldt : timestamps) {
                if (ldt.getMinute() != 0) return false; // Si no es el minuto 0, salir

                avgMinutes += ldt.getMinute();
                avgSeconds += ldt.getSecond();
            }
            avgMinutes /= timestamps.size();
            avgSeconds /= timestamps.size();

            // Si ha hecho la pole <days> días seguidos en el mismo minuto...
            return avgMinutes == 0 && avgSeconds <= 2;
        }
        return false;
    }

    /**
     * Sistema antiflood de las poles. Intenta frenar los intentos de hacer pole mediante ponerlo 3432 veces seguidas
     * @param userid El usuario que se está analizando
     * @param groupId El grupo donde el usuario está siendo analizado
     * @return true si está realizando flood, falso si ha pasado el filtro
     */
    public boolean isFlooding(Integer userid, Long groupId) {
        UserInGroup key = new UserInGroup(userid, groupId);
        if (!antiFloodDataMap.containsKey(key)) {
            antiFloodDataMap.put(key, new AntiFloodData());
        }
        AntiFloodData data = antiFloodDataMap.get(key);
        return data.isFlooding();
    }

    public void loadBannedUsers() {
        bannedUsers.clear();
        try (Connection connection = botServer.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT userid FROM cadibot_users WHERE isBanned=1");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                bannedUsers.add(rs.getInt("userid"));
            }
            log.info("Cargados " + bannedUsers.size() + " usuarios baneados");
        } catch (SQLException ex) {
            log.severe("Error cargando la lista de usuarios baneados: ");
            log.severe(ex.getMessage());
        }
    }

    /**
     * Banear un usuario
     * @param userid ID del usuario. Debe ser válida y corresponder a un usuario existente
     */
    public void banUser(Integer userid) {
        try (Connection connection = botServer.getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE cadibot_users SET isBanned=1, banTime=NOW() WHERE userid=?;");
            statement.setInt(1, userid);
            statement.executeUpdate();

        } catch (SQLException ex) {
            log.severe("Error cargando la lista de usuarios baneados: ");
            log.severe(ex.getMessage());
        }
    }

    public boolean isUserBanned(Integer userid) {
        return bannedUsers.contains(userid);
    }

    /**
     * Pequeño 'struct' para agrupar el par userid->groupid y poder compararlos con EqualsAndHashCode
     */
    @EqualsAndHashCode
    @AllArgsConstructor
    private class UserInGroup {
        Integer user;
        Long group;
    }

    /**
     * Pequeña subclase para controlar los datos del antiflood
     */
    private class AntiFloodData {
        final long[] lastMessages = {0L, 0L, 0L};
        long lastSpam = 0L;

        public boolean isFlooding() {
            long now = System.currentTimeMillis();
            if (now - lastSpam < 30000) {
                // Tiene que esperar aún medio minuto
                return true;
            }
            long delta = now - lastMessages[2];
            lastMessages[2] = lastMessages[1];
            lastMessages[1] = lastMessages[0];
            lastMessages[0] = now;
            // 4 mensajes en menos de 3 segundos -> Spam
            boolean isFlooding = delta < 3000;
            if (isFlooding) {
                lastSpam = now;
            }
            return isFlooding;
        }
    }
}
