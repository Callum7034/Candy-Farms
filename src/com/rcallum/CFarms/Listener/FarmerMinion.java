package com.rcallum.CFarms.Listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.rcallum.CFarms.utils.NBTHandler;


public class FarmerMinion implements Listener{
	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack i = e.getPlayer().getItemInHand();
			if (i == null) return;
			if (i.getType() == Material.AIR) return;
			if (NBTHandler.getInstance().hasNBT(i, "Minion")) {
				e.setCancelled(true);
			}
		}
	}
}
