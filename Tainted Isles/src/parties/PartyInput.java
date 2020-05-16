package parties;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import player.PlayerAPI;

public class PartyInput implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			
			Player pl = (Player) cs;
			
			if (cmd.getName().equalsIgnoreCase("party")) {
				if (args.length == 1) {
					String input = args[0];
					
					if (input.equalsIgnoreCase("invite")) {
						pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /party invite <name>.");
						return true;
					}
					
					if (input.equalsIgnoreCase("kick")) {
						pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /party kick <name>.");
						return true;
					}
					
					if (input.equalsIgnoreCase("create")) {
						if (!PartyAPI.isInParty(pl)) {
							PartyAPI.createParty(pl);
						} else {
							pl.sendMessage(ChatColor.GRAY + "You are already in a party. To leave, type /party quit.");
						}
						
						return true;
					}
					
					if (input.equalsIgnoreCase("quit")) {
						if (PartyAPI.isInParty(pl)) {
							PartyAPI.leave(pl);	
						} else {
							pl.sendMessage(ChatColor.GRAY + "You are not in a party. Type /party create to make one.");
						}
						return true;
					}
					
					if (input.equalsIgnoreCase("disband")) {
						if (PartyAPI.isInParty(pl)) {
							if (PartyAPI.getParty(pl).getOwner().equals(pl)) {
								PartyAPI.disband(pl);		
							} else {
								pl.sendMessage(ChatColor.YELLOW + "You are not the owner of the party.");
							} 
						} else {
							pl.sendMessage(ChatColor.GRAY + "You are not in a party. Type /party create to make one.");
						}
						return true;
					}
					
					if (input.equalsIgnoreCase("decline")) {
						if (PartyAPI.hasInvite(pl)) {
							PartyAPI.decline(pl);
						} else {
							pl.sendMessage(ChatColor.YELLOW + "You do not have a party invite.");
						}
						
						return true;
					}
					
					if (input.equalsIgnoreCase("accept")) {
						if (PartyAPI.hasInvite(pl)) {
							PartyAPI.join(pl, PartyAPI.getParty(PartyAPI.getInviter(pl)));
						} else {
							pl.sendMessage(ChatColor.YELLOW + "You do not have a party invite.");
						}
						
						return true;
					}
					
				} else if (args.length == 2) {
					
					String option = args[0];
					String name = args[1];
					
					if (option.equalsIgnoreCase("invite")) {
						if (PartyAPI.isInParty(pl)) {
							
							Party party = PartyAPI.getParty(pl);
							
							if (party.getOwner().equals(pl)) {
								if (PlayerAPI.isOnline(name)) {
									Player invited = PlayerAPI.getOnlinePlayer(name);
									
									if (party.getMembers().contains(invited)) {
										pl.sendMessage(ChatColor.YELLOW + "That player is already in your party.");
										return true;
									}
									
									if (PartyAPI.isFull(party)) {
										pl.sendMessage(ChatColor.YELLOW + "Your party is full..");
										return true;
									}
									
									if (PartyAPI.isInParty(invited)) {
										pl.sendMessage(ChatColor.YELLOW + "That player is already in a party.");
										return true;
									}

									PartyAPI.sendInvite(invited, pl);                         
								} else {
									pl.sendMessage(ChatColor.YELLOW + "That player is not online.");
								}
							} else {
								pl.sendMessage(ChatColor.YELLOW + "You are not the owner of the party.");
							}
							
						} else {
							pl.sendMessage(ChatColor.GRAY + "You are not in a party. Type /party create to make one.");
						}
						
						return true;
					}
					
					if (option.equalsIgnoreCase("kick")) {
						if (PartyAPI.isInParty(pl)) {
							
							Party party = PartyAPI.getParty(pl);
							
							if (party.getOwner().equals(pl)) {
								if (PlayerAPI.isOnline(name)) {
									Player kicked = PlayerAPI.getOnlinePlayer(name);
									
									if (party.getMembers().contains(kicked)) {
										
										if (party.getOwner().equals(kicked)) {
											pl.sendMessage(ChatColor.YELLOW + "You can't kick yourself.");
											return true;
										}
										
										PartyAPI.kick(kicked);
										
									} else {
										pl.sendMessage(ChatColor.YELLOW + "That player is not in your party.");
									}
								} else {
									pl.sendMessage(ChatColor.YELLOW + "That player is not online.");
								}
							} else {
								pl.sendMessage(ChatColor.YELLOW + "You are not the owner of the party.");
							}
							
						} else {
							pl.sendMessage(ChatColor.GRAY + "You are not in a party. Type /party create to make one.");
						}
						
						return true;
					}
				}
				
				//because we return in each option, this will always  be the fallback
				pl.sendMessage(ChatColor.GRAY + "Unknown option. /party [option]");
				pl.sendMessage(ChatColor.GRAY + "Options: [create, invite, kick, decline, accept, quit, disband]");
				
			}
			
		}
		return true;
	}

}
