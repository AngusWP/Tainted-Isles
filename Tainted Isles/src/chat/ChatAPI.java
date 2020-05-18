package chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import factions.FactionAPI;
import perms.PermissionAPI;

public class ChatAPI {
	
	public static String getFullName(Player pl) {
		String tag = "";
		
		
		ChatColor factionColor = ChatColor.WHITE;
		
		String rank = "";

		ChatColor rankColor = PermissionAPI.getRankColor(PermissionAPI.getRank(pl.getUniqueId()));
		
		if (PermissionAPI.isStaff(pl.getUniqueId()) || PermissionAPI.isBuilder(pl.getUniqueId())) {
			rank = PermissionAPI.getRank(pl.getUniqueId()).toString() + " "; // formatting
			
			if (rank.contains("NORMAL")) {
				rank = "";
			}
		}
		
		
		if (FactionAPI.hasData(pl.getUniqueId())) {
			factionColor = FactionAPI.getFactionColor(FactionAPI.getFaction(pl.getUniqueId()));
		}
		

		tag = rankColor + rank + factionColor + pl.getName();
		
		return tag;
	}

}
