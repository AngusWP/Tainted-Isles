package chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import game.GameAPI;

public class ChatListener implements Listener {

	public static List<UUID> allChatCooldown = new ArrayList<UUID>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}

	public void onUnload() {

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			e.setCancelled(true);
			String msg = e.getMessage();

			List<Player> send_to = new ArrayList<Player>();

			for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
				if (pl.getWorld() == e.getPlayer().getWorld()) {
					if (pl.getLocation().distance(e.getPlayer().getLocation()) <= 75) {
						send_to.add(pl);
					}
				}
			}

			if (send_to.size() == 1) { // if the sender themselves is the only one in that radius
				e.getPlayer().sendMessage(ChatAPI.getFullName(e.getPlayer()) + ChatColor.GRAY + ": " + msg);
				e.getPlayer().sendMessage(ChatColor.GRAY + "Nobody heard you.");
			} else {
				for (Player pl : send_to) {
					pl.sendMessage(ChatAPI.getFullName(e.getPlayer()) + ChatColor.GRAY + ": " + msg);
				}

			}
		}
	}

}
