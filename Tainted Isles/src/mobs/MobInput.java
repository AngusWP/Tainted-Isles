package mobs;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import enums.MobType;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;
import perms.PermissionAPI;

public class MobInput implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("spawnmob")) {
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (PermissionAPI.isHighStaff(pl.getUniqueId())) {
					
					if (args.length == 2) {
						
						String t = args[0].toUpperCase();
						String tier_input = args[1];
						
						MobType type = null;
						
						for (MobType ty : MobType.values()) {
							if (t.equalsIgnoreCase(ty.toString())){
								type = ty;
							}
						}
						
						try {
							int tier = Integer.parseInt(tier_input);
							
							if (type != null) {
							
								if (tier >= 1 && tier <= 5) {
									
									if (type.equals(MobType.GOBLIN)) {
										Mob.spawn(ItemAPI.getTier(tier), type, pl.getLocation());
									}
									
									pl.sendMessage(ChatColor.GREEN + "Mob spawned.");
								} else {
									pl.sendMessage(ChatColor.GRAY + "Tier must be between 1 and 5. /spawnmob <type> <tier 1-5>.");
									pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(MobType.values()));
								}
								
							} else {
								pl.sendMessage(ChatColor.GRAY + "Invalid type. /spawnmob <type> <tier 1-5>.");
								pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(MobType.values()));
							}
							

							
						} catch (NumberFormatException e) {
							pl.sendMessage(ChatColor.GRAY + "Tier must be an integer. /spawnmob <type> <tier 1-5>.");
							pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(MobType.values()));
							e.printStackTrace();
						}
						
					} else {
						pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /spawnmob <type> <tier 1-5>.");
						pl.sendMessage(ChatColor.GRAY + "Available types: " + Arrays.asList(MobType.values()));
					}
					
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("showms")) {
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
					return true;
				}
				
				if (args.length != 1) {
					pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /showms <radius>.");
					return true;
				}
				
				try {
					int radius = Integer.parseInt(args[0]);
					
					int i, j, k;
					int x = (int) pl.getLocation().getX();
					int y = (int) pl.getLocation().getY();
					int z = (int) pl.getLocation().getZ();
					
					int count = 0;
					for (i = -radius; i <= radius; i++) {
						for (j = -radius; j <= radius; j++) {
							for (k = -radius; k <= radius; k++) {
								Location loc = pl.getWorld().getBlockAt(x + i, y + j, z + k).getLocation();
								if (MobListener.spawners.containsKey(loc)) {
									if (loc.getBlock().getType().equals(Material.AIR)) {
										count++;
										loc.getBlock().setType(Material.SPAWNER);	
									}
								}
							}
						}
					}
					
					pl.sendMessage(ChatColor.YELLOW + "Displaying " + count + " mob spawners in a " + radius + " block radius.");
					pl.sendMessage(ChatColor.YELLOW + "Break them to remove the spawnpoint.");
					
				} catch (NumberFormatException e) {
					pl.sendMessage(ChatColor.GRAY + "That radius must be an integer. /showms <radius>.");
					e.printStackTrace();
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("hidems")) {
			if (cs instanceof Player) {
				Player pl = (Player) cs;
				
				if (!PermissionAPI.isHighStaff(pl.getUniqueId())) {
					return true;
				}
				
				if (args.length != 1) {
					pl.sendMessage(ChatColor.GRAY + "Incorrect syntax. /hidems <radius>.");
					return true;
				}
				
				try {
					int radius = Integer.parseInt(args[0]);
					
					int i, j, k;
					int x = (int) pl.getLocation().getX();
					int y = (int) pl.getLocation().getY();
					int z = (int) pl.getLocation().getZ();
					
					int count = 0;
					for (i = -radius; i <= radius; i++) {
						for (j = -radius; j <= radius; j++) {
							for (k = -radius; k <= radius; k++) {
								Location loc = pl.getWorld().getBlockAt(x + i, y + j, z + k).getLocation();
								if (MobListener.spawners.containsKey(loc)) {
									if (loc.getBlock().getType().equals(Material.SPAWNER)) {
										count++;
										loc.getBlock().setType(Material.AIR);	
									}
								}
							}
						}
					}
					
					pl.sendMessage(ChatColor.YELLOW + "Hiding " + count + " mob spawners in a " + radius + " block radius.");
					
				} catch (NumberFormatException e) {
					pl.sendMessage(ChatColor.GRAY + "That radius must be an integer. /hidems <radius>.");
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

	
	
}
