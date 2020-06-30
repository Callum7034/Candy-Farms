package com.rcallum.CFarms.Listener;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.FarmData;
import com.rcallum.CFarms.FarmManager.PendingFarmData;
import com.rcallum.CFarms.items.SeedMaker;
import com.rcallum.CFarms.utils.NBTHandler;

public class ClickFarm
  implements Listener
{
  @EventHandler
  public void onClick(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
    {
      String playerUUID = p.getUniqueId().toString();
      String result = PendingFarmData.getInstance().isBlockClaim(e.getClickedBlock().getLocation(), playerUUID);
      if (result != null)
      {
        e.setCancelled(true);
        if (result == playerUUID)
        {
          Location loc = e.getClickedBlock().getLocation();
          pendingGUI(p, PendingFarmData.getInstance().locToString(loc));
        }
      }
      if (FarmData.getInstance().isBlockClaim(e.getClickedBlock().getLocation()))
      {
        Set<UUID> members = FarmData.getInstance()
          .getMembers(PendingFarmData.getInstance().locToString(e.getClickedBlock().getLocation()));
        members.add(FarmData.getInstance().getOwner(
          PendingFarmData.getInstance().locToString(e.getClickedBlock().getLocation())));
        e.setCancelled(true);
        if (members.contains(UUID.fromString(playerUUID)))
        {
          Location loc = e.getClickedBlock().getLocation();
          realFarmGUI(p, PendingFarmData.getInstance().locToString(loc));
        }
      }
    }
    if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
    {
      if (p.getItemInHand().getType() == null) {
        return;
      }
      if (p.getItemInHand().getType() == Material.AIR) {
        return;
      }
      if (NBTHandler.getInstance().hasNBT(p.getItemInHand(), "SeedBox"))
      {
        String rarity = NBTHandler.getInstance().getNBT(p.getItemInHand(), "Rarity");
        ConfigurationSection sec = CandyFarms.config.getConfigurationSection("Seeds." + rarity);
        int size = sec.getKeys(false).size();
        
        Random r = new Random();
        int rNum = r.nextInt(size);
        int counter = 0;
        for (String s : sec.getKeys(false))
        {
          if (rNum == counter)
          {
            p.getInventory().addItem(SeedMaker.getInstance().getItem(rarity, sec.getString(s + ".SeedName")));
            e.setCancelled(true);
          }
          counter++;
        }
        if (p.getItemInHand().getAmount() == 1) {
          p.setItemInHand(null);
        } else {
          p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
        }
      }
    }
  }
  
  public void realFarmGUI(Player p, String id)
  {
    ConfigurationSection c = CandyFarms.config.getConfigurationSection("Farm_Menu");
    Inventory gui = Bukkit.createInventory(null, c.getInt("GUISize"), color(c.getString("GUIName")));
    ItemStack is = new ItemStack(Material.getMaterial(c.getString("GUIFillerItem")), 1, 
      (short)c.getInt("GUIFillerItemDamage"));
    is = NBTHandler.getInstance().setNBT(is, "UUID", id);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(" ");
    is.setItemMeta(im);
    for (int i = 0; i < 27; i++) {
      gui.setItem(i, is);
    }
    is = new ItemStack(Material.getMaterial(c.getString("InfoItem")));
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("InfoItemName")));
    is.setItemMeta(im);
    gui.setItem(c.getInt("InfoItemSlot"), is);
    
    is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("MembersName")));
    is.setItemMeta(im);
    gui.setItem(c.getInt("MembersItemSlot"), is);
    
    is = new ItemStack(Material.getMaterial(c.getString("MinionItem")));
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("MinionItemName")));
    is.setItemMeta(im);
    gui.setItem(c.getInt("MinionItemSlot"), is);
    
    is = new ItemStack(Material.getMaterial(c.getString("StorageItem")));
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("StorageItemName")));
    is.setItemMeta(im);
    gui.setItem(c.getInt("StorageItemSlot"), is);
    
    is = new ItemStack(Material.getMaterial(c.getString("DeleteItem")));
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("DeleteItemName")));
    is.setItemMeta(im);
    gui.setItem(c.getInt("DeleteItemSlot"), is);
    
    p.openInventory(gui);
  }
  
  public void pendingGUI(Player p, String id)
  {
    FileConfiguration c = CandyFarms.config;
    Inventory gui = Bukkit.createInventory(null, 27, "§8Placement Confirmation");
    ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
    is = NBTHandler.getInstance().setNBT(is, "UUID", id);
    ItemMeta im = is.getItemMeta();
    im.setDisplayName(" ");
    is.setItemMeta(im);
    for (int i = 0; i < 27; i++) {
      gui.setItem(i, is);
    }
    is = new ItemStack(Material.STAINED_CLAY, 1, (short)13);
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("placementConfirm")));
    is.setItemMeta(im);
    gui.setItem(15, is);
    
    is = new ItemStack(Material.STAINED_CLAY, 1, (short)14);
    im = is.getItemMeta();
    im.setDisplayName(color(c.getString("placementCancel")));
    is.setItemMeta(im);
    gui.setItem(11, is);
    
    p.openInventory(gui);
  }
  
  public String color(String input)
  {
    return ChatColor.translateAlternateColorCodes('&', input);
  }
}
