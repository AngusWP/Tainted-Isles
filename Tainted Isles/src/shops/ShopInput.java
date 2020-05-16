package shops;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import perms.PermissionAPI;

public class ShopInput implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (!(cs instanceof Player)) {
			return true;
		}
		
		Player pl = (Player) cs;
		
		if (cmd.getName().equalsIgnoreCase("setshop")) {
			
			if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
				return true;
			}
			
			ShopAPI.toggleAddingShopNPC(pl);
			
		}
		
		return true;
	}

	
	
}
