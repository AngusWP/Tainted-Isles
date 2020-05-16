package mounts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import enums.Mount;
import game.GameAPI;
import items.Custom;
import net.minecraft.server.v1_14_R1.GenericAttributes;

public class MountAPI {

	public static void start() {	
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (isMounting(pl)) {
						if (getMountingTimer(pl) != 0) { 
							pl.sendMessage(ChatColor.DARK_AQUA + "Summoning your horse in " + getMountingTimer(pl) + "...");
							setMountingTimer(pl, getMountingTimer(pl) - 1);
							
						} else {
							pl.sendMessage(ChatColor.DARK_AQUA + "Mount summoned.");
							MountAPI.mount(pl, Mount.ONE);
						}
					}
				}
			}
		}, 20L, 20L);
	}
	
	public static void stopMounting(Player pl) {
		MountListener.mounting.remove(pl.getUniqueId());
		MountListener.mountLoc.remove(pl.getUniqueId());
		
		pl.sendMessage(ChatColor.DARK_AQUA + "Mount Summon - " + ChatColor.BOLD + "CANCELLED");
	}	
	
	public static boolean isMounted(Player pl) {
		if (pl.isInsideVehicle() && pl.getVehicle().getType() == EntityType.HORSE) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isMounting(Player pl) {
		if (MountListener.mounting.containsKey(pl.getUniqueId())) {
			return true;
		}
		
		return false;
	}
	
	public static void setMountingTimer(Player pl, int time) {
		MountListener.mounting.put(pl.getUniqueId(), time);
	}
	
	public static void setMounting(Player pl, int time, Location loc) {
		MountListener.mounting.put(pl.getUniqueId(), time);
		MountListener.mountLoc.put(pl.getUniqueId(), loc);
	}
	
	public static Location getMountingLoc(Player pl) {
		Location loc = null;
		
		if (isMounting(pl)) {
			loc = MountListener.mountLoc.get(pl.getUniqueId());
		}
		
		return loc;
	}
	
	public static void give(Player pl, Mount m) {
		
		ItemStack item = null;
		
		switch (m) { // coding it as a switch for now as we know there will be more, so just preparing.
			case ONE:
				item = Custom.MOUNT; // only mount atm, so just rename this when we code another.
		}
		
		pl.getInventory().addItem(item);
		
	}
	
	public static int getMountingTimer(Player pl) {
		
		int timer = -1;
		
		if (isMounting(pl)) {
			timer = MountListener.mounting.get(pl.getUniqueId());
		}
		
		
		return timer;
	}
	
	public static boolean isMountItem(ItemStack item) {
		// for now just do a simple check
		
		if (item.getType() == Material.SADDLE) {
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void mount(Player pl, Mount m) {
		MountListener.mounting.remove(pl.getUniqueId());
		MountListener.mountLoc.remove(pl.getUniqueId());
		
		float speed = 0.22F;
		float jump = 0.75F;
		
		/*
		if (h.equals(Horse.TWO)) {
			increase speed and jump
		}
		*/
		
        Horse h = (Horse) pl.getWorld().spawnEntity(pl.getLocation(), EntityType.HORSE);
        h.setAdult();
        h.setTamed(true);
        h.setOwner(pl);
        h.setStyle(Horse.Style.NONE);
        h.setMaxHealth(20);
        h.setHealth(20);
        h.setAgeLock(true);
        h.getInventory().setArmor(new ItemStack(Material.AIR));
        h.setDomestication(100);
        h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        h.setColor(Color.BROWN);
        
        ((CraftLivingEntity) h).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        h.setJumpStrength(jump);
        h.addPassenger(pl);
	}
	
	public static void dismount(Player pl) {
		pl.eject();
		// we have an event to remove the horse, so just eject will do fine here.
	}
	
}
