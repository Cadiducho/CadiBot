package com.cadiducho.bot.api.command;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.api.command.simple.SimpleGifCMD;
import com.cadiducho.bot.api.command.simple.SimplePhotoCMD;
import com.cadiducho.bot.api.command.simple.SimpleTextCMD;
import com.cadiducho.bot.api.command.simple.SimpleVoiceCMD;
import com.cadiducho.bot.cmds.*;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.TelegramBot;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class to handle and add all his commands
 *
 * @author Cadiducho
 */
@RequiredArgsConstructor
public class CommandManager {

    private final Map<String, BotCommand> commandMap = new HashMap<>();
    private final BotServer server;

    public void load() {
        register(new VersionCMD());
        register(new ChangelogCMD());
        register(new HoraCMD());
        register(new IntentarCMD());
        register(new BroadcastCMD());
        register(new CatCMD());

        register(new SimpleTextCMD(Arrays.asList(":)"), Arrays.asList(":)", ":D", ":smile:", ":smiley:", ":grinning:", ":blush:")));
        register(new SimpleTextCMD(Arrays.asList("haber si me muero"), "Ojalá"));
        register(new SimpleTextCMD(Arrays.asList("/patata"), "Soy una patata y tú no. Bienvenido a tu cinta"));
        register(new SimplePhotoCMD(Arrays.asList("emosido engañado", "emosido engañados"), "AgADBAADJKkxGygIoVKAZOwKkgWHWn7avBkABP0h-V4_8VernoACAAEC"));
        register(new SimpleGifCMD(Arrays.asList("what"), "CgADBAADMwMAAjAZZAenCigGk2AkogI"));
        register(new SimpleGifCMD(Arrays.asList("aplausos"), "CgADBAADTAMAAjEZZAfPEedEXgJYPwI"));
        register(new SimpleGifCMD(Arrays.asList("/servilleta"), "CgADBAADiwMAAt_WwFGgpX45Eh_AKAI"));
        register(new SimpleVoiceCMD(Arrays.asList("/titanic"), "AwADBAADujYAAv4dZAcEaOHAa3eQ8wI"));
        register(new SimpleVoiceCMD(Arrays.asList("puigdemont", "/viva"), "AwADBAAD0AIAAnhgYFAc6it_QeBppwI"));
    }

    public void register(BotCommand cmd) {
        cmd.getAliases().forEach(alias -> commandMap.put(alias, cmd));
    }
    
    public Optional<BotCommand> getCommand(String alias) {
        return Optional.ofNullable(commandMap.get(alias));
    }
    
    /**
     * Ejecutar un comando
     * @param bot Bot que recibe la update
     * @param update Update del comando
     * @return Verdadero si se ha ejecutado, falso si no
     * @throws com.cadiducho.telegrambotapi.exception.TelegramException Excepcion
     */
    public boolean onCmd(TelegramBot bot, Update update) throws TelegramException {
        Instant now = Instant.now();
        Message message = update.getMessage();
        User from = update.getMessage().getFrom();

        System.out.println(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(now) + " " + (from.getUsername() == null ? from.getFirst_name() : ("@" + from.getUsername())) + ": " + message.getText());

        String[] rawcmd = message.getText().split(" ");
        if (rawcmd.length == 0) {
            return false;
        }
        
        String sentLabel = rawcmd[0].toLowerCase().replace("@" + bot.getMe().getUsername(), "");
        Optional<BotCommand> target = getCommand(sentLabel);
        if (!target.isPresent()) {
            // no encontrado por primer alias, buscar frase entera
            target = getCommand(message.getText().toLowerCase());
            if (!target.isPresent()) {
                return false; // ni alias ni frase entera
            }
        }
        
        // Si el mensaje es respondiendo a alguien, dirigir respuesta a ese mensaje
        Integer replyId = message.getMessage_id();
        if (message.getReply_to_message() != null) {
            replyId = message.getReply_to_message().getMessage_id();
        }

        System.out.println(" # Ejecutando '" + target.get().getName() + "'");
        target.get().execute(message.getChat(), from, sentLabel, Arrays.copyOfRange(rawcmd, 1, rawcmd.length), replyId, now);
        return true;
    }
}
