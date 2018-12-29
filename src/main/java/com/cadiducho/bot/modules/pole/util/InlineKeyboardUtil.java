package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Clase para obtener los InlineKeyboards "prefabricados" usados en las poles
 */
public class InlineKeyboardUtil {

    private static final Emoji flechaAnterior = EmojiManager.getForAlias("rewind");
    private static final Emoji flechaSiguiente = EmojiManager.getForAlias("fast_forward");
    private static final Emoji diaActual = EmojiManager.getForAlias("spiral_calendar_pad");

    static final DateTimeFormatter otherDayFormatter = DateTimeFormatter.ofPattern("d/M");

    private static final String RANKING_GLOBAL = "mostrarRankingGlobal";
    private static final String TOP_GRUPO = "mostrarTopGrupo";
    private static final String TOP_GLOBAL_GRUPAL = "mostrarRankingPorGrupos";
    private static final String RESUMEN_GRUPO = "mostrarResumenGrupo";

    /**
     * Generar botón para el top de poles global
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingGlobal(String fecha) {
        InlineKeyboardButton rankingGlobal = new InlineKeyboardButton();
        rankingGlobal.setText("Top global");
        rankingGlobal.setCallbackData(RANKING_GLOBAL + "#" + fecha);
        return rankingGlobal;
    }

    /**
     * Generar botón para el top de poles del grupo
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton topGrupo(String fecha) {
        InlineKeyboardButton topGrupo = new InlineKeyboardButton();
        topGrupo.setText("Top del grupo");
        topGrupo.setCallbackData(TOP_GRUPO + "#" + fecha);
        return topGrupo;
    }

    /**
     * Generar botón para el top de poles global reunido por grupos
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingPorGrupos(String fecha) {
        InlineKeyboardButton rankingPorGrupos = new InlineKeyboardButton();
        rankingPorGrupos.setText("Top por grupos");
        rankingPorGrupos.setCallbackData(TOP_GLOBAL_GRUPAL + "#" + fecha);
        return rankingPorGrupos;
    }

    /**
     * Generar botón para el resumen diario
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton resumenDia(String fecha) {
        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        resumenDia.setText("Resumen del día");
        resumenDia.setCallbackData(RESUMEN_GRUPO + "#" + fecha);
        return resumenDia;
    }

    /**
     * Generar un botón que redirige al mismo nombre de callbackquery pero del día anterior
     * @param fecha La fecha original
     * @param originalCallback El nombre de callbackquery al que se dirigirá
     * @return El botón para el día anterior
     */
    private static InlineKeyboardButton diaAnterior(LocalDate fecha, String originalCallback) {
        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        fecha = fecha.minusDays(1);
        resumenDia.setText(flechaAnterior.getUnicode() + " " + otherDayFormatter.format(fecha));
        resumenDia.setCallbackData(originalCallback + "#" + fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return resumenDia;
    }

    /**
     * Generar un botón que redirige al mismo nombre de callbackquery pero del día siguiente
     * @param fecha La fecha original
     * @param originalCallback El nombre de callbackquery al que se dirigirá
     * @return El botón para el día siguiente
     */
    private static InlineKeyboardButton diaSiguiente(LocalDate fecha, String originalCallback) {
        fecha = fecha.plusDays(1);
        if (fecha.isAfter(LocalDate.now()) || fecha.isEqual(LocalDate.now())) return null;

        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        resumenDia.setText(flechaSiguiente.getUnicode() + " " + otherDayFormatter.format(fecha));
        resumenDia.setCallbackData(originalCallback + "#" + fecha.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return resumenDia;
    }

    /**
     * Generar un botón que redirige al mismo nombre de callbackquery pero actualizando la fecha al dia actual
     * @param originalCallback El nombre de callbackquery al que se dirigirá
     * @return El botón para el día actual
     */
    private static InlineKeyboardButton diaActual(LocalDate fecha, String originalCallback) {
        if (fecha.isEqual(LocalDate.now())) return null;

        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        LocalDate today = LocalDate.now();
        resumenDia.setText(diaActual.getUnicode() + " Hoy");
        resumenDia.setCallbackData(originalCallback + "#" + today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return resumenDia;
    }

    /**
     * Generar teclado para mostrar los tops
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarTops(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Collections.singletonList(topGrupo(fecha)),
                        Arrays.asList(
                                rankingGlobal(fecha),
                                rankingPorGrupos(fecha)
                        ),
                        Stream.of(
                                diaAnterior(date, RESUMEN_GRUPO),
                                diaActual(date, RESUMEN_GRUPO),
                                diaSiguiente(date, RESUMEN_GRUPO)
                        ).filter(Objects::nonNull).collect(Collectors.toList())
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para volver a mostrar el resumen diario o los tops globales
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarResumen(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Collections.singletonList(resumenDia(fecha)),
                        Arrays.asList(
                                rankingGlobal(fecha),
                                rankingPorGrupos(fecha)
                        ),
                        Stream.of(
                                diaAnterior(date, TOP_GRUPO),
                                diaActual(date, TOP_GRUPO),
                                diaSiguiente(date, TOP_GRUPO)
                        ).filter(Objects::nonNull).collect(Collectors.toList())
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global por grupos
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGrupal(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Arrays.asList(
                                resumenDia(fecha),
                                topGrupo(fecha)
                        ),
                        Collections.singletonList(rankingPorGrupos(fecha)),
                        Stream.of(
                                diaAnterior(date, RANKING_GLOBAL),
                                diaActual(date, RANKING_GLOBAL),
                                diaSiguiente(date, RANKING_GLOBAL)
                        ).filter(Objects::nonNull).collect(Collectors.toList())
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGlobal(LocalDate date) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        String fecha =  date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Arrays.asList(
                                resumenDia(fecha),
                                topGrupo(fecha)
                        ),
                        Collections.singletonList(rankingGlobal(fecha)),
                        Stream.of(
                                diaAnterior(date, TOP_GLOBAL_GRUPAL),
                                diaActual(date, TOP_GLOBAL_GRUPAL),
                                diaSiguiente(date, TOP_GLOBAL_GRUPAL)
                        ).filter(Objects::nonNull).collect(Collectors.toList())
                )
        );
        return inlineKeyboard;
    }
}
