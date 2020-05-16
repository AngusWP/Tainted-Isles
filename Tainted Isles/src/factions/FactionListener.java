package factions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import enums.Faction;
import game.GameAPI;
import graphics.GraphicsAPI;

public class FactionListener implements Listener{

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Choose your faction!")) {
			if (FactionAPI.hasData(e.getPlayer().getUniqueId())) {
				return;
			}
			
			new BukkitRunnable() {
				public void run() {
					FactionAPI.openMenu((Player) e.getPlayer());
				}
			}.runTaskLater(GameAPI.getInstance(), 10L); 
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Choose your faction!")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) {
				String name = e.getCurrentItem().getItemMeta().getDisplayName();
				
				name = ChatColor.stripColor(name);
				
				if (name.equals("Kingdom of Leore")) {
					FactionAPI.createData(e.getWhoClicked().getUniqueId(), Faction.LEORE);
				}
				
				if (name.equals("Allista're Freehold")) {
					FactionAPI.createData(e.getWhoClicked().getUniqueId(), Faction.ALLISTARE);
				}
				
				if (name.equals("Gitari Empire")) {
					FactionAPI.createData(e.getWhoClicked().getUniqueId(), Faction.GITARI);
				}
				
				e.getWhoClicked().closeInventory();
				GraphicsAPI.sendTitle((Player) e.getWhoClicked(), ChatColor.AQUA + FactionAPI.getFaction(FactionAPI.getFaction(e.getWhoClicked().getUniqueId())), 
						ChatColor.GRAY + "You have chosen your faction.");
				
				e.getWhoClicked().teleport(FactionAPI.getSpawnLocation(FactionAPI.getFaction(e.getWhoClicked().getUniqueId())));
			}
		}
	}
	
}

