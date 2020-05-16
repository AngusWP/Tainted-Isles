package player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import chat.ChatAPI;
import database.DatabaseAPI;
import game.GameAPI;

public class PlayerAPI {
	
	public static void sendDeathMessage(Player pl, DamageCause cause) {
		List<Player> send_to = new ArrayList<Player>();

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getWorld() == pl.getWorld()) {
				if (p.getLocation().distance(pl.getLocation()) <= 75) {
					send_to.add(p);
				}
			}
		}

		List<String> death_messages = new ArrayList<String>();
		
		if (cause.equals(DamageCause.DROWNING)) {
			death_messages.add(ChatColor.GRAY + " had a drinking contest with the ocean, and lost.");
		} else {
			death_messages.add(ChatColor.GRAY + " was mauled to death.");
			death_messages.add(ChatColor.GRAY + " met an unforunate end.");
		}

		int msg = new Random().nextInt(death_messages.size());
		
		for (Player p : send_to) {
			p.sendMessage(ChatAPI.getFullName(pl) + death_messages.get(msg));
		}
	}
	
	public static boolean isOnline(String name) {
		try {
			if (Bukkit.getPlayer(name) != null) {
				return true;
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isOnline(UUID uuid) {
		try {
			if (Bukkit.getPlayer(uuid) != null) {
				return true;
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Player getOnlinePlayer(String name) {
		if (isOnline(name)) {
			return Bukkit.getPlayer(name);
		}
		
		return null;
	}
	
	public static Player getOnlinePlayer(UUID uuid) {
		if (isOnline(uuid)) {
			return Bukkit.getPlayer(uuid);
		}
		
		return null;
	}
	
	public static boolean isNPC(Player pl) {
		if (pl.hasMetadata("NPC")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean hasPlayedBefore(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/players/" + uuid + ".yml");
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static void createPlayerData(Player pl) {
		DatabaseAPI.setString("players/" + pl.getUniqueId() + ".yml", "Username (the first time that the player joined)", pl.getName()); 
		// this creates the file for us, and stores the name that they joined with originally.
	}
	
}
