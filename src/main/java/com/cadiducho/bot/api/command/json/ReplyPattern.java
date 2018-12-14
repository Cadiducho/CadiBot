package com.cadiducho.bot.api.command.json;

import com.squareup.moshi.Json;

/**
 * Enum para determinar a quien va a responder el comando.
 * Ejemplo:
 *  Se envía un mensaje 'A' y este mensaje 'A' es respondido con un comando 'B'. Lo que sea que envíe será respondido
 *  a nadie, al original 'B' o al que era respondido 'A'
 */
public enum ReplyPattern {
    @Json(name = "none") TO_NONE,
    @Json(name = "original") TO_ORIGINAL,
    @Json(name = "answered") TO_ANSWERED
}
