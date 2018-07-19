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

    private Integer first, second, third;

    public Optional<Integer> getFirst() {
        return Optional.ofNullable(first);
    }

    public Optional<Integer> getSecond() {
        return Optional.ofNullable(second);
    }

    public Optional<Integer> getThird() {
        return Optional.ofNullable(third);
    }

    public boolean contains(Integer userId) {
        return (Objects.equals(userId, first) || Objects.equals(userId, second) || Objects.equals(userId, third));
    }
}
