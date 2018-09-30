package com.cadiducho.bot.api.command.simple;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Comando que responde con un gif
 * @author Cadiducho
 */
public class SimpleGifCMD extends SimpleCommand {

    private final List<String> gifs;

    public SimpleGifCMD(List<String> aliases, String gifId) {
        this(aliases, ReplyPattern.TO_ANSWERED, Collections.singletonList(gifId));
    }
    
    public SimpleGifCMD(List<String> aliases, List<String> gifs) {
        this(aliases, ReplyPattern.TO_ANSWERED, gifs);
    }
    
    public SimpleGifCMD(List<String> aliases, ReplyPattern replyPattern, List<String> gifs) {
        super(aliases, replyPattern);
        this.gifs = gifs;
    }

    @Override
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getNano());
        String gifId = gifs.get(rand.nextInt(gifs.size()));

        getBot().sendDocument(chat.getId(), gifId, false, replyTheCommandTo(messageId, replyingTo), null);
    }
}
