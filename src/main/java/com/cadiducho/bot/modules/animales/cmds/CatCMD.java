package com.cadiducho.bot.modules.animales.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.cadiducho.zincite.api.command.args.Argument;
import com.cadiducho.zincite.api.command.args.CommandParseException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log
@CommandInfo(
        aliases = {"cat", "miau", "meow", "gato", "/gato", "/miau"},
        description = "Fotos de gatos",
        arguments = {@Argument(name="code", type = String.class, required = false, description = "Código de http")}
)
public class CatCMD implements BotCommand {

    private static final String catApi = "https://api.thecatapi.com/v1/images/search";
    private static final String catHttp = "https://http.cat/";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        try {
            // Enviar gatos de codigos http
            Optional<String> code = context.get("code");
            if (code.isPresent()) {
                String catFile = catHttp + code.get();
                getBot().sendPhoto(chat.getId(), catFile, null, false, null, null);
                return;
            }
        } catch (CommandParseException e) {
            log.warning("Error parseando un cat http code: ");
            log.warning(e.getMessage());
        }

        // Caso por defecto
        try (Response response = httpClient.newCall(new Request.Builder().url(catApi).build()).execute()) {
            Type listMyData = Types.newParameterizedType(List.class, CatAPIWrapper.class);
            JsonAdapter<List<CatAPIWrapper>> adapter = moshi.adapter(listMyData);
            String catFile = Objects.requireNonNull(adapter.fromJson(Objects.requireNonNull(response.body()).source())).get(0).getUrl();
            getBot().sendPhoto(chat.getId(), catFile);
        } catch (IOException | NullPointerException ex) {
            getBot().sendMessage(chat.getId(), "No he podido encontrar un gato :(");
            log.warning("Error procesando un pato");
            log.warning(ex.getMessage());
            log.warning(ex.getCause().getMessage());
        }
    }

    private static class CatAPIWrapper {
        @Getter
        private String url;
    }
}
