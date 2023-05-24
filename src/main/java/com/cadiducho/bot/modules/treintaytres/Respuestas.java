package com.cadiducho.bot.modules.treintaytres;

import com.cadiducho.telegrambotapi.util.MoshiProvider;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;
import lombok.Getter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Log
final class Respuestas{

    private static final String URL_RESPUESTAS = "https://raw.githubusercontent.com/Alonsistas/33/main/frases.json";

    private static final OkHttpClient client = new OkHttpClient();
    private static final JsonAdapter<List<String>> adapter = MoshiProvider.getMoshi().adapter(Types.newParameterizedType(List.class, String.class));

    @Getter
    private List<String> respuestas;

    private static Respuestas INSTANCE;
    private Respuestas(){
        if(INSTANCE != null) throw new UnsupportedOperationException("Cannot create singleton class twice");
        INSTANCE = this;
        try {
            respuestas = obtenerRespuestas();
        } catch (IOException e) {
            log.log(Level.WARNING,"Error obteniendo respuestas del módulo 33",e);
            respuestas = new ArrayList<>(List.of("33"));
        }

    }

    private static List<String> obtenerRespuestas() throws IOException {
        List<String> lista = new ArrayList<>();
        Request request = new Request.Builder()
                .url(URL_RESPUESTAS)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            lista = adapter.fromJson(response.body().source());
        }
        return lista;
    }

    public void refrescar() throws IOException {
            respuestas = obtenerRespuestas();
    }

    public static Respuestas getInstance() {
        if(INSTANCE == null){
            INSTANCE = new Respuestas();
        }
        return INSTANCE;
    }
}