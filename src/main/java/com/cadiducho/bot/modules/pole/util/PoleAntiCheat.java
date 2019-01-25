package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.modules.pole.CachedGroup;
import com.cadiducho.bot.modules.pole.PoleCollection;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.exception.TelegramException;
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

    /**
     * Analizar ultimos comportamientos de un usuario para determinar si son sospechosos o no
     * @param group Grupo donde se está analizando
     * @param poles Colección de poles del día en el que se analiza
     * @param updated La pole que se ha actualizado (es decir, la posición del usuario el día que se analiza)
     */
    public void checkSuspiciousBehaviour(CachedGroup group, PoleCollection poles, int updated) {
        Integer userid = module.getPoleCacheManager().getUserIdFromUpdatedPoleCollection(poles, updated);
        try {
            Connection connection = botServer.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `time` FROM cadibot_poles " +
                            "WHERE userid=? " +
                            "AND groupid=? " +
                            "AND `time` >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                            "GROUP BY `time` ORDER BY `time` DESC;");
            statement.setInt(1, userid);
            statement.setLong(2, group.getId());
            ResultSet rs = statement.executeQuery();
            ArrayList<LocalDateTime> timestamps = new ArrayList<>();
            while (rs.next()) {
                timestamps.add(rs.getTimestamp("time").toLocalDateTime());
            }
            PreparedStatement statement2 = connection.prepareStatement("SELECT username, name FROM cadibot_users WHERE userid=?");
            statement2.setInt(1, userid);
            ResultSet rs2 = statement2.executeQuery();

            String username = "", name = "";
            if (rs2.next()) {
                username = rs2.getString("username");
                name = rs2.getString("name");
            }
            botServer.getDatabase().closeConnection(connection);

            final String fName = name;
            final String fUsername = username;
            if (checkSuspiciousBehaviour(timestamps)) {
                try {
                    log.info("Comportamiento sospechoso de " + fName + "@" + fUsername + "#" + userid + " en " + group.getTitle() + "#" + group.getId());
                    TelegramBot bot = botServer.getCadibot();
                    Long ownerId = botServer.getOwnerId();
                    bot.sendMessage(ownerId, "Posible uso de mensajes automáticos por " + fName + "@" + fUsername + "#" + userid + " en " + group.getTitle() + "#" + group.getId());
                    StringBuilder sb = new StringBuilder();
                    timestamps.forEach(t -> sb.append(t.format(DateTimeFormatter.ofPattern("d/M → HH:mm:ss.SSS"))).append('\n'));
                    bot.sendMessage(ownerId, sb.toString());
                } catch (TelegramException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            log.severe("Error analizando comportamiento sospechoso: ");
            log.severe(ex.getMessage());
        }
    }

    /**
     * Analizar una lista de fechas para determinar si son sospechosos o no
     * @param timestamps Lista de timestamps a analizar
     * @return Verdadero si el comportamiento es sopechoso
     */
    public boolean checkSuspiciousBehaviour(List<LocalDateTime> timestamps) {
        // Si ha hecho pole los 5 días seguidos
        if (timestamps.size() == 5) {
            float avgMinutes = 0;
            float avgSeconds = 0;
            for (LocalDateTime ldt : timestamps) {
                if (ldt.getMinute() != 0) return false; // Si no es el minuto 0, salir

                avgMinutes += ldt.getMinute();
                avgSeconds += ldt.getSecond();
            }
            avgMinutes /= 5;
            avgSeconds /= 5;

            // Si ha hecho la pole 5 días seguidos en el mismo minuto...
            if (avgMinutes == 0 && avgSeconds <= 2) {
                //Comportamiento sospechoso
                return true;
            }
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
        long[] lastMessages = {0L, 0L, 0L};
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
