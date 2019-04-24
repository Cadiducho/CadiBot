package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.api.command.*;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandParseException;
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
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

@Log
@CommandInfo(module = PoleModule.class,
        aliases = {"/poles", "/polelist"},
        arguments = @Argument(name = "dia", type = LocalDate.class, required = false, description = "Día en el que observar las poles")
)
public class PoleListCMD implements BotCommand, CallbackListener {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (module.isChatUnsafe(getBot(), chat)) return;

        LocalDate atDate = LocalDate.now();
        try {
            Optional<LocalDate> diaDeseado = context.get("dia");
            if (diaDeseado.isPresent()) {
                atDate = diaDeseado.get();
            }
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "No he entendido esa fecha. Aquí tienes las poles a día de hoy", null, null, false, messageId, null);
        }
        try {
            final String body = PoleMessengerUtil.showPoleRank(chat,5, atDate, true);
            final InlineKeyboardMarkup inlineKeyboard = InlineKeyboardUtil.getMostrarTops(atDate);

            getBot().sendMessage(chat.getId(), body, "html", null, null, null, inlineKeyboard);
        } catch (SQLException ex) {
            getBot().sendMessage(chat.getId(), "No se ha podido conectar a la base de datos: ```" + ex.getMessage() + "```", "markdown", null, null, null, null);
            log.warning("Error generando respuesta para /polelist");
            log.warning(ex.getMessage());
        }
    }

    @ListenTo("mostrarTopGrupo")
    public void mostrarTopGrupo(CallbackQuery callbackQuery) throws TelegramException {
        LocalDate date = LocalDate.parse(callbackQuery.getData().split("#")[1]);
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),100, date, false);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarResumen(date));
    }

    @ListenTo("mostrarResumenGrupo")
    public void mostrarResumenGrupo(CallbackQuery callbackQuery) throws TelegramException {
        LocalDate date = LocalDate.parse(callbackQuery.getData().split("#")[1]);
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),5, date, true);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarTops(date));
    }

    @ListenTo("mostrarRankingGlobal")
    public void mostrarRankingGlobal(CallbackQuery callbackQuery) throws TelegramException {
        LocalDate date = LocalDate.parse(callbackQuery.getData().split("#")[1]);
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGlobalRanking(date, 5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global individual de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGrupal(date));
    }

    @ListenTo("mostrarRankingPorGrupos")
    public void mostrarRankingPorGrupos(CallbackQuery callbackQuery) throws TelegramException {
        LocalDate date = LocalDate.parse(callbackQuery.getData().split("#")[1]);
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGroupalGlobalRanking(date, 5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global por grupos de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGlobal(date));
    }

    private void editPoleListMessage(CallbackQuery callbackQuery, Supplier<String> bodySupplier, InlineKeyboardMarkup inlineKeyboard) throws TelegramException {
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                bodySupplier.get(), "html", false, inlineKeyboard);
    }
}
