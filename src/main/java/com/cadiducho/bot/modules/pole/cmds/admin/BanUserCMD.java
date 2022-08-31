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
        aliases = "/banuser",
        hidden = true,
        arguments = {
                @Argument(name = "usuario", type = Long.class, description = "ID del usuario"),
                @Argument(name = "grupo", type = Long.class, description = "ID del grupo donde cometió la falta"),
                @Argument(name = "razon", type = String.class, description = "La razón")
        })
public class BanUserCMD implements BotCommand {

    private final PoleModule module = (PoleModule) getModule();

    @Override
    public void execute(Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        if (!from.getUsername().equalsIgnoreCase("cadiducho")) {
            getBot().sendMessage(chat.getId(), "No tienes permiso para usar este comando", null, null,false, null, messageId, null);
            return;
        }
        try {
            Optional<Long> usuario = context.get("usuario");
            Optional<Long> grupo = context.get("grupo");
            Optional<String> razon = context.getLastArguments();
            if (usuario.isEmpty()) {
                getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null,false, null, messageId, null);
                return;
            }

            final PoleAntiCheat antiCheat = module.getPoleAntiCheat();

            StringBuilder mensaje = new StringBuilder();
            String[] nombreUsuario = module.getUsername(usuario.get());
            mensaje.append("El usuario '")
                    .append(nombreUsuario[0])
                    .append("@").append(nombreUsuario[1]).append("#").append(usuario.get())
                    .append("' ha sido baneado del sistema de poles por ");
            if (grupo.isPresent() && razon.isPresent()) {
                Optional<String> nombreGrupo = module.getGroupName(grupo.get());
                mensaje.append(razon.get());
                if (nombreGrupo.isPresent()) {
                    mensaje.append(" en ");
                    mensaje.append("'").append(nombreGrupo.get()).append("'");
                }
            } else {
                mensaje.append(razon);
            }

            final String fMessage = mensaje.toString();
            antiCheat.banUser(usuario.get(), grupo.orElse(null), fMessage);
            getBot().sendMessage(from.getId(), fMessage);
        } catch (CommandParseException ex) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null,false, null, messageId, null);
        }
    }
}
