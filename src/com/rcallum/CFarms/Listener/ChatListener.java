package com.rcallum.CFarms.Listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.rcallum.CFarms.CandyFarms;
import com.rcallum.CFarms.FarmManager.FarmData;

public class ChatListener implements Listener{
	private static ChatListener instance;

	public ChatListener() {
		instance = this;
	}

	public static ChatListener getInstance() {
		if (instance == null) {
			instance = new ChatListener();
		}
		return instance;
	}
	
private static HashMap<String, String> Players = new HashMap<String, String>();
	
	public void getPlayerChat(Player p, String uuid) {
		Players.put(p.getName(), uuid);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (Players.containsKey(e.getPlayer().getName())) {
			String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ChatColor.stripColor(e.getMessage())));
			e.setCancelled(true);
			if (Bukkit.getPlayer(name) == null) {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("membersGUIAddError")));
				Players.remove(e.getPlayer().getName());
				return;
			}
			
			FarmData.getInstance().addMember(name, Players.get(e.getPlayer().getName()));
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CandyFarms.messages.getString("membersGUIAddSuccess")));
			Players.remove(e.getPlayer().getName());
		}
	}
}
