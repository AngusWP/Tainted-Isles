package dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import factions.FactionAPI;
import game.GameAPI;

public class DialogueAPI {

	public static void init() {
		getDialogue().put("Hermit", getDialogue("Hermit"));
	}

	public static HashMap<String, List<String>> getDialogue() {
		return DialogueListener.dialogue;
	}

	public static boolean hasFunction(String npc) {
		if (npc.equals("Hermit")) { // add any quest npc here.
			return true;
		}

		return false;
	}

	public static void setProgress(UUID uuid, int amount) {
		DialogueListener.progress.put(uuid, amount);
	}

	public static Integer getProgress(UUID uuid) {
		if (isInDialogue(uuid)) {
			return DialogueListener.progress.get(uuid);
		}

		return 0;
	}

	public static List<String> getDialogue(String name) {
		List<String> list = new ArrayList<String>();

		if (name.equals("Hermit")) {
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Morning, stranger! I found you washed up on my island with no belongings! What happened?");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "You can't remember? Don't worry, I struggle with my memory too!");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Let me rejog your memory, then maybe it will come back to you!");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Near us, there are 3 main factions who govern their Isle.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "One of these factions are the aristocratic Allista're Freehold, the mountain clan.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Another faction are the religous, proud and ancient Gitari Empire, who reside in the forests.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "The last of the great factions is the fuedal state of Leore, who's Isle is covered in hills and huge great plains.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Recently, a new Isle has been discovered - and all 3 lay claim to it!");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "The 3 factions have turned mad with the desire of expansion.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "The war of the Isles has begun.");
			list.add(ChatColor.GRAY + name + ": " + ChatColor.WHITE + "Now, can you recall where you were from?");
		}

		return list;
	}

	public static boolean hasDialogue(String npc) {
		if (getDialogue().get(npc) != null) {
			return true;
		}

		return false;
	}

	public static boolean isNPC(Player pl) {
		if (pl.hasMetadata("NPC")) {
			return true;
		}
		
		return false;
	}

	public static void start(Player pl, String npc) {
		List<String> dialogue = getDialogue(npc);

		if (npc.equals("Hermit")){
			if (FactionAPI.hasData(pl.getUniqueId())) {
				return;
			}
		}
		
		DialogueAPI.setCurrentDialogue(pl.getUniqueId(), npc);
		DialogueAPI.setProgress(pl.getUniqueId(), 0);
		
		new BukkitRunnable() {
			public void run() {
				if (getProgress(pl.getUniqueId()) == dialogue.size()) {
					runFunction(pl, npc);
					cancel();
					return;
				}

				pl.sendMessage(ChatColor.DARK_AQUA + "[" + (getProgress(pl.getUniqueId()) + 1) + "/" + (dialogue.size()) + "] " + dialogue.get(getProgress(pl.getUniqueId())));
				setProgress(pl.getUniqueId(), getProgress(pl.getUniqueId()) + 1);
			}
		}.runTaskTimer(GameAPI.getInstance(), 1L, 60L);
	}

	public static void runFunction(Player pl, String npc) {
		if (npc.equals("Hermit")) {
			pl.sendMessage(ChatColor.RED + "WARNING: This decision is irreversible.");
			FactionAPI.openMenu(pl);
		}
		
		DialogueListener.current_dialogue.remove(pl.getUniqueId());
		DialogueListener.progress.remove(pl.getUniqueId());
	}

	public static void setCurrentDialogue(UUID uuid, String npc) {
		DialogueListener.current_dialogue.put(uuid, npc);
	}

	public static String getCurrentDialogue(UUID uuid) {
		return DialogueListener.current_dialogue.get(uuid);
	}

	public static boolean isInDialogue(UUID uuid) {
		if (DialogueListener.current_dialogue.containsKey(uuid)) {
			return true;
		}

		return false;
	}

}
