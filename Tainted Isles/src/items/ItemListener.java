package items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import game.GameAPI;
import health.HealthAPI;

public class ItemListener implements Listener {

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
			LivingEntity ent = (LivingEntity) e.getEntity();
			LivingEntity d = (LivingEntity) e.getDamager();
			
			if (!e.isCancelled() && e.getDamage() != 0) {
				e.setDamage(0);
				
				ItemAPI.handleStats(ent, d, e);
				// the graphical updates are handled in there as we need to know whether the attack is cancelled
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getSlot() == 40 && e.getInventory().getType().equals(InventoryType.CRAFTING)) { 
			// for some reason, the debugged inventory is crafting.. https://prnt.sc/sb656m
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(GameAPI.getInstance(), new Runnable() {
				public void run() {
					ItemAPI.updateTrophyStats((Player) e.getWhoClicked());
				}
			}, 1L); // delay it so we know if it was taken out or clicked in
		}
		
		if (e.getCurrentItem() != null) {
			if ((e.getSlotType() != null && e.getSlotType().equals(SlotType.ARMOR)) || ItemAPI.isArmor(ItemAPI.getType(e.getCurrentItem()))) {
					Player pl = (Player) e.getWhoClicked();
				
					Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(GameAPI.getInstance(), new Runnable() {
						public void run() {
							HealthAPI.updateHealth(pl);
						}
					}, 1L);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getItem() != null) {
				ItemStack item = e.getItem();
				
				if (ItemAPI.isArmor(ItemAPI.getType(item))) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "You must manually equip armour.");
					e.setCancelled(true);
					// so update their health when they equip armor
				}
			}
		}
	}
	
}
