package world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import game.GameAPI;
import graphics.GraphicsAPI;
import net.md_5.bungee.api.ChatColor;

public class WorldListener implements Listener {

	// these are generic listeners.

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}

	public void onUnload() {

	}

	@EventHandler
	public void onPing(ServerListPingEvent e) {
		e.setMaxPlayers(100);
		e.setMotd(GameAPI.getMOTD());
	}

	@EventHandler
	public void onChange(BlockFadeEvent e) {
		if (e.getBlock().getType() == Material.FARMLAND) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEnter(PlayerMoveEvent e) {

		// probably a better way of doing it, but this works..
			Player pl = e.getPlayer();
			RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));

			for (ProtectedRegion region : regions.getApplicableRegions(BukkitAdapter.asBlockVector(e.getTo()))) {
				
				String id = region.getId();
				
				if (!WorldAPI.getRegionName(e.getFrom()).equalsIgnoreCase(id)) {
					switch (id) {
					case "test":
					GraphicsAPI.sendTitle(pl, ChatColor.GRAY + "this works",
							ChatColor.GRAY + "just add regions here that you want a display for");
					}	
				}
			}
	}
}
