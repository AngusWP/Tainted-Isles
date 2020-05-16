package gold;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import game.GameAPI;

public class GoldListener implements Listener{

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!GoldAPI.hasData(e.getPlayer().getUniqueId())) {
			GoldAPI.create(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.SUNFLOWER)) {
			e.setCancelled(true); // just dont have any sunflowers
		}
	}
	
}
