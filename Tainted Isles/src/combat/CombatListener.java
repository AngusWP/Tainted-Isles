package combat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import game.GameAPI;

public class CombatListener implements Listener{

	public static HashMap<UUID, Integer> combat = new HashMap<UUID, Integer>();
	public static HashMap<UUID, BossBar> bar = new HashMap<UUID, BossBar>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		CombatAPI.start();
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && !e.isCancelled()) {
			Player pl = (Player) e.getEntity();
			CombatAPI.setCombatTimer(pl, 5);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && !e.isCancelled()) {
			Player pl = (Player) e.getDamager();
			
			CombatAPI.setCombatTimer(pl, 5);
		}
	}
	
}
