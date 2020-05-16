package guilds;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import game.GameAPI;

public class GuildListener implements Listener {

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	
	
}
