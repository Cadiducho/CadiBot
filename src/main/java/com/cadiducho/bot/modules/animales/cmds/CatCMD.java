package com.cadiducho.bot.modules.animales.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;

import java.time.Instant;

@CommandInfo(aliases = {"cat", "miau", "meow", "gato"}, description = "Fotos de gatos")
public class CatCMD implements BotCommand {
    
    private static final String catApi = "https://api.thecatapi.com/v1/images/search";

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String catFile = catApi + "?" + instant.getNano(); //añadir numero para tener variación
        getBot().sendPhoto(chat.getId(), catFile, null, false, null, inlineKeyboard);
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
