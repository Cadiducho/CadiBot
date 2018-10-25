package com.cadiducho.bot.modules.animales.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;

import java.time.Instant;

@CommandInfo(aliases = {"cat", "miau", "meow", "gato"})
public class CatCMD implements BotCommand {
    
    private static final String catApi = "https://cataas.com/cat/cute";

    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        /*InlineKeyboardButton voteUp = new InlineKeyboardButton();
        voteUp.setText(EmojiManager.getForAlias("thumbsup").getUnicode());
        voteUp.setCallback_data("catVoteUp()");
        InlineKeyboardButton voteDown = new InlineKeyboardButton();
        voteDown.setText(EmojiManager.getForAlias("thumbsdown").getUnicode());
        voteDown.setCallback_data("catVoteDown()");
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(voteUp, voteDown)));*/
        
        String catFile = catApi + "?" + instant.getNano(); //añadir numero para tener variación
        getBot().sendPhoto(chat.getId(), catFile, null, false, null, inlineKeyboard);
    }
}
