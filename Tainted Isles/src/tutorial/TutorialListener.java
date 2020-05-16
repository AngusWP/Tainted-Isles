package tutorial;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import factions.FactionAPI;
import game.GameAPI;

public class TutorialListener implements Listener {

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!FactionAPI.hasData(e.getPlayer().getUniqueId())) {
			// tp to Hermit island.
		}
	}
	
}
