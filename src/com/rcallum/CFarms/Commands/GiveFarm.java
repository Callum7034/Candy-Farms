package com.rcallum.CFarms.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.items.Farm;

public class GiveFarm {
	private static GiveFarm instance;

	public GiveFarm() {
		instance = this;
	}

	public static GiveFarm getInstance() {
		if (instance == null) {
			instance = new GiveFarm();
		}
		return instance;
	}
	
	public boolean give(Player sender, String target1) {
		Player target = Bukkit.getPlayer(target1);
		if (target == null) {
			String message = ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("playerNotFound"));
			sender.sendMessage(message);
			return false;
		}
		boolean space = false;
		for (ItemStack item : target.getInventory().getContents()) {
			if (item == null) space = true;
		}
		if (space == true) {
			target.getInventory().addItem(Farm.getInstance().makeItem(1));
			return true;
		} else {
			return false;
		}
		
		
	}
}
