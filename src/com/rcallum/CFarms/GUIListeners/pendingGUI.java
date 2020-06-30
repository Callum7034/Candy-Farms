package com.rcallum.CFarms.GUIListeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.BuildFarm;
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.rcallum.CFarms.items.Farm;
import com.rcallum.CFarms.utils.NBTHandler;

public class pendingGUI implements Listener {
	ConfigurationSection data = CandyFarms.getInstance().getData();
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("§8Placement Confirmation")) {
			Player p = (Player) e.getWhoClicked();
			if (e.getCurrentItem() == null) {
				e.setCancelled(true);
				return;
			}
			if (e.getCurrentItem().hasItemMeta()) {
				if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
					if (e.getCurrentItem().getType() == Material.STAINED_CLAY) {
						String s = e.getCurrentItem().getItemMeta().getDisplayName();
						if (s.replaceAll("§", "&").equalsIgnoreCase(CandyFarms.config.getString("placementConfirm"))) {
							
							String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
							Location loc = PendingFarmData.getInstance().stringToLoc(locID);
							PendingFarmData.getInstance().removePendingBlocks(locID);
							
							ConfigurationSection sec = data.getConfigurationSection("PendingFarms."+locID);
							FarmData.getInstance().storeData(sec);

							BuildFarm.getInstance().buildFarm(loc, sec.getString("Direction"));
							PendingFarmData.getInstance().removeFromData(locID);
							p.closeInventory();
							
							
						}
						if (s.replaceAll("§", "&").equalsIgnoreCase(CandyFarms.config.getString("placementCancel"))) {
							
							String uuid = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
							Location loc = PendingFarmData.getInstance().stringToLoc(uuid);
							PendingFarmData.getInstance().removePendingBlocks(uuid);
							PendingFarmData.getInstance().removeFromData(uuid);
							loc.getBlock().setType(Material.AIR);
							p.getInventory().addItem(Farm.getInstance().makeItem(1));
							p.closeInventory();

						}
					}

				}
			}

			e.setCancelled(true);
		}
	}

}
