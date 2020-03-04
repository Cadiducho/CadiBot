package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CallbackListener;
import com.cadiducho.bot.api.command.ListenTo;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Log
@RequiredArgsConstructor
public class BanUserListener implements CallbackListener {

    private static final BotServer botServer = BotServer.getInstance();
    private final PoleModule module;

    @ListenTo("askBanUser")
    public void askBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String[] callbackData = callbackQuery.getData().split("#");
        Integer userid = Integer.parseInt(callbackData[1]);
        Long groupid = Long.parseLong(callbackData[2]);
        String[] nombreUsuario = module.getUsername(userid);
        Optional<String> nombreGrupo = module.getGroupName(groupid);

        final InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final InlineKeyboardButton confirmacion = new InlineKeyboardButton();
        confirmacion.setText("Confirmar");
        confirmacion.setCallbackData("executeBanUser#" + callbackData[1] + "#" + callbackData[2]);
        final InlineKeyboardButton cancelar = new InlineKeyboardButton();
        cancelar.setText("Cancelar");
        cancelar.setCallbackData("cancelBanUser#" + callbackData[1] + "#" + callbackData[2]);

        inlineKeyboard.setInlineKeyboard(Collections.singletonList(Arrays.asList(confirmacion, cancelar)));

        String body = "¿Estás seguro de que quieres banear a " + nombreUsuario[0] + "@" + nombreUsuario[1] + " por sus acciones en " + nombreGrupo.get() + "?";
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body, "html", false, inlineKeyboard);
    }

    @ListenTo("executeBanUser")
    public void executeBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String[] callbackData = callbackQuery.getData().split("#");
        Integer userid = Integer.parseInt(callbackData[1]);
        Long groupid = Long.parseLong(callbackData[2]);
        String[] nombreUsuario = module.getUsername(userid);
        Optional<String> nombreGrupo = module.getGroupName(groupid);
        String body = "El usuario " + nombreUsuario[0] + "@" + nombreUsuario[1] + "#" + userid + " ha sido baneado del sistema de poles por el uso de cheats en ";

        module.getPoleAntiCheat().banUser(userid, groupid, body + "este grupo");
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body + nombreGrupo.get(), "html", false, null);
    }

    @ListenTo("cancelBanUser")
    public void cancelBanUser(CallbackQuery callbackQuery) throws TelegramException {
        String body = "No se han tomado acciones contra este usuario";
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body, "html", false, null);
    }
}
