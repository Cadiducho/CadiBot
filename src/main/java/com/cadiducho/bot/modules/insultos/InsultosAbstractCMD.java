package com.cadiducho.bot.modules.insultos;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.vdurmont.emoji.EmojiParser;

import java.time.Instant;
import java.util.List;
import java.util.Random;

public class InsultosAbstractCMD implements BotCommand {

    private final List<String> aliases;
    private final List<String> replies;

    @Override
    public String getName() {
        return aliases.get(0);
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    public InsultosAbstractCMD(List<String> aliases, List<String> replies) {
        this.aliases = aliases;
        this.replies = replies;
    }

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getNano());
        String reply = EmojiParser.parseToUnicode(replies.get(rand.nextInt(replies.size())));

        getBot().sendMessage(chat.getId(), reply, null, null, false, null, messageId, null);
    }
}
