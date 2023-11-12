package com.cadiducho.bot;

import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DesmotivacionesModuleTest {

    @Test
    void getAPost() {
        int expectedNulls = 0;
        for (int i = 0; i < 1000; i++) {
            String cartel = DesmotivacionesModule.getAPost();
            if (cartel == null) {
                expectedNulls++;
            }else {
                assertTrue(cartel.contains("jpg"));
            }
        }
        assertEquals(0, expectedNulls);
    }
}