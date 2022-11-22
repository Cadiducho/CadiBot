package com.cadiducho.bot.modules.desmotivaciones.cmds;

import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import lombok.extern.java.Log;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log
@CommandInfo(
    aliases = {"/desmotivacion", "/desmotivaciones", "/desmotiva"},
    description = "Obtiene un cartel aleatorio de desmotivaciones.es"
)
public class DesmotivacionCMD implements BotCommand {

    static final int MAX_IMAGES_PER_DAY = 10;
    static final String RATE_LIMITED_RESPONSE = "⛏⛏⛏⛏⛏⛏⛏⛏⛏⛏";

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Map<Long, Integer> imageSentCount = new java.util.HashMap<>();

    public DesmotivacionCMD() {
        executor.scheduleAtFixedRate(imageSentCount::clear, 0, 1, java.util.concurrent.TimeUnit.DAYS);
    }

    @Override
    public void execute(Chat chat, User user, CommandContext commandContext, Integer integer, Message message, Instant instant) throws TelegramException {

        if(imageSentCount.containsKey(chat.getId())){
            if( imageSentCount.get(chat.getId()) >= MAX_IMAGES_PER_DAY){
                getBot().sendMessage(chat.getId(), RATE_LIMITED_RESPONSE);
            } else {
                imageSentCount.put(chat.getId(), imageSentCount.get(chat.getId()) + 1);
                sendDesmotivacion(chat);
            }
        } else {
            imageSentCount.put(chat.getId(), 1);
            sendDesmotivacion(chat);
        }

    }

    private void sendDesmotivacion(Chat chat) throws TelegramException {
        String cartel = DesmotivacionesModule.getAPost();
        getBot().sendPhoto(chat.getId(), cartel);
    }


}