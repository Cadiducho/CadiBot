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

    /**
     * Crear directorio del caché de poles si este no existe
     */
    void initializeDirectory() {
        File dataFolder = new File(BotServer.getInstance().getModuleManager().getModulesFolder(), "poles");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
    }

    /**
     * Inicializar caché de un grupo al realizar una pole o para agilizarla después
     * synchronized porque dos personajes pueden realizar una pole *simultáneamente* y el caché del grupo se duplicaría
     * @param groupId
     * @param title
     */
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
                            "SELECT `userid`, `poleType` FROM `" + PoleModule.TABLA_POLES + "` WHERE "
                                    + "DATE(time)=DATE(CURDATE()) AND "
                                    + "`groupchat`=?"
                                    + " ORDER BY `poleType`");
                    statement.setLong(1, groupId);

                    LinkedHashMap<Integer, Integer> poles = new LinkedHashMap<>();
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        poles.put(rs.getInt("poleType"), rs.getInt("userid"));
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

    /**
     * Comprobar todos los archivos dentro del directorio del caché y cargarlos en memoria si este archivo es válido
     */
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

    /**
     * Eliminar caché pasado 3 días para no hacer enormes los archivos y a la vez no sobrecargar la ram
     * @param group Grupo a limpiar
     * @param today Día para contar 3 días hacia atrás
     */
    public void clearOldCache(CachedGroup group, LocalDate today) {
        for (LocalDate poleDate : group.getPolesMap().keySet()) {
            if (poleDate.plusDays(3).isBefore(today)) { //si han pasado 3 días se borra ese cache
                group.getPolesMap().remove(poleDate);
            }
        }
    }

    /**
     * Insertar una pole en la base de datos. Se recomienda usar asíncronamente
     * @param group Grupo donde se realizó la pole
     * @param poles Conjunto de poles de ese día
     * @param updated La posición que se ha actualizado
     */
    @SuppressWarnings("ConstantConditions")
    public void saveToDatabase(CachedGroup group, PoleCollection poles, int updated) {
        try {
            Integer userid;
            switch (updated) {
                case 1:
                    userid = poles.getFirst().get();
                    break;
                case 2:
                    userid = poles.getSecond().get();
                    break;
                default:
                    userid = poles.getThird().get();
                    break;
            }
            botServer.getMysql().updateUsername(userid, group.getId());
            botServer.getMysql().updateGroup(group.getId(), group.getTitle());

            PreparedStatement insert = botServer.getMysql().openConnection().prepareStatement("INSERT INTO `" + PoleModule.TABLA_POLES + "` (`userid`, `groupchat`, `poleType`) VALUES (?, ?, ?)");

            insert.setInt(1, userid);
            insert.setLong(2, group.getId());
            insert.setInt(3, updated);
            insert.executeUpdate();
        } catch (SQLException ex) {
            BotServer.logger.severe(ex.getMessage());
        }
    }

    /**
     * Obtener un nombre de usuario a partir de su ID desde la base de datos
     *  La API de Telegram nos permite obtener este dato, pero sólo si el usuario se encuentra en el grupo.
     *  Si el usuario dejó el grupo, la API nos daría una excepción y no se podría generar la lista de poles
     * @param id ID del usuario
     * @return Nombre del usuario
     * @throws SQLException Excepción de la base de datos
     */
    public String getUsername(int id) throws SQLException {
        PreparedStatement statement = botServer.getMysql().openConnection().prepareStatement("SELECT `name` FROM `" + MySQL.TABLE_USERS + "` WHERE `userid`=?");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        return "/WTF (Usuario desconocido)";
    }
}
