package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CallbackListener;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.api.command.ListenTo;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.pole.util.InlineKeyboardUtil;
import com.cadiducho.bot.modules.pole.util.PoleMessengerUtil;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import lombok.extern.java.Log;

import java.sql.SQLException;
import java.time.Instant;
import java.util.function.Supplier;

@Log
@CallbackListener
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
            log.warning("Error generando respuesta para /polelist");
            log.warning(ex.getMessage());
        }
    }

    @ListenTo("mostrarTopGrupo")
    public void mostrarTopGrupo(CallbackQuery callbackQuery) throws TelegramException {
        log.info("mostrando top grupo");
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),100, false);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarResumen());
    }

    @ListenTo("mostrarResumenGrupo")
    public void mostrarResumenGrupo(CallbackQuery callbackQuery) throws TelegramException {
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),5, true);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarTops());
    }

    @ListenTo("mostrarRankingGlobal")
    public void mostrarRankingGlobal(CallbackQuery callbackQuery) throws TelegramException {
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGlobalRanking(5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global individual de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGrupal());
    }

    @ListenTo("mostrarRankingPorGrupos")
    public void mostrarRankingPorGrupos(CallbackQuery callbackQuery) throws TelegramException {
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGroupalGlobalRanking(5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global por grupos de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGlobal());
    }

    private void editPoleListMessage(CallbackQuery callbackQuery, Supplier<String> bodySupplier, InlineKeyboardMarkup inlineKeyboard) throws TelegramException {
        log.info("Editando");
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessage_id(), callbackQuery.getInline_message_id(),
                bodySupplier.get(), "html", false, inlineKeyboard);
    }
}
