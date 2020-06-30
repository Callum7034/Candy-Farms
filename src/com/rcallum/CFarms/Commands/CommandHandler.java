package com.rcallum.CFarms.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rcallum.CFarms.FastGrowing.GrowAll;
import com.rcallum.CFarms.GUIListeners.tokenShopGUI;

public class CommandHandler implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("drugshop")) {
			tokenShopGUI.getInstance().mainMenu(p);
		}
		if (command.getName().equalsIgnoreCase("CFarms")) {
			if (p.hasPermission("cfarms.admin")) {
				//Run Main command
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("give")) {
						GiveFarm.getInstance().give(p, args[1]);
						return false;
					}
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("debug")) {
						debug(p);
						GrowAll.getInstance().doAll();
						return false;
					}
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("seeds")) {
						ListSeeds.getInstance().SeedMainMenu(p);
						return false;
					}
				}
				sendHelpMessage(p);
			}
		}
		
		return false;
	}
	
	public void sendHelpMessage(Player p) {
		p.sendMessage("§7§l(§2§l!§7§l) §a§l--== &eCandyFarms &a==--");
		p.sendMessage("§7§l(§2§l!§7§l) §a --== &eAdmin Commands &a==--");
		p.sendMessage("§7§l(§2§l!§7§l)");
		p.sendMessage("§7§l(§2§l!§7§l) §a- /CFarms help");
		p.sendMessage("§7§l(§2§l!§7§l) §a- /CFarms give [player]");
		p.sendMessage("§7§l(§2§l!§7§l) §a- /CFarms seeds");
		p.sendMessage("§7§l(§2§l!§7§l) §a- /");

	}
	
	public void debug(Player p) {
	
	}
}
