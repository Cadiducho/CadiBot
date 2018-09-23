package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class PoleUser {

    private Integer id;
    private String name;
    private String username;

    /**
     * Obtener el nombre si está disponible
     * @return Nombre del usuario
     */
    public String getName() {
        return name != null ? name : "/WTF (Usuario desconocido)";
    }
}
