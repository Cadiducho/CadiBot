package com.cadiducho.bot;

import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleManager;
import com.cadiducho.telegrambotapi.TelegramBot;
import lombok.Getter;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class BotServer {

    /**
     * The logger for this server
     */
    public static final Logger logger = Logger.getLogger("Cadibot-Server");

    /**
     * Server / bot version
     */
    public static final String VERSION = "2.1-beta";

    @Getter private static BotServer instance;
    @Getter private final ModuleManager moduleManager;
    @Getter private final CommandManager commandManager;
    private final ConsoleManager consoleManager;
    @Getter private MySQL mysql;
    @Getter private TelegramBot cadibot;

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
        moduleManager = new ModuleManager(instance, "modules");
        consoleManager = new ConsoleManager(instance);
        commandManager = new CommandManager(instance);
    }
    
    private void startServer(CommandLine cmd) {
        logger.info("Servidor arrancado");

        consoleManager.startConsole();
        consoleManager.startFile("logs/log-%D.txt");
        cadibot = new TelegramBot(cmd.getOptionValue("token"));
        cadibot.getUpdatesPoller().setOwnerId(Long.parseLong(cmd.getOptionValue("owner")));
        
        try {
            moduleManager.loadModules();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            logger.warning("Can't load modules!");
            logger.warning(ex.getMessage());
        }
        
        try {
            mysql = new MySQL(instance, 
                    cmd.getOptionValue("database-host"), 
                    cmd.getOptionValue("database-port"), 
                    cmd.getOptionValue("database-name"), 
                    cmd.getOptionValue("database-user"), 
                    cmd.getOptionValue("database-pass"));
            
            mysql.openConnection();
            logger.info("SQL connection established");
        } catch (SQLException ex) {
            logger.warning("Can't connect to database!");
            logger.warning(ex.getMessage());
        }
        UpdatesHandler events = new UpdatesHandler(cadibot, instance);
        cadibot.getUpdatesPoller().setHandler(events);
        
        commandManager.load(); //ToDo: ¿Pasar todos a módulos?


        logger.info("Bot " + VERSION + " iniciado");
    }
    
    public void shutdown() {
        moduleManager.getModules().forEach(Module::onClose);
        try {
            mysql.closeConnection();
        } catch (SQLException ignored) {
        }

        logger.info("Terminando...");
        consoleManager.stop();
        System.exit(0);
    }
}
