package com.cadiducho.bot.api.command.simple;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.telegrambotapi.Message;

import java.util.List;

/**
 * Comandos simples para ser creados en una linea del constructor
 * @author Cadiducho
 */
public abstract class SimpleCommand implements BotCommand {

    private final List<String> aliases;
    protected final ReplyPattern replyPattern;

    public SimpleCommand(List<String> aliases, ReplyPattern replyPattern) {
       this.aliases = aliases;
       this.replyPattern = replyPattern;
    }
    
    @Override
    public String getName() {
        return aliases.get(0);
    }
    
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    Integer replyTheCommandTo(Integer originalId, Message replyingTo) {
        Integer replyTheCommandTo = null;
        switch (replyPattern) {
            case TO_ANSWERED:
                if (replyingTo != null) replyTheCommandTo = replyingTo.getMessage_id();
                break;
            case TO_ORIGINAL:
                replyTheCommandTo = originalId;
                break;
        }
        return replyTheCommandTo;
    }
}
