package com.cadiducho.bot.modules.purabulla;

import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.java.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Log
@ModuleInfo(name = "PuraBulla", description = "Pura Bulla")
public class PuraBullaModule implements ZinciteModule {

    private final Random rand = new Random();
    private final List<String> respuestas = Arrays.asList("NO TE OIGO MUCHO RUIDO", "Efectivamente vaso de aluminio",
            "¿ALGUIEN DIJO BULLA?", "A que hora se callan?", "¿⏰?", "🍷 a que hora?", "Ni las voces de mi cabeza hacen tanta bulla...",
            "Y la de trabajar se la saben?", "⚠️: ALTOS NIVELES DE BULLA", "Quisiera tomarlos en serio, pero no dejan escuchar nada!", "*sonidos de vaso de aluminio cayendose*",
            "varios vasos de aluminio entraron al chat...", "LE ESTÁ GANANDO AL VASO", "*vaso de aluminio ha iniciado sesión*");

    @Override
    public void onPostCommand(Update update, boolean success) {
        final String text = update.getMessage().getText().toLowerCase();
        if (text.contains("pura bulla") || text.contains("purabulla") || text.contains("vaso de aluminio")) {
            String reply = EmojiParser.parseToUnicode(respuestas.get(rand.nextInt(respuestas.size())));

            try {
                ZinciteBot.getInstance().getTelegramBot().sendMessage(update.getMessage().getChat().getId(), reply, null, null, false, null, update.getMessage().getMessageId(), null);
            } catch (TelegramException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
