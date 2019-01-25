package com.cadiducho.bot;

import com.cadiducho.bot.modules.pole.CachedGroup;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleCollection;
import com.cadiducho.bot.modules.pole.util.PoleAntiCheat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PoleTest {

    private PoleCacheManager manager;
    private PoleAntiCheat antiCheat;

    @BeforeEach
    public void setup() {
        manager = new PoleCacheManager(null);
        antiCheat = new PoleAntiCheat(null);
    }

    @Test
    @DisplayName("Estructura básica de poles en caché")
    public void testSimplePoles() {
        Long groupId = 1000L;
        Integer firstUserId = 1;
        Integer secondUserId = 2;
        Integer thirdUserId = 3;
        LocalDateTime today = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        manager.initializeGroupCache(groupId, "Chat test", new LinkedHashMap<>(), today.toLocalDate().minusDays(1));

        CachedGroup cachedGroup = manager.getCachedGroup(groupId).get();
        Optional<PoleCollection> poles = cachedGroup.getPolesOfADay(today.toLocalDate());
        assertTrue(!poles.isPresent());

        PoleCollection polesHoy = PoleCollection.builder().first(firstUserId).build();
        cachedGroup.updatePoles(today.toLocalDate(), polesHoy);
        manager.clearOldCache(cachedGroup, today.toLocalDate());
        poles = cachedGroup.getPolesOfADay(today.toLocalDate());
        assertTrue(poles.get().getFirst().isPresent());
        assertEquals(poles.get().getFirst().get(), firstUserId);

        poles.get().setSecond(secondUserId);
        cachedGroup.updatePoles(today.toLocalDate(), polesHoy);
        manager.clearOldCache(cachedGroup, today.toLocalDate());
        poles = cachedGroup.getPolesOfADay(today.toLocalDate());
        assertTrue(poles.get().getSecond().isPresent());
        assertEquals(poles.get().getSecond().get(), secondUserId);

        poles.get().setThird(thirdUserId);
        cachedGroup.updatePoles(today.toLocalDate(), polesHoy);
        manager.clearOldCache(cachedGroup, today.toLocalDate());
        poles = cachedGroup.getPolesOfADay(today.toLocalDate());
        assertTrue(poles.get().getThird().isPresent());
        assertEquals(poles.get().getThird().get(), thirdUserId);
    }

    @Test
    @DisplayName("Cambio de mes")
    public void testNewMonth() {
        Long groupId = 1000L;
        
        LocalDate treintaSeptiembre = LocalDate.of(2018, 9, 30);
        LocalDate unoOctubre = LocalDate.of(2018, 10, 1);
        LocalDate dosOctubre = LocalDate.of(2018, 10, 2);

        manager.initializeGroupCache(groupId, "Chat test", new LinkedHashMap<>(), treintaSeptiembre.minusDays(1));

        CachedGroup cachedGroup = manager.getCachedGroup(groupId).get();
        PoleCollection polesTreinta = PoleCollection.builder().first(1).build();
        cachedGroup.updatePoles(treintaSeptiembre, polesTreinta);
        manager.clearOldCache(cachedGroup, treintaSeptiembre);
        final Optional<PoleCollection> polesA = cachedGroup.getPolesOfADay(treintaSeptiembre);
        assertAll(() -> {
            assertEquals(polesA.get().getFirst().get(), (Integer) 1);
            assertFalse(polesA.get().getSecond().isPresent());
            assertFalse(polesA.get().getThird().isPresent());
        });

        cachedGroup = manager.getCachedGroup(groupId).get();
        PoleCollection polesUno = PoleCollection.builder().first(58).second(489).build();
        cachedGroup.updatePoles(unoOctubre, polesUno);
        manager.clearOldCache(cachedGroup, unoOctubre);
        final Optional<PoleCollection> polesB = cachedGroup.getPolesOfADay(unoOctubre);

        assertAll(() -> {
            assertEquals(polesB.get().getFirst().get(), (Integer) 58);
            assertEquals(polesB.get().getSecond().get(), (Integer) 489);
            assertFalse(polesB.get().getThird().isPresent());
        });

        cachedGroup = manager.getCachedGroup(groupId).get();
        PoleCollection polesDos = PoleCollection.builder().first(586).second(858).third(7896).build();
        cachedGroup.updatePoles(dosOctubre, polesDos);
        manager.clearOldCache(cachedGroup, dosOctubre);
        final Optional<PoleCollection> polesC = cachedGroup.getPolesOfADay(dosOctubre);

        assertAll(() -> {
            assertEquals(polesC.get().getFirst().get(), (Integer) 586);
            assertEquals(polesC.get().getSecond().get(), (Integer) 858);
            assertEquals(polesC.get().getThird().get(), (Integer) 7896);
        });
    }

    @Test
    @DisplayName("No hacer poles justo al añadir al bot")
    public void testNoHacerPolesElDiaDeInstalacion() {
        Long groupId = 1000L;

        manager.initializeGroupCache(groupId, "Chat test", new LinkedHashMap<>(), LocalDate.now().minusDays(1));

        CachedGroup cachedGroup = manager.getCachedGroup(groupId).get();
        assertNotEquals(LocalDate.now(), cachedGroup.getLastAdded());
    }

    @Test
    @DisplayName("Antiflood")
    public void antiFloodTest() {
        Integer userid = 1;
        Long groupOne = 1L;
        Long groupTwo = 2L;
        assertFalse(antiCheat.isFlooding(userid, groupOne)); //la primera vez debe dar falso
        antiCheat.isFlooding(userid, groupOne);
        antiCheat.isFlooding(userid, groupOne);
        antiCheat.isFlooding(userid, groupOne);
        antiCheat.isFlooding(userid, groupOne);
        assertTrue(antiCheat.isFlooding(userid, groupOne)); // tras 4 intentos seguidos, falta el antiflood

        assertFalse(antiCheat.isFlooding(userid, groupTwo)); // en otro grupo no cuenta como flood
    }

    @Test
    @DisplayName("Anticheat - Antibot")
    public void testAntibot() {
        List<LocalDateTime> timestamps = Arrays.asList(
                Timestamp.valueOf("2019-01-25 00:00:00.021").toLocalDateTime(),
                Timestamp.valueOf("2019-01-24 00:00:00.011").toLocalDateTime(),
                Timestamp.valueOf("2019-01-22 00:00:00.281").toLocalDateTime(),
                Timestamp.valueOf("2019-01-21 00:00:00.481").toLocalDateTime()
        );
        // Falso -> Tiempos relativamente sospechosos, pero no ha sido todos los días seguidos
        assertFalse(antiCheat.checkSuspiciousBehaviour(timestamps));

        timestamps = Arrays.asList(
                Timestamp.valueOf("2019-01-25 00:00:01.021").toLocalDateTime(),
                Timestamp.valueOf("2019-01-24 00:00:01.011").toLocalDateTime(),
                Timestamp.valueOf("2019-01-23 00:00:00.521").toLocalDateTime(),
                Timestamp.valueOf("2019-01-22 00:00:04.281").toLocalDateTime(),
                Timestamp.valueOf("2019-01-21 00:00:05.481").toLocalDateTime()
        );
        // Falso -> Tiempos "normales" con segundos variados que superan la media de 2 segundos
        assertFalse(antiCheat.checkSuspiciousBehaviour(timestamps));

        timestamps = Arrays.asList(
                Timestamp.valueOf("2019-01-25 00:00:00.2892").toLocalDateTime(),
                Timestamp.valueOf("2019-01-24 00:00:00.3512").toLocalDateTime(),
                Timestamp.valueOf("2019-01-23 00:00:00.3894").toLocalDateTime(),
                Timestamp.valueOf("2019-01-22 00:00:00.3832").toLocalDateTime(),
                Timestamp.valueOf("2019-01-21 00:00:00.3934").toLocalDateTime()
        );
        // Verdadero -> Días seguidos, en el mismo segundo y con milesimas de diferencias
        assertTrue(antiCheat.checkSuspiciousBehaviour(timestamps));
    }
}
