package com.rcallum.CFarms.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.items.SeedBox;
import com.rcallum.CFarms.items.SeedMaker;

public class ListSeeds implements Listener {
	private static ListSeeds instance;

	public ListSeeds() {
		instance = this;
	}

	public static ListSeeds getInstance() {
		if (instance == null) {
			instance = new ListSeeds();
		}
		return instance;
	}

	public void ListSeedsGUI(Player p) {
		Inventory GUI = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Seeds");
		int counter = 0;
		for (ItemStack item : SeedMaker.getInstance().seeds) {
			if (counter > 53)
				continue;
			GUI.setItem(counter, item);
			counter++;
		}
		p.openInventory(GUI);
	}

	public void SeedBoxGUI(Player p) {
		Inventory GUI = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Seed Boxes");
		int counter = 0;
		for (ItemStack item : SeedBox.getInstance().seedBoxes) {
			if (counter > 53)
				continue;
			GUI.setItem(counter, item);
			counter++;
		}
		p.openInventory(GUI);
	}

	public void SeedMainMenu(Player p) {
		Inventory GUI = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Seed Menu");
		String boxes = CandyFarms.config.getString("SeedConfigs.SeedBoxes.Item");

		ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta im = filler.getItemMeta();
		im.setDisplayName(" ");
		filler.setItemMeta(im);
		for (int i = 0; i < GUI.getSize(); i++) {
			GUI.setItem(i, filler);
		}
		ItemStack box = new ItemStack(Material.getMaterial(boxes));
		im = box.getItemMeta();
		im.setDisplayName("§bSeed Boxes");
		box.setItemMeta(im);

		ItemStack seeds = new ItemStack(Material.SEEDS);
		im = seeds.getItemMeta();
		im.setDisplayName("§aSeeds");
		seeds.setItemMeta(im);

		GUI.setItem(2, box);
		GUI.setItem(6, seeds);
		p.openInventory(GUI);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		try {
			if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Seed Menu")) {
				e.setCancelled(true);
				if (e.getCurrentItem().getType() == Material.SEEDS) {
					ListSeedsGUI((Player) e.getWhoClicked());
				}
				String boxes = CandyFarms.config.getString("SeedConfigs.SeedBoxes.Item");
				if (e.getCurrentItem().getType() == Material.getMaterial(boxes)) {
					SeedBoxGUI((Player) e.getWhoClicked());
				}
			}
			if ((e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Seed Boxes"))
					|| (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Seeds"))) {
				e.setCancelled(true);
				if (e.getCurrentItem() == null)
					return;
				e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
			}
		} catch (Exception ex) {
			return;
		}
		
	}
}
