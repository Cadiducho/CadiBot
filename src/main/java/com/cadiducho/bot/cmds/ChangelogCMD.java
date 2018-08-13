package com.cadiducho.bot.cmds;

import com.cadiducho.bot.MySQL;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@CommandInfo(aliases = "/changelog")
public class ChangelogCMD implements BotCommand {
    
    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        List<String> cambios = getChangelog(5); //ToDo: /changelog <número>
        if (cambios.isEmpty()) {
            getBot().sendMessage(chat.getId(), "No he podido cargar el changelog " + EmojiManager.getForAlias("scream_cat").getUnicode());
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        cambios.forEach(version -> sb.append(version).append(System.getProperty("line.separator")));
        getBot().sendMessage(chat.getId(), sb.toString());
    }
    
    private List<String> getChangelog(int limit) {
        List<String> versions = new LinkedList<>();
        try {
            PreparedStatement statement = getMySQL().openConnection().prepareStatement("SELECT * FROM `" + MySQL.TABLE_CHANGELOG + "` ORDER BY `major` DESC, `minor` DESC LIMIT ?;");
            statement.setInt(1, limit);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                versions.add(rs.getInt("major") + "." + rs.getInt("minor") + ": " + rs.getString("changes"));
            }
        } catch (SQLException ignored) {
        }
        return versions;
    }
}
