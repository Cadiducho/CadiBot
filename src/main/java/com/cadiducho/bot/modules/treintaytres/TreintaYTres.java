package com.cadiducho.bot.modules.treintaytres;

import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.vdurmont.emoji.EmojiParser;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@ModuleInfo(name = "33", description = "cómo?")
public class TreintaYTres implements ZinciteModule {

    private final List<String> respuestas = Arrays.asList("cómo", "CÓMO", "CÓMO????", "me están filmando", "NONONONONO", "ESTOY DANDO VOLTERETAS", "Eh?", "CÓMO 33?", "me estáis jodiendo de cojones", "Mis cojones 33?", "33? sabes cosas", "Como 33", "Fernando Alonso Díaz (Oviedo, Asturias; 29 de julio de 1981). Ha ganado dos veces el Mundial de Fórmula 1 en 2005 y 2006, una vez el Mundial de Resistencia de la FIA en 2019, las 24 Horas de Le Mans en 2018 y 2019, las 24 Horas de Daytona de 2019 y el Mundial de Karting en 1996.", "Fernando Alonso Díaz. \uD83D\uDC10", "Si hombre jodeme mas", "¿33?", "Filmación.", "esquizofrenia y locura", "COMO TREINTA Y TRES QUE NO QUE NO Y QUE NO \n" + "NANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANONANO", "NONONO", "Camisa de fuerza", "Me voy a hacer 33 pajas para rematar el día", "THE GOAT. \uD83D\uDC10", "Estoy viendo cosas", "33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33\n33".formatted(), "sabe cosas", "Esto es una animalada.", "Como nos lo vamos a pasar...", "Que viene el coco, bufones.", "Esto es demencial", "Histeria colectiva", "Lobi?", "Fernando Alonso va a pelear este mundial. Ahora mismo Red Bull está un paso por delante, pero esa brecha se va a ir cerrando con el paso de los Gps.", "PADRE", "Va a hacer esto sin coñas", "THIS IS A LOVELY CAR TO DRIVE", "Este tío es un enfermo mental y debe ser ingresado en una clínica.", "nadie\n" + "\n" + "absolutamente nadie\n" + "\n" + "el alonsista menos esquizofrénico:", "Esto es una animalada.", "Cuanto hemos estado esperando...", "Va a pelear el mundial", "BYE BYE", "Estoy cagado sin coñas. Tengo un mojonazo en el culo.", "This is a lovely car to drive.", "Este demente lo va a hacer", "Lo va a hacer.", "Cago líquido", "Feliz 2006 queridos amigos", "Me oís gritar?", "Invadiremos Cibeles con la 33", "Perdón?", "Esto no es ni medio normal", "filmando alonso", "OJO", "Y EN NADA, 34");

    @Override
    public void onPostCommand(Update update, boolean success) {

        if (update.getMessage().getText().contains("33") || update.getMessage().getText().length() == 33) {
            Random rand = new Random();
            String reply = EmojiParser.parseToUnicode(respuestas.get(rand.nextInt(respuestas.size())));
            
            try {
                ZinciteBot.getInstance().getTelegramBot().sendMessage(update.getMessage().getChat().getId(), reply, null, null, false, null, update.getMessage().getMessageId(), null);
            } catch (TelegramException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
