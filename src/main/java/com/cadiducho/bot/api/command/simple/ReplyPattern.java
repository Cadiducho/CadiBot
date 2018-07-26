package com.cadiducho.bot.api.command.simple;

/**
 * Enum para determinar a quien va a responder el comando.
 * Ejemplo:
 *  Se envía un mensaje 'A' y este mensaje 'A' es respondido con un comando 'B'. Lo que sea que envíe será respondido
 *  a nadie, al original 'B' o al que era respondido 'A'
 */
public enum ReplyPattern {
    TO_NONE,
    TO_ORIGINAL,
    TO_ANSWERED
}
