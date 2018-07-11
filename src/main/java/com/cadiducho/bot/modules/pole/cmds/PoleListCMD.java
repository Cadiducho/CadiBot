package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@CommandInfo(module = PoleModule.class, aliases = {"/poles", "/polelist"})
public class PoleListCMD implements BotCommand {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Instant instant) throws TelegramException {
        if (!module.isChatSafe(getBot(), chat, from)) return;

        LocalDateTime today = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        PoleCacheManager manager = module.getPoleCacheManager();
        Long groupId = Long.parseLong(chat.getId());

        try {
            Map<Integer, Integer> poles = getPolesOfToday(today, groupId);

            StringBuilder body = new StringBuilder();
            if (poles.isEmpty()) {
                body.append("Nadie ha hecho hoy la pole :(");
            } else {
                body.append("Lista de poles del día <b>").append(today.getDayOfMonth()).append("/").append(today.getMonthValue()).append("/").append(today.getYear()).append("</b>\n");
                for (Map.Entry<Integer, Integer> entry : poles.entrySet()) {
                    String pole_username = manager.getUsername(entry.getValue());

                    Emoji trophy = EmojiManager.getForAlias("trophy");
                    Emoji medal = EmojiManager.getForAlias("sports_medal");
                    Emoji dis = EmojiManager.getForAlias("disappointed_relieved");
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
                    body.append(puesto).append(": ").append(EmojiParser.parseToUnicode(pole_username)).append("\n");
                }
            }
            body.append("\n\nRanking total: \n");
            Emoji gold = EmojiManager.getForAlias("first_place_medal");
            Emoji silver = EmojiManager.getForAlias("second_place_medal");
            Emoji bronze = EmojiManager.getForAlias("third_place_medal");
            Map<Integer, Integer> topPoles = getTopPoles(Long.parseLong(chat.getId()), 1);
            parseTopToStringBuilder(manager, gold.getUnicode() + " Poles " + gold.getUnicode(), body, topPoles);

            Map<Integer, Integer> topSubpoles = getTopPoles(Long.parseLong(chat.getId()), 2);
            parseTopToStringBuilder(manager, silver.getUnicode() + " Subpoles " + silver.getUnicode(), body, topSubpoles);

            Map<Integer, Integer> topBronces = getTopPoles(Long.parseLong(chat.getId()), 3);
            parseTopToStringBuilder(manager, bronze.getUnicode() + " Bronces " + bronze.getUnicode(), body, topBronces);

            getBot().sendMessage(chat.getId(), body.toString(), "html", null, null, null, null);
        } catch (SQLException ex) {
            getBot().sendMessage(chat.getId(), "No se ha podido conectar a la base de datos: ```" + ex.getMessage() + "```", "markdown", null, null, null, null);
            BotServer.logger.warning(ex.getMessage());
        }

    }

    private void parseTopToStringBuilder(PoleCacheManager manager, String title, StringBuilder body, Map<Integer, Integer> top) throws SQLException {
        if (!top.isEmpty()) {
            body.append("\n").append(title).append("\n");
            for (Map.Entry<Integer, Integer> entry : top.entrySet()) {
                String pole_username = EmojiParser.parseToUnicode(manager.getUsername(entry.getKey()));
                body.append(pole_username).append(" => ").append(entry.getValue()).append("\n");
            }
        }
    }

    private LinkedHashMap<Integer, Integer> getPolesOfToday(LocalDateTime today, long chatId) throws SQLException {
        PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement(
                "SELECT * FROM `" + PoleModule.TABLA_POLES + "` WHERE "
                        + "DATE(time)=DATE(?) AND "
                        + "`groupchat`=?"
                        + " ORDER BY `poleType`");
        statement.setTimestamp(1, Timestamp.valueOf(today));
        statement.setLong(2, chatId);
        ResultSet rs = statement.executeQuery();

        LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            poles.put(rs.getRow(), rs.getInt("userid"));
        }

        return poles;
    }

    private LinkedHashMap<Integer, Integer> getTopPoles(long chatId, int type) throws SQLException {
        PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement("SELECT count(*) AS `totales`,`userid` FROM `" + PoleModule.TABLA_POLES + "`"
                + " WHERE `groupchat`=? AND `poleType`=? GROUP BY `userid` ORDER BY `totales` DESC");
        statement.setLong(1, chatId);
        statement.setInt(2, type);
        ResultSet rs = statement.executeQuery();
        LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
        while (rs.next()) {
            poles.put(rs.getInt("userid"), rs.getInt("totales"));
        }

        return poles;
    }
}
