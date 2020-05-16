package instances;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;

import game.GameAPI;

public class InstanceListener implements Listener {

	public static HashMap<UUID, Instance> instance = new HashMap<UUID, Instance>();
	// keep track of their specific instance.
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (InstanceAPI.inInstance(e.getEntity().getUniqueId())) {
			InstanceAPI.leaveInstance(e.getEntity());
		}
	}
	
	@EventHandler
	public void onInit(WorldInitEvent e) {
		if (e.getWorld().getName().equals("world")) {
			return;
		}
		
		e.getWorld().setKeepSpawnInMemory(false);
		e.getWorld().setAutoSave(false);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (InstanceAPI.inInstance(e.getPlayer().getUniqueId())) {
			InstanceAPI.leaveInstance(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onChange(PlayerChangedWorldEvent e) {
		if (e.getFrom().getName().equalsIgnoreCase("world")) {
			return;
		}
		
		if (e.getFrom().getPlayers().isEmpty() && (e.getFrom().getName().contains("Dungeon") || e.getFrom().getName().contains("dungeon"))) {
			Bukkit.unloadWorld(e.getFrom(), false);
			InstanceAPI.deleteWorld(e.getFrom().getName());
			// unload the world from memory
		}
	}
	
}
