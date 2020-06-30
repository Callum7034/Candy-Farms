package com.rcallum.CFarms.utils;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class NBTHandler {
	private static NBTHandler instance;

	public NBTHandler() {
		instance = this;
	}

	public static NBTHandler getInstance() {
		if (instance == null) {
			instance = new NBTHandler();
		}
		return instance;
	}
	
	public ItemStack setNBT(ItemStack item, String key, String value) {
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		compound.setString(key, value);
		nmsItem.setTag(compound);
		ItemStack output = CraftItemStack.asBukkitCopy(nmsItem);
		return output;
	}
	
	public String getNBT(ItemStack item, String key) {
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		if (!compound.hasKey(key)) {
			return "";
		}
		return compound.getString(key);
	}
	
	public boolean hasNBT(ItemStack item, String key) {
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		if (compound.hasKey(key)) {
			return true;
		}
		else {
			return false;
		}
	}
}
