package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.Getter;
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
    private final Long id;

    /**
     * Nombre del usuario
     */
    private final String name;

    /**
     * Username del usuario. Puede ser null
     */
    private final String username;

    /**
     * Usado para el top por grupos, guardar temporalmente el nombre del grupo de ese registro. Puede ser null
     */
    private final String groupname;

    /**
     * Si el usuario está baneado
     */
    @Getter private final boolean isBanned;

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
