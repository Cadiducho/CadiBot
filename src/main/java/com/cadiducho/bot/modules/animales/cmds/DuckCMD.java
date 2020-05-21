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
@CommandInfo(aliases = {"/duck", "cuack", "/cuack", "pato"})
public class DuckCMD implements BotCommand {

    private static final String duckAPI = "https://random-d.uk/api/v1/random?type=jpg";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        try (Response response = httpClient.newCall(new Request.Builder().url(duckAPI).build()).execute()) {
            String duckFile = Objects.requireNonNull(moshi.adapter(DuckAPIWrapper.class).fromJson(Objects.requireNonNull(response.body()).source())).getUrl();
            getBot().sendPhoto(chat.getId(), duckFile);
        } catch (IOException | NullPointerException ex) {
            getBot().sendMessage(chat.getId(), "No he podido encontrar un pato :(");
            log.warning("Error procesando un pato");
            log.warning(ex.getMessage());
            log.warning(ex.getCause().getMessage());
        }
    }

    private static class DuckAPIWrapper {
        @Getter private String url;
    }
}
