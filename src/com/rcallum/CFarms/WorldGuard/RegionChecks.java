package com.rcallum.CFarms.WorldGuard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class RegionChecks {
	private static RegionChecks instance;

	public RegionChecks() {
		instance = this;
	}

	public static RegionChecks getInstance() {
		if (instance == null) {
			instance = new RegionChecks();
		}
		return instance;
	}

	public boolean permToPlace(Player p, Location loc) {
		WorldGuardPlugin wg = WorldGuardPlugin.inst();
		RegionContainer container = wg.getRegionContainer();
		RegionQuery query = container.createQuery();
		if (query.testState(loc, p, DefaultFlag.BUILD)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean farmRegionPerms(String farmID, Player p) {
		boolean TotalResult = true;
		
		Location loc = PendingFarmData.getInstance().stringToLoc(farmID);
		String dir = PendingFarmData.getInstance().getCardinalDirection(p);
		int x1 = 0;
		int y1 = 0;
		int z1 = 0;
		
		if (dir == null) {
			x1 = loc.getBlockX();
			y1 = loc.getBlockY();
			z1 = loc.getBlockZ();
		}
		
		if (dir.equalsIgnoreCase("North")) {
			x1 = loc.getBlockX() - 2;
			y1 = loc.getBlockY() - 1;
			z1 = loc.getBlockZ() - 5;
		}
		if (dir.equalsIgnoreCase("South")) {
			x1 = loc.getBlockX() - 2;
			y1 = loc.getBlockY() - 1;
			z1 = loc.getBlockZ() + 1;
		}
		if (dir.equalsIgnoreCase("East")) {
			x1 = loc.getBlockX() + 1;
			y1 = loc.getBlockY() - 1;
			z1 = loc.getBlockZ() - 2;
		}
		if (dir.equalsIgnoreCase("West")) {
			x1 = loc.getBlockX() - 5;
			y1 = loc.getBlockY() - 1;
			z1 = loc.getBlockZ() - 2;
		}
		World w = loc.getWorld();
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 5; z++) {
					Location newLoc = new Location(w, x1+x, y1+y, z1+z);
					if (!permToPlace(p, newLoc)) {
						return false;
					}
				}
			}
		}
		
		return TotalResult;
	}
}
