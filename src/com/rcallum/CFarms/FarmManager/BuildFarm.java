package com.rcallum.CFarms.FarmManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class BuildFarm {
	private static BuildFarm instance;

	public BuildFarm() {
		instance = this;
	}

	public static BuildFarm getInstance() {
		if (instance == null) {
			instance = new BuildFarm();
		}
		return instance;
	}
	
	public void buildFarm(Location loc, String direction) {
		
		int x1 = 0;
		int y1 = 0;
		int z1 = 0;
		Location mid = loc;
		
		if (direction.equalsIgnoreCase("North")) {
			x1 = loc.getBlockX() - 2;
			z1 = loc.getBlockZ() - 5;
			mid.add(0, 0, -3);
		}
		if (direction.equalsIgnoreCase("South")) {
			x1 = loc.getBlockX() - 2;
			z1 = loc.getBlockZ() + 1;
			mid.add(0, 0, 3);
		}
		if (direction.equalsIgnoreCase("East")) {
			x1 = loc.getBlockX() + 1;
			z1 = loc.getBlockZ() - 2;
			mid.add(3, 0, 0);
		}
		if (direction.equalsIgnoreCase("West")) {
			x1 = loc.getBlockX() - 5;
			z1 = loc.getBlockZ() - 2;
			mid.add(-3, 0, 0);
		}
		y1 = loc.getBlockY() - 1;
		World w = loc.getWorld();
		for (int x = 0; x< 5; x++) {
			for (int y = 0; y< 3; y++) {
				for (int z = 0; z< 5; z++) {
					Location newLoc = new Location(w, x1+x, y1+y, z1+z);
					newLoc.getBlock().setType(Material.AIR);
				}
			}
		}
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				Location newLoc = new Location(w, x1+x, y1, z1+z);
				newLoc.getBlock().setType(Material.SOIL);
			}
		}
		mid.getBlock().setType(Material.STEP);
		mid.add(0, -1, 0);
		mid.getBlock().setType(Material.WATER);
		
	}
}
