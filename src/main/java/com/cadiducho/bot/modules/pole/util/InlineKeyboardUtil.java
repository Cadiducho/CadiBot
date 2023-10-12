package com.cadiducho.bot.modules.pole.util;

import com.cadiducho.telegrambotapi.inline.InlineKeyboardButton;
import com.cadiducho.telegrambotapi.inline.InlineKeyboardMarkup;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private static InlineKeyboardButton rankingGlobal(String fecha, String finIntervalo) {
        final InlineKeyboardButton rankingGlobal = new InlineKeyboardButton();
        if (finIntervalo != null) {
            rankingGlobal.setText("Top global (int.)");
        } else {
            rankingGlobal.setText("Top global");
        }
        rankingGlobal.setCallbackData(RANKING_GLOBAL + "#" + fecha + "#" + finIntervalo);
        return rankingGlobal;
    }

    /**
     * Generar botón para el top de poles del grupo
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton topGrupo(String fecha, String finIntervalo) {
        final InlineKeyboardButton topGrupo = new InlineKeyboardButton();
        if (finIntervalo != null) {
            topGrupo.setText("Top del grupo (int.)");
        } else {
            topGrupo.setText("Top del grupo");
        }
        topGrupo.setCallbackData(TOP_GRUPO + "#" + fecha + "#" + finIntervalo);
        return topGrupo;
    }

    /**
     * Generar botón para el top de poles global reunido por grupos
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton rankingPorGrupos(String fecha, String finIntervalo) {
        final InlineKeyboardButton rankingPorGrupos = new InlineKeyboardButton();
        if (finIntervalo != null) {
            rankingPorGrupos.setText("Top por grupos (int.)");
        } else {
            rankingPorGrupos.setText("Top por grupos");
        }
        rankingPorGrupos.setCallbackData(TOP_GLOBAL_GRUPAL + "#" + fecha + "#" + finIntervalo);
        return rankingPorGrupos;
    }

    /**
     * Generar botón para el resumen diario
     * @return InlineKeyboardButton
     */
    private static InlineKeyboardButton resumenDia(String fecha, String finIntervalo) {
        InlineKeyboardButton resumenDia = new InlineKeyboardButton();
        if (finIntervalo != null) {
            resumenDia.setText("Resumen del día (int.)");
        } else {
            resumenDia.setText("Resumen del día");
        }
        resumenDia.setCallbackData(RESUMEN_GRUPO + "#" + fecha + "#" + finIntervalo);
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
        resumenDia.setCallbackData(originalCallback + "#" + fecha.format(DateTimeFormatter.ISO_LOCAL_DATE) + "#null");
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
        resumenDia.setCallbackData(originalCallback + "#" + fecha.format(DateTimeFormatter.ISO_LOCAL_DATE) + "#null");
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
        resumenDia.setCallbackData(originalCallback + "#" + today.format(DateTimeFormatter.ISO_LOCAL_DATE) + "#null");
        return resumenDia;
    }

    /**
     * Generar la fila inferior del teclado, solo mostrar si no es un intervalo (endDate es null)
     * @param date Fecha donde se está viendo las poles
     * @param endDate El fin del intervalo, si existe
     * @param tipo El tipo de mensaje de callback que mandarán los botones
     * @return Lista de botones para poner según el caso
     */
    private static List<InlineKeyboardButton> generarFilaInferior(LocalDate date, LocalDate endDate, String tipo) {
        if (endDate != null) {
            return List.of();
        } else {
            return Stream.of(
                    diaAnterior(date, tipo),
                    diaActual(date, tipo),
                    diaSiguiente(date, tipo)
            ).filter(Objects::nonNull).toList();
        }
    }

    /**
     * Generar teclado para mostrar los tops
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarTops(LocalDate date, LocalDate endDate) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final String fecha = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String finIntervalo = (endDate != null) ? endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Collections.singletonList(topGrupo(fecha, finIntervalo)),
                        Arrays.asList(
                                rankingGlobal(fecha, finIntervalo),
                                rankingPorGrupos(fecha, finIntervalo)
                        ),
                        generarFilaInferior(date, endDate, RESUMEN_GRUPO)
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para volver a mostrar el resumen diario o los tops globales
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getMostrarResumen(LocalDate date, LocalDate endDate) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final String fecha = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String finIntervalo = (endDate != null) ? endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Collections.singletonList(resumenDia(fecha, finIntervalo)),
                        Arrays.asList(
                                rankingGlobal(fecha, finIntervalo),
                                rankingPorGrupos(fecha, finIntervalo)
                        ),
                        generarFilaInferior(date, endDate, TOP_GRUPO)
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global por grupos
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGrupal(LocalDate date, LocalDate endDate) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final String fecha = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String finIntervalo = (endDate != null) ? endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Arrays.asList(
                                resumenDia(fecha, finIntervalo),
                                topGrupo(fecha, finIntervalo)
                        ),
                        Collections.singletonList(rankingPorGrupos(fecha, finIntervalo)),
                        generarFilaInferior(date, endDate, RANKING_GLOBAL)
                )
        );
        return inlineKeyboard;
    }

    /**
     * Generar teclado para mostrar los resumenes del grupo o el top global
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getResumenesYTopGlobal(LocalDate date, LocalDate endDate) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        final String fecha = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        final String finIntervalo = (endDate != null) ? endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
        inlineKeyboard.setInlineKeyboard(
                Arrays.asList(
                        Arrays.asList(
                                resumenDia(fecha, finIntervalo),
                                topGrupo(fecha, finIntervalo)
                        ),
                        Collections.singletonList(rankingGlobal(fecha, finIntervalo)),
                        generarFilaInferior(date, endDate, TOP_GLOBAL_GRUPAL)
                )
        );
        return inlineKeyboard;
    }
}
