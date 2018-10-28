package com.cadiducho.bot;

import com.cadiducho.bot.api.command.json.CommandFuncionality;
import com.cadiducho.bot.api.command.json.ImageFuncionality;
import com.cadiducho.bot.api.command.json.JsonCommand;
import com.cadiducho.bot.api.command.json.TextFuncionality;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JsonCommandsTest {

    static Moshi moshi;

    @BeforeAll
    static void setUp() {
         moshi = new Moshi.Builder()
                .add(PolymorphicJsonAdapterFactory.of(CommandFuncionality.class, "type")
                        .withSubtype(TextFuncionality.class, "text")
                        .withSubtype(ImageFuncionality.class, "image"))
                .build();
    }

    @Test
    void testEmptyTypeThrowsError() {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String json = "{}";
        assertThrows(JsonDataException.class, () -> adapter.fromJson(json), "Missing label for tye");
    }

    @Test
    void readCommandFucionalityText() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonText = "{\"type\":\"text\",\"text\":\"Hola!\"}";
        CommandFuncionality funcionalityText = adapter.fromJson(jsonText);
        assertEquals(funcionalityText, new TextFuncionality("Hola!"));
    }

    @Test
    void readCommandFucionalityImage() throws IOException {
        JsonAdapter<CommandFuncionality> adapter = moshi.adapter(CommandFuncionality.class);
        String jsonImage = "{\"type\":\"image\",\"imageId\":\"5815687\"}";
        CommandFuncionality funcionalityImage = adapter.fromJson(jsonImage);
        assertEquals(funcionalityImage, new ImageFuncionality("5815687"));
    }

    @Test
    void parseJsonCommand() throws IOException {
        JsonAdapter<JsonCommand> adapter = moshi.adapter(JsonCommand.class);
        String json = "{\"aliases\":[\"ping\"],\"description\":\"ICMP\",\"funcionalities\":[{\"type\":\"text\",\"text\":\"pong\"},{\"type\":\"text\",\"text\":\"pung\"},{\"type\":\"image\",\"imageId\":\"5684\"}]}";
        JsonCommand parsedComand = adapter.fromJson(json);
        assertEquals(parsedComand, JsonCommand.builder()
                .aliases(Collections.singletonList("ping"))
                .description("ICMP")
                .funcionalities(Arrays.asList(new TextFuncionality("pong"), new TextFuncionality("pung"), new ImageFuncionality("5684")))
                .build());

        assertEquals("<code>ping</code>: ICMP", parsedComand.getUsage());
    }
}
