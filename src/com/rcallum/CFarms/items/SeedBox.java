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

public class SeedBox {
	private static SeedBox instance;

	public SeedBox() {
		instance = this;
	}

	public static SeedBox getInstance() {
		if (instance == null) {
			instance = new SeedBox();
		}
		return instance;
	}

	public ArrayList<ItemStack> seedBoxes;

	public void loadSeeds() {
		seedBoxes = new ArrayList<ItemStack>();
		ConfigurationSection sec = CandyFarms.config.getConfigurationSection("SeedConfigs");
		for (String rarity : sec.getStringList("Sections")) {
			sec = CandyFarms.config.getConfigurationSection("SeedConfigs.SeedBoxes");

			String itemName = sec.getString("Name");
			String type = sec.getString("Item");
			boolean glow = sec.getBoolean("Glow");
			List<String> lore = sec.getStringList("Lore");
			int price = CandyFarms.config.getConfigurationSection("SeedConfigs.Prices").getInt(rarity);

			seedBoxes.add(makeItem(itemName, type, lore, glow, rarity, price));

		}
	}

	public ItemStack makeItem(String itemName, String type, List<String> itemLore, boolean glow, String rarity, int price) {
		ItemStack item = new ItemStack(Material.getMaterial(type));

		NBTHandler nbt = NBTHandler.getInstance();
		item = nbt.setNBT(item, "SeedBox", "true");
		item = nbt.setNBT(item, "Rarity", rarity);
		item = nbt.setNBT(item, "price", price+"");

		if (glow)
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

		ItemMeta im = item.getItemMeta();
		itemName = itemName.replaceAll("%name%", rarity);
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

		List<String> lore = new ArrayList<String>();
		for (String s : itemLore) {
			if (s.contains("%seedname%")) {
				ConfigurationSection sec = CandyFarms.config.getConfigurationSection("Seeds");
				if (sec.contains(rarity)) {
					for (String itemNumber : sec.getConfigurationSection(rarity).getKeys(false)) {
						sec = CandyFarms.config.getConfigurationSection("Seeds")
								.getConfigurationSection(rarity + "." + itemNumber);
						String seedName = ChatColor.translateAlternateColorCodes('&', sec.getString("SeedName"));
						seedName = seedName.replaceAll("_", " ");
						String ss = ChatColor.translateAlternateColorCodes('&', s);
						ss = ss.replaceAll("%seedname%", seedName);
						lore.add(ss);

					}
				}
			} else {
				String ss = ChatColor.translateAlternateColorCodes('&', s);
				ss = ss.replaceAll("%name%", rarity);
				lore.add(ss);
			}

		}
		im.setLore(lore);

		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		item.setItemMeta(im);
		return item;
	}
}
