package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@Data
@Builder
public class CachedGroup {

    private final Long id;
    private String title;

    @Builder.Default private HashMap<LocalDate, PoleCollection> polesMap = new HashMap<>();

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
