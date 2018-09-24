package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import java.util.Arrays;

/**
 * Clase para obtener los InlineKeyboards "prefabricados" usados en las poles
 */
public class InlineKeyboardUtil {

    private static Emoji top = EmojiManager.getForAlias("top");

    /**
     * Generar botón para el top de poles global
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingGlobal() {
        InlineKeyboardButton rankingGlobal = new InlineKeyboardButton();
        rankingGlobal.setText("Top global");
        rankingGlobal.setCallback_data("mostrarRankingGlobal");
        return rankingGlobal;
    }

    /**
     * Generar botón para el top de poles del grupo
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton topGrupo() {
        InlineKeyboardButton topGrupo = new InlineKeyboardButton();
        topGrupo.setText("Top del grupo");
        topGrupo.setCallback_data("mostrarTopGrupo");
        return topGrupo;
    }

    /**
     * Generar botón para el top de poles global reunido por grupos
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingPorGrupos() {
        InlineKeyboardButton rankingPorGrupos = new InlineKeyboardButton();
        rankingPorGrupos.setText("Top por grupos");
        rankingPorGrupos.setCallback_data("mostrarRankingPorGrupos");
        return rankingPorGrupos;
    }

    /**
     * Generar botón para el resumen diario
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton resumenDia() {
        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        resumenDia.setText("Resumen del día");
        resumenDia.setCallback_data("mostrarResumenGrupo");
        return resumenDia;
    }

    /**
     * Generar teclado para mostrar los tops
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarTops() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(topGrupo()), Arrays.asList(rankingGlobal(), rankingPorGrupos())));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para volver a mostrar el teclado o los tops globales
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarResumen() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(resumenDia()), Arrays.asList(rankingGlobal(), rankingPorGrupos())));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global por grupos
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGrupal() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(resumenDia(), topGrupo()), Arrays.asList(rankingPorGrupos())));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGlobal() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setInline_keyboard(Arrays.asList(Arrays.asList(resumenDia(), topGrupo()), Arrays.asList(rankingGlobal())));
        return inlineKeyboard;
    }
}
