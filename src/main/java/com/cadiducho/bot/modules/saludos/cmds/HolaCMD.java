package com.cadiducho.bot.modules.saludos.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.saludos.SaludosModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
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
    public void execute(final Chat chat, final User from, final String label, final String[] args, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        Random rand = new Random(instant.getEpochSecond());
        String reponse = saludos.get(rand.nextInt(saludos.size())).replace("@user", from.getFirstName());
        getBot().sendMessage(chat.getId(), reponse, null, null, false, messageId, null);
    }
}