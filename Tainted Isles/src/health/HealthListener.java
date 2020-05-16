package health;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import game.GameAPI;
import player.PlayerAPI;

public class HealthListener implements Listener {

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		HealthAPI.updateHealthUnderName();
		HealthAPI.regenerate();
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		HealthAPI.resetHealth(e.getPlayer());
	}
	
	@EventHandler
	public void onFirstJoin(PlayerJoinEvent e) {
		if (!PlayerAPI.hasPlayedBefore(e.getPlayer().getUniqueId())) {
			HealthAPI.resetHealth(e.getPlayer());
		}
		
		e.getPlayer().setHealthScale(20);
	}
	
	@EventHandler
	public void onRegain(EntityRegainHealthEvent e) {
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onChange(EntityDamageEvent e) {
		if (e.getDamage() != 0 && !e.isCancelled()) {
			if (e.getEntity() instanceof Player) {
				Player pl = (Player) e.getEntity();
				
				if (pl.isDead()) {
					pl.setLevel(0);
				} else {
					pl.setLevel(HealthAPI.getHealth(pl) - (int) e.getDamage());	
				}
			}
		}
	}	
	
}
