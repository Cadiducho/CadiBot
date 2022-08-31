package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.cadiducho.zincite.api.command.args.Argument;
import com.cadiducho.zincite.api.command.args.CommandParseException;
import com.vdurmont.emoji.EmojiManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@CommandInfo(aliases = "/changelog", description = "Ver los cambios del servidor", arguments = @Argument(name = "cantidad", type = Integer.class, description = "Número de versiones a visualizar"))
public class ChangelogCMD implements BotCommand {
    
    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        int limit = 5;
        try {
            Optional<Integer> cantidad = context.get("cantidad");
            if (cantidad.isPresent()) {
                int newlimit = cantidad.get();
                if (newlimit < 50) { //evitar consultar cientos de versiones
                    limit = newlimit;
                }
            }
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null, false, null, messageId, null);
            return;
        }
        List<String> cambios = getChangelog(limit);
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
        try (Connection connection = CadiBotServer.getInstance().getDatabase().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `cadibot_changelog` ORDER BY `major` DESC, `minor` DESC LIMIT ?;");
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
