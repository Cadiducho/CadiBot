package com.cadiducho.bot.modules.pole.cmds.admin;

import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.pole.util.PoleAntiCheat;
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

import java.time.Instant;
import java.util.Optional;

@CommandInfo(module = PoleModule.class,
        aliases = "/analyzeuser",
        hidden = true,
        description = "Analiza el comportamiento de un usuario",
        arguments = {
            @Argument(name = "usuario", type = Integer.class, description = "ID del usuario"),
            @Argument(name = "grupo", type = Long.class, description = "ID del grupo donde analizar"),
})
public class AnalyzeUserCMD implements BotCommand {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null, false, messageId, null);
            return;
        }
        try {
            Optional<Integer> usuario = context.get("usuario");
            Optional<Long> grupo = context.get("grupo");
            if (usuario.isEmpty() || grupo.isEmpty()) {
                getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null, false, messageId, null);
                return;
            }

            final PoleAntiCheat antiCheat = module.getPoleAntiCheat();
            boolean check = antiCheat.checkSuspiciousBehaviour(grupo.get(), usuario.get(), 7);
            if (!check) {
                getBot().sendMessage(chat.getId(), "El usuario con id " + usuario.get() + " no es sospechoso de usar cheats en " + grupo.get());
            }
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null, false, messageId, null);
        }
    }
}
