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
import com.rcallum.CFarms.items.MinionItem;
import com.rcallum.CFarms.items.SeedBox;
import com.rcallum.CFarms.utils.NBTHandler;
import com.rcallum.CFarms.utils.TokenHook;

public class tokenShopGUI implements Listener {
	private static tokenShopGUI instance;

	public tokenShopGUI() {
		instance = this;
	}

	public static tokenShopGUI getInstance() {
		if (instance == null) {
			instance = new tokenShopGUI();
		}
		return instance;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null)
			return;
		if (e.getCurrentItem().getType() == Material.AIR)
			return;
		Player p = (Player) e.getWhoClicked();
		String guiName = e.getView().getTitle();
		ConfigurationSection sec = CandyFarms.tshop.getConfigurationSection("MainMenu");
		if (guiName.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', sec.getString("GUIName")))) {
			e.setCancelled(true);

			if (e.getCurrentItem().hasItemMeta()) {
				if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
					String itemName = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§", "&");
					if (itemName.equalsIgnoreCase(sec.getString("SeedBox.ItemName"))) {
						seedBoxMenu(p);
					}
					if (itemName.equalsIgnoreCase(sec.getString("Minion.ItemName"))) {
						int price = Integer.valueOf(NBTHandler.getInstance().getNBT(e.getCurrentItem(), "price"));
						if (TokenHook.getInstance().withdrawTokens(p.getUniqueId(), price)) {
							p.getInventory().addItem(MinionItem.minionItem);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									CandyFarms.messages.getString("successfulMinionPurchase").replaceAll("%price%", price+"")));
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									CandyFarms.messages.getString("notEnoughTokens")));
						}
					}

				}
			}

		}

		if (guiName.equalsIgnoreCase(
				ChatColor.translateAlternateColorCodes('&', CandyFarms.tshop.getString("SeedBoxMenu.GUIName")))) {
			e.setCancelled(true);
			if (e.getCurrentItem().hasItemMeta()) {
				if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
					int slot = e.getSlot();
					ArrayList<ItemStack> boxes = SeedBox.getInstance().seedBoxes;
					int start = CandyFarms.tshop.getConfigurationSection("SeedBoxMenu").getInt("StartSlot");
					slot = slot - start;
					ItemStack box = boxes.get(slot);
					int price = Integer.valueOf(NBTHandler.getInstance().getNBT(box, "price"));
					if (TokenHook.getInstance().withdrawTokens(p.getUniqueId(), price)) {
						p.getInventory().addItem(box);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								CandyFarms.messages.getString("successfulBoxPurchase").replaceAll("%price%", price+"")));
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								CandyFarms.messages.getString("notEnoughTokens")));
					}

				}
			}
		}
	}

	public void mainMenu(Player p) {
		ConfigurationSection sec = CandyFarms.tshop.getConfigurationSection("MainMenu");
		String GUIName = ChatColor.translateAlternateColorCodes('&', sec.getString("GUIName"));
		int GUISize = sec.getInt("GUISize");

		Inventory GUI = Bukkit.createInventory(null, GUISize, GUIName);

		String FillerItem = sec.getString("FillerItem");
		int FillerItemDamage = sec.getInt("FillerItemDamage");

		ItemStack fillerItem = new ItemStack(Material.getMaterial(FillerItem), 1, (short) FillerItemDamage);
		ItemMeta im = fillerItem.getItemMeta();
		im.setDisplayName(" ");
		fillerItem.setItemMeta(im);

		for (int i = 0; i < GUISize; i++) {
			GUI.setItem(i, fillerItem);
		}
		ConfigurationSection sb = sec.getConfigurationSection("SeedBox");
		String itemType = sb.getString("Item");
		String itemName = ChatColor.translateAlternateColorCodes('&', sb.getString("ItemName"));
		int slot = sb.getInt("Slot");
		List<String> lore = new ArrayList<String>();
		for (String s : sb.getStringList("Lore")) {
			s = ChatColor.translateAlternateColorCodes('&', s);
			lore.add(s);
		}
		ItemStack sbItem = new ItemStack(Material.getMaterial(itemType));
		im = sbItem.getItemMeta();
		im.setDisplayName(itemName);
		im.setLore(lore);
		sbItem.setItemMeta(im);
		GUI.setItem(slot, sbItem);

		ConfigurationSection m = sec.getConfigurationSection("Minion");
		itemType = m.getString("Item");
		itemName = ChatColor.translateAlternateColorCodes('&', m.getString("ItemName"));
		slot = m.getInt("Slot");
		lore = new ArrayList<String>();
		int price = m.getInt("Price");
		for (String s : m.getStringList("Lore")) {
			s = ChatColor.translateAlternateColorCodes('&', s);
			s = s.replaceAll("%price%", ""+price);
			lore.add(s);
		}
		ItemStack mItem = new ItemStack(Material.getMaterial(itemType));
		im = mItem.getItemMeta();
		im.setDisplayName(itemName);
		im.setLore(lore);
		mItem.setItemMeta(im);
		mItem = NBTHandler.getInstance().setNBT(mItem, "price", price+"");
		GUI.setItem(slot, mItem);

		p.openInventory(GUI);
	}

	public void seedBoxMenu(Player p) {
		ConfigurationSection sec = CandyFarms.tshop.getConfigurationSection("SeedBoxMenu");
		String GUIName = ChatColor.translateAlternateColorCodes('&', sec.getString("GUIName"));
		int GUISize = sec.getInt("GUISize");

		Inventory GUI = Bukkit.createInventory(null, GUISize, GUIName);

		String FillerItem = sec.getString("FillerItem");
		int FillerItemDamage = sec.getInt("FillerItemDamage");

		ItemStack fillerItem = new ItemStack(Material.getMaterial(FillerItem), 1, (short) FillerItemDamage);
		ItemMeta im = fillerItem.getItemMeta();
		im.setDisplayName(" ");
		fillerItem.setItemMeta(im);

		for (int i = 0; i < GUISize; i++) {
			GUI.setItem(i, fillerItem);
		}

		ArrayList<ItemStack> boxes = SeedBox.getInstance().seedBoxes;

		int start = sec.getInt("StartSlot");
		int end = sec.getInt("EndSlot");
		int slot = start;

		for (ItemStack box : boxes) {
			if (slot > end)
				continue;

			String rarity = NBTHandler.getInstance().getNBT(box, "Rarity");
			int price = CandyFarms.config.getInt("SeedConfigs.Prices." + rarity);

			box = NBTHandler.getInstance().setNBT(box, "slot", slot + "");
			box = NBTHandler.getInstance().setNBT(box, "price", price + "");

			String loreLine = CandyFarms.config.getString("SeedConfigs.Prices.Price");
			loreLine = loreLine.replaceAll("%price%", price + "");
			loreLine = loreLine.replaceAll("&", "§");

			im = box.getItemMeta();
			List<String> lore = im.getLore();
			lore.add(0, loreLine);
			im.setLore(lore);
			box.setItemMeta(im);

			GUI.setItem(slot, box);
			slot++;
		}

		p.openInventory(GUI);
	}

	public void minionMenu(Player p) {

	}
}
