package com.cadiducho.bot.modules.pole;

import com.cadiducho.bot.BotServer;
import com.cadiducho.bot.MySQL;
import com.cadiducho.bot.modules.pole.util.LocalDateConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@AllArgsConstructor
public class PoleCacheManager {

    private static final BotServer botServer = BotServer.getInstance();
    private final PoleModule module;
    private final HashMap<Long, CachedGroup> cacheMap = new HashMap<>();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateConverter()).create();

    void initializeDirectory() {
        File dataFolder = new File(BotServer.getInstance().getModuleManager().getModulesFolder(), "poles");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
    }

    public synchronized void initializeGroupCache(Long groupId, String title) {
        CachedGroup cachedGroup = null;
        File cacheFile = new File(BotServer.getInstance().getModuleManager().getModulesFolder() + "/poles", groupId.toString() + ".json");

        Type type = new TypeToken<CachedGroup>() {}.getType();
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
                cachedGroup = new CachedGroup(groupId, title);
                try {
                    PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement(
                            "SELECT * FROM `" + PoleModule.TABLA_POLES + "` WHERE "
                                    + "DATE(time)=DATE(CURDATE()) AND "
                                    + "`groupchat`=?"
                                    + " ORDER BY `poleType`");
                    statement.setLong(1, groupId);

                    LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        poles.put(rs.getRow(), rs.getInt("userid"));
                    }
                    if (!poles.isEmpty()) { //solo crear objeto PoleCollection si hay poles de verdad. Rellenar el optional con el objeto vacío repercutirá en /pole fuertemente
                        PoleCollection polesHoy = new PoleCollection(poles.get(1), poles.get(2), poles.get(3));
                        cachedGroup.getPolesMap().put(LocalDate.now(ZoneId.systemDefault()), polesHoy);
                    }
                } catch (SQLException ex) {
                    BotServer.logger.log(Level.WARNING, "No se ha podido cargar las poles en caché del grupo " + groupId, ex);
                }

                try (Writer writer = new FileWriter(cacheFile)) {
                    gson.toJson(cachedGroup, writer);
                }
            } catch (IOException ex) {
                BotServer.logger.log(Level.WARNING, "No se ha podido crear el archivo de caché del grupo " + groupId, ex);
            }
        }

        try {
            if (cachedGroup == null) {
                cachedGroup = gson.fromJson(new FileReader(cacheFile), type);
            }
            cacheMap.remove(cachedGroup.getId());
            cacheMap.put(cachedGroup.getId(), cachedGroup);
        } catch (FileNotFoundException ignore) {
        }
    }

    void loadCachedGroups() {
        for (File f : Objects.requireNonNull(new File(BotServer.getInstance().getModuleManager().getModulesFolder() + "/poles").listFiles())) {
            if (f != null && f.isFile() && f.getName().contains(".json")) {
                try {
                    initializeGroupCache(Long.parseLong(f.getName().replace(".json", "")), null); //el titulo es null porque lo leerá del archivo
                } catch (NumberFormatException ignored) {
                    BotServer.logger.info("El archivo " + f.getName() + " no tiene nombre válido para un grupo en caché y ha sido saltado");
                }
            }
        }

    }

    /**
     * Check if certain group id is cached
     * @param groupId The group ID
     * @return true is the group is cached
     */
    public boolean isCached(Long groupId) {
        return cacheMap.containsKey(groupId);
    }

    /**
     * Returns the cached group of certain group id, if it exist
     * @param groupId The group ID
     * @return Optional with the object of the CachedGroup
     */
    public Optional<CachedGroup> getCachedGroup(Long groupId) {
        return Optional.ofNullable(cacheMap.getOrDefault(groupId, null));
    }

    /**
     * Guardar el caché de un grupo en su archivo
     * @param cachedGroup grupo a guardar
     */
    public void writeFile(CachedGroup cachedGroup) {
        File cacheFile = new File(BotServer.getInstance().getModuleManager().getModulesFolder() + "/poles", cachedGroup.getId().toString() + ".json");
        try (Writer writer = new FileWriter(cacheFile)) {
            gson.toJson(cachedGroup, writer);
        } catch (IOException ignored) { }
    }

    public void clearOldCache(CachedGroup group, LocalDate today) {
        for (LocalDate poleDate : group.getPolesMap().keySet()) {
            if (poleDate.plusDays(3).isBefore(today)) { //si han pasado 3 días se borra ese cache
                group.getPolesMap().remove(poleDate);
            }
        }
    }
}
