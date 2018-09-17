package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.cadiducho.bot.modules.pole.cmds.PoleCMD;
import com.cadiducho.bot.modules.pole.cmds.PoleListCMD;
import com.cadiducho.bot.modules.pole.cmds.UpdateUsernameCMD;
import com.cadiducho.bot.modules.pole.util.PoleMessengerUtil;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@ModuleInfo(name = "Poles", description = "Módulo para hacer poles cada día")
public class PoleModule implements Module {

    public static final String TABLA_POLES = "cadibot_poles";

    @Getter private PoleCacheManager poleCacheManager;

    @Override
    public void onLoad() {
        poleCacheManager = new PoleCacheManager(this);
        poleCacheManager.loadCachedGroups();

        CommandManager commandManager = BotServer.getInstance().getCommandManager();
        commandManager.register(new PoleCMD());
        commandManager.register(new PoleListCMD());
        commandManager.register(new UpdateUsernameCMD());
    }

    @Override
    public void onCallbackQuery(CallbackQuery callbackQuery) {
        try {
            if (callbackQuery.getData().equals("mostrarMasPoles")) {
                Chat chat = callbackQuery.getMessage().getChat();
                String body;
                try {
                    body = PoleMessengerUtil.showPoleRank(chat, poleCacheManager, 100, false);
                } catch (SQLException ex) {
                    body = "No se ha podido obtener el top de poles: " + ex.getMessage();
                }
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                InlineKeyboardButton showLess = new InlineKeyboardButton();
                showLess.setText("Mostrar menos");
                showLess.setCallback_data("mostrarMenosPoles");
                inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(showLess)));
                botServer.getCadibot().editMessageText(chat.getId(), callbackQuery.getMessage().getMessage_id(), callbackQuery.getInline_message_id(),
                        body, "html", null, inlineKeyboard);
            } else if (callbackQuery.getData().equals("mostrarMenosPoles")) {
                Chat chat = callbackQuery.getMessage().getChat();
                String body;
                try {
                    body = PoleMessengerUtil.showPoleRank(chat, poleCacheManager, 5, true);
                } catch (SQLException ex) {
                    body = "No se ha podido obtener el top de poles: " + ex.getMessage();
                }
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                InlineKeyboardButton showMore = new InlineKeyboardButton();
                showMore.setText("Mostrar más");
                showMore.setCallback_data("mostrarMasPoles");
                inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(showMore)));
                botServer.getCadibot().editMessageText(chat.getId(), callbackQuery.getMessage().getMessage_id(), callbackQuery.getInline_message_id(),
                        body, "html", null, inlineKeyboard);
            }
        } catch (TelegramException ex) {
            BotServer.logger.warning(ex.getMessage());
        }
    }

    /**
     * Comprobar si un chat está autorizado a hacer poles
     * @param bot Instancia del bot para mandar los mensajes de error
     * @param chat Chat para realizar comprobaciones
     * @param user Usuario para realizar comprobaciones
     * @return True si se pueden realizar poles
     */
    public boolean isChatSafe(TelegramBot bot, Chat chat, User user) throws TelegramException {
        if (chat.isUser()) {
            bot.sendMessage(chat.getId(), "No se vale hacer poles por privado loko");
            return false;
        }
        if (bot.getChatMembersCount(chat.getId()) < 3) {
            bot.sendMessage(chat.getId(), "Este grupo no tiene el mínimo de usuarios para hacer poles " + EmojiManager.getForAlias("sweat_smile").getUnicode());
            return false;
        }
        return true;
    }
}
