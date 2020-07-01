package com.rcallum.CFarms.FarmManager;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FastGrowing.GrowAll;
import com.rcallum.CFarms.FastGrowing.Growth;
import com.rcallum.CFarms.GUIListeners.StorageGUI;
import com.rcallum.CFarms.utils.TokenHook;

public class MinionManager {
	private static MinionManager instance;

	public MinionManager() {
		instance = this;
	}

	public static MinionManager getInstance() {
		if (instance == null) {
			instance = new MinionManager();
		}
		return instance;
	}
	ConfigurationSection data = CandyFarms.getInstance().getData();
	
	public void runMinionCheck() {
		if (!data.contains("Farms")) return;
		ConfigurationSection farms = data.getConfigurationSection("Farms");
		for (String farm : farms.getKeys(false)) {
			if (farms.getBoolean(farm + ".Minion.Added")) {
				for (String seed : farms.getStringList(farm+".Planted")) {
					String[] split = seed.split("__");
					Location loc = PendingFarmData.getInstance().stringToLoc(split[0]);
					if (Growth.getInstance().getState(loc) == 7) {
						StorageGUI.getInstance().addItem(farm, split[1]);
						UUID owner = UUID.fromString(farms.getString(farm+".owner"));
						int price = CandyFarms.config.getInt("SeedConfigs.Prices." + split[2]);
						if (TokenHook.getInstance().withdrawTokens(owner, price)) {
							GrowAll.getInstance().setToSeeded(loc);
						} else {
							FarmData.getInstance().removePlanted(farm, seed,
									PendingFarmData.getInstance().stringToLoc(split[0]));
						}
						
					}
				}
			}
		}
	}
}
