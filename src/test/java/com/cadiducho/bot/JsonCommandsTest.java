package com.cadiducho.bot;

import com.cadiducho.bot.api.command.json.*;
import com.cadiducho.bot.modules.json.JsonModule;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JsonCommandsTest {

    static Moshi moshi = JsonModule.getMoshi();

    @Test
    void testEmptyTypeThrowsError() {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String json = "{}";
        assertThrows(JsonDataException.class, () -> adapter.fromJson(json), "Missing label for tye");
    }

    @Test
    @DisplayName("Funcionality -> Text")
    void readCommandFucionalityText() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonText = ""
                + "{"
                + "   \"type\": \"text\", "
                + "   \"text\": \"Hola!\","
                + "   \"reply_to\": \"none\""
                + "}";
        CommandFuncionality funcionalityText = adapter.fromJson(jsonText);
        assertEquals(funcionalityText, TextFuncionality.builder().text("Hola!").build());
    }

    @Test
    @DisplayName("Funcionality -> Image")
    void readCommandFucionalityImage() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonImage = ""
                + "{"
                + "   \"type\": \"image\","
                + "   \"image_id\": \"5815687\","
                + "   \"reply_to\": \"answered\""
                + "}";
        CommandFuncionality funcionalityImage = adapter.fromJson(jsonImage);
        assertEquals(funcionalityImage, ImageFuncionality.builder().imageId("5815687").replyPattern(ReplyPattern.TO_ANSWERED).build());
    }

    @Test
    @DisplayName("Funcionality -> Gif")
    void readCommandFucionalityGif() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonImage = ""
                + "{"
                + "   \"type\": \"gif\","
                + "   \"gif_id\": \"5815687\","
                + "   \"reply_to\": \"original\""
                + "}";
        CommandFuncionality funcionalityImage = adapter.fromJson(jsonImage);
        assertEquals(funcionalityImage, GifFuncionality.builder().gifId("5815687").replyPattern(ReplyPattern.TO_ORIGINAL).build());
    }

    @Test
    @DisplayName("Funcionality -> Video")
    void readCommandFucionalityVideo() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonImage = ""
                + "{"
                + "   \"type\": \"video\","
                + "   \"video_id\": \"5815687\","
                + "   \"reply_to\": \"original\""
                + "}";
        CommandFuncionality funcionalityImage = adapter.fromJson(jsonImage);
        assertEquals(funcionalityImage, VideoFuncionality.builder().videoId("5815687").replyPattern(ReplyPattern.TO_ORIGINAL).build());
    }

    @Test
    @DisplayName("Funcionality -> Voice")
    void readCommandFucionalityVoice() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonImage = ""
                + "{"
                + "   \"type\": \"voice\","
                + "   \"voice_id\": \"5815687\","
                + "   \"reply_to\": \"original\""
                + "}";
        CommandFuncionality funcionalityImage = adapter.fromJson(jsonImage);
        assertEquals(funcionalityImage, VoiceFuncionality.builder().voiceId("5815687").replyPattern(ReplyPattern.TO_ORIGINAL).build());
    }

    @Test
    void parseJsonCommand() throws IOException {
        JsonAdapter<JsonCommand> adapter = moshi.adapter(JsonCommand.class);
        String json = ""
                + "{" +
                "   \"aliases\": " +
                "       [" +
                "           \"ping\"" +
                "       ]," +
                "   \"description\": \"ICMP\"," +
                "   \"funcionalities\": " +
                "       [" +
                "           {" +
                "               \"type\": \"text\"," +
                "               \"text\": \"pong\"," +
                "               \"reply_to\": \"none\"" +
                "           }," +
                "           {" +
                "               \"type\": \"text\"," +
                "               \"text\": \"pung\"," +
                "               \"reply_to\": \"none\"" +
                "           }," +
                "           {" +
                "               \"type\": \"image\"," +
                "               \"image_id\": \"5684\"," +
                "               \"reply_to\": \"none\"" +
                "           }" +
                "       ]" +
                "   }";
        JsonCommand parsedComand = adapter.fromJson(json);
        assertEquals(parsedComand, JsonCommand.builder()
                .aliases(Collections.singletonList("ping"))
                .description("ICMP")
                .funcionalities(Arrays.asList(
                        TextFuncionality.builder().text("pong").build(),
                        TextFuncionality.builder().text("pung").build(),
                        ImageFuncionality.builder().imageId("5684").build()))
                .build());

        assertEquals("<code>ping</code>: ICMP", parsedComand.getUsage());
    }
}
