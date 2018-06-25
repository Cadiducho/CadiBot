package com.cadiducho.bot;

import com.cadiducho.bot.api.command.CommandManager;
import com.cadiducho.bot.api.module.Module;
import com.cadiducho.bot.api.module.ModuleManager;
import com.cadiducho.telegrambotapi.TelegramBot;
import lombok.Getter;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class BotServer {

    public static final String VERSION = "2.0";
    
    @Getter static boolean running = true;
    private static final Scanner in = new Scanner(System.in);

    @Getter private static BotServer instance;
    @Getter private final ModuleManager moduleManager;
    @Getter private final CommandManager commandManager;
    @Getter private MySQL mysql;
    @Getter private TelegramBot cadibot;
    
    public static final SimpleDateFormat fulltime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

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
    
    public BotServer() {
        instance = this;
        moduleManager = new ModuleManager(instance, "modules");
        commandManager = new CommandManager(instance);
    }
    
    private void startServer(CommandLine cmd) {
        System.out.println("Servidor arrancado");
        
        cadibot = new TelegramBot(cmd.getOptionValue("token"));
        cadibot.getUpdatesPoller().setOwnerId(Long.parseLong(cmd.getOptionValue("owner")));
        
        try {
            moduleManager.loadModules();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            System.out.println("Can't load modules!");
            System.out.println(ex.getMessage());
        }
        
        try {
            mysql = new MySQL(instance, 
                    cmd.getOptionValue("database-host"), 
                    cmd.getOptionValue("database-port"), 
                    cmd.getOptionValue("database-name"), 
                    cmd.getOptionValue("database-user"), 
                    cmd.getOptionValue("database-pass"));
            
            mysql.openConnection();
            System.out.println("SQL connection established");
        } catch (SQLException ex) {
            System.out.println("Can't connect to database!");
            System.out.println(ex.getMessage());
        }
        UpdatesHandler events = new UpdatesHandler(cadibot, instance);
        cadibot.getUpdatesPoller().setHandler(events);
        
        commandManager.load(); //ToDo: ¿Pasar todos a módulos?
        

        System.out.println("Bot " + VERSION + " iniciado");
        
        while (isRunning()) {
            System.out.println("Elige una opción numérica:"
                + "\n- stop Salir"
                + "\n- ping. Ping"
            );
            String respuesta = in.next();
            switch (respuesta) {
                case "stop":
                    running = false;
                    break;
                case "ping":
                    System.out.println("Estoy vivo");
                    break;
                case "version":
                    System.out.println("Ejecutando versión " + VERSION);
                    break;
                default: 
                    System.out.println("Opción no válida.\n");
            }
        }
        close();
    }
    
    private void close() {
        moduleManager.getModules().forEach(Module::onClose);
        try {
            mysql.closeConnection();
        } catch (SQLException ignored) {
        }

        System.out.println("Terminando...");
        System.exit(0);
    }
}
