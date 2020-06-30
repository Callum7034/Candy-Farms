package com.rcallum.CFarms.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.utils.NBTHandler;

public class CandyItems {
	private static CandyItems instance;

	public CandyItems() {
		instance = this;
	}

	public static CandyItems getInstance() {
		if (instance == null) {
			instance = new CandyItems();
		}
		return instance;
	}

	public ItemStack makeItem(String itemName, String type, String CandyName, List<String> itemLore, boolean glow) {
		ItemStack item = new ItemStack(Material.getMaterial(type));

		NBTHandler nbt = NBTHandler.getInstance();
		item = nbt.setNBT(item, "CustomCandy", "true");
		item = nbt.setNBT(item, "CandyItem", CandyName);
		if (glow) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

		List<String> lore = new ArrayList<String>();
		for (String s : itemLore) {
			String ss = ChatColor.translateAlternateColorCodes('&', s);
			lore.add(ss);
		}
		im.setLore(lore);

		im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });

		item.setItemMeta(im);
		return item;
	}

	public ItemStack getItem(String candyType) {
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("CandyItems." + candyType);
		String itemName = sec.getString("ItemName");
		String type = sec.getString("ItemType");
		boolean glow = sec.getBoolean("Glow");
		List<String> lore = sec.getStringList("Lore");
		return makeItem(itemName, type, candyType, lore, glow);
	}

	public ItemStack getItemAmount(String candyType, int amount) {
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("CandyItems." + candyType);
		String itemName = sec.getString("ItemName");
		String type = sec.getString("ItemType");
		boolean glow = sec.getBoolean("Glow");
		List<String> lore = sec.getStringList("Lore");
		String xtraLore = ChatColor.translateAlternateColorCodes('&',
				CandyFarms.config.getString("Farm_Menu.StorageGUI.CandyItemsExtraLore"));
		String xtraLoreG = ChatColor.translateAlternateColorCodes('&',
				CandyFarms.config.getString("Farm_Menu.StorageGUI.CandyItemsExtraLoreGet"));
		xtraLore = xtraLore.replaceAll("%amount%", amount + "");
		lore.add(0, xtraLoreG);
		lore.add(0, xtraLore);
		return makeItem(itemName, type, candyType, lore, glow);
	}
}
