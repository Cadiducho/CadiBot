package com.cadiducho.bot.api.command.json;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Getter
@Builder
@EqualsAndHashCode
public class JsonCommand implements BotCommand {

    private List<String> aliases;
    private String module;
    private List<CommandFuncionality> funcionalities;
    private String description;

    @Override
    public String getName() {
        return aliases.get(0);
    }

    @Override
    public Module getModule() {
        if (botServer == null) {
            return null; //FixMe: Solución temporal a usar JsonCommand.getModule() en JUnit 5, donde botServer va a ser null
        }
        return botServer.getModuleManager().getModule(module)
                .orElse(null); //ToDo: ¿devolver null, o meterlos a un módulo de json o al core?
    }

    @Override
    public void execute(Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) throws TelegramException {
        Random random = new Random(instant.toEpochMilli());
        funcionalities.get(random.nextInt(funcionalities.size())).execute(getBot(), chat, from, context, messageId, replyingTo, instant);
    }
}