package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private static InlineKeyboardButton rankingGlobal(String fecha) {
        InlineKeyboardButton rankingGlobal = new InlineKeyboardButton();
        rankingGlobal.setText("Top global");
        rankingGlobal.setCallbackData("mostrarRankingGlobal#" + fecha);
        return rankingGlobal;
    }

    /**
     * Generar botón para el top de poles del grupo
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton topGrupo(String fecha) {
        InlineKeyboardButton topGrupo = new InlineKeyboardButton();
        topGrupo.setText("Top del grupo");
        topGrupo.setCallbackData("mostrarTopGrupo#" + fecha);
        return topGrupo;
    }

    /**
     * Generar botón para el top de poles global reunido por grupos
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingPorGrupos(String fecha) {
        InlineKeyboardButton rankingPorGrupos = new InlineKeyboardButton();
        rankingPorGrupos.setText("Top por grupos");
        rankingPorGrupos.setCallbackData("mostrarRankingPorGrupos#" + fecha);
        return rankingPorGrupos;
    }

    /**
     * Generar botón para el resumen diario
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton resumenDia(String fecha) {
        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        resumenDia.setText("Resumen del día");
        resumenDia.setCallbackData("mostrarResumenGrupo#" + fecha);
        return resumenDia;
    }

    /**
     * Generar teclado para mostrar los tops
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarTops(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(Arrays.asList(
                Arrays.asList(topGrupo(fecha)), Arrays.asList(rankingGlobal(fecha), rankingPorGrupos(fecha))));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para volver a mostrar el teclado o los tops globales
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarResumen(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(Arrays.asList(
                Arrays.asList(resumenDia(fecha)), Arrays.asList(rankingGlobal(fecha), rankingPorGrupos(fecha))));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global por grupos
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGrupal(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(Arrays.asList(
                Arrays.asList(resumenDia(fecha), topGrupo(fecha)), Arrays.asList(rankingPorGrupos(fecha))));
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGlobal(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(Arrays.asList(
                Arrays.asList(resumenDia(fecha), topGrupo(fecha)), Arrays.asList(rankingGlobal(fecha))));
        return inlineKeyboard;
    }
}
