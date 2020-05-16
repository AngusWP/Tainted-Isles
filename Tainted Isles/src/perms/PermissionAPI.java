package perms;

import java.util.UUID;

import org.bukkit.ChatColor;

import database.DatabaseAPI;
import enums.Rank;
import player.PlayerAPI;

public class PermissionAPI {
	
	public static boolean isStaff(UUID uuid) {
		if (getRank(uuid) == Rank.MOD || isHighStaff(uuid)) {
			return true;
		}
		
		return false;
	}

	public static boolean isHighStaff(UUID uuid) {
		if (getRank(uuid) == Rank.CM || getRank(uuid) == Rank.DEV || getRank(uuid) == Rank.ADMIN) {
			return true;
		}
		
		return false;
	}
	
	public static ChatColor getRankColor(Rank rank) {
		if (rank.equals(Rank.DEV)) {
			return ChatColor.GOLD;
		}
		
		if (rank.equals(Rank.ADMIN)) {
			return ChatColor.GOLD;
		}

		if (rank.equals(Rank.CM)) {
			return ChatColor.GOLD;
		}
		
		if (rank.equals(Rank.BUILDER)) {
			return ChatColor.GREEN;
		}
		
		if (rank.equals(Rank.DESIGNER)) {
			return ChatColor.GREEN;
		}
		
		if (rank.equals(Rank.MOD)){
			return ChatColor.GREEN;
		}

		return ChatColor.GRAY; // no faction
	}
	
	public static boolean hasRank(UUID uuid) {
		if (PlayerAPI.hasPlayedBefore(uuid)) {
			return true;
		}
		
		return false;
	}
	
	public static void createData(UUID uuid) {
		if (!hasRank(uuid)) {
			DatabaseAPI.createYMLFile("perms/" + uuid + ".yml");
			DatabaseAPI.setString("perms/" + uuid + ".yml", "rank", Rank.NORMAL.toString().toUpperCase());	
		}
	}
	
	public static void setRank(UUID uuid, Rank rank) {
		DatabaseAPI.setString("perms/" + uuid + ".yml", "rank", rank.toString().toUpperCase()); 
		// the reason we don't check if the file exists is because this creates it anyway.
	}
	
	public static boolean isDeveloper(UUID uuid) {
		if (getRank(uuid) == Rank.DEV) {
			return true;
		}
		
		return false;
	}
	
	public static Rank getRank(UUID uuid) {
		if (!hasRank(uuid)){
			return Rank.NORMAL;
		}
		
		return Rank.valueOf(DatabaseAPI.getString("perms/" + uuid + ".yml", "rank").toUpperCase());
	}
}
