package loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import game.GameAPI;
import perms.PermissionAPI;

public class LootListener implements Listener{

	// loot chests
	public static HashMap<Location, Integer> chest = new HashMap<>();
	public static HashMap<Location, Integer> respawning = new HashMap<Location, Integer>();
	public static HashMap<Location, Chest> data = new HashMap<Location, Chest>();
	
	public static List<Player> loot_mode = new ArrayList<Player>();
	public static HashMap<Player, Location> choosing = new HashMap<Player, Location>();
	// admins placing chests
	
	public static List<Inventory> inv = new ArrayList<Inventory>();
	// this is to stop the stack overflow error of looping functions
	


	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		LootAPI.load();
		LootAPI.count();
	}
	
	public void onUnload() {
		LootAPI.save();
		
		for (Location l : respawning.keySet()) {
			l.getBlock().setType(Material.CHEST);
			GameAPI.log("[Loot] Respawned all loot chests.");
			// make them respawn instead of all being deleted when server reloads.
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (LootAPI.isLootChest(e.getBlock().getLocation())) {
			if (PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) {
				
				if (loot_mode.contains(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "Tier " + chest.get(e.getBlock().getLocation()) + " loot chest removed.");
					chest.remove(e.getBlock().getLocation());
					LootAPI.save();
					
					if (respawning.containsKey(e.getBlock().getLocation())) {
						respawning.remove(e.getBlock().getLocation());
					}
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.GRAY + "You must be in loot mode to remove loot chests. Type /loot to enter.");
				}
			}
		}
	}
	
	@EventHandler
	public void onExit(InventoryCloseEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Choose Tier")) {
			if (choosing.containsKey(e.getPlayer())) {
				// we will only remove them if it goes successfully
				choosing.get(e.getPlayer()).getBlock().setType(Material.AIR);
				choosing.remove(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (loot_mode.contains(e.getPlayer())) {
			if (e.getBlock().getType() == Material.CHEST) {
				
				if (LootAPI.isLootChest(e.getBlock().getLocation())) { // check its not already a loot chest location
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.GRAY + "There is already a loot chest in this location.");
					return;
				}
				
				LootAPI.loadTierMenu(e.getPlayer());
				choosing.put(e.getPlayer(), e.getBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.GRAY + "Please choose a tier.");
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Choose Tier")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
				String name = e.getCurrentItem().getItemMeta().getDisplayName();
				name = ChatColor.stripColor(name);
				Player pl = (Player) e.getWhoClicked();
				
				int tier = 0;
				
				switch (name) {
					case "Tier 1":
						tier = 1;
						break;
					case "Tier 2":
						tier = 2;
						break;
					case "Tier 3":
						tier = 3;
						break;
					case "Tier 4":
						tier = 4;
						break;
					case "Tier 5":
						tier = 5;
						break;
				}
				
				if (tier != 0) {
					Location loc = choosing.get(pl);
					chest.put(loc, tier);
					choosing.remove(pl);
					pl.sendMessage(ChatColor.GRAY + "You have placed a Tier " + tier + " loot chest.");
					pl.closeInventory();
					LootAPI.save();
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (loot_mode.contains(e.getPlayer())) {
			loot_mode.remove(e.getPlayer());
		}
		
		if (choosing.containsKey(e.getPlayer())) {
			choosing.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (LootAPI.isLootInventory(e.getInventory())) {
			 Player pl = (Player) e.getPlayer();
			 Chest chest = LootAPI.getChest(e.getInventory());
			 
			 chest.removeViewer(pl);
			 
			 List<Player> list = new ArrayList<Player>();
			 list.addAll(chest.getViewers());
			 //stop CME (concurrent modification exception)
			 
			 if (!LootAPI.isRespawning(LootAPI.getLocation(e.getInventory()))) {
				 LootAPI.destroyChest(LootAPI.getLocation(e.getInventory()), e.getInventory());
			 }
			 
			 if (chest.hasViewers()) { 
				 for (Player viewer : list) {
					 viewer.closeInventory();
					 viewer.playSound(pl.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1F, 1F);
				 }
			 }
			 
			 pl.playSound(pl.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1F, 1F);
			 


			 data.remove(LootAPI.getLocation(e.getInventory()));		
			 inv.remove(e.getInventory());
		}
	}
	
	
	@EventHandler
	public void onChestOpen(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getType() != null && e.getClickedBlock().getType().equals(Material.CHEST)) {
				Location loc = e.getClickedBlock().getLocation();
				
				if (!LootAPI.isLootChest(loc)) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "You can't open this.");
					e.setCancelled(true);
					return;
				} else {
					
					e.setCancelled(true);
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 1F);
					
					if (LootAPI.isChestOpen(loc)) {
						LootAPI.loadOpenChest(e.getPlayer(), loc);
						return;
					} 
					
					LootAPI.openChest(e.getPlayer(), loc);
				}
			}
		}
	}
	
}
