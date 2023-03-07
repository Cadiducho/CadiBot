package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.java.Log;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Log
@CommandInfo(module = PoleModule.class, aliases = "poel", description = "Falla una pole")
public class PoleFailCMD implements BotCommand {

    private final List<String> respuestas = Arrays.asList("ayy casii jaj", "pringao", "cagaste");

    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random();
        String reply = EmojiParser.parseToUnicode(respuestas.get(rand.nextInt(respuestas.size())));

        getBot().sendMessage(chat.getId(), reply, null, null, false, null, messageId, null);
    }
}
