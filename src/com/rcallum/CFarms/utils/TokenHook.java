package com.rcallum.CFarms.utils;

import java.util.UUID;

import org.bukkit.entity.Player;

public class TokenHook {
	private static TokenHook instance;

	public TokenHook() {
		instance = this;
	}

	public static TokenHook getInstance() {
		if (instance == null) {
			instance = new TokenHook();
		}
		return instance;
	}
	
	public boolean withdrawTokens(Player p, int amount) {
		UUID uuid = p.getUniqueId();
		/*
		 * 
		 * 
		 * GET TOKENS HERE
		 * if (player has >= tokens)
		 * withdraw tokens return true
		 * else return false
		 * 
		 * 
		 */
		int tokens = 100000;
		
		if (tokens >= amount) {
			tokens = tokens - amount;
			p.sendMessage("Player: " + uuid.toString());
			p.sendMessage("Start Amount: " + 100000);
			p.sendMessage("After cost: " + tokens);
			return true;
		} else {
			return true;
		}
		
		
	}
}
