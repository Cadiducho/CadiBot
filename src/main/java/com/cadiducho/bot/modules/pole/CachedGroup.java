package com.cadiducho.bot.modules.pole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@Data
@RequiredArgsConstructor
public class CachedGroup {

    private final Long id;
    private final String title;

    private HashMap<LocalDate, PoleCollection> polesMap = new HashMap<>();

    public Optional<PoleCollection> getPolesOfADay(LocalDate day) {
        //poleMap puede ser null si ha sido cargado de un archivo malformado. Corregir en ese caso
        if (polesMap == null) polesMap = new HashMap<>();

        synchronized (this) {
            return Optional.ofNullable(polesMap.getOrDefault(day, null));
        }
    }

    public synchronized void updatePoles(LocalDate day, PoleCollection poles) {
        polesMap.remove(day);
        polesMap.put(day, poles);
    }
}
