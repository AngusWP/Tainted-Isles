package admin;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import enums.Mount;
import enums.Rank;
import enums.Type;
import items.Item;
import items.ItemAPI;
import mounts.MountAPI;
import net.md_5.bungee.api.ChatColor;
import perms.PermissionAPI;
import player.PlayerAPI;

public class AdminInput implements CommandExecutor{

	// REMEMBER THERE IS A SPLIT IN THESE COMMANDS. BEFORE THE COMMENT IS ALL STAFF, AFTER THE COMMENT IS ONLY HIGH STTAFF
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (!PermissionAPI.isStaff(pl.getUniqueId())) {
					return true;
				}
			} // so from this point on we know they are atleast staff.

			if (cmd.getName().equalsIgnoreCase("uuid")) {			
				if (args.length == 1) {
					String name = args[0];
					
					if (PlayerAPI.isOnline(name)) {
						Player get = PlayerAPI.getOnlinePlayer(name);
						
						cs.sendMessage(get.getUniqueId().toString());
					}  else {
						cs.sendMessage(ChatColor.GRAY + "That player isn't online.");
					}
				} else {
					cs.sendMessage(ChatColor.GRAY + "Incorrect syntax. /uuid <player name>.");
				}		
			}
			
			
			/*
			 * 
			 * ANY COMMAND ENTERED AFTER THIS POINT IS HIGH STAFF ONLY. ANY COMMAND BEFORE IS FOR MODS + HIGH STAFF. VERY IMPORTANT
			 * 
			 */
			
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
					return true;
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("trophy")) {
				Player pl = (Player) cs;
				
				if (args.length == 1) {
					
					String t = args[0];
					
					try {
						int tier = Integer.parseInt(t);
						
						pl.sendMessage(ChatColor.GREEN + "Trophy created.");
						pl.getInventory().addItem(ItemAPI.generateTrophy(ItemAPI.getTier(tier)));
						
					} catch (NumberFormatException e) {
						pl.sendMessage(ChatColor.RED + "The tier must be a number.");
						return true;
					}
					
				} else {
					pl.sendMessage(ChatColor.RED + "Incorrect syntax. /trophy <tier>.");
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("item")) {
				if (cs instanceof Player) {
					Player pl = (Player) cs;
					
					if (PermissionAPI.isHighStaff(pl.getUniqueId())) {
						
						if (args.length == 2) {
							
							String t = args[0].toUpperCase();
							String tier_input = args[1];
							
							Type type = null;
							
							for (Type ty : Type.values()) {
								if (t.equalsIgnoreCase(ty.toString())){
									type = ty;
								}
							}
							
							try {
								int tier = Integer.parseInt(tier_input);
								
								if (type != null) {
								
									if (tier >= 1 && tier <= 5) {
										pl.getInventory().addItem(new Item(tier, type).get());
										pl.sendMessage(ChatColor.GREEN + "Item recieved.");
									} else {
										pl.sendMessage(ChatColor.GRAY + "Tier must be between 1 and 5. /item <type> <tier 1-5>.");
										pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(Type.values()));
									}
									
								} else {
									pl.sendMessage(ChatColor.GRAY + "Invalid type. /item <type> <tier 1-5>.");
									pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(Type.values()));
								}
								

								
							} catch (NumberFormatException e) {
								pl.sendMessage(ChatColor.GRAY + "Tier must be an integer. /item <type> <tier 1-5>.");
								pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(Type.values()));
								e.printStackTrace();
							}
							
						} else {
							pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /item <type> <tier 1-5>.");
							pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(Type.values()));
						}
						
					}
				}
			}
			
			if (cmd.getName().equalsIgnoreCase("set")) {
					
					if (args.length == 2) {
					
						String name = args[0];
						
						if (PlayerAPI.isOnline(name)) {
							Player set = PlayerAPI.getOnlinePlayer(name);
							
							String rank = args[1].toUpperCase(); // we are making it upper case so it matches enum protocol
							Rank r = null;
							
							for (Rank ra : Rank.values()) {
								if (rank.equalsIgnoreCase(ra.toString())){
									r = ra;
								}
							}
							
							if (r != null) {
								
								PermissionAPI.setRank(set.getUniqueId(), Rank.valueOf(rank));
								set.sendMessage(ChatColor.DARK_AQUA + "Your rank has been set to: " + rank);
								cs.sendMessage(ChatColor.GRAY + "Rank for " + set.getName() + " set to " + rank + ".");
								
								
							} else {
								cs.sendMessage(ChatColor.GRAY + "That isn't a valid rank.");
								cs.sendMessage(ChatColor.GRAY + "Available ranks: " + Arrays.asList(Rank.values()));
							}
							
						} else {
							cs.sendMessage(ChatColor.GRAY + "That player isn't online.");
						}
					
					} else {
						cs.sendMessage(ChatColor.GRAY + "Incorrect syntax. /set <player name> <rank>.");
						cs.sendMessage(ChatColor.GRAY + "Available ranks: " + Arrays.asList(Rank.values()));
					}
			}
			
			if (cmd.getName().equalsIgnoreCase("mount") && cs instanceof Player) {
				if (args.length == 1) {
					String horse = args[0].toUpperCase(); // we are making it upper case so it matches enum protocol
					
					Mount h = null;
					
					for (Mount hr : Mount.values()) {
						if (horse.equalsIgnoreCase(hr.toString())){
							h = hr;
						}
					}
					
					if (h != null) {
						Player pl = (Player) cs;
						
						MountAPI.give(pl, h);
						
					} else {
						cs.sendMessage(ChatColor.GRAY + "Incorrect syntax. /mount <tier>.");
						cs.sendMessage(ChatColor.GRAY + "Available horses: " + Arrays.asList(Mount.values()));
					}
				} else {
					cs.sendMessage(ChatColor.GRAY + "Incorrect syntax. /mount <tier>.");
					cs.sendMessage(ChatColor.GRAY + "Available horses: " + Arrays.asList(Mount.values()));
				}
			}
			
		return true;
	}

	
	
}
