package enhancement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import database.DatabaseAPI;
import enums.Enhancement;
import game.GameAPI;
import graphics.GraphicsAPI;
import items.ItemAPI;

public class EnhancementAPI {

	public static boolean hasData(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/enhancements/" + uuid);
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static int getAdditionalHealth(Player pl) {
		int hp = 0;
		
		if (hasData(pl.getUniqueId())) {
			if (isComplete(pl, Enhancement.PLACEHOLDER)) {
				hp += 5;
				// so this is how we will handle any enhancement health increase
			}	
		}
		
		return hp;
	}
	
	public static void createData(UUID uuid) {
		if (!hasData(uuid)) {
			DatabaseAPI.createFolder("/enhancements/" + uuid);
		}
	}
	
	public static String getName(Enhancement enhancement) {
		String name = enhancement.toString().toLowerCase();
		name = name.substring(0, 1).toUpperCase() + enhancement.toString().substring(1).toLowerCase();
		
		if (name.contains("_")) {
			
			String[] split = name.split("_");
			String holder = "";
			int spaces = 0;
			
			for (String s : split) {
				holder += s.substring(0, 1).toUpperCase();
				spaces++;
				
				if (split.length != spaces) {
					holder += " ";
				}
			}
			
			
			name = holder;
		}
		
		return name;
	}
	
	public static void updateStats(UUID uuid) {
		
		File placeholder = new File(GameAPI.getInstance().getDataFolder() + "/enhancements/" + uuid + "/placeholder.yml");
		
		List<File> list = new ArrayList<File>();
		list.add(placeholder);
		
		if (hasData(uuid)) {
			// check for faction, and update any new skills
			// make sure that the name of the .yml file is the EXACT SAME AS THE ENUM. VERY IMPORTANT.
			
			for (File file : list) {
				YamlConfiguration config = new YamlConfiguration();
				config.set("key", 0);
				
				if (!file.exists()) {
					try {
						file.createNewFile();
						config.save(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		} 
	}
	
	public static void setComplete(Player pl, Enhancement enhancement) { 
		// i don't think strings are going to be an aspect but they could be changed. it's all just going to be saved integer data, but there's support for strings.

		String nameForTitle = getName(enhancement);
		
		GraphicsAPI.sendTitle(pl, ChatColor.GOLD + nameForTitle, ChatColor.GRAY + "Enhancement Complete");
		String name = enhancement.toString().toLowerCase();
		
		if (DatabaseAPI.isInt("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key")) {
			setAmount(pl, -1, enhancement);
		}
		
		if (DatabaseAPI.isString("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key")) {
			DatabaseAPI.setString("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key", "COMPLETE");
		}
	}
	
	public static boolean isComplete(Player pl, Enhancement enhancement) {
		String name = enhancement.toString().toLowerCase();
		
		if (DatabaseAPI.isInt("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key")) {
			if (DatabaseAPI.getInt("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key") == -1) {
				return true;
			}
		}
		
		if (DatabaseAPI.isString("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key")) {
			if (DatabaseAPI.getString("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key") == "COMPLETE") {
				return true;
			}
		}
		
		return false;
	}

	public static void setAmount(Player pl, int amount, Enhancement enhancement) {
		String name = enhancement.toString().toLowerCase();
		DatabaseAPI.setInt("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key", amount);
	}
	
	public static void openInventory(Player pl) {
		Inventory inv = Bukkit.createInventory(null, 9, "Enhancements");
		pl.playSound(pl.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 1F);

		// check for faction and add the proper ones
		
		ItemStack placeholder = getEnhancementItem(pl, Enhancement.PLACEHOLDER);
		
		
		inv.setItem(0, placeholder);
		pl.openInventory(inv);
	}
	
	public static int getAmount(Player pl, Enhancement enhancement) {
		String name = enhancement.toString().toLowerCase();
		
		return DatabaseAPI.getInt("enhancements/" + pl.getUniqueId() + "/" + name + ".yml", "key");
	}
	
	public static ItemStack getEnhancementItem(Player pl, Enhancement enhancement) {
		// so here we get the stats and specifics for the inventory
		
		String name = getName(enhancement);
		
		if (isComplete(pl, enhancement)) {
			name += " " + ChatColor.GRAY + "[" + ChatColor.GREEN + "✔" + ChatColor.GRAY + "]";
		}
		
		List<String> l = new ArrayList<String>();
		
		
		switch (enhancement) {
			case PLACEHOLDER:
				if (!isComplete(pl, enhancement)) {
					l = Arrays.asList(ChatColor.WHITE + "Kill 5 skeletons to recieve 5 bonus health.", ChatColor.GRAY.toString() + "[" + getAmount(pl, enhancement) + "/5]");
				} else {
					l = Arrays.asList(ChatColor.WHITE + "Kill 5 skeletons to recieve 5 bonus health.", ChatColor.GRAY + "[5/5]");
				}
			
		}
		
		
		
		ItemStack item = ItemAPI.create(Material.MUSIC_DISC_CHIRP, ChatColor.WHITE + name, l, 1);
		return item;
	}
	
}
