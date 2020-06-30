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

public class MinionItem {
	private static MinionItem instance;

	public MinionItem() {
		instance = this;
	}

	public static MinionItem getInstance() {
		if (instance == null) {
			instance = new MinionItem();
		}
		return instance;
	}
	
	public static ItemStack minionItem;
	
	public void loadMinionItem() {
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("MinionConfigs");
		String itemname = sec.getString("ItemName");
		String itemType = sec.getString("ItemType");
		boolean glow = sec.getBoolean("Glow");
		List<String> lore = new ArrayList<String>();
		ItemStack item = new ItemStack(Material.getMaterial(itemType));
		if (glow) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemname));
		for (String s : sec.getStringList("Lore")) {
			String ss = ChatColor.translateAlternateColorCodes('&', s);
			lore.add(ss);
		}
		im.setLore(lore);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(im);
		
		item = NBTHandler.getInstance().setNBT(item, "Minion", "true");
		minionItem = item;
	}
}
