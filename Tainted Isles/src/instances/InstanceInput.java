package instances;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import parties.PartyAPI;
import perms.PermissionAPI;

public class InstanceInput implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (cs instanceof Player) {
			
			Player pl = (Player) cs;
			
			if (cmd.getName().equalsIgnoreCase("instance")) {
				if (PermissionAPI.isHighStaff(pl.getUniqueId())) {
					
					if (InstanceAPI.inInstance(pl.getUniqueId())) {
						pl.sendMessage(ChatColor.GRAY + "You are already in a loaded instance.");
					} else {
						
						if (PartyAPI.isInParty(pl) && PartyAPI.getParty(pl).getOwner().equals(pl)) {
							pl.sendMessage(ChatColor.GRAY + "Loading instance...");
							// at a later date check for args and if the dungeon actually exists
							
							InstanceAPI.startInstance("T1Dungeon", pl);	
						} else {
							pl.sendMessage(ChatColor.GRAY + "You must be the owner of a party to start an instance.");
						}
						

					}
				}
			}
			
		}
	
		
		return true;
	}

	
	
}
