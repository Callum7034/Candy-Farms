package com.rcallum.CFarms.FastGrowing;

import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.PendingFarmData;

public class GrowAll {
	private static GrowAll instance;

	public GrowAll() {
		instance = this;
	}

	public static GrowAll getInstance() {
		if (instance == null) {
			instance = new GrowAll();
		}
		return instance;
	}
	ConfigurationSection data = CandyFarms.getInstance().getData();
	public void increaseGrowth(Location loc) {
		BlockState state = loc.getBlock().getState();
		MaterialData data = state.getData();
		if (data instanceof Crops) {
			CropState c = Growth.getInstance().getNextState(((Crops) data).getState());
			((Crops) data).setState(c);
			state.setData(data);
			state.update();
		}
	}
	
	public void setToSeeded(Location loc) {
		BlockState state = loc.getBlock().getState();
		MaterialData data = state.getData();
		if (data instanceof Crops) {
			((Crops) data).setState(CropState.SEEDED);
			state.setData(data);
			state.update();
		}
	}
	
	public void doAll() {
		if (!data.contains("Farms")) {
			return;
		}
		ConfigurationSection sec = data.getConfigurationSection("Farms");
		for (String farm : sec.getKeys(false)) {
			sec = data.getConfigurationSection("Farms."+farm);
			if (!sec.contains("Planted")) continue;
			for (String crop : sec.getStringList("Planted")) {
				String[] split = crop.split("__");
				Location loc = PendingFarmData.getInstance().stringToLoc(split[0]);
				increaseGrowth(loc);
			}
		}
	}
}
