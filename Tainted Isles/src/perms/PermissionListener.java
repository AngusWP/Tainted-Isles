package perms;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import game.GameAPI;

public class PermissionListener implements Listener{
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!PermissionAPI.hasRank(e.getPlayer().getUniqueId())) {
			PermissionAPI.createData(e.getPlayer().getUniqueId());
		}
	}
	
}
