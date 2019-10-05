package com.cadiducho.bot.database;

import com.cadiducho.bot.BotServer;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import lombok.extern.java.Log;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Log
public class MySQLDatabase {

    public static final String TABLE_USERS = "cadibot_users";
    public static final String TABLE_GRUPOS = "cadibot_grupos";

    private final HikariConnectionManager manager;
    private final BotServer server;

    public MySQLDatabase(BotServer server, String hostname, String port, String database, String username, String passphrase) throws SQLException {
        this.server = server;
        this.manager = new HikariConnectionManager(hostname, port, database, username, passphrase);

        manager.setupPool();

        try (Connection connection = getConnection()) {
            if (connection == null) {
                throw new SQLException("Connection is null");
            }
        }
    }

    /**
     * Cerrar todas las conexiones a la base de datos
     */
    public void closeDatabase() {
        manager.shutdownConnPool();
    }

    /**
     * Obtener una conexión segura de Hikari
     * @return Connection
     */
    public Connection getConnection() {
        return manager.getConnection();
    }

    /*
     * Queries
     */


    public ArrayList<String> getGroupsIds() {
        ArrayList<String> grupos = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT `groupid` FROM `" + TABLE_GRUPOS + "`");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                grupos.add(rs.getString("groupid"));
            }
        } catch (SQLException ex) {
            log.warning("Error obteniendo las ids de los grupos");
            ex.printStackTrace();
        }
        return grupos;
    }

    /**
     * Actualizar un username. Devuelve true si se ha actualizado correctamente
     * @param userid La id del usuario
     * @param groupchat La id del chat
     * @return true si se ha actualizado correctamente
     */
    public boolean updateUsername(int userid, Long groupchat) {
        Optional<User> user = Optional.empty();
        boolean actualizado = false;
        try {
            user = Optional.ofNullable(server.getCadibot().getChatMember(groupchat, userid).getUser());
        } catch (TelegramException ignored) {
        }
        if (user.isPresent()) {
            String currentname = user.get().getFirstName();

            try (Connection connection = getConnection()) {
                PreparedStatement update_user_name = connection.prepareStatement("INSERT INTO `" + TABLE_USERS + "` (`userid`, `name`, `username`, `lang`) VALUES(?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE `name`=?, `username`=?, `lang`=?");
                update_user_name.setInt(1, user.get().getId());
                update_user_name.setString(2, currentname);
                update_user_name.setString(3, user.get().getUsername());
                update_user_name.setString(4, user.get().getLanguageCode());
                update_user_name.setString(5, currentname);
                update_user_name.setString(6, user.get().getUsername());
                update_user_name.setString(7, user.get().getLanguageCode());
                update_user_name.executeUpdate();
                actualizado = true;
            } catch (SQLException ex) {
                log.warning("Error actualizando el username de " + userid + " en " + groupchat);
                ex.printStackTrace();
            }
        }
        return actualizado;
    }

    public void updateGroup(Object groupId, String groupName, boolean addedNow) {
        try (Connection connection = getConnection()) {
            PreparedStatement registerGroup = connection.prepareStatement("INSERT INTO `" + TABLE_GRUPOS + "` (`groupid`, `name`) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE `name`=? " + (addedNow ? ", `lastAdded`=?" : ""));
            registerGroup.setObject(1, groupId);
            registerGroup.setString(2, groupName);
            registerGroup.setString(3, groupName);
            if (addedNow) registerGroup.setTimestamp(4, Timestamp.from(Instant.now()));
            registerGroup.executeUpdate();
        } catch (SQLException ex) {
            log.warning("Error actualizando el grupo " + groupId + "#" + groupName + " con addedNow="+addedNow);
            ex.printStackTrace();
        }
    }

    public void disableGroup(Object groupId) {
        try (Connection connection = getConnection()) {
            PreparedStatement disableGroup = connection.prepareStatement("UPDATE `" + TABLE_GRUPOS + "` SET `valid`='0' WHERE  `groupid`=?");
            disableGroup.setObject(1, groupId);
            disableGroup.executeUpdate();
        } catch (SQLException ex) {
            log.warning("Error desactivando el grupo " + groupId);
            ex.printStackTrace();
        }
    }
}