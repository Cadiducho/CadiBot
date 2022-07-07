package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

@Setter
@Builder
@ToString
public class PoleCollection {

    /**
     * IDs del usuario primero, segundo y tercero
     */
    private Long first, second, third;

    /**
     * ID del primero en la colección de poles
     * @return ID de la primera posición, o un opcional vacío
     */
    public Optional<Long> getFirst() {
        return Optional.ofNullable(first);
    }

    /**
     * ID del segundo en la colección de poles
     * @return ID de la segunda posición, o un opcional vacío
     */
    public Optional<Long> getSecond() {
        return Optional.ofNullable(second);
    }

    /**
     * ID del tercero en la colección de poles
     * @return ID de la tercera posición, o un opcional vacío
     */
    public Optional<Long> getThird() {
        return Optional.ofNullable(third);
    }

    /**
     * Comprobar si la colección de poles contiene a un usuario concreto
     * @param userId La id del usuario a comprobar
     * @return Verdadero si la colección contiene a ese usuario
     */
    public boolean contains(Long userId) {
        return (Objects.equals(userId, first) || Objects.equals(userId, second) || Objects.equals(userId, third));
    }
}
