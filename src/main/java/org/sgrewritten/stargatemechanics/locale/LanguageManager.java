package org.sgrewritten.stargatemechanics.locale;

import org.sgrewritten.stargatemechanics.StargateMechanics;

import java.io.*;
import java.util.Properties;

public class LanguageManager {

    private final Properties defaultLocale;
    private final File pluginFolder;
    private Properties properties;

    public LanguageManager(File pluginFolder, String language) throws IOException {
        this.properties = new Properties();
        this.defaultLocale = new Properties();
        this.pluginFolder = pluginFolder;
        this.load(language);
    }

    public void load(String language) throws IOException {
        properties.clear();
        defaultLocale.clear();
        try(InputStream stream = new FileInputStream(new File(pluginFolder,"locale/" + language + ".txt"))) {
            properties.load(stream);
        }
        try(InputStream stream = StargateMechanics.class.getResourceAsStream("/locale/en.txt")){
            defaultLocale.load(stream);
        }
    }

    public String getLocalizedMsg(LocalizedMessageType msgType){
        String localizedMsg = properties.getProperty(msgType.getKey());
        if(localizedMsg == null){
            localizedMsg = defaultLocale.getProperty(msgType.getKey());
        }
        return localizedMsg;
    }
}
