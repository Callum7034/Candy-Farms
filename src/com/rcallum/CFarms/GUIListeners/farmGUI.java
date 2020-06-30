package com.rcallum.CFarms.GUIListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import org.bukkit.inventory.meta.SkullMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.Listener.ChatListener;
import com.rcallum.CFarms.utils.NBTHandler;

public class farmGUI implements Listener{
	ConfigurationSection data = CandyFarms.getInstance().getData();
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		ConfigurationSection messages = CandyFarms.messages;
		ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu");
		if (e.getView().getTitle().equalsIgnoreCase(color(config.getString("GUIName"))))
	    {
	      Player p = (Player)e.getWhoClicked();
	      if (e.getCurrentItem() == null)
	      {
	        e.setCancelled(true);
	        return;
	      }
	      if ((e.getCurrentItem().hasItemMeta()) && 
	        (e.getCurrentItem().getItemMeta().hasDisplayName()))
	      {
	        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
	        
	        itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("InfoItemName"));
	        if (itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("MembersName")))
	        {
	          String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
	          membersGUI(locID, p);
	        }
	        if (itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("MinionItemName")))
	        {
	          String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
	          MinionGUI.getInstance().openGUI(p, locID);
	        }
	        
	        if (itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("StorageItemName")))
	        {
	          String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
	          StorageGUI.getInstance().openGUI(p, locID);
	        }
	        
	        if (itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("DeleteItemName")))
	        {
	          String locID = NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(0), "UUID");
	          FarmData.getInstance().removeFarmAndData(p, locID);
	          p.closeInventory();
	        }
	      }
	      e.setCancelled(true);
	    }
	    if (e.getView().getTitle().equalsIgnoreCase(color(config.getString("MinionGUI.GUIName")))) {
	      e.setCancelled(true);
	    }
	    if (e.getView().getTitle().equalsIgnoreCase(color(config.getString("MembersMenu.MenuName"))))
	    {
	      Player p = (Player)e.getWhoClicked();
	      if (e.getCurrentItem() == null)
	      {
	        e.setCancelled(true);
	        return;
	      }
	      if ((e.getCurrentItem().hasItemMeta()) && 
	        (e.getCurrentItem().getItemMeta().hasDisplayName()))
	      {
	        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
	        if (itemName.replaceAll("§", "&").equalsIgnoreCase(config.getString("MembersMenu.AddPlayer")))
	        {
	          p.sendMessage(" ");
	          p.sendMessage(" ");
	          p.sendMessage(color(messages.getString("membersGUIAddMessage")));
	          p.sendMessage(" ");
	          p.sendMessage(" ");
	          ChatListener.getInstance().getPlayerChat(p, 
	            NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(40), "UUID"));
	          p.closeInventory();
	          e.setCancelled(true);
	          return;
	        }
	        if (e.getCurrentItem().getType() == Material.getMaterial(config.getString("MembersMenu.ExitItem"))) {
	          p.closeInventory();
	        }
	        if (e.getCurrentItem().getType() == Material.SKULL_ITEM)
	        {
	          String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
	          Player Uplayer = Bukkit.getPlayer(name);
	          FarmData.getInstance().removeMember(Uplayer.getUniqueId().toString(), 
	            NBTHandler.getInstance().getNBT(e.getClickedInventory().getItem(40), "UUID"));
	          p.closeInventory();
	          p.sendMessage(color(messages.getString("membersRemove")));
	        }
	      }
	      e.setCancelled(true);
	    }
	  }
	  
	  public String color(String input)
	  {
	    return ChatColor.translateAlternateColorCodes('&', input);
	  }
	  
	  public void membersGUI(String uuid, Player p)
	  {
	    ItemStack bottomRow = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)3);
	    ItemMeta im = bottomRow.getItemMeta();
	    im.setDisplayName(" ");
	    
	    bottomRow.setItemMeta(im);
	    bottomRow = NBTHandler.getInstance().setNBT(bottomRow, "UUID", uuid);
	    
	    ConfigurationSection config = CandyFarms.config.getConfigurationSection("Farm_Menu.MembersMenu");
	    
	    ItemStack addMember = new ItemStack(Material.getMaterial(config.getString("AddPlayerItem")));
	    im = addMember.getItemMeta();
	    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("AddPlayer")));
	    addMember.setItemMeta(im);
	    
	    ItemStack exit = new ItemStack(Material.getMaterial(config.getString("ExitItem")));
	    im = exit.getItemMeta();
	    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Exit")));
	    exit.setItemMeta(im);
	    
	    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
	    SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
	    
	    Inventory membersGUI = Bukkit.createInventory(null, 54, 
	      ChatColor.translateAlternateColorCodes('&', config.getString("MenuName")));
	    
	    Set<UUID> members = FarmData.getInstance().getMembers(uuid);
	    int counter = 1;
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.translateAlternateColorCodes('&', config.getString("OwnerHeadLore")));
	    String nameOwner = data.getString("Farms." + uuid + ".owner");
	    UUID ownerID = UUID.fromString(nameOwner);
	    skullMeta.setOwner(Bukkit.getOfflinePlayer(ownerID).getName());
	    skullMeta.setDisplayName(
	      ChatColor.translateAlternateColorCodes('&', "&e" + Bukkit.getOfflinePlayer(ownerID).getName()));
	    skullMeta.setLore(lore);
	    head.setItemMeta(skullMeta);
	    
	    membersGUI.setItem(0, head);
	    
	    lore.clear();
	    lore.add(ChatColor.translateAlternateColorCodes('&', config.getString("HeadLore")));
	    for (UUID membersuuid : members) {
	      if (membersuuid != p.getUniqueId())
	      {
	        skullMeta.setOwner(Bukkit.getPlayer(membersuuid).getName());
	        skullMeta.setDisplayName(
	          ChatColor.translateAlternateColorCodes('&', "&e" + Bukkit.getPlayer(membersuuid).getName()));
	        skullMeta.setLore(lore);
	        head.setItemMeta(skullMeta);
	        
	        membersGUI.setItem(counter, head);
	        counter++;
	      }
	    }
	    for (int i = 36; i < 45; i++) {
	      membersGUI.setItem(i, bottomRow);
	    }
	    membersGUI.setItem(45, addMember);
	    membersGUI.setItem(53, exit);
	    p.openInventory(membersGUI);
	  }
	
}