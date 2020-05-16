package dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import game.GameAPI;

public class DialogueListener implements Listener{

	public static HashMap<String, List<String>> dialogue = new HashMap<>();
	public static HashMap<UUID, String> current_dialogue = new HashMap<UUID, String>();
	public static HashMap<UUID, Integer> progress = new HashMap<UUID, Integer>();
	
	public void onLoad() {
		DialogueAPI.init();
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (current_dialogue.containsKey(e.getPlayer().getUniqueId())) {
			current_dialogue.remove(e.getPlayer().getUniqueId());
			progress.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getHand().equals(EquipmentSlot.HAND)) {
			if (e.getRightClicked() instanceof Player) {
				Player pl = e.getPlayer();
				Player npc = (Player) e.getRightClicked();
				
				if (DialogueAPI.isNPC(npc)) { 
					String name = npc.getName();
					
					if (DialogueAPI.hasDialogue(name)) {
						
						if (DialogueAPI.isInDialogue(e.getPlayer().getUniqueId())){
							if (DialogueAPI.hasDialogue(name) && !(DialogueAPI.getCurrentDialogue(pl.getUniqueId()).equals(name))) {
								pl.sendMessage(ChatColor.GRAY + "Please finish your current dialogue.");
							}
							
							return;
						}

						DialogueAPI.start(pl, name);	
					}
				}
			}	
		}
	}
	
}
