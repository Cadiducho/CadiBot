package com.cadiducho.bot;

import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class MySQL {

    @Getter protected Connection connection;

    public static final String TABLE_POLES = "cadibot_poles";
    public static final String TABLE_USERS = "cadibot_users";
    public static final String TABLE_GRUPOS = "cadibot_grupos";
    public static final String TABLE_CHANGELOG = "cadibot_changelog";
    
    private final String user, database, password, port, hostname;
    private final BotServer server;

    MySQL(BotServer server, String hostname, String port, String database, String username, String password) {
        this.server = server;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    public Connection openConnection() throws SQLException {
        try {
            if (!checkConnection()) {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://"
                        + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Madrid", this.user, this.password);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println("Error opening connection: " + ex.getMessage());
        }
        return connection;
    }
    
    /* Queries */
    
    public ArrayList<String> getGroupsIds() {
        ArrayList<String> grupos = new ArrayList<>();
        try {
            PreparedStatement statement = openConnection().prepareStatement("SELECT `groupid` FROM `" + TABLE_GRUPOS + "`");
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                grupos.add(rs.getString("groupid"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return grupos;
    }
    
    public void updateUsername(int userid, String groupchat) throws SQLException {
        Optional<User> user = Optional.empty();
        try {
            user = Optional.ofNullable(server.getCadibot().getChatMember(groupchat, userid).getUser());
        } catch (TelegramException ignored) {
        }
        if (user.isPresent()) {
            String currentname = user.get().getFirst_name();
            String safe = EmojiParser.parseToAliases(currentname);
            PreparedStatement update_user_name = openConnection().prepareStatement("INSERT INTO `" + TABLE_USERS + "` (`userid`, `name`, `username`, `lang`) VALUES(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE `name`=?, `username`=?, `lang`=?");
            update_user_name.setInt(1, user.get().getId());
            update_user_name.setString(2, safe);
            update_user_name.setString(3, user.get().getUsername());
            update_user_name.setString(4, user.get().getLanguage_code());
            update_user_name.setString(5, safe);
            update_user_name.setString(6, user.get().getUsername());
            update_user_name.setString(7, user.get().getLanguage_code());
            update_user_name.executeUpdate();
        }
    }    
    
    public void updateGroup(Object groupId, String groupName) {
        try {
            PreparedStatement registerGroup = openConnection().prepareStatement("INSERT INTO `" + TABLE_GRUPOS + "` (`groupid`, `name`) VALUES (?, ?) "
                    + "ON DUPLICATE KEY UPDATE `name`=?");
            registerGroup.setObject(1, groupId);
            registerGroup.setString(2, groupName);
            registerGroup.setString(3, groupName);
            registerGroup.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
    
    public void disableGroup(Object groupId) {
        try {
            PreparedStatement disableGroup = openConnection().prepareStatement("UPDATE `" + TABLE_GRUPOS + "` SET `valid`='1' WHERE  `groupid`=?");
            disableGroup.setObject(1, groupId);
            disableGroup.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
