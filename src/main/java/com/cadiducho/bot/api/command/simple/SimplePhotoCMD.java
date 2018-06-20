package com.cadiducho.bot.api.command.simple;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import java.time.Instant;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Comando que responde con una foto
 * @author Cadiducho
 */
public class SimplePhotoCMD extends SimpleCommand {

    private final List<String> photos;

    public SimplePhotoCMD(List<String> aliases, String photoId) {
        this(aliases, true, Arrays.asList(photoId));
    }
    
    public SimplePhotoCMD(List<String> aliases, List<String> photos) {
        this(aliases, true, photos);
    }
    
    public SimplePhotoCMD(List<String> aliases, boolean isReplying, List<String> photos) {
        super(aliases, isReplying);
        this.photos = photos;
    }
    
    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer replyId, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getNano());
        String photoId = photos.get(rand.nextInt(photos.size()));
        
        getBot().sendPhoto(chat.getId(), photoId, null, false, isReplying ? replyId : null, null);
    }
}
