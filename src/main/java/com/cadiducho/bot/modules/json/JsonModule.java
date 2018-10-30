package com.cadiducho.bot.modules.json;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.command.json.*;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleInfo;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import lombok.Getter;
import lombok.extern.java.Log;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log
@ModuleInfo(name = "JsonModule", description = "Modulo de utilidades para cargar los json commands")
public class JsonModule implements Module {

    private CommandManager commandManager;
    @Getter private static final Moshi moshi = new Moshi.Builder()
            .add(PolymorphicJsonAdapterFactory.of(CommandFuncionality.class, "type")
                    .withSubtype(TextFuncionality.class, "text")
                    .withSubtype(GifFuncionality.class, "gif")
                    .withSubtype(ImageFuncionality.class, "image")
                    .withSubtype(VideoFuncionality.class, "video")
                    .withSubtype(VoiceFuncionality.class, "voice")
            )
            .build();

    @Override
    public void onLoad() {
        commandManager = BotServer.getInstance().getCommandManager();

        File commandsFolder = new File("commands");
        if (!commandsFolder.exists()) {
            commandsFolder.mkdirs();
        }

        try {
            Files.newDirectoryStream(commandsFolder.toPath(),
                    path -> path.toString().endsWith(".json"))
                    .forEach(this::registerJson);
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }


    private void registerJson(Path path) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(path));

            JsonAdapter<JsonCommand> adapter = moshi.adapter(JsonCommand.class);
            JsonCommand parsedComand = adapter.fromJson(source);
            commandManager.register(parsedComand);
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }
}
