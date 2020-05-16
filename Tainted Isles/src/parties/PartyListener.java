package parties;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import game.GameAPI;

public class PartyListener implements Listener {

	public static HashMap<UUID, Party> party = new HashMap<UUID, Party>();

	
	public static HashMap<UUID, UUID> invite = new HashMap<UUID, UUID>();
	public static HashMap<UUID, Integer> invite_timer = new HashMap<UUID, Integer>();
	
	// this is just to store the party object
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		PartyAPI.update();
	}
	
	public void onUnload() {	
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (PartyAPI.isInParty(e.getPlayer())) {
			
			Party party = PartyAPI.getParty(e.getPlayer());
			
			if (party.getOwner().equals(e.getPlayer())) {
				PartyAPI.disband(e.getPlayer());
			} else {
				PartyAPI.leave(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player ent = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
		
			if (!e.isCancelled()) {
				if (PartyAPI.isInParty(d)) {
					Party party = PartyAPI.getParty(d);
					
					if (party.getMembers().contains(ent)) {
						e.setCancelled(true);
						d.sendMessage(ChatColor.RED + "You cannot damage your own party members.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent e) {
		if (e.getMessage().startsWith("!")){
			
			if (e.getMessage().length() == 1) {
				return;
			}
			
			if (PartyAPI.isInParty(e.getPlayer())) {
				e.setCancelled(true);
				
				String msg = e.getMessage().substring(1);
				PartyAPI.sendChat(e.getPlayer(), msg);	
			}
			
		}
	}
	
}
