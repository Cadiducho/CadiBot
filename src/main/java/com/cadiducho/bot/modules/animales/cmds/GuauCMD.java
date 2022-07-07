package com.cadiducho.bot.modules.animales.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.squareup.moshi.Moshi;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Log
@CommandInfo(aliases = {"/guau", "guau", "/dog", "perro"}, description = "Fotos de perros")
public class GuauCMD implements BotCommand {

    private static final String dogAPI = "https://dog.ceo/api/breeds/image/random";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        try (Response response = httpClient.newCall(new Request.Builder().url(dogAPI).build()).execute()) {
            String dogFile = Objects.requireNonNull(moshi.adapter(DogAPIWrapper.class).fromJson(Objects.requireNonNull(response.body()).source())).getMessage();
            getBot().sendPhoto(chat.getId(), dogFile);
        } catch (IOException | NullPointerException ex) {
            getBot().sendMessage(chat.getId(), "No he podido encontrar un perro :(");
            log.warning("Error procesando un pato");
            log.warning(ex.getMessage());
            log.warning(ex.getCause().getMessage());
        }
    }

    private static class DogAPIWrapper {
        @Getter private String message;
    }
}
