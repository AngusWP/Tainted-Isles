package gold;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import perms.PermissionAPI;

public class GoldInput implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		
		if (!(cs instanceof Player)) {
			return false;
		}
		
		Player pl = (Player) cs;
		
		if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
			return false;
		}
		
		if (cmd.getName().equalsIgnoreCase("note")) {
			if (args.length != 1) {
				pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /note <amount>.");
				return false;
			}
			
			try {
				int am = Integer.parseInt(args[0]);
				
				if (am < 1) {
					pl.sendMessage(ChatColor.GRAY + "The amount must be more than 0.");
					return false;
				}
				
				pl.sendMessage(ChatColor.GRAY + "Note created.");
				pl.getInventory().addItem(GoldAPI.createGoldCheque(am));
				
			} catch (NumberFormatException e) {
				pl.sendMessage(ChatColor.GRAY + "The amount must be an integer.");
				e.printStackTrace();
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("gold")) {
			if (args.length != 1) {
				pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /gold <amount>.");
				return false;
			}
			
			try {
				int am = Integer.parseInt(args[0]);
				
				if (am > 64 || am < 1) {
					pl.sendMessage(ChatColor.GRAY + "The amount must be in the range of 1-64.");
					return false;
				}
				
				pl.sendMessage(ChatColor.GRAY + "Gold created.");
				pl.getInventory().addItem(GoldAPI.createGoldCoins(am));
				
			} catch (NumberFormatException e) {
				pl.sendMessage(ChatColor.GRAY + "The amount must be an integer.");
				e.printStackTrace();
			}
		}
		
		return true;
	}

}
