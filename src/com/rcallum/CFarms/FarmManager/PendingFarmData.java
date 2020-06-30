package com.rcallum.CFarms.FarmManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.rcallum.CFarms.CandyFarms;

public class PendingFarmData {
	private static PendingFarmData instance;

	public PendingFarmData() {
		instance = this;
	}

	public static PendingFarmData getInstance() {
		if (instance == null) {
			instance = new PendingFarmData();
		}
		return instance;
	}
	ConfigurationSection data = CandyFarms.getInstance().getData();
	public void storePending(Location loc, Player p, ArrayList<Block> blocks) {
		if (!data.contains("PendingFarms")) {
			data.createSection("PendingFarms");
		}
		ConfigurationSection cs = data.getConfigurationSection("PendingFarms");
		String uuid = (loc.getWorld().getName() + "," + loc.getBlockX() + "," 
		+ loc.getBlockY() + "," + loc.getBlockZ());
		cs.createSection(uuid);
		cs = cs.getConfigurationSection(uuid);
		cs.set("owner", p.getUniqueId().toString());
		cs.set("Direction", getCardinalDirection(p));
		cs.set("OldBlocks", saveBlockData(blocks));
		CandyFarms.getInstance().saveData();
	}
	
	
	public String isBlockClaim(Location loc, String playerUUID) {
		if (!data.contains("PendingFarms")) return null;
		ConfigurationSection sec = data.getConfigurationSection("PendingFarms");
		for (String word : sec.getKeys(false)) {
			String[] words = word.split(",");
			World world = Bukkit.getWorld(words[0]);
			int x = Integer.parseInt(words[1]);
			int y = Integer.parseInt(words[2]);
			int z = Integer.parseInt(words[3]);
			Location checkLoc = new Location(world, x, y, z);
			if (checkLoc.toString().equals(loc.toString())) {
				if (sec.getConfigurationSection(word).getString("owner").equalsIgnoreCase(playerUUID)) {
					return playerUUID;
				} else {
					return "wrongplayer";
				}
				
			}
		}
		return null;
	}
	
	
	public List<String> saveBlockData(ArrayList<Block> blocks){
		ArrayList<String> data = new ArrayList<String>();
		for (Block b : blocks) {
			if (b.getType() == Material.AIR) continue;
			String uuid = locToString(b.getLocation());
			String material = b.getType().name();
			@SuppressWarnings("deprecation")
			String damage = Byte.toString(b.getData());
			String mix = (uuid + "__" + material+":"+damage);
			data.add(mix);
		}
		return data;
	}
	
	public String locToString(Location loc) {
		String uuid = (loc.getWorld().getName() + "," + loc.getBlockX() + "," 
				+ loc.getBlockY() + "," + loc.getBlockZ());
		return uuid;
	}
	
	public Location stringToLoc(String s) {
		String[] words = s.split(",");
		World world = Bukkit.getWorld(words[0]);
		int x = Integer.parseInt(words[1]);
		int y = Integer.parseInt(words[2]);
		int z = Integer.parseInt(words[3]);
		Location checkLoc = new Location(world, x, y, z);
		return checkLoc;
	}
	
	public String getCardinalDirection(Player p) {
		double rotation = (p.getLocation().getYaw() - 90.0F) % 360.0F;
		if (rotation < 0.0D) {
			rotation += 360.0D;
		}
		if ((0.0D <= rotation) && rotation < 45.0D) return "West";
		if ((45.0D <= rotation) && rotation < 135.0D) return "North";
		if ((135.0D <= rotation) && rotation < 225.0D) return "East";
		if ((225.0D <= rotation) && rotation < 315.0D) return "South";
		if ((315.0D <= rotation) && rotation < 360.0D) return "West";
		return null;
	}
	
	public void removeFromData(String id) {
		data.getConfigurationSection("PendingFarms").set(id, null);
		Set<String> keys = data.getConfigurationSection("PendingFarms").getKeys(false);
		keys.remove(id);
		CandyFarms.getInstance().saveData();
	}
	
	@SuppressWarnings("deprecation")
	public void removePendingBlocks(String loc) {
		ConfigurationSection sec = data.getConfigurationSection("PendingFarms").getConfigurationSection(loc);
		String dir = sec.getString("Direction");
		String[] splitLoc = loc.split(",");
		
		World w = Bukkit.getWorld(splitLoc[0]);
		int x = Integer.valueOf(splitLoc[1]);
		int y = Integer.valueOf(splitLoc[2]);
		y = y -1;
		int z = Integer.valueOf(splitLoc[3]);
		
		if (dir.equalsIgnoreCase("North")) {
			x = x - 2;
			z = z - 5;
		}
		if (dir.equalsIgnoreCase("South")) {
			x = x - 2;
			z = z + 1;
		}
		if (dir.equalsIgnoreCase("East")) {
			x = x + 1;
			z = z - 2;
		}
		if (dir.equalsIgnoreCase("West")) {
			x = x - 5;
			z = z - 2;
		}
		for (int x1 = 0; x1 < 5; x1++) {
			for (int y1 = 0; y1 < 3; y1++) {
				for (int z1 = 0; z1 < 5; z1++) {
					Location newLoc = new Location(w, x1+x, y1+y, z1+z);
					newLoc.getBlock().setType(Material.AIR);
				}
			}
		}
		List<String> data = sec.getStringList("OldBlocks");
		for (String a : data) {
			String[] split = a.split("__");
			String uuid = split[0];
			String[] matFull = split[1].split(":");
			String mat = matFull[0];
			Byte matid = Byte.valueOf(matFull[1]);
			Location Uuid = stringToLoc(uuid);
			Material Mat = Material.getMaterial(mat);
			Uuid.getBlock().setType(Mat);
			Uuid.getBlock().setData(matid);
			BlockState state = Uuid.getBlock().getState();
			state.setRawData(matid);
			state.update(true);
		}
	}
}
