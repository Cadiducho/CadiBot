package com.cadiducho.bot.api.module;

import com.cadiducho.bot.BotServer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModuleManager {
    
    @Getter private final List<Module> modules = new ArrayList<>();
    
    private final BotServer server;
    private final String moduleFolderPathName;
    
    private void addLocalModules() {
        
    }
    
    public void loadModules() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        final File moduleFolder = new File(moduleFolderPathName);
        if (Files.notExists(moduleFolder.toPath())) {
            Files.createDirectories(moduleFolder.toPath());
        }

        final File[] files = moduleFolder.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".jar"));
        if (files == null) {
            return;
        }

        final URL[] urls = new URL[files.length];

        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
            //server.debugLog(urls[i].toString());
        }

        final URLClassLoader classLoader = new URLClassLoader(urls);

        for (final File file : files) {
            final JarFile jarFile = new JarFile(file);
            final Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    //server.debugLog(jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6));
                    Class<?> targetClass = classLoader.loadClass(jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6));
                    if (Module.class.isAssignableFrom(targetClass)) {
                        final Module module = (Module) targetClass.newInstance();
                        modules.add(module);
                    }
                }
            }
        }
        
        addLocalModules();

        modules.forEach(Module::onLoad);
    }
    
    /**
     * Obten un modulo por su id
     *
     * @param id la id para buscar
     * @return el modulo, o Optional.empty() si no ha sido encontrado
     */
    public Optional<Module> getModule(String id) {
        for (Module mod : modules) {
            if (mod.getName().equalsIgnoreCase(id)) {
                return Optional.of(mod);
            }
        }
        return Optional.empty();
    }
}
