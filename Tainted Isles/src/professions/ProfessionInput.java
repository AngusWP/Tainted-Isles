package professions;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import enums.Profession;
import perms.PermissionAPI;

public class ProfessionInput implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (!(cs instanceof Player)) {
			return true;
		}
		
		Player pl = (Player) cs;
		
		if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("prof")) {
			if (args.length != 2) {
				pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /prof <profession> <level>.");
				pl.sendMessage(ChatColor.GRAY + "Available professions: " + Arrays.asList(Profession.values()));
				return true;
			}
			
			String p = args[0].toUpperCase();
			Profession prof = null;

			try {
				prof = Profession.valueOf(p);
			} catch (IllegalArgumentException e) {
				pl.sendMessage(ChatColor.RED + "That is not a valid profession.");
				return true;
			}
			
			try {
				int level = Integer.parseInt(args[1]);
				
				if (level >= 1 && level <= ProfessionAPI.getCapLevel()) {
					ProfessionAPI.setLevel(pl.getUniqueId(), prof, level);
					pl.sendMessage(ChatColor.GRAY + "Your " + p + " level has been set to " + level + ".");
					
				} else {
					pl.sendMessage(ChatColor.RED + "The level set must be between 1 and " + ProfessionAPI.getCapLevel() + ".");
					return true;
				}
				
			} catch (NumberFormatException e) {
				pl.sendMessage(ChatColor.RED + "The level set must be a number.");
				e.printStackTrace();
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("artisan")){
			if (ProfessionListener.placing_artisan.contains(pl)) {
				ProfessionListener.placing_artisan.remove(pl);
				
				pl.sendMessage(ChatColor.YELLOW + "You are no longer in Artisan mode.");
				pl.sendMessage(ChatColor.RED + "Type /artisan to re-enter this mode.");
			} else {
				ProfessionListener.placing_artisan.add(pl);
				
				pl.sendMessage(ChatColor.YELLOW + "You can now place Artisan profession blocks.");
				
				pl.sendMessage(ChatColor.GRAY + "Tier 1 - Spruce");
				pl.sendMessage(ChatColor.GRAY + "Tier 2 - Birch");
				pl.sendMessage(ChatColor.GRAY + "Tier 3 - Oak");
				pl.sendMessage(ChatColor.GRAY + "Tier 4 - Eben (jungle log)");
				pl.sendMessage(ChatColor.GRAY + "Tier 5 - Acacia");
				
				pl.sendMessage(ChatColor.RED + "Type /artisan to exit this mode.");
			}
		}
		
		return true;
	}

	
	
}
