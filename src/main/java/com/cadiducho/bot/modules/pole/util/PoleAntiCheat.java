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
            Connection connection =  botServer.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `time` FROM cadibot_poles " +
                            "WHERE userid=? " +
                            "AND groupid=? " +
                            "AND `time` >= DATE_SUB(NOW(), INTERVAL 7 DAY)" +
                            "GROUP BY `time` ORDER BY `time` DESC;");
            statement.setLong(1, userid);
            statement.setLong(2, group.getId());
            ResultSet rs = statement.executeQuery();
            ArrayList<LocalDateTime> timestamps = new ArrayList<>();
            while (rs.next()) {
                timestamps.add(rs.getTimestamp("time").toLocalDateTime());
            }
            botServer.getDatabase().closeConnection(connection);

            // Si ha hecho pole los 7 días seguidos
            if (timestamps.size() == 7) {
                int avgMinutes = 0;
                int avgSeconds = 0;
                for (LocalDateTime ldt : timestamps) {
                    if (ldt.getMinute() != 0) return; // Si no es el minuto 0, salir
                    avgMinutes += ldt.getMinute();
                    avgSeconds += ldt.getSecond();
                }
                avgMinutes /= 7;
                avgSeconds /= 7;

                // Si ha hecho la pole 7 días seguidos en el mismo minuto...
                if (avgMinutes == 0 && avgSeconds <= 2) {
                    //Comportamiento sospechoso
                    log.info("Comportamiento sospechoso de " + userid + " en " + group.getTitle() + "#" + group.getId());
                    try {
                        TelegramBot bot = botServer.getCadibot();
                        Long ownerId = botServer.getOwnerId();
                        bot.sendMessage(ownerId, "Posible uso de mensajes automáticos por " + userid + " en " + group.getTitle() + "#" + group.getId());
                        StringBuilder sb = new StringBuilder();
                        timestamps.forEach(t -> sb.append(t.format(DateTimeFormatter.ofPattern("d/M → HH:mm:ss.SSS"))).append('\n'));
                        bot.sendMessage(ownerId, sb.toString());
                    } catch (TelegramException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            log.severe("Error analizando comportamiento sospechoso: ");
            log.severe(ex.getMessage());
        }
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

    @EqualsAndHashCode
    @AllArgsConstructor
    private class UserInGroup {
        Integer user;
        Long group;
    }

    private class AntiFloodData {
        long lastMessages[] = {0L, 0L, 0L};
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
