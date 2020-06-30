package com.rcallum.CFarms.FarmManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.items.Farm;

public class FarmData {
	private static FarmData instance;

	public FarmData() {
		instance = this;
	}

	public static FarmData getInstance() {
		if (instance == null) {
			instance = new FarmData();
		}
		return instance;
	}
	ConfigurationSection data = CandyFarms.getInstance().getData();
	public void storeData(ConfigurationSection pendingSection) {
		String locID = pendingSection.getName();
		String owner = pendingSection.getString("owner");
		String direction = pendingSection.getString("Direction");
		List<String> oldBlocks = pendingSection.getStringList("OldBlocks");
		if (!data.contains("Farms")) data.createSection("Farms");
		ConfigurationSection cs = data.getConfigurationSection("Farms");
		cs.createSection(locID);
		cs = cs.getConfigurationSection(locID);
		cs.set("owner", owner);
		cs.set("Direction", direction);
		cs.set("OldBlocks", oldBlocks);
		CandyFarms.getInstance().saveData();
	}
	
	public boolean isBlockClaim(Location loc) {
		if (!data.contains("Farms")) return false;
		ConfigurationSection sec = data.getConfigurationSection("Farms");
		for (String word : sec.getKeys(false)) {
			if (word.equalsIgnoreCase(PendingFarmData.getInstance().locToString(loc))) {
				return true;
				
			}
		}
		return false;
	}
	private void removeFromData(String id) {
		data.getConfigurationSection("Farms").set(id, null);
		Set<String> keys = data.getConfigurationSection("Farms").getKeys(false);
		keys.remove(id);
		CandyFarms.getInstance().saveData();
	}
	
	public Set<UUID> getMembers(String locID){
		Set<UUID> members = new HashSet<UUID>();
		ConfigurationSection sec = data.getConfigurationSection("Farms."+locID);
		for(String s : sec.getStringList("Members")) {
			members.add(UUID.fromString(s));
		}
		return members;
	}
	public UUID getOwner(String locID){
		ConfigurationSection sec = data.getConfigurationSection("Farms."+locID);
		UUID returned = UUID.fromString(sec.getString("owner"));
		return returned;
	}
	public void addMember(String ign, String locID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+locID);
		List<String> list = sec.getStringList("Members");
		Player p = Bukkit.getPlayer(ign);
		list.add(p.getUniqueId().toString());
		sec.set("Members", list);
		CandyFarms.getInstance().saveData();
	}
	public void removeMember(String playerUUID, String locID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+locID);
		List<String> list = sec.getStringList("Members");
		list.remove(playerUUID);
		sec.set("Members", list);
		CandyFarms.getInstance().saveData();
	}
	
	@SuppressWarnings("deprecation")
	public void removeFarmAndData(Player p, String locID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms").getConfigurationSection(locID);
		String dir = sec.getString("Direction");
		String[] splitLoc = locID.split(",");
		
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
		PendingFarmData.getInstance().stringToLoc(locID).getBlock().setType(Material.AIR);
		List<String> data = sec.getStringList("OldBlocks");
		for (String a : data) {
			String[] split = a.split("__");
			String uuid = split[0];
			String[] matFull = split[1].split(":");
			String mat = matFull[0];
			Byte matid = Byte.valueOf(matFull[1]);
			Location Uuid = PendingFarmData.getInstance().stringToLoc(uuid);
			Material Mat = Material.getMaterial(mat);
			Uuid.getBlock().setType(Mat);
			Uuid.getBlock().setData(matid);
			BlockState state = Uuid.getBlock().getState();
			state.setRawData(matid);
			state.update(true);
		}
		p.getInventory().addItem(Farm.getInstance().makeItem(1));
		removeFromData(locID);
		
	}
	
	public void removePlanted(String locID, String seedLocID, Location seedLoc) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+locID);
		List<String> list = sec.getStringList("Planted");
		list.remove(seedLocID);
		sec.set("Planted", list);
		CandyFarms.getInstance().saveData();
		seedLoc.getBlock().setType(Material.AIR);
	}
	
	public boolean hasMinion(String farmID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+farmID);
		if(sec.contains("Minion")) {
			if (sec.getBoolean("Minion.Added")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void addMinion(String farmID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+farmID);
		if (!sec.contains("Minion")) {
			sec.createSection("Minion");
		}
		sec = sec.getConfigurationSection("Minion");
		sec.set("Added", true);
		CandyFarms.getInstance().saveData();
	}
	
	public void removeMinion(String farmID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms."+farmID+".Minion");
		sec.set("Added", false);
		CandyFarms.getInstance().saveData();

	}
}
