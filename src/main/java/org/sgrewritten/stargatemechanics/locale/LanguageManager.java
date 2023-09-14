package org.sgrewritten.stargatemechanics.locale;

import net.md_5.bungee.api.ChatColor;
import org.sgrewritten.stargatemechanics.StargateMechanics;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

public class LanguageManager {

    private final Properties defaultLocale;
    private final File pluginFolder;
    private final StargateMechanics plugin;
    private Properties properties;

    private ChatColor prefixColor = ChatColor.of("#46634e");

    public LanguageManager(StargateMechanics plugin, String language) throws IOException {
        this.properties = new Properties();
        this.defaultLocale = new Properties();
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
        this.load(language);
    }

    public void load(String language) throws IOException {

        properties.clear();
        defaultLocale.clear();
        File localeFolder = new File(pluginFolder, "locale");
        if(!localeFolder.exists() && !localeFolder.mkdir()){
            StargateMechanics.log(Level.WARNING, "Could not make locale directory");
        }
        File languageFile = new File(localeFolder,language + ".txt");
        if(!languageFile.exists()){
            String internalPath = "locale/" + language + ".txt";
            StargateMechanics.log(Level.INFO, internalPath);
            plugin.saveResource(internalPath,false);
        }
        try(InputStream stream = new FileInputStream(languageFile)) {
            properties.load(stream);
        }
        try(InputStream stream = StargateMechanics.class.getResourceAsStream("/locale/en.txt")){
            defaultLocale.load(stream);
        }
    }

    public String getLocalizedString(LocalizedMessageType msgType){
        String localizedMsg = properties.getProperty(msgType.getKey());
        if(localizedMsg == null){
            localizedMsg = defaultLocale.getProperty(msgType.getKey());
        }
        return localizedMsg;
    }

    public String getLocalizedMsg(LocalizedMessageType msgType){
        return prefixColor + getLocalizedString(LocalizedMessageType.PREFIX) + " " + ChatColor.RESET + getLocalizedString(msgType);
    }
}
