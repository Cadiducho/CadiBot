package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.pole.util.InlineKeyboardUtil;
import com.cadiducho.bot.modules.pole.util.PoleMessengerUtil;
import com.cadiducho.telegrambotapi.*;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.*;
import com.cadiducho.zincite.api.command.args.Argument;
import com.cadiducho.zincite.api.command.args.CommandParseException;
import lombok.extern.java.Log;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Log
@CommandInfo(module = PoleModule.class,
        aliases = {"/poles", "/polelist"},
        arguments = {
                @Argument(name = "dia", type = LocalDate.class, required = false, description = "Día concreto o inicio del intervalo el que observar las poles"),
                @Argument(name = "finIntervalo", type = LocalDate.class, required = false, description = "Día de fin del intervalo en el que observar las poles")
        },
        description = "Ver los resultados de las poles de este grupo"
)
public class PoleListCMD implements BotCommand, CallbackListener {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (module.isChatUnsafe(getBot(), chat)) return;

        LocalDate atDate = LocalDate.now();
        LocalDate endInterval = null;
        try {
            Optional<LocalDate> diaDeseado = context.get("dia");
            Optional<LocalDate> finIntervalo = context.get("finIntervalo");

            if (diaDeseado.isPresent()) {
                atDate = diaDeseado.get();
                if (finIntervalo.isPresent()) {
                    endInterval = finIntervalo.get();
                }
            }
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "No he entendido esa fecha. Usa dd/MM/yy. Aquí tienes las poles a día de hoy", null, null,false, null, messageId, null);
        }

        try {
            final String body = PoleMessengerUtil.showPoleRank(chat,5, atDate, endInterval, true);
            final InlineKeyboardMarkup inlineKeyboard = InlineKeyboardUtil.getMostrarTops(atDate, endInterval);

            getBot().sendMessage(chat.getId(), body,  ParseMode.HTML, null, null, null, null, inlineKeyboard);
        } catch (SQLException ex) {
            getBot().sendMessage(chat.getId(), "No se ha podido conectar a la base de datos: ```" + ex.getMessage() + "```",  ParseMode.MARKDOWN, null, null, null, null, null);
            log.warning("Error generando respuesta para /polelist");
            log.warning(ex.getMessage());
        }
    }

    @ListenTo("mostrarTopGrupo")
    public void mostrarTopGrupo(CallbackQuery callbackQuery) throws TelegramException {
        final String[] queries = callbackQuery.getData().split("#");
        final LocalDate date = LocalDate.parse(queries[1]);
        final String endDateQuery = queries[2];
        final LocalDate endDate = (!Objects.equals(endDateQuery, "null")) ? LocalDate.parse(endDateQuery) : null;
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),100, date, null, false);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarResumen(date, endDate));
    }

    @ListenTo("mostrarResumenGrupo")
    public void mostrarResumenGrupo(CallbackQuery callbackQuery) throws TelegramException {
        final String[] queries = callbackQuery.getData().split("#");
        final LocalDate date = LocalDate.parse(queries[1]);
        final String endDateQuery = queries[2];
        final LocalDate endDate = (!Objects.equals(endDateQuery, "null")) ? LocalDate.parse(endDateQuery) : null;
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showPoleRank(callbackQuery.getMessage().getChat(),5, date, endDate, true);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el top de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getMostrarTops(date, endDate));
    }

    @ListenTo("mostrarRankingGlobal")
    public void mostrarRankingGlobal(CallbackQuery callbackQuery) throws TelegramException {
        final String[] queries = callbackQuery.getData().split("#");
        final LocalDate date = LocalDate.parse(queries[1]);
        final String endDateQuery = queries[2];
        final LocalDate endDate = (!Objects.equals(endDateQuery, "null")) ? LocalDate.parse(endDateQuery) : null;
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGlobalRanking(date, endDate, 5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global individual de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGrupal(date, endDate));
    }

    @ListenTo("mostrarRankingPorGrupos")
    public void mostrarRankingPorGrupos(CallbackQuery callbackQuery) throws TelegramException {
        final String[] queries = callbackQuery.getData().split("#");
        final LocalDate date = LocalDate.parse(queries[1]);
        final String endDateQuery = queries[2];
        final LocalDate endDate = (!Objects.equals(endDateQuery, "null")) ? LocalDate.parse(endDateQuery) : null;
        editPoleListMessage(callbackQuery,
                () -> {
                    try {
                        return PoleMessengerUtil.showGrupalGlobalRanking(date, endDate, 5);
                    } catch (SQLException ex) {
                        return "No se ha podido obtener el ranking global por grupos de poles: " + ex.getMessage();
                    }
                },
                InlineKeyboardUtil.getResumenesYTopGlobal(date, endDate));
    }

    private void editPoleListMessage(CallbackQuery callbackQuery, Supplier<String> bodySupplier, InlineKeyboardMarkup inlineKeyboard) throws TelegramException {
        CadiBotServer.getInstance().getCadibot().getTelegramBot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                bodySupplier.get(),  ParseMode.HTML, false, inlineKeyboard);
    }
}
