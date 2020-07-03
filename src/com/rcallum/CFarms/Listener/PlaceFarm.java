package com.rcallum.CFarms.Listener;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.rcallum.CFarms.WorldGuard.RegionChecks;

public class PlaceFarm implements Listener{
	@EventHandler
	public void beforePlace(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ConfigurationSection cs = CandyFarms.config.getConfigurationSection("FarmItem");
			if (!p.getItemInHand().hasItemMeta()) return;
			if (!p.getItemInHand().getItemMeta().hasDisplayName()) return;
			if (p.getItemInHand().getItemMeta().getDisplayName().replaceAll("§", "&").equalsIgnoreCase(cs.getString("Name"))) {
				Location l = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
				String farmID = PendingFarmData.getInstance().locToString(l);
				if (RegionChecks.getInstance().farmRegionPerms(farmID, p)) {
					drawOutline(p, l);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("cannotPlaceFarmHere")));
				}
				
				e.setCancelled(true);
			}
			
		}
	}
	
	public void drawOutline(Player p, Location loc) {
		loc.getBlock().setType(p.getItemInHand().getType());
		if (p.getItemInHand().getAmount() == 1) {
			p.setItemInHand(null);
		}else {
			p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
		}
		
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
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 5; z++) {
					Location newLoc = new Location(w, x1+x, y1+y, z1+z);
					blocks.add(newLoc.getBlock());
				}
			}
		}
		PendingFarmData.getInstance().storePending(loc, p, blocks);
		for (Block newBlock : blocks) {
			Material mat = Material.getMaterial(CandyFarms.config.getString("GhostBlockFill"));
			newBlock.setType(mat);
		}
		ClickFarm cf = new ClickFarm();
		cf.pendingGUI(p, PendingFarmData.getInstance().locToString(loc));
		
		
	}
}
