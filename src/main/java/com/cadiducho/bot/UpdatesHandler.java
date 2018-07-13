package com.cadiducho.bot;

import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.handlers.LongPollingHandler;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdatesHandler implements LongPollingHandler {

    private final TelegramBot bot;
    private final BotServer server;
    private final Random rand = new Random();

    private final List<String> sad = Arrays.asList("Sonríe, princesa", "dont be sad bb", ":)", "no me calientes a ver si te voy a dar motivos para estar sad", "Sonríe, no cambiará nada pero te verás menos feo", "Yo también estaría triste si tuviese tu cara", "tampoco me llores crack");
    private final List<String> mañana  = Arrays.asList("Buenos días, @user!", "Hola bb", "Ya era hora de levantarse", "van siendo horas de ponerse a trabajar desgraciao");
    private final List<String> ok = Arrays.asList("OK", "No te enfades bb", "ok", "ok de k hijo de puta", "ko", "Va", "ya");
    private final List<String> insultos = Arrays.asList("tus muertos", "cuerpoescombro", "canica", "anormal", "retrasado", "parguelas", "jueputa", "malparido", "gonorrea", "cabezabuque", "gilipollas", "cabezón", "cabezon", "subnormal", "joputa", "me cago en tus ancestros", "desgraciado", "desgraciao", "tonto", "mortadelo", "lonchas");

    @Override
    public void handleUpdate(Update update) {
        if (update.getCallback_query() != null) {
            server.getModuleManager().getModules().forEach(m -> m.onCallbackQuery(update.getCallback_query()));
        }
        
        try {
            if (update.getMessage().getType().equals(Message.Type.TEXT)) {
                //Si la update fue recibida hace más de 10 minutos, ignorarla
                if (Instant.ofEpochSecond(update.getMessage().getDate().longValue()).isBefore(Instant.now().minusSeconds(10 * 60))) return;

                boolean success = server.getCommandManager().onCmd(bot, update);
                server.getModuleManager().getModules().forEach(m -> m.onPostCommand(update, success));

                if (success) return; //Si es ejecutado por el framework de comandos no hacer nada más
            }

                  
            String chatId = update.getMessage().getChat().getId();
            Integer reponseId = update.getMessage().getMessage_id();
            String msg = update.getMessage().getText().toLowerCase();

            if (update.getMessage().getReply_to_message() != null) {
                reponseId = update.getMessage().getReply_to_message().getMessage_id();
            }
            
            msg = msg.replace("@" + bot.getMe().getUsername(), "");
            String reponse;
            
            //ToDo: a módulo de insultos
            if (insultos.contains(msg)) {
                reponse = insultos.get(rand.nextInt(insultos.size()));
                bot.sendMessage(chatId, reponse, null, null, false, reponseId, null);
            }
            
            //ToDo: a comandos de texto con respuestas random
            switch (msg) {
                case "/hola":
                    reponse = mañana.get(rand.nextInt(mañana.size())).replace("@user", update.getMessage().getFrom().getFirst_name());
                    bot.sendMessage(chatId, reponse, null, null, false, reponseId, null);
                    break;
                case ":(":
                    reponse = sad.get(rand.nextInt(this.sad.size()));
                    bot.sendMessage(chatId, reponse, null, null, false, reponseId, null);
                    break;
                case "ok":
                    reponse = ok.get(rand.nextInt(this.ok.size()));
                    bot.sendMessage(chatId, reponse, null, null, false, reponseId, null);
                    break;
                case "buenos dias":
                case "buenos días":
                    reponse = mañana.get(rand.nextInt(mañana.size())).replace("@user", update.getMessage().getFrom().getFirst_name());
                    bot.sendMessage(chatId, reponse, null, null, false, reponseId, null);
                    break;
            }
        } catch (TelegramException ex) {
            Logger.getLogger(UpdatesHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
