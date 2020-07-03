package com.rcallum.CFarms.MinionManager;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

import com.rcallum.CFarms.CandyFarms;

public class ArmorStandMinion {
	private static ArmorStandMinion instance;

	public ArmorStandMinion() {
		instance = this;
	}

	public static ArmorStandMinion getInstance() {
		if (instance == null) {
			instance = new ArmorStandMinion();
		}
		return instance;
	}
	
	public void spawnArmorStand(String farmID) {
		Location spawnLoc = getMid(farmID);
		ArmorStand as = (ArmorStand) spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
		
		String minionName = ChatColor.translateAlternateColorCodes('&', CandyFarms.config.getString("MinionName"));
		
		as.setCustomName(minionName);
		as.setCustomNameVisible(true);
        as.setBasePlate(false);
        as.setArms(true);
        as.setVisible(true);
        //as.setsetInvulnerable(true);
        as.setCanPickupItems(false);
        as.setGravity(false);
        as.setSmall(true);
	}
	
	public void removeArmorStand(String farmID) {
		getArmorStand(farmID).remove();
	}
	
	public ArmorStand getArmorStand(String farmID) {
		Location loc = getMid(farmID);
		Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
		
		String minionName = ChatColor.translateAlternateColorCodes('&', CandyFarms.config.getString("MinionName"));
		for (Entity e : entities) {
			if (e.isCustomNameVisible()) {
				String name = e.getCustomName();
				if (name == minionName) {
					if (e.getType() == EntityType.ARMOR_STAND) {
						return (ArmorStand)e;
					}
					
				}
			}
		}
		return null;
		
	}
	
	
	public void makeArmorStandLookAtBlock(ArmorStand stand, Location loc) {
	    Location lookDir = loc.subtract(stand.getLocation());
	    EulerAngle poseAngle = directionToEuler(lookDir);      
	    stand.setHeadPose(poseAngle);
	}
	 
	private EulerAngle directionToEuler(Location dir) {
	    double xzLength = Math.sqrt(dir.getX()*dir.getX() + dir.getZ()*dir.getZ());
	    double pitch = Math.atan2(xzLength, dir.getY()) - Math.PI / 2;
	    double yaw = -Math.atan2(dir.getX(), dir.getZ()) + Math.PI / 4;
	    return new EulerAngle(pitch, yaw, 0);
	}
	
	public Location getMid(String farmID) {
		Location start = stringToLoc(farmID);
		String direction = CandyFarms.getInstance().getData().getString("Farms." + farmID + ".Direction");
		Location mid = start;
		if (direction.equalsIgnoreCase("North")) {
			mid.add(0, 0, -3);
		}
		if (direction.equalsIgnoreCase("South")) {
			mid.add(0, 0, 3);
		}
		if (direction.equalsIgnoreCase("East")) {
			mid.add(3, 0, 0);
		}
		if (direction.equalsIgnoreCase("West")) {
			mid.add(-3, 0, 0);
		}
		mid.add(0, 1.5, 0);
		return mid;
		
	}
	
	public String locToString(Location loc) {
		String uuid = (loc.getWorld().getName() + "," + loc.getBlockX() + "," 
				+ loc.getBlockY() + "," + loc.getBlockZ());
		return uuid;
	}
	
	public Location stringToLoc(String s) {
		String[] words = s.split(",");
		World world = Bukkit.getWorld(words[0]);
		int x = Integer.parseInt(words[1]);
		int y = Integer.parseInt(words[2]);
		int z = Integer.parseInt(words[3]);
		Location checkLoc = new Location(world, x, y, z);
		return checkLoc;
	}
}
