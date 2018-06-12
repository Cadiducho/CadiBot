package com.cadiducho.bot.api.command.simple;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiParser;

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
        this(aliases, true, Arrays.asList(text));
    }
    
    public SimpleTextCMD(List<String> aliases, List<String> replies) {
        this(aliases, true, replies);
    }
    
    public SimpleTextCMD(List<String> aliases, boolean isReplying, List<String> replies) {
        super(aliases, isReplying);
        this.replies = replies;
    }

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer replyId, Date date) throws TelegramException {
        Random rand = new Random(date.getTime());
        String reply = EmojiParser.parseToUnicode(replies.get(rand.nextInt(replies.size())));
        
        getBot().sendMessage(chat.getId(), reply, null, null, false, isReplying ? replyId : null, null);
    }
}
