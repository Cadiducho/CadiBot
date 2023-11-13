package com.cadiducho.bot;

import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DesmotivacionesModuleTest {

    @Test
    void getAPost() {
        int expectedNulls = 0;
        for (int i = 0; i < 1000; i++) {
            Optional<String> cartel = DesmotivacionesModule.getAPost();
            if (cartel.isEmpty()) {
                expectedNulls++;
            }else {
                assertTrue(cartel.get().contains("jpg"));
            }
        }
        assertEquals(0, expectedNulls);
    }
}