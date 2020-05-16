package factions;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import database.DatabaseAPI;
import enums.Faction;
import game.GameAPI;
import items.ItemAPI;

public class FactionAPI {

	public static boolean hasData(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/factions/" + uuid + ".yml");
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static Location getSpawnLocation(Faction faction) {
		if (faction.equals(Faction.GITARI)) {
			return new Location(Bukkit.getWorld("world"), 133, 71, -2267);
		}
		
		return new Location(Bukkit.getWorld("world"), 133, 71, -2267); // temp
	}
	
	public static ChatColor getFactionColor(Faction faction) {
		if (faction.equals(Faction.LEORE)) {
			return ChatColor.GREEN;
		}
		
		if (faction.equals(Faction.GITARI)) {
			return ChatColor.AQUA;
		}

		if (faction.equals(Faction.ALLISTARE)) {
			return ChatColor.RED;
		}

		return ChatColor.GRAY; // no faction
	}
	
	public static void createData(UUID uuid, Faction faction) {
		DatabaseAPI.createYMLFile("factions/" + uuid + ".yml");
		DatabaseAPI.setString("factions/" + uuid + ".yml", "faction", getFaction(faction));
	}

	public static Faction getFaction(UUID uuid) {
		return getFaction(DatabaseAPI.getString("factions/" + uuid + ".yml", "faction"));
	}
	
	public static String getFaction(Faction faction) {
		String f = faction.toString().substring(0, 1).toUpperCase() + faction.toString().substring(1).toLowerCase();
		return f;
	}
	
	public static Faction getFaction(String s) {
		if (Faction.valueOf(s.toUpperCase()) != null) {
			return Faction.valueOf(s.toUpperCase());
		}
		
		return null;
	}
	
	public static void openMenu(Player pl) {
		pl.playSound(pl.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 1F);
		Inventory inv = Bukkit.createInventory(null, 9, "Choose your faction!");
		
		inv.addItem(ItemAPI.create(Material.SHIELD, ChatColor.WHITE + "Gitari Empire", Arrays.asList(ChatColor.WHITE + "Click here to choose this faction."), 1));
		inv.addItem(ItemAPI.create(Material.SHIELD, ChatColor.WHITE + "Kingdom of Leore", Arrays.asList(ChatColor.WHITE + "Click here to choose this faction."), 1));
		inv.addItem(ItemAPI.create(Material.SHIELD, ChatColor.WHITE + "Allista're Freehold", Arrays.asList(ChatColor.WHITE + "Click here to choose this faction."), 1));
		
		pl.openInventory(inv);
	}
	
}
