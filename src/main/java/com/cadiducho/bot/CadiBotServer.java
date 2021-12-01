package com.cadiducho.bot;

import com.cadiducho.bot.database.MySQLDatabase;
import com.cadiducho.bot.modules.animales.AnimalesModule;
import com.cadiducho.bot.modules.core.CoreModule;
import com.cadiducho.bot.modules.insultos.InsultosModule;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.modules.json.JsonModule;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.cli.*;

import java.sql.SQLException;

@Log
public class CadiBotServer {

    /**
     * Server / bot version
     */
    public static final String VERSION = "3.2";

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
        Options options = new Options();

        Option token = new Option("t", "token", true, "telegram bot token");
        Option dbh = new Option("dbh", "database-host", true, "database host");
        Option dbp = new Option("dbp", "database-port", true, "database port");
        Option dbn = new Option("dbn", "database-name", true, "database name");
        Option dbu = new Option("dbu", "database-user", true, "database user");
        Option dbpa = new Option("dbpa", "database-pass", true, "database passphrase");
        Option owner = new Option("o", "owner", true, "ID of the owner");
        
        token.setRequired(true);
        dbh.setRequired(true);
        dbp.setRequired(true);
        dbn.setRequired(true);  
        dbu.setRequired(true);
        dbpa.setRequired(true);
        owner.setRequired(true);
        
        options.addOption(token);
        options.addOption(dbh);
        options.addOption(dbp);
        options.addOption(dbn);
        options.addOption(dbu);
        options.addOption(dbpa);
        options.addOption(owner);
        
        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("cadibot", options);

            System.exit(1);
            return;
        }

        CadiBotServer bot = new CadiBotServer();
        bot.initCadibot(commandLine);
    }
    
    private CadiBotServer() {
        instance = this;
    }
    
    private void initCadibot(CommandLine cmd) {
        this.ownerId = Long.parseLong(cmd.getOptionValue("owner"));

        try {
            database = new MySQLDatabase(instance,
                    cmd.getOptionValue("database-host"),
                    cmd.getOptionValue("database-port"),
                    cmd.getOptionValue("database-name"),
                    cmd.getOptionValue("database-user"),
                    cmd.getOptionValue("database-pass"));

            System.out.println("SQL connection established");
        } catch (SQLException ex) {
            System.err.println("Can't connect to database!");
            System.err.println(ex.getMessage());
        }

        this.cadibot = new ZinciteBot(cmd.getOptionValue("token"), ownerId, VERSION);

        cadibot.getModuleManager().registerModule(new CoreModule());
        cadibot.getModuleManager().registerModule(new JsonModule());
        cadibot.getModuleManager().registerModule(new PoleModule());
        cadibot.getModuleManager().registerModule(new InsultosModule());
        cadibot.getModuleManager().registerModule(new AnimalesModule());

        cadibot.startServer();
    }
    
    public void shutdown() {
        database.closeDatabase();
        cadibot.shutdown();
    }
}
