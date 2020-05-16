package loot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import perms.PermissionAPI;

public class LootInput implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("loot")) {
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (PermissionAPI.isHighStaff(pl.getUniqueId())) {
					if (LootListener.loot_mode.contains(pl)) {
						LootListener.loot_mode.remove(pl);
						pl.sendMessage(ChatColor.GRAY + "You can no longer place or destroy loot chests.");
						pl.sendMessage(ChatColor.GRAY + "Type /loot to re-enable this.");
						return true;
					}
					
					pl.sendMessage(ChatColor.GRAY + "You can now place or destroy loot chests.");
					pl.sendMessage(ChatColor.GRAY + "Type /loot to disable this.");
					LootListener.loot_mode.add(pl);
				}
			}
		}
		
		return true;
	}

	
	
}
