package com.cadiducho.bot.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;

import java.util.Date;

@CommandInfo(aliases = {"cat", "miau", "meow", "gato"})
public class CatCMD implements BotCommand {
    
    private static final String catApi = "https://cataas.com/cat/cute";

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
        
        String catFile = catApi + "?" + date.getTime(); //añadir numero para tener variación
        getBot().sendPhoto(chat.getId(), catFile, null, false, null, inlineKeyboard);
    }
}
