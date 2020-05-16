package chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.GameAPI;

public class ChatInput implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
	        Player pl = (Player) cs;
			
			if (cmd.getName().equalsIgnoreCase("all")){
	        	
				if (args.length == 0) {
					pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /all <message>.");
					return true;
				}
				
	        	if (ChatListener.allChatCooldown.contains(pl.getUniqueId())) {
	        		pl.sendMessage(ChatColor.GRAY + "Please wait 3 seconds before each all chat message.");
	        		return true;
	        	}
	        	
	        	ChatListener.allChatCooldown.add(pl.getUniqueId());
	        	
	        	String msg = "";
	        	
	        	for (int i = 0; i < args.length; i++) {
	        		msg += args[i] + " ";
	        	}

	        	for (Player all : Bukkit.getOnlinePlayers()) {
	        		all.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "All" + ChatColor.WHITE + "] " + ChatAPI.getFullName(pl) + ChatColor.GRAY + ": " + msg);
	        	}
	        	
	        	new BukkitRunnable() {
	        		public void run() {
	        			ChatListener.allChatCooldown.remove(pl.getUniqueId());
	        		}
	        	}.runTaskLater(GameAPI.getInstance(), 50L);
			}
		}
		
		return true;
	}
	
}
