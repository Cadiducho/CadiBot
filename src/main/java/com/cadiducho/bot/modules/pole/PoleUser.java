package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.ToString;

import java.util.Optional;

/**
 * Clase para representar un registro de pole en la base de datos
 */
@Builder
@ToString
public class PoleUser {

    /**
     * ID del usuario que realizó la pole
     */
    private Integer id;

    /**
     * Nombre del usuario
     */
    private String name;

    /**
     * Username del usuario. Puede ser null
     */
    private String username;

    /**
     * Usado para el top por grupos, guardar temporalmente el nombre del grupo de ese registro. Puede ser null
     */
    private String groupname;

    /**
     * Obtener el nombre si está disponible
     * @return Nombre del usuario
     */
    public String getName() {
        return name != null ? name : "/WTF (Usuario desconocido)";
    }
    public Optional<String> groupname() {
        return Optional.ofNullable(groupname);
    }
}
