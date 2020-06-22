package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.CadiBotServer;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.cadiducho.zincite.api.command.CallbackListener;
import com.cadiducho.zincite.api.command.ListenTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Log
@RequiredArgsConstructor
public class BanUserListener implements CallbackListener {

    private static final CadiBotServer cadiBotServer = CadiBotServer.getInstance();
    private final PoleModule module;

    @ListenTo("askBanUser")
    public void askBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String[] callbackData = callbackQuery.getData().split("#");
        Integer userid = Integer.parseInt(callbackData[1]);
        Long groupid = Long.parseLong(callbackData[2]);
        String[] nombreUsuario = module.getUsername(userid);
        Optional<String> nombreGrupo = module.getGroupName(groupid);
        if (nombreGrupo.isEmpty()) {
            return;
        }

        final InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final InlineKeyboardButton confirmacion = new InlineKeyboardButton();
        confirmacion.setText("Confirmar");
        confirmacion.setCallbackData("executeBanUser#" + callbackData[1] + "#" + callbackData[2]);
        final InlineKeyboardButton cancelar = new InlineKeyboardButton();
        cancelar.setText("Cancelar");
        cancelar.setCallbackData("cancelBanUser#" + callbackData[1] + "#" + callbackData[2]);

        inlineKeyboard.setInlineKeyboard(Collections.singletonList(Arrays.asList(confirmacion, cancelar)));

        String body = "¿Estás seguro de que quieres banear a " + nombreUsuario[0] + "@" + nombreUsuario[1] + " por sus acciones en " + nombreGrupo.get() + "?";
        cadiBotServer.getCadibot().getTelegramBot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body,  ParseMode.HTML, false, inlineKeyboard);
    }

    @ListenTo("executeBanUser")
    public void executeBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String[] callbackData = callbackQuery.getData().split("#");
        Integer userid = Integer.parseInt(callbackData[1]);
        Long groupid = Long.parseLong(callbackData[2]);
        String[] nombreUsuario = module.getUsername(userid);
        Optional<String> nombreGrupo = module.getGroupName(groupid);
        if (nombreGrupo.isEmpty()) {
            return;
        }

        String body = "El usuario " + nombreUsuario[0] + "@" + nombreUsuario[1] + "#" + userid + " ha sido baneado del sistema de poles por el uso de cheats en ";

        module.getPoleAntiCheat().banUser(userid, groupid, body + "este grupo");
        cadiBotServer.getCadibot().getTelegramBot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body + nombreGrupo.get(), ParseMode.HTML, false, null);
    }

    @ListenTo("cancelBanUser")
    public void cancelBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String body = "No se han tomado acciones contra este usuario";
        cadiBotServer.getCadibot().getTelegramBot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body, ParseMode.HTML, false, null);
    }
}
