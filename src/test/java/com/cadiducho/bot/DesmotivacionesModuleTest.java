package com.cadiducho.bot;

import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DesmotivacionesModuleTest {

    @Test
    void getAPost() {
        int expectedNulls = 0;
        for (int i = 0; i < 100; i++) {
            String cartel = DesmotivacionesModule.getAPost();
            if (cartel == null) {
                expectedNulls++;
            }
        }
        assertEquals(0, expectedNulls);
    }
}