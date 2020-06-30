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

public class Farm {
	private static Farm instance;

	public Farm() {
		instance = this;
	}

	public static Farm getInstance() {
		if (instance == null) {
			instance = new Farm();
		}
		return instance;
	}
	
	public ItemStack makeItem(int Level) {
		ConfigurationSection cs = CandyFarms.config.getConfigurationSection("FarmItem");
		String material = cs.getString("Material");
		ItemStack is = new ItemStack(Material.getMaterial(material));
		if (cs.getBoolean("Glow")) {
			is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			
		}
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', cs.getString("Name")));
		List<String> lore = new ArrayList<String>();
		for (String s : cs.getStringList("Lore")) {
			String ss = ChatColor.translateAlternateColorCodes('&', s);
			lore.add(ss);
		}
		im.setLore(lore);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		
		is.setItemMeta(im);
		return is;
	}
}
