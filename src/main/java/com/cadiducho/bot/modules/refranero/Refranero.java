package com.cadiducho.bot.modules.refranero;

import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.util.MoshiProvider;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

@Log
@ModuleInfo(name = "refranero", description = "Obtiene un refran del precioso idioma que es el Castellano")
public class Refranero implements ZinciteModule {


    @Override
    public void onLoad() {
        log.info("Cargando módulo refranero");
        commandManager.register(new RefranCMD());
    }

    @CommandInfo(
        aliases = {"/refran", "/refranero", "/sabiduria"},
        description = "Obtiene un refran del precioso idioma que es el Castellano"
    )
    class RefranCMD implements BotCommand {
        private final Random rand = new Random();

        @Override
        public void execute(Chat chat, User user, CommandContext commandContext, Integer integer, Message message, Instant instant) throws TelegramException {
            List<String> respuestas = Respuestas.getInstance().getRespuestas();
            String reply = respuestas.get(rand.nextInt(respuestas.size()));
            getBot().sendMessage(chat.getId(), reply);
        }
    }
}