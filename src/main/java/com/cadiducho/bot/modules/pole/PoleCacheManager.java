package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class PoleCacheManager {

    private static final BotServer botServer = BotServer.getInstance();
    private final PoleModule module;
    private final HashMap<Long, CachedGroup> cacheMap = new HashMap<>();

    /**
     * Inicializar caché de un grupo al realizar una pole o para agilizarla después
     * synchronized porque dos personajes pueden realizar una pole *simultáneamente* y el caché del grupo se duplicaría
     * @param groupId Id del grupo
     * @param title Titulo del grupo
     * @param lastAdded Fecha en la que el bot ha sido añadido al grupo
     */
    public synchronized void initializeGroupCache(Long groupId, String title, LinkedHashMap<Integer, Integer> poles, LocalDate lastAdded) {
        CachedGroup cachedGroup = CachedGroup.builder().id(groupId).title(title).lastAdded(lastAdded).build();
        if (!poles.isEmpty()) { //solo crear objeto PoleCollection si hay poles de verdad. Rellenar el optional con el objeto vacío repercutirá en /pole fuertemente
            PoleCollection polesHoy = new PoleCollection(poles.get(1), poles.get(2), poles.get(3));
            cachedGroup.getPolesMap().put(LocalDate.now(ZoneId.systemDefault()), polesHoy);
        }

        cacheMap.putIfAbsent(groupId, cachedGroup);
    }

    public synchronized void initializeGroupCache(Long groupId, String title, LocalDate lastAdded) {
        initializeGroupCache(groupId, title, getPolesOfGroupchat(groupId), lastAdded);
    }

    private LinkedHashMap<Integer, Integer> getPolesOfGroupchat(Long groupId) {
        LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
        try {
            Connection connection =  botServer.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `userid`, `poleType` FROM `" + PoleModule.TABLA_POLES + "` WHERE "
                            + "DATE(time)=DATE(CURDATE()) AND "
                            + "`groupchat`=?"
                            + " ORDER BY `poleType`");
            statement.setLong(1, groupId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                poles.put(rs.getInt("poleType"), rs.getInt("userid"));
            }
            botServer.getDatabase().closeConnection(connection);
        } catch (SQLException ex) {
            log.log(Level.WARNING, "No se ha podido cargar las poles en caché del grupo " + groupId, ex);
        }

        return poles;
    }

    /**
     * Comprobar todos los archivos dentro del directorio del caché y cargarlos en memoria si este archivo es válido
     */
    void loadCachedGroups() {
        log.info("Iniciando caché de grupos");
        try {
            Connection connection =  botServer.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT p.groupchat, g.name, g.lastAdded FROM cadibot_poles p " +
                            "JOIN cadibot_grupos g ON (p.groupchat = g.groupid) " +
                            "WHERE DATE(time)=DATE(CURDATE()) " +
                            "GROUP BY groupchat");
            ResultSet rs = statement.executeQuery(); //lista de grupos que han hecho HOY una pole para poner en cache
            while (rs.next()) {
                initializeGroupCache(rs.getLong("groupchat"),
                        rs.getString("name"),
                        rs.getTimestamp("lastAdded").toLocalDateTime().toLocalDate());
            }
            botServer.getDatabase().closeConnection(connection);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "No se han podido cargar los grupos en caché", ex);
        }
    }

    /**
     * Check if certain group id is cached
     * @param groupId The group ID
     * @return true is the group is cached
     */
    public boolean isCached(Long groupId) {
        return cacheMap.containsKey(groupId);
    }

    /**
     * Returns the cached group of certain group id, if it exist
     * @param groupId The group ID
     * @return Optional with the object of the CachedGroup
     */
    public Optional<CachedGroup> getCachedGroup(Long groupId) {
        return Optional.ofNullable(cacheMap.getOrDefault(groupId, null));
    }

    /**
     * Eliminar caché pasado 3 días para no hacer enormes los archivos y a la vez no sobrecargar la ram
     * @param group Grupo a limpiar
     * @param today Día para contar 3 días hacia atrás
     */
    public void clearOldCache(CachedGroup group, LocalDate today) {
        for (LocalDate poleDate : group.getPolesMap().keySet()) {
            if (poleDate.plusDays(3).isBefore(today)) { //si han pasado 3 días se borra ese cache
                group.getPolesMap().remove(poleDate);
            }
        }
    }

    public Integer getUserIdFromUpdatedPoleCollection(PoleCollection poles, int updated) {
        Integer userid;
        switch (updated) {
            case 1:
                userid = poles.getFirst().get();
                break;
            case 2:
                userid = poles.getSecond().get();
                break;
            default:
                userid = poles.getThird().get();
                break;
        }
        return userid;
    }
    /**
     * Insertar una pole en la base de datos. Se recomienda usar asíncronamente
     * @param group Grupo donde se realizó la pole
     * @param poles Conjunto de poles de ese día
     * @param updated La posición que se ha actualizado
     */
    @SuppressWarnings("ConstantConditions")
    public void savePoleToDatabase(CachedGroup group, PoleCollection poles, int updated) {
        try {
            Integer userid = getUserIdFromUpdatedPoleCollection(poles, updated);
            botServer.getDatabase().updateUsername(userid, group.getId());
            botServer.getDatabase().updateGroup(group.getId(), group.getTitle(), false);

            Connection connection =  botServer.getDatabase().getConnection();
            PreparedStatement insert = connection.prepareStatement("INSERT INTO `" + PoleModule.TABLA_POLES + "` (`userid`, `groupchat`, `poleType`) VALUES (?, ?, ?)");

            insert.setInt(1, userid);
            insert.setLong(2, group.getId());
            insert.setInt(3, updated);
            insert.executeUpdate();
            botServer.getDatabase().closeConnection(connection);
        } catch (SQLException ex) {
            log.severe("Error insertando una colección de poles en la base de datos: ");
            log.severe(ex.getMessage());
        }
    }

    /**
     * Obtener la fecha en la que el bot fue añadido por última bot a ese grupo
     * @param groupId La id del grupo
     * @return Fecha en la que el bot fue añadido
     */
    public LocalDate getChatLastAdded(Long groupId) {
        try {
            Connection connection =  botServer.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT lastAdded FROM cadibot_grupos WHERE groupid=?");
            statement.setLong(1, groupId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                LocalDate date = rs.getTimestamp("lastAdded").toLocalDateTime().toLocalDate();
                botServer.getDatabase().closeConnection(connection);
                return date;
            }
        } catch (SQLException ex) {
            log.severe("Error obteniendo la fecha de agreción del bot en el grupo " + groupId);
            log.severe(ex.getMessage());
        }
        return null;
    }

    /**
     * Establecer la fecha en la que el bot fue añadido por última vez a hoy
     * @param chatId La id del grupo en la que fue añadido
     */
    public void setGroupLastAdded(Long chatId) {
        getCachedGroup(chatId).ifPresent(cachedGroup -> cachedGroup.setLastAdded(LocalDate.now()));
    }
}
