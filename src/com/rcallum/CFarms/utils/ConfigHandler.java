package com.rcallum.CFarms.utils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.codec.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rcallum.CFarms.CandyFarms;

public class ConfigHandler {
	public String filename;
    public File configfile;
    private FileConfiguration config;
    private CandyFarms plugin;
    
    public ConfigHandler(String filename, CandyFarms plugin){
        this.plugin = plugin;
        this.filename = filename;
        makeConfig();
    }
    
    public void makeConfig(){
        configfile = new File(this.plugin.getDataFolder(),filename);

        if(!configfile.exists()){
            configfile.getParentFile().mkdirs();
            plugin.saveResource(filename,false);
        }

        config = new YamlConfiguration();
        try{
            config.load(configfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void reloadConfig() {
    	configfile = new File(this.plugin.getDataFolder(),filename);
    	config = new YamlConfiguration();
    	try{
            config.load(configfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return this.config;
    }

    public void saveConfig(){
    	config = YamlConfiguration.loadConfiguration(configfile);
    	InputStream defIMessagesStream = this.plugin.getResource(filename);
    	if (defIMessagesStream != null) {
    		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defIMessagesStream, Charsets.UTF_8)));
        }
    }
    public void saveData(){
        try{
            config.save(configfile);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
