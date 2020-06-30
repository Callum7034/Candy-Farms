package com.rcallum.CFarms.GUIListeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.items.CandyItems;
import com.rcallum.CFarms.utils.NBTHandler;

public class StorageGUI implements Listener {
	private static StorageGUI instance;
	ConfigurationSection data = CandyFarms.getInstance().getData();

	public StorageGUI() {
		instance = this;
	}

	public static StorageGUI getInstance() {
		if (instance == null) {
			instance = new StorageGUI();
		}
		return instance;
	}
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu.StorageGUI");
		String GUIName = color(config.getString("GUIName"));
		if (e.getView().getTitle().equalsIgnoreCase(GUIName)) {
			e.setCancelled(true);
		}
	}

	public void openGUI(Player p, String farmID) {
		ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu.StorageGUI");
		String GUIName = config.getString("GUIName");
		int GUISize = config.getInt("GUISize");
		String FillerItem = config.getString("GUIFillerItem");
		int FillerItemDamage = config.getInt("GUIFillerItemDamage");

		Inventory GUI = Bukkit.createInventory(null, GUISize, color(GUIName));

		ItemStack filler = new ItemStack(Material.getMaterial(FillerItem), 1, (short) FillerItemDamage);
		filler = NBTHandler.getInstance().setNBT(filler, "UUID", farmID);
		ItemMeta im = filler.getItemMeta();
		im.setDisplayName(" ");
		filler.setItemMeta(im);
		for (int i = 0; i < GUISize; i++) {
			GUI.setItem(i, filler);
		}
		int startSlot = config.getInt("StartSlot");
		HashMap<String, Integer> storage = getStorage(farmID);
		if (storage != null) {
			for (String s : storage.keySet()) {
				int amount = storage.get(s);
				ItemStack candy = CandyItems.getInstance().getItemAmount(s, amount);
				if (startSlot >= GUISize - 1)
					continue;
				GUI.setItem(startSlot, candy);
				startSlot++;
			}
		}
		p.openInventory(GUI);
		
	}

	public HashMap<String, Integer> getStorage(String farmID) {
		HashMap<String, Integer> items = new HashMap<String, Integer>();
		ConfigurationSection sec = data.getConfigurationSection("Farms." + farmID);
		if (!sec.contains("Storage")) {
			return null;
		}
		sec = sec.getConfigurationSection("Storage");
		for (String s : sec.getKeys(false)) {
			int amt = sec.getInt(s);
			items.put(s, amt);
		}
		return items;
	}

	public void addItem(String farmID, String candyName) {
		ConfigurationSection sec = data.getConfigurationSection("Farms." + farmID);
		if (!sec.contains("Storage")) {
			sec.createSection("Storage");
		}
		sec = sec.getConfigurationSection("Storage");
		if (sec.contains(candyName)) {
			int amt = sec.getInt(candyName);
			sec.set(candyName, amt + 1);
		} else {
			sec.set(candyName, 1);
		}
	}
	public String color(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}
