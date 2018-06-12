package com.cadiducho.bot.api.command.simple;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Comando que responde con un gif
 * @author Cadiducho
 */
public class SimpleGifCMD extends SimpleCommand {

    private final List<String> gifs;

    public SimpleGifCMD(List<String> aliases, String gifId) {
        this(aliases, true, Arrays.asList(gifId));
    }
    
    public SimpleGifCMD(List<String> aliases, List<String> gifs) {
        this(aliases, true, gifs);
    }
    
    public SimpleGifCMD(List<String> aliases, boolean isReplying, List<String> gifs) {
        super(aliases, isReplying);
        this.gifs = gifs;
    }

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer replyId, Date date) throws TelegramException {
        Random rand = new Random(date.getTime());
        String gifId = gifs.get(rand.nextInt(gifs.size()));
        
        getBot().sendDocument(chat.getId(), gifId, false, isReplying ? replyId : null, null);
    }
}
