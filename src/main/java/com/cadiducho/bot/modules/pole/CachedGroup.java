package com.cadiducho.bot.modules.pole;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class CachedGroup {

    private final Long id;
    private String title;
    private LocalDate lastAdded;

    @Builder.Default private ConcurrentHashMap<LocalDate, PoleCollection> polesMap = new ConcurrentHashMap<>();

    public Optional<PoleCollection> getPolesOfADay(LocalDate day) {
        //poleMap puede ser null si ha sido cargado de un archivo malformado. Corregir en ese caso
        if (polesMap == null) polesMap = new ConcurrentHashMap<>();

        synchronized (this) {
            return Optional.ofNullable(polesMap.getOrDefault(day, null));
        }
    }

    public synchronized void updatePoles(LocalDate day, PoleCollection poles) {
        polesMap.remove(day);
        polesMap.put(day, poles);
    }
}
