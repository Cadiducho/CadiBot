package com.cadiducho.bot.modules.pole.cmds.admin;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.*;
import com.cadiducho.bot.api.command.args.Argument;
import com.cadiducho.bot.api.command.args.CommandArguments;
import com.cadiducho.bot.api.command.args.CommandParseException;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import lombok.extern.java.Log;

import java.sql.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Log
@CommandInfo(module = PoleModule.class, aliases = "/migrategroup")
@CommandArguments({
        @Argument(name = "viejoGrupo", type = Long.class, description = "ID del viejo grupo"),
        @Argument(name = "nuevoGrupo", type = Long.class, description = "ID del nuevo grupo")
})
public class MigrateGroupCMD implements BotCommand, CallbackListener {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null, false, messageId, null);
            return;
        }
        try {
            Optional<Long> viejoGrupo = context.get("viejoGrupo");
            Optional<Long> nuevoGrupo = context.get("nuevoGrupo");
            if (!viejoGrupo.isPresent() || !nuevoGrupo.isPresent()) {
                getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(), "html", null, false, messageId, null);
                return;
            }

            Optional<String> nombreViejoGrupo = module.getGroupName(viejoGrupo.get());
            Optional<String> nombreNuevoGrupo = module.getGroupName(nuevoGrupo.get());
            if (!nombreViejoGrupo.isPresent() || !nombreNuevoGrupo.isPresent()) {
                getBot().sendMessage(chat.getId(), "Grupos no reconocidos.");
                return;
            }

            final InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
            final InlineKeyboardButton confirmacion = new InlineKeyboardButton();
            confirmacion.setText("Confirmar");
            confirmacion.setCallbackData("confirmarMigracionGrupo#" + viejoGrupo.get() + "#" + nuevoGrupo.get());
            final InlineKeyboardButton cancelar = new InlineKeyboardButton();
            cancelar.setText("Cancelar");
            cancelar.setCallbackData("cancelarMigracionGrupo#" + viejoGrupo.get() + "#" + nuevoGrupo.get());

            inlineKeyboard.setInlineKeyboard(Collections.singletonList(Arrays.asList(confirmacion, cancelar)));

            String body = "¿Estás seguro de que quieres migrar todas las poles de \n"
                    + "<b>" + nombreViejoGrupo.get() + "</b>[<i>" + viejoGrupo.get() + "</i>] "
                    + "al nuevo <b>" + nombreNuevoGrupo.get() + "</b>[<i>" + nuevoGrupo.get() + "</i>]?\n"
                    + "Esto alterará todas las poles de esos dos grupos.";
            getBot().sendMessage(chat.getId(), body, "html", null, null, null, inlineKeyboard);
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(), "html", null, false, messageId, null);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @ListenTo("confirmarMigracionGrupo")
    public void confirmarMigracionGrupo(CallbackQuery callbackQuery) throws TelegramException {
        if (!callbackQuery.getFrom().getUsername().equalsIgnoreCase("cadiducho")) return;
        String[] callbackData = callbackQuery.getData().split("#");
        Long oldId = Long.parseLong(callbackData[1]);
        Long newId = Long.parseLong(callbackData[2]);
        Optional<String> nombreViejoGrupo = module.getGroupName(oldId);
        Optional<String> nombreNuevoGrupo = module.getGroupName(newId);
        int updated = migratePoles(oldId, newId);

        // Informar por mensaje a los grupos afectados en el cambio
        String body = "Se han migrado las " + updated + " poles de <b>" + nombreViejoGrupo.get() + "</b>[<i>" + oldId + "</i>]"
                + " a <b>" + nombreNuevoGrupo.get() + "</b>[<i>" + newId + "</i>]";
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body, "html", false, null);

        body = "Se han migrado " + updated + " registros de poles desde el grupo <b>" + nombreViejoGrupo.get() + "</b>[<i>" + newId + "</i>] a este";
        botServer.getCadibot().sendMessage(oldId, body,
                "HTML", false, false, null, null);

        try {
            body = "Las poles que habían en este grupo han sido migradas a  <b>" + nombreNuevoGrupo.get() + "</b>[<i>" + oldId + "</i>]";
            botServer.getCadibot().sendMessage(newId, body,
                "HTML", false, false, null, null);
        } catch (TelegramException ex) {
            log.info("Disabling group " + oldId);
            botServer.getDatabase().disableGroup(oldId);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @ListenTo("cancelarMigracionGrupo")
    public void cancelarMigracionGrupo(CallbackQuery callbackQuery) throws TelegramException {
        if (!callbackQuery.getFrom().getUsername().equalsIgnoreCase("cadiducho")) return;
        String[] callbackData = callbackQuery.getData().split("#");
        String body = "Has cancelado la migración del grupo <b>" + module.getGroupName(Long.parseLong(callbackData[1])).get() + "</b>[<i>" + callbackData[1] + "</i>]"
                + " a <b>" + module.getGroupName(Long.parseLong(callbackData[2])).get() + "</b>[<i>" + callbackData[2] + "</i>]";
        botServer.getCadibot().editMessageText(callbackQuery.getMessage().getChat().getId(), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(),
                body, "html", false, null);
    }

    private int migratePoles(Long oldId, Long newId) {
        int updated = -1;
        try {
            Connection connection = BotServer.getInstance().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE cadibot_poles SET groupid=? WHERE groupid=?");
            statement.setLong(1, newId);
            statement.setLong(2, oldId);
            updated = statement.executeUpdate();
            BotServer.getInstance().getDatabase().closeConnection(connection);
            return updated;
        } catch (SQLException exception) {
            log.severe("Error migrando un grupo");
            log.severe(exception.toString());
        }
        return updated;
    }
}
