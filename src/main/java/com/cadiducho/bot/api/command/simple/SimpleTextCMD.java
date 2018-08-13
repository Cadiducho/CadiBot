package com.cadiducho.bot.api.command.simple;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiParser;
import java.time.Instant;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Comando que responde con un texto
 * @author Cadiducho
 */
public class SimpleTextCMD extends SimpleCommand {

    private final List<String> replies;

    public SimpleTextCMD(List<String> aliases, String text) {
        this(aliases, ReplyPattern.TO_ANSWERED, Arrays.asList(text));
    }
    
    public SimpleTextCMD(List<String> aliases, List<String> replies) {
        this(aliases, ReplyPattern.TO_ANSWERED, replies);
    }
    
    public SimpleTextCMD(List<String> aliases, ReplyPattern replyPattern, List<String> replies) {
        super(aliases, replyPattern);
        this.replies = replies;
    }

    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getNano());
        String reply = EmojiParser.parseToUnicode(replies.get(rand.nextInt(replies.size())));

        getBot().sendMessage(chat.getId(), reply, null, null, false, replyTheCommandTo(messageId, replyingTo), null);
    }
}
