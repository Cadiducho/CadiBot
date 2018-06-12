package com.cadiducho.bot.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.vdurmont.emoji.EmojiManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@CommandInfo(aliases = {"cat", "miau", "meow", "gato"})
public class CatCMD implements BotCommand {
    
    private static final String catApi = "http://aws.random.cat/meow";
    private static final ArrayList<String> catsSended = new ArrayList<>();

    @Override
    public void execute(Chat chat, User from, String label, String[] args, Integer messageId, Date date) throws TelegramException {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        /*InlineKeyboardButton voteUp = new InlineKeyboardButton();
        voteUp.setText(EmojiManager.getForAlias("thumbsup").getUnicode());
        voteUp.setCallback_data("catVoteUp()");
        InlineKeyboardButton voteDown = new InlineKeyboardButton();
        voteDown.setText(EmojiManager.getForAlias("thumbsdown").getUnicode());
        voteDown.setCallback_data("catVoteDown()");
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(voteUp, voteDown)));*/
        //ToDo: Guardar puntuación de gatos para enviar esos en caso de error
        try {
            JsonObject jsonObject = new JsonParser().parse(Unirest.get(catApi).asString().getBody()).getAsJsonObject();
            String catFile = jsonObject.get("file").getAsString();
            Message photo = getBot().sendPhoto(chat.getId(), catFile, null, false, null, inlineKeyboard);
            catsSended.add(photo.getPhoto().get(0).getFile_id());
        } catch (UnirestException ex) {
            if (catsSended.size() > 0) {
                Random rand = new Random(date.getTime());  
                getBot().sendPhoto(chat.getId(), catsSended.get(rand.nextInt(catsSended.size())), null, false, null, inlineKeyboard);
            } else {
                getBot().sendMessage(chat.getId(), "No he podido encontrar un miau " + EmojiManager.getForAlias("scream_cat").getUnicode());
                System.out.println(ex.getMessage());
            }
        }
    }
}
