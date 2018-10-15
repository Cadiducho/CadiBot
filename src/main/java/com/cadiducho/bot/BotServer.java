package com.cadiducho.bot;

import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleManager;
import com.cadiducho.telegrambotapi.TelegramBot;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Log
public class BotServer {

    /**
     * Server / bot version
     */
    public static final String VERSION = "2.10-beta";

    /**
     * The Module manager
     */
    @Getter private final ModuleManager moduleManager;

    /**
     * The (Telegram) Command manager
     */
    @Getter private final CommandManager commandManager;

    /**
     * The Console manager
     */
    private final ConsoleManager consoleManager;

    /**
     * The database (MySQL) connector
     */
    @Getter private MySQL mysql;

    @Getter private TelegramBot cadibot;
    @Getter private static BotServer instance;

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
        
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("cadibot", options);

            System.exit(1);
            return;
        }
        BotServer bot = new BotServer();
        bot.startServer(cmd);
    }
    
    private BotServer() {
        instance = this;
        consoleManager = new ConsoleManager(instance);
        moduleManager = new ModuleManager(instance, new File("modules"));
        commandManager = new CommandManager(instance);
    }
    
    private void startServer(CommandLine cmd) {
        log.info("Servidor arrancado");

        consoleManager.startConsole();
        consoleManager.startFile("logs/log-%D.txt");
        cadibot = new TelegramBot(cmd.getOptionValue("token"));
        cadibot.getUpdatesPoller().setOwnerId(Long.parseLong(cmd.getOptionValue("owner")));

        try {
            mysql = new MySQL(instance,
                    cmd.getOptionValue("database-host"),
                    cmd.getOptionValue("database-port"),
                    cmd.getOptionValue("database-name"),
                    cmd.getOptionValue("database-user"),
                    cmd.getOptionValue("database-pass"));

            mysql.openConnection();
            log.info("SQL connection established");
        } catch (SQLException ex) {
            log.warning("Can't connect to database!");
            log.warning(ex.getMessage());
        }

        try {
            moduleManager.loadModules();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            log.warning("Can't load modules!");
            log.warning(ex.getMessage());
        }

        UpdatesHandler events = new UpdatesHandler(cadibot, instance);
        cadibot.getUpdatesPoller().setHandler(events);


        log.info("Bot " + VERSION + " iniciado completamente");
    }
    
    public void shutdown() {
        moduleManager.getModules().forEach(Module::onClose);
        try {
            mysql.closeConnection();
        } catch (SQLException ignored) {
        }

        log.info("Terminando...");
        consoleManager.stop();
        System.exit(0);
    }
}
