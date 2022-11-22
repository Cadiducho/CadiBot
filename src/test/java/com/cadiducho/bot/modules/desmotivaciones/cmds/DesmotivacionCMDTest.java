package com.cadiducho.bot.modules.desmotivaciones.cmds;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DesmotivacionCMDTest {

    @Test
    void desmotivacionRateLimitTest() throws TelegramException {
        DesmotivacionCMD desmotivacionCMD = Mockito.spy(new DesmotivacionCMD());

        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);

        Chat chat = Mockito.mock(Chat.class);

        List<String> desmotivaciones = new ArrayList<>();

        Mockito.when(chat.getId()).thenReturn(1L);
        Mockito.when(telegramBot.getChat(Any.ANY)).thenReturn(chat);
        Mockito.when(telegramBot.sendPhoto(Mockito.any(), Mockito.anyString())).then(invocation -> {
            desmotivaciones.add(invocation.getArgument(1));
            return null;
        });
        Mockito.when(telegramBot.sendMessage(Mockito.any(), Mockito.anyString())).then(invocation -> {
            desmotivaciones.add(invocation.getArgument(1));
            return null;
        });

        Mockito.doReturn(telegramBot).when(desmotivacionCMD).getBot();

        // Go over the limit
        for (int i = 0; i < DesmotivacionCMD.MAX_IMAGES_PER_DAY +1; i++) {
            desmotivacionCMD.execute(chat, null, null, null, null, null);
        }

        // Check that the limit was reached, and that the limit message was sent
        assertTrue(desmotivaciones.contains(DesmotivacionCMD.RATE_LIMITED_RESPONSE));

    }
}