package com.cadiducho.bot.modules.core.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.cadiducho.zincite.api.command.args.Argument;
import com.vdurmont.emoji.EmojiManager;

import java.time.Instant;
import java.util.Optional;

@CommandInfo(aliases = "/intentar", arguments = @Argument(name = "accion", type = String.class, description = "Acción a intentar"))
public class IntentarCMD implements BotCommand {

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Optional<String> accion = context.getLastArguments();
        if (accion.isEmpty()) {
            getBot().sendMessage(chat.getId(), "<b>Usa:</b> " + this.getUsage(),  ParseMode.HTML, null, false, messageId, null);
            return;
        }
        String tried = "intenta";
        String end = "y lo consigue";
        if (Math.random() < 0.5D) {
            tried = "intentó";
            end = "pero falló";
        }
        getBot().sendMessage(chat.getId(), EmojiManager.getForAlias("game_die").getUnicode() + " " + from.getFirstName() + " " + tried + " " + accion.get() + " " + end);
    }
}
