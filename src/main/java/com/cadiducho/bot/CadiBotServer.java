package com.cadiducho.bot;

import com.cadiducho.bot.database.MySQLDatabase;
import com.cadiducho.bot.modules.animales.AnimalesModule;
import com.cadiducho.bot.modules.core.CoreModule;
import com.cadiducho.bot.modules.desmotivaciones.DesmotivacionesModule;
import com.cadiducho.bot.modules.insultos.InsultosModule;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.purabulla.PuraBullaModule;
import com.cadiducho.bot.modules.treintaytres.TreintaYTres;
import com.cadiducho.bot.modules.refranero.Refranero;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.ZinciteConfig;
import com.cadiducho.zincite.modules.json.JsonModule;
import lombok.Getter;
import lombok.extern.java.Log;

import java.sql.SQLException;

@Log
public class CadiBotServer {

    /**
     * Server / bot version
     */
    public static final String VERSION = "3.7";

    /**
     * The database (MySQL)
     */
    @Getter private MySQLDatabase database;

    /**
     * The owner Telegram ID, if is set
     */
    @Getter private Long ownerId;

    @Getter private ZinciteBot cadibot;
    @Getter private static CadiBotServer instance;

    public static void main(String[] args) {
        CadiBotServer bot = new CadiBotServer();
        bot.initCadibot();
    }
    
    private CadiBotServer() {
        instance = this;
    }
    
    private void initCadibot() {
        String token = System.getenv("BOT_TOKEN");
        String dbHost = System.getenv("DATABASE_HOST");
        String dbPort = System.getenv("DATABASE_PORT");
        String dbName = System.getenv("DATABASE_NAME");
        String dbUser = System.getenv("DATABASE_USER");
        String dbPass = System.getenv("DATABASE_PASS");
        String ownerIdStr = System.getenv("OWNER_ID");

        if (token == null || dbHost == null || dbPort == null || dbName == null ||
                dbUser == null || dbPass == null || ownerIdStr == null) {
            System.err.println("Error: Faltan variables de entorno requeridas");
            System.exit(1);
            return;
        }

        this.ownerId = Long.parseLong(ownerIdStr);

        try {
            database = new MySQLDatabase(instance, dbHost, dbPort, dbName, dbUser, dbPass);
            System.out.println("SQL connection established");
        } catch (SQLException ex) {
            System.err.println("Can't connect to database!");
            System.err.println(ex.getMessage());
        }

        ZinciteConfig config = ZinciteConfig.builder()
                .token(token)
                .ownerId(ownerId)
                .version(VERSION)
                .build();
        this.cadibot = new ZinciteBot(config);

        cadibot.getModuleManager().registerModule(new CoreModule());
        cadibot.getModuleManager().registerModule(new JsonModule());
        cadibot.getModuleManager().registerModule(new PoleModule());
        cadibot.getModuleManager().registerModule(new InsultosModule());
        cadibot.getModuleManager().registerModule(new AnimalesModule());
        cadibot.getModuleManager().registerModule(new DesmotivacionesModule());
        cadibot.getModuleManager().registerModule(new TreintaYTres());
        cadibot.getModuleManager().registerModule(new PuraBullaModule());
        cadibot.getModuleManager().registerModule(new Refranero());

        cadibot.startServer();
    }
    
    public void shutdown() {
        database.closeDatabase();
        cadibot.shutdown();
    }
}
