package com.cadiducho.bot.modules.pole;

import lombok.Getter;

public enum PoleType {
    WINNER(1),
    SUBPOLE(2),
    BRONZE(3);

    @Getter private final int id;

    PoleType(int id) {
        this.id = id;
    }
}
