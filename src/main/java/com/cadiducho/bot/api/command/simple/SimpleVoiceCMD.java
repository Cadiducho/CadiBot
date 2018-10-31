package com.cadiducho.bot.api.command.simple;

import com.cadiducho.bot.api.command.CommandContext;
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
 * Comando que responde con una nota de voz
 * @author Cadiducho
 */
@Deprecated
public class SimpleVoiceCMD extends SimpleCommand {

    private final List<String> voices;

    public SimpleVoiceCMD(List<String> aliases, String voiceId) {
        this(aliases, ReplyPattern.TO_ANSWERED, Collections.singletonList(voiceId));
    }
    
    public SimpleVoiceCMD(List<String> aliases, List<String> voices) {
        this(aliases, ReplyPattern.TO_ANSWERED, voices);
    }
    
    public SimpleVoiceCMD(List<String> aliases, ReplyPattern replyPattern, List<String> voices) {
        super(aliases, replyPattern);
        this.voices = voices;
    }

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getNano());
        String voiceId = voices.get(rand.nextInt(voices.size()));
        
        getBot().sendVoice(chat.getId(), voiceId, null, null, false, replyTheCommandTo(messageId, replyingTo), null);
    }
}
