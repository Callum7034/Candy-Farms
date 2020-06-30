package com.rcallum.CFarms.Listener;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.utils.NBTHandler;

public class EatCandy implements Listener{
	@EventHandler
	public void eatCandy(PlayerItemConsumeEvent e) {
		ItemStack item = e.getItem();
		if (NBTHandler.getInstance().hasNBT(item, "CustomCandy")) {
			String candyType = NBTHandler.getInstance().getNBT(item, "CandyItem");
			ConfigurationSection sec = CandyFarms.config.getConfigurationSection("CandyItems."+candyType);
			ConfigurationSection effects = sec.getConfigurationSection("Effects");
			
			Player p = e.getPlayer();
			for (String effect : effects.getKeys(false)) {
				int time = effects.getInt(effect+".Time");
				int power = effects.getInt(effect+".Power");
				PotionEffectType ptype = PotionEffectType.getByName(effect);
				PotionEffect pe = new PotionEffect(ptype, time*20, power-1, true);
				if (p.hasPotionEffect(ptype)) p.removePotionEffect(ptype);
				p.addPotionEffect(pe);
			}
			
		}
	}
}
