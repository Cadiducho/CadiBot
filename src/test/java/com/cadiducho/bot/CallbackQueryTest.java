package com.cadiducho.bot;

import com.cadiducho.bot.api.command.*;
import com.cadiducho.telegrambotapi.CallbackQuery;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CallbackQueryTest {

    private static CallbackQuery query;
    private static CommandManager commandManager;

    @BeforeAll
    static void setUp() {
        query = new CallbackQuery();
        query.setId("1");
        query.setData("myListenerName#myData#myOtherData");

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("TestUser");
        when(user.getFirstName()).thenReturn("Test user");
        query.setFrom(user);

        commandManager = new CommandManager();
    }

    @Test
    void testCallbackQueryListener() throws InterruptedException {
        TestListener listener = new TestListener();
        assertEquals("", listener.data1);
        assertEquals("", listener.data2);

        commandManager.registerCallbackQueryListener(listener);
        commandManager.onCallbackQuery(query);
        Thread.sleep(2);

        assertEquals("myData", listener.data1);
        assertEquals("myOtherData", listener.data2);
    }

    public class TestListener implements BotCommand, CallbackListener {

        public String data1 = "";
        public String data2 = "";

        @ListenTo("myListenerName")
        public void listen(CallbackQuery query) {
            String[] sendedData = query.getData().split("#");
            data1 = sendedData[1];
            data2 = sendedData[2];
        }

        @Override
        public void execute(Chat chat, User from, CommandContext context, Integer messageId, Message replyingTo, Instant instant) {
        }
    }
}
