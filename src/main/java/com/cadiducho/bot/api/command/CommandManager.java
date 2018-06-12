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
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to handle and add all his commands
 *
 * @author Cadiducho
 */
@RequiredArgsConstructor
public class CommandManager {

    private final List<BotCommand> cmds = new ArrayList<>();
    private final BotServer server;
    
    public void register(BotCommand cmd) {
        //server.debugLog("Registrando comando /" + cmd.getName());
        cmds.add(cmd);
    }

    public void load() {
        register(new VersionCMD());
        register(new ChangelogCMD());
        register(new HoraCMD());
        register(new IntentarCMD());
        register(new UpdateUsernameCMD());
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

    /**
     * Ejecutar un comando
     * @param bot Bot que recibe la update
     * @param update Update del comando
     * @return Verdadero si se ha ejecutado, falso si no
     */
    public boolean onCmd(TelegramBot bot, Update update) {
        Date now = new Date();
        Message message = update.getMessage();
        User from = update.getMessage().getFrom();
        
        System.out.println(BotServer.fulltime.format(now) + " " + (from.getUsername() == null ? from.getFirst_name() : ("@" + from.getUsername())) + ": " + message.getText());
        
        try {
            String[] rawcmd = message.getText().split(" ");
            String label = rawcmd[0].replace("@" + bot.getMe().getUsername(), "");
            Integer replyId = message.getMessage_id();
            
            //Si el mensaje es respondiendo a alguien, dirigir respuesta a ese mensaje
            if (message.getReply_to_message() != null) {
                replyId = message.getReply_to_message().getMessage_id();
            }
            
            for (BotCommand cmd : cmds) {
                if ((cmd.getName().split(" ").length > 1) && ((cmd.getName().equalsIgnoreCase(message.getText())) || (cmd.getAliases().contains(message.getText()))) || (cmd.getName().equalsIgnoreCase(label)) || (cmd.getAliases().contains(label.toLowerCase()))) {
                    System.out.println(" # Ejecutando '" + cmd.getName() + "'");
                    cmd.execute(message.getChat(), from, label, Arrays.copyOfRange(rawcmd, 1, rawcmd.length), replyId, now);
                    server.getModuleManager().getModules().forEach(m -> m.onCommandExecuted(cmd));
                    return true;
                }
            }
        } catch (TelegramException ex) {
            Logger.getLogger(CommandManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        server.getModuleManager().getModules().forEach(m -> m.onPostCommand(update));
        return false;
    }
}
