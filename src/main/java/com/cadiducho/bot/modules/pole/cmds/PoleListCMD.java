package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.pole.util.InlineKeyboardUtil;
import com.cadiducho.bot.modules.pole.util.PoleMessengerUtil;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@CommandInfo(module = PoleModule.class, aliases = {"/poles", "/polelist"})
public class PoleListCMD implements BotCommand {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (!module.isChatSafe(getBot(), chat, from)) return;

        try {
            final String body = PoleMessengerUtil.showPoleRank(chat, 5, true);
            final InlineKeyboardMarkup inlineKeyboard = InlineKeyboardUtil.getMostrarTops();

            getBot().sendMessage(chat.getId(), body, "html", null, null, null, inlineKeyboard);
        } catch (SQLException ex) {
            getBot().sendMessage(chat.getId(), "No se ha podido conectar a la base de datos: ```" + ex.getMessage() + "```", "markdown", null, null, null, null);
            BotServer.logger.warning(ex.getMessage());
        }
    }
}
