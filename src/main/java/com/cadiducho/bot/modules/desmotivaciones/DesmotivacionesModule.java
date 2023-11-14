package com.cadiducho.bot.modules.desmotivaciones;

import com.cadiducho.bot.modules.desmotivaciones.cmds.DesmotivacionCMD;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.CommandManager;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
@ModuleInfo(name = "Desmotivaciones", description = "Carteles aleatorios de desmotivaciones.es")
public class DesmotivacionesModule implements ZinciteModule {

    private static final Queue<String> postsBuffer = new LinkedBlockingDeque<>();
    private static final String desmotivacionesWeb = "http://desmotivaciones.es/aleatorio";
    private static final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public void onLoad() {
        CommandManager commandManager = ZinciteBot.getInstance().getCommandManager();

        commandManager.register(new DesmotivacionCMD());

        log.info("Módulo de Desmotivaciones cargado");
        getPosts();
        log.info("Buffer de carteles cargado " + postsBuffer.size());
    }

    public static void getPosts() {
        try (Response response = httpClient.newCall(new Request.Builder().url(desmotivacionesWeb).build()).execute()) {

            String html = Objects.requireNonNull(response.body()).string();

            List<String> carteles = new ArrayList<>();
            Matcher image = Pattern.compile("<img\\s.*?src=(?:'|\")https?:\\/\\/img.desmotivaciones.es\\/([^'\">]+)(?:'|\")").matcher(html);

            while (image.find()) {
                carteles.add(image.group(1));
            }

            postsBuffer.addAll(carteles);

        } catch (IOException | NullPointerException ex) {
            log.warning("Error procesando un cartel");
            log.warning(ex.getMessage());
            log.warning(ex.getCause().getMessage());
        }
    }

    public static Optional<String> getAPost() {
        String id = postsBuffer.poll();
        if (id == null) {
            getPosts(); //Carga en el hilo principal, bloquea
            id = postsBuffer.poll();
        } else if (postsBuffer.size() < 5) {
            Executors.newSingleThreadExecutor().submit(DesmotivacionesModule::getPosts); //Carga en segundo plano
        }

        if (id == null) { //Si sigue siendo null, no se ha podido cargar
            log.warning("No se han podido cargar carteles");
            return Optional.empty();
        }
        return Optional.of("http://img.desmotivaciones.es/" + id);
    }

}
