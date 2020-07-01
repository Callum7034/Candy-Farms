package com.rcallum.CFarms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.rcallum.CFarms.Commands.CommandHandler;
import com.rcallum.CFarms.Commands.ListSeeds;
import com.rcallum.CFarms.FarmManager.MinionManager;
import com.rcallum.CFarms.FastGrowing.GrowAll;
import com.rcallum.CFarms.GUIListeners.MinionGUI;
import com.rcallum.CFarms.GUIListeners.StorageGUI;
import com.rcallum.CFarms.GUIListeners.farmGUI;
import com.rcallum.CFarms.GUIListeners.pendingGUI;
import com.rcallum.CFarms.GUIListeners.tokenShopGUI;
import com.rcallum.CFarms.Listener.ChatListener;
import com.rcallum.CFarms.Listener.ClickFarm;
import com.rcallum.CFarms.Listener.EatCandy;
import com.rcallum.CFarms.Listener.FarmerMinion;
import com.rcallum.CFarms.Listener.NoBreakFarm;
import com.rcallum.CFarms.Listener.PlaceFarm;
import com.rcallum.CFarms.Listener.PlantSeed;
import com.rcallum.CFarms.items.MinionItem;
import com.rcallum.CFarms.items.SeedBox;
import com.rcallum.CFarms.items.SeedMaker;
import com.rcallum.CFarms.utils.ConfigHandler;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class CandyFarms extends JavaPlugin {
	public static WorldGuardPlugin wg;
	
	public ConfigHandler messagesFile;
    public ConfigHandler tshopFile;
    public static FileConfiguration messages;
    public static FileConfiguration config;
    public static FileConfiguration tshop;
    
	private static CandyFarms instance;

	public CandyFarms() {
		instance = this;
	}

	public static CandyFarms getInstance() {
		if (instance == null) {
			instance = new CandyFarms();
		}
		return instance;
	}
	public void onEnable() {
		wg = WorldGuardPlugin.inst();
		
		saveDefaultConfig();
		
		config = getConfig();
		
		messagesFile = new ConfigHandler("messages.yml",this);
		messagesFile.getConfig().options().copyDefaults(true);
		messagesFile.saveConfig();
		messages = messagesFile.getConfig();
		
		tshopFile = new ConfigHandler("TokenShop.yml",this);
		tshopFile.getConfig().options().copyDefaults(true);
		tshopFile.saveConfig();
		tshop = tshopFile.getConfig();
		
		
		Bukkit.getPluginCommand("CFarms").setExecutor(new CommandHandler());
		Bukkit.getPluginCommand("drugshop").setExecutor(new CommandHandler());
		
		getServer().getPluginManager().registerEvents(new PlaceFarm(), this);
		getServer().getPluginManager().registerEvents(new ClickFarm(), this);
		getServer().getPluginManager().registerEvents(new pendingGUI(), this);
		getServer().getPluginManager().registerEvents(new NoBreakFarm(), this);
		getServer().getPluginManager().registerEvents(new farmGUI(), this);
		getServer().getPluginManager().registerEvents(new ListSeeds(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlantSeed(), this);
		getServer().getPluginManager().registerEvents(new EatCandy(), this);
		getServer().getPluginManager().registerEvents(new tokenShopGUI(), this);
		getServer().getPluginManager().registerEvents(new FarmerMinion(), this);
		getServer().getPluginManager().registerEvents(new StorageGUI(), this);
		getServer().getPluginManager().registerEvents(new MinionGUI(), this);
		
		SeedMaker.getInstance().loadSeeds();
		SeedBox.getInstance().loadSeeds();
		MinionItem.getInstance().loadMinionItem();
		
		long delay = config.getLong("growDelay");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				GrowAll.getInstance().doAll();
				MinionManager.getInstance().runMinionCheck();
			}
			
		}, 0L, delay);
	}
	
	private FileConfiguration data = null;
	private File dataFile = null;
	
	public void reloadData() {
		if (dataFile == null) {
			dataFile = new File(getDataFolder(), "data.yml");
			dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
		}
		data = YamlConfiguration.loadConfiguration(dataFile);

		InputStream defConfigStream = getResource("data.yml");
		if (defConfigStream == null) {
			Bukkit.getConsoleSender().sendMessage("Error Loading data.yml");
		}
	}
	public FileConfiguration getData() {
		if (data == null) {
			reloadData();
		}
		return data;
	}
	public void saveData() {
		if ((this.data == null) || (this.dataFile == null)) {
			Bukkit.getConsoleSender().sendMessage("Could Not Save");
			return;
		}
		try {
			getData().save(this.dataFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + this.dataFile, ex);
		}
	}
	
}
