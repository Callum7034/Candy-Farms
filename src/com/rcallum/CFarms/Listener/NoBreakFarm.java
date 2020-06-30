package com.rcallum.CFarms.Listener;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.rcallum.CFarms.FastGrowing.Growth;
import com.rcallum.CFarms.items.CandyItems;
import com.rcallum.CFarms.items.SeedMaker;

import net.md_5.bungee.api.ChatColor;

public class NoBreakFarm implements Listener {
	private static NoBreakFarm instance;

	public NoBreakFarm() {
		instance = this;
	}

	public static NoBreakFarm getInstance() {
		if (instance == null) {
			instance = new NoBreakFarm();
		}
		return instance;
	}
	ConfigurationSection data = CandyFarms.getInstance().getData();

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (getRegionResult("PendingFarms", e.getBlock().getLocation()) != null) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(
					ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("cannotBreakBlockHere")));
			return;
		}
		if (getRegionResult("Farms", e.getBlock().getLocation()) != null) {
			e.setCancelled(true);
			if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
				return;
			}
			e.getPlayer().sendMessage(
					ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("cannotBreakBlockHere")));
			return;
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (getRegionResult("Farms", e.getBlock().getLocation()) != null) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(
					ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("cannotPlaceBlockHere")));
			return;
		}

	}

	@EventHandler
	public void onBreakCrops(PlayerInteractEvent e) {
		if ((e.getAction() == Action.PHYSICAL) && (e.getClickedBlock().getType() == Material.SOIL)) {
			if (getRegionResult("Farms", e.getClickedBlock().getLocation()) != null) {
				e.setCancelled(true);
			}
		}
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			String region = getRegionResult("Farms", e.getClickedBlock().getLocation());
			if (region != null) {
				ConfigurationSection sec = data.getConfigurationSection("Farms." + region);
				String owner = sec.getString("owner");
				Set<UUID> members = FarmData.getInstance().getMembers(region);
				members.add(UUID.fromString(owner));
				if (!members.contains(UUID.fromString(e.getPlayer().getUniqueId().toString()))) {
					if (e.getClickedBlock().getType() == Material.CROPS) {
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
							CandyFarms.messages.getString("cannotBreakBlockHere")));
					}
					return;
				}
				for (String location : sec.getStringList("Planted")) {
					String[] split = location.split("__");
					if (split[0].equalsIgnoreCase(
							PendingFarmData.getInstance().locToString(e.getClickedBlock().getLocation()))) {

						int stage = Growth.getInstance().getState(e.getClickedBlock().getLocation());
						if (stage != 7) {
							e.getPlayer().getInventory().addItem(SeedMaker.getInstance().getItem(split[2], split[1]));
						} else {
							e.getPlayer().getInventory().addItem(CandyItems.getInstance().getItem(split[1]));
							int chance = CandyFarms.config.getInt("SeedConfigs.SeedBackChance");
							if (chance != 0) {
								Random r = new Random();
								int random = r.nextInt(101);
								if ((random < chance) || (chance == 100)) {
									e.getPlayer().getInventory()
											.addItem(SeedMaker.getInstance().getItem(split[2], split[1]));
									e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
											CandyFarms.messages.getString("getSeedBack")));
								}
							}
						}
						FarmData.getInstance().removePlanted(region, location,
								PendingFarmData.getInstance().stringToLoc(split[0]));

					}
				}
			}
		}

	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent e) {
		if (getRegionResult("Farms", e.getBlockClicked().getLocation()) != null) {
			e.setCancelled(true);
		}

	}

	public String getRegionResult(String section, Location block) {
		if (!data.contains(section)) {
			return null;
		}
		ConfigurationSection sec = data.getConfigurationSection(section);
		Location check = block;
		if (sec == null)
			return null;
		for (String s : sec.getKeys(false)) {

			sec = data.getConfigurationSection(section).getConfigurationSection(s);
			String dir = sec.get("Direction").toString();
			if (s.equalsIgnoreCase(PendingFarmData.getInstance().locToString(check))) {
				return s;
			}
			Location loc1 = PendingFarmData.getInstance().stringToLoc(s);
			Location loc2 = PendingFarmData.getInstance().stringToLoc(s);

			if (loc1 == check) {
				return s;
			}
			if (dir.equalsIgnoreCase("North")) {
				loc1.add(-2, -1, -5);
				loc2.add(2, 1, -1);
			}
			if (dir.equalsIgnoreCase("South")) {
				loc1.add(-2, -1, 1);
				loc2.add(2, 1, 5);
			}
			if (dir.equalsIgnoreCase("East")) {
				loc1.add(1, -1, -2);
				loc2.add(5, 1, 2);
			}
			if (dir.equalsIgnoreCase("West")) {
				loc1.add(-5, -1, -2);
				loc2.add(-1, 1, 2);
			}

			if (inRegion(loc1, loc2, check)) {
				return s;
			}

		}
		return null;
	}

	public boolean inRegion(Location loc1, Location loc2, Location check) {
		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		if ((check.getBlockX() >= x1) && (check.getBlockY() >= y1) && (check.getBlockZ() >= z1)) {
			if ((check.getBlockX() <= x2) && (check.getBlockY() <= y2) && (check.getBlockZ() <= z2)) {
				return true;
			}
		}
		return false;
	}
}
