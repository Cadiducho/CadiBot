package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.Setter;

import java.util.Optional;

@Setter
@Builder
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
        return (userId.equals(first) || userId.equals(second) || userId.equals(third));
    }
}
