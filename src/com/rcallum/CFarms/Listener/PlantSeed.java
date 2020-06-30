package com.rcallum.CFarms.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.rcallum.CFarms.utils.NBTHandler;

import net.md_5.bungee.api.ChatColor;

public class PlantSeed implements Listener {
	ConfigurationSection data = CandyFarms.getInstance().getData();
	
	@EventHandler
	public void plantSeed(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (e.getClickedBlock().getType() == Material.SOIL) {
				if (e.getPlayer().getItemInHand().getType() == null)
					return;
				if (e.getPlayer().getItemInHand().getType() == Material.AIR)
					return;
				if (!NBTHandler.getInstance().hasNBT(e.getPlayer().getItemInHand(), "CustomSeed")) {
					return;
				}
				Player p = e.getPlayer();
				String result = NoBreakFarm.getInstance().getRegionResult("Farms", e.getClickedBlock().getLocation());
				if (result == null)
					return;
				e.setCancelled(true);
				ConfigurationSection sec = data.getConfigurationSection("Farms." + result);
				Set<UUID> members = FarmData.getInstance().getMembers(result);
				members.add(FarmData.getInstance().getOwner(result));
				if (!members.contains(UUID.fromString(e.getPlayer().getUniqueId().toString()))){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("cannotPlantHere")));
					return;
				}
				List<String> planted = new ArrayList<String>();
				if (sec.contains("Planted"))
					planted = sec.getStringList("Planted");
				String seedName = NBTHandler.getInstance().getNBT(p.getItemInHand(), "Seed");
				String rarity = NBTHandler.getInstance().getNBT(p.getItemInHand(), "Rarity");
				planted.add(PendingFarmData.getInstance().locToString(e.getClickedBlock().getLocation().add(0, 1, 0))
						+ "__" + seedName + "__" + rarity);
				sec.set("Planted", planted);
				e.getClickedBlock().getLocation().add(0, 1, 0).getBlock().setType(Material.CROPS);
				CandyFarms.getInstance().saveData();

				if (p.getItemInHand().getAmount() == 1) {
					p.setItemInHand(null);
				} else {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				}

			}
		}
	}
}
