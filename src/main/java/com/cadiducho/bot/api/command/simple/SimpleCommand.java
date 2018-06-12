package com.cadiducho.bot.api.command.simple;

import com.cadiducho.bot.api.command.BotCommand;

import java.util.List;

/**
 * Comandos simples para ser creados en una linea del constructor
 * @author Cadiducho
 */
public abstract class SimpleCommand implements BotCommand {

    private final List<String> aliases;
    protected final boolean isReplying;
    
    public SimpleCommand(List<String> aliases, boolean isReplying) {
       this.aliases = aliases;
       this.isReplying = isReplying;
    }
    
    @Override
    public String getName() {
        return aliases.get(0);
    }
    
    @Override
    public List<String> getAliases() {
        return aliases;
    }
}
