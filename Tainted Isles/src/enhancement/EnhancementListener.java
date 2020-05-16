package enhancement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import enums.Enhancement;
import game.GameAPI;
import health.HealthAPI;

public class EnhancementListener implements Listener{

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}

	public void onUnload() {
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!EnhancementAPI.hasData(e.getPlayer().getUniqueId())) {
			EnhancementAPI.createData(e.getPlayer().getUniqueId());
		}
		
		EnhancementAPI.updateStats(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onSwitchHands(PlayerSwapHandItemsEvent e) {
		e.setCancelled(true);
		
		EnhancementAPI.openInventory(e.getPlayer());
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Enhancements")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e) {
		if (e.getEntity() instanceof Skeleton && e.getEntity().getKiller() instanceof Player) {
			if (!EnhancementAPI.isComplete(e.getEntity().getKiller(), Enhancement.PLACEHOLDER)) {
				int amount = EnhancementAPI.getAmount(e.getEntity().getKiller(), Enhancement.PLACEHOLDER);
				
				amount++;
				
				if (amount == 5) {
					EnhancementAPI.setComplete(e.getEntity().getKiller(), Enhancement.PLACEHOLDER);
					HealthAPI.setMaxHealth(e.getEntity().getKiller(), HealthAPI.getMaxHealth(e.getEntity().getKiller()) + 5);
				} else {
					EnhancementAPI.setAmount(e.getEntity().getKiller(), amount, Enhancement.PLACEHOLDER);
				}
			}
		}
	}
	
}