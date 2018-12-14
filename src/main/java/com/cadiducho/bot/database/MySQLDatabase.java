package com.cadiducho.bot.database;

import com.cadiducho.bot.BotServer;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiParser;
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

        Connection connection = manager.getConnection();
        if (connection == null) {
            throw new SQLException("Connection is null");
        }

        manager.closeConnection(connection);
    }

    /**
     * Cerrar todas las conexiones a la base de datos
     */
    public void closeDatabase() {
        manager.shutdownConnPool();
    }

    public Connection getConnection() {
        return manager.getConnection();
    }

    public void closeConnection(Connection connection) {
        manager.closeConnection(connection);
    }

    /*
     * Queries
     */


    public ArrayList<String> getGroupsIds() {
        ArrayList<String> grupos = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT `groupid` FROM `" + TABLE_GRUPOS + "`");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                grupos.add(rs.getString("groupid"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return grupos;
    }

    public void updateUsername(int userid, Long groupchat) throws SQLException {
        Optional<User> user = Optional.empty();
        try {
            user = Optional.ofNullable(server.getCadibot().getChatMember(groupchat, userid).getUser());
        } catch (TelegramException ignored) {
        }
        if (user.isPresent()) {
            String currentname = user.get().getFirstName();
            String safe = EmojiParser.parseToAliases(currentname);

            Connection connection = manager.getConnection();
            PreparedStatement update_user_name = connection.prepareStatement("INSERT INTO `" + TABLE_USERS + "` (`userid`, `name`, `username`, `lang`) VALUES(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE `name`=?, `username`=?, `lang`=?");
            update_user_name.setInt(1, user.get().getId());
            update_user_name.setString(2, safe);
            update_user_name.setString(3, user.get().getUsername());
            update_user_name.setString(4, user.get().getLanguageCode());
            update_user_name.setString(5, safe);
            update_user_name.setString(6, user.get().getUsername());
            update_user_name.setString(7, user.get().getLanguageCode());
            update_user_name.executeUpdate();

            manager.closeConnection(connection);
        }
    }

    public void updateGroup(Object groupId, String groupName, boolean addedNow) {
        try {
            Connection connection = manager.getConnection();
            PreparedStatement registerGroup = connection.prepareStatement("INSERT INTO `" + TABLE_GRUPOS + "` (`groupid`, `name`) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE `name`=? " + (addedNow ? ", `lastAdded`=?" : ""));
            registerGroup.setObject(1, groupId);
            registerGroup.setString(2, groupName);
            registerGroup.setString(3, groupName);
            if (addedNow) registerGroup.setTimestamp(4, Timestamp.from(Instant.now()));
            registerGroup.executeUpdate();

            manager.closeConnection(connection);
        } catch (SQLException ignored) { }
    }

    public void disableGroup(Object groupId) {
        try {
            Connection connection = manager.getConnection();
            PreparedStatement disableGroup = connection.prepareStatement("UPDATE `" + TABLE_GRUPOS + "` SET `valid`='0' WHERE  `groupid`=?");
            disableGroup.setObject(1, groupId);
            disableGroup.executeUpdate();

            manager.closeConnection(connection);
        } catch (SQLException ignored) {
        }
    }
}