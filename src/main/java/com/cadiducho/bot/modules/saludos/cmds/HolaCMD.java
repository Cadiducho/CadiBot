package com.cadiducho.bot.modules.saludos.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.saludos.SaludosModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@CommandInfo(module = SaludosModule.class, aliases = {"/hola", "buenos días", "buenos dias"})
public class HolaCMD implements BotCommand {

    private final List<String> saludos = Arrays.asList("Buenos días, @user!", "Hola bb", "Ya era hora de levantarse", "van siendo horas de ponerse a trabajar desgraciao");

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getEpochSecond());
        String reponse = saludos.get(rand.nextInt(saludos.size())).replace("@user", from.getFirst_name());
        getBot().sendMessage(chat.getId(), reponse, null, null, false, messageId, null);
    }
}