package com.rcallum.CFarms.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.utils.NBTHandler;

import net.md_5.bungee.api.ChatColor;

public class SeedMaker {
	private static SeedMaker instance;

	public SeedMaker() {
		instance = this;
	}

	public static SeedMaker getInstance() {
		if (instance == null) {
			instance = new SeedMaker();
		}
		return instance;
	}
	
	
	public Set<ItemStack> seeds;
	
	public void loadSeeds() {
		seeds = new HashSet<ItemStack>();
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("Seeds");
		for (String rarity : sec.getKeys(false)) {
			sec = CandyFarms.config.getConfigurationSection("Seeds");
			for (String num : sec.getConfigurationSection(rarity).getKeys(false)) {
				sec = CandyFarms.config.getConfigurationSection("Seeds");
				sec = sec.getConfigurationSection(rarity).getConfigurationSection(num);
				
				String itemName = sec.getString("ItemName");
				String type = sec.getString("ItemType");
				String seedName = sec.getString("SeedName");
				boolean glow = sec.getBoolean("Glow");
				List<String> lore = sec.getStringList("Lore");
				
				seeds.add(makeItem(itemName, type, seedName, lore, glow, rarity));
			}
		}
	}
	
	public ItemStack makeItem(String itemName, String type, String seedName, 
			List<String> itemLore, boolean glow, String rarity) {
		ItemStack item = new ItemStack(Material.getMaterial(type));
		
		NBTHandler nbt = NBTHandler.getInstance();
		item = nbt.setNBT(item, "CustomSeed", "true");
		item = nbt.setNBT(item, "Seed", seedName);
		item = nbt.setNBT(item, "Rarity", rarity);
		
		if (glow) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
		
		List<String> lore = new ArrayList<String>();
		for (String s : itemLore) {
			String ss = ChatColor.translateAlternateColorCodes('&', s);
			lore.add(ss);
		}
		im.setLore(lore);
		
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(im);
		return item;
	}
	
	public ItemStack getItem(String rarity, String seedType) {
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("Seeds."+rarity);
		for (String num : sec.getKeys(false)) {
			sec = CandyFarms.config.getConfigurationSection("Seeds."+rarity+"."+num);
			if (seedType.equalsIgnoreCase(sec.getString("SeedName"))) {
				String itemName = sec.getString("ItemName");
				String type = sec.getString("ItemType");
				String seedName = sec.getString("SeedName");
				boolean glow = sec.getBoolean("Glow");
				List<String> lore = sec.getStringList("Lore");
				return makeItem(itemName, type, seedName, lore, glow, rarity);
			}
		}
		return null;
	}
}
