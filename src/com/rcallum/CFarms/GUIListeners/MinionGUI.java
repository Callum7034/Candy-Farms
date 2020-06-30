package com.rcallum.CFarms.GUIListeners;

import java.util.ArrayList;
import java.util.List;

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
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.items.MinionItem;
import com.rcallum.CFarms.utils.NBTHandler;

public class MinionGUI implements Listener {
	private static MinionGUI instance;

	public MinionGUI() {
		instance = this;
	}

	public static MinionGUI getInstance() {
		if (instance == null) {
			instance = new MinionGUI();
		}
		return instance;
	}

	ConfigurationSection data = CandyFarms.getInstance().getData();

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu.MinionGUI");
		String GUIName = color(config.getString("GUIName"));
		if (e.getView().getTitle().equalsIgnoreCase(GUIName)) {
			e.setCancelled(true);
			int addSlot = config.getInt("MinionAdd.Slot");
			
			int removeSlot = config.getInt("MinionRemove.Slot");
			int clickedSlot = e.getSlot();
			
			Player p = (Player) e.getWhoClicked();
			
			String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
			if (clickedSlot == addSlot) {
				if (FarmData.getInstance().hasMinion(locID)) {
					p.sendMessage(color(CandyFarms.messages.getString("alreadyHaveMinion")));
					return;
				} else {
					int index = 0;
					for (ItemStack i : p.getInventory().getContents()) {
						index++;
						if (i == null) continue;
						if (i.getType() == Material.AIR) continue;
						if (NBTHandler.getInstance().hasNBT(i, "Minion")) {
							if (i.getAmount() == 1) {
								p.getInventory().setItem(index-1, null);
							}else {
								i.setAmount(i.getAmount()-1);
							}
							p.updateInventory();
							FarmData.getInstance().addMinion(locID);
							p.sendMessage(color(CandyFarms.messages.getString("MinionAdded")));
							openGUI(p, locID);
							return;
						} 
					}
					p.sendMessage(color(CandyFarms.messages.getString("needMinionInv")));
					return;
				}
			}
			if (clickedSlot == removeSlot) {
				if (FarmData.getInstance().hasMinion(locID)) {
					FarmData.getInstance().removeMinion(locID);
					p.getInventory().addItem(MinionItem.minionItem);
					p.sendMessage(color(CandyFarms.messages.getString("MinionRemoved")));
					openGUI(p, locID);
				} else {
					p.sendMessage(color(CandyFarms.messages.getString("dontHaveMinion")));
					return;
				}
			}

		}
	}

	public void openGUI(Player p, String farmID) {
		ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu.MinionGUI");

		String GUIName = config.getString("GUIName");
		int GUISize = config.getInt("GUISize");
		String FillerItem = config.getString("GUIFillerItem");
		int FillerItemDamage = config.getInt("GUIFillerItemDamage");

		String addItemType = config.getString("MinionAdd.Item");
		int addItemDamage = config.getInt("MinionAdd.ItemDamage");
		String addItemName = config.getString("MinionAdd.ItemName");
		int addslot = config.getInt("MinionAdd.Slot");
		List<String> addItemlore = new ArrayList<String>();
		for (String s : config.getStringList("MinionAdd.Lore")) {
			String ss = color(s);
			addItemlore.add(ss);
		}

		String removeItemType = config.getString("MinionRemove.Item");
		int removeItemDamage = config.getInt("MinionRemove.ItemDamage");
		String removeItemName = config.getString("MinionRemove.ItemName");
		int removeslot = config.getInt("MinionRemove.Slot");
		List<String> removeItemlore = new ArrayList<String>();
		for (String s : config.getStringList("MinionRemove.Lore")) {
			String ss = color(s);
			removeItemlore.add(ss);
		}

		Inventory GUI = Bukkit.createInventory(null, GUISize, color(GUIName));

		ItemStack filler = new ItemStack(Material.getMaterial(FillerItem), 1, (short) FillerItemDamage);
		filler = NBTHandler.getInstance().setNBT(filler, "UUID", farmID);
		ItemMeta im = filler.getItemMeta();
		im.setDisplayName(" ");
		filler.setItemMeta(im);
		for (int i = 0; i < GUISize; i++) {
			GUI.setItem(i, filler);
		}

		ItemStack addItem = new ItemStack(Material.getMaterial(addItemType), 1, (short) addItemDamage);
		im = addItem.getItemMeta();
		im.setDisplayName(color(addItemName));
		im.setLore(addItemlore);
		addItem.setItemMeta(im);
		GUI.setItem(addslot, addItem);

		ItemStack remove = new ItemStack(Material.getMaterial(removeItemType), 1, (short) removeItemDamage);
		im = remove.getItemMeta();
		im.setDisplayName(color(removeItemName));
		im.setLore(removeItemlore);
		remove.setItemMeta(im);
		GUI.setItem(removeslot, remove);

		boolean minionHave = FarmData.getInstance().hasMinion(farmID);
		if (!minionHave) {
			int nslot = config.getInt("CurrentMinion.Slot");
			ConfigurationSection cm = config.getConfigurationSection("CurrentMinion.NotAdded");

			String nType = cm.getString("Item");
			int nItemDamage = cm.getInt("ItemDamage");
			String nItemName = cm.getString("ItemName");
			List<String> nItemlore = new ArrayList<String>();
			for (String s : config.getStringList("Lore")) {
				String ss = color(s);
				nItemlore.add(ss);
			}
			ItemStack nItem = new ItemStack(Material.getMaterial(nType), 1, (short) nItemDamage);
			im = nItem.getItemMeta();
			im.setDisplayName(color(nItemName));
			im.setLore(nItemlore);
			nItem.setItemMeta(im);
			GUI.setItem(nslot, nItem);

		} else {
			int nslot = config.getInt("CurrentMinion.Slot");
			ConfigurationSection cm = config.getConfigurationSection("CurrentMinion.Added");

			String nType = cm.getString("Item");
			int nItemDamage = cm.getInt("ItemDamage");
			String nItemName = cm.getString("ItemName");
			List<String> nItemlore = new ArrayList<String>();
			for (String s : config.getStringList("Lore")) {
				String ss = color(s);
				nItemlore.add(ss);
			}
			ItemStack nItem = new ItemStack(Material.getMaterial(nType), 1, (short) nItemDamage);
			im = nItem.getItemMeta();
			im.setDisplayName(color(nItemName));
			im.setLore(nItemlore);
			nItem.setItemMeta(im);
			GUI.setItem(nslot, nItem);
		}
		p.openInventory(GUI);

	}

	public ConfigurationSection getMinionData(String farmID) {
		ConfigurationSection sec = data.getConfigurationSection("Farms." + farmID);
		if (sec.contains("Minion")) {
			return sec.getConfigurationSection("Minion");
		} else {
			return null;
		}
	}

	public String color(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}
