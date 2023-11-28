package com.cadiducho.bot.modules.treintaytres;

import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.util.MoshiProvider;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Log
@ModuleInfo(name = "33", description = "cómo?")
public class TreintaYTres implements ZinciteModule {

    private final Random rand = new Random();
    @Override
    public void onPostCommand(Update update, boolean success) {
        final String text = update.getMessage().getText().toLowerCase();
        if ((text.contains("33") || text.length() == 33 || text.contains("xxxiii") || text.contains("treintaytres") || text.contains("treinta y tres") || text.contains("treinta y 3")) && !text.contains("http")) {
            List<String> respuestas = Respuestas.getInstance().getRespuestas();

            String reply = EmojiParser.parseToUnicode(respuestas.get(rand.nextInt(respuestas.size())));
            
            try {
                ZinciteBot.getInstance().getTelegramBot().sendMessage(update.getMessage().getChat().getId(), reply, null, null, false, null, update.getMessage().getMessageId(), null);
            } catch (TelegramException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onLoad() {
        log.info("Cargando módulo 33");
        CommandManager commandManager = ZinciteBot.getInstance().getCommandManager();
        commandManager.register(new Reload33Cmd());
    }

}