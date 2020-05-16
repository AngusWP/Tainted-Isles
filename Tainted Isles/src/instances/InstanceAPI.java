package instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import game.GameAPI;

public class InstanceAPI {

	static int amount = 0;
	
	public static void startInstance(String name, Player pl) {
		
		List<Player> players = new ArrayList<Player>();
		players.add(pl);
		// if player is in party, add the rest of them to the list.
		// do this when we code parties
		
		GameAPI.log("[Instance] New initiation process started.");
		loadWorld(name, players);
	}
	
	public static Instance getInstance(UUID uuid) {
		if (inInstance(uuid)) {
			return InstanceListener.instance.get(uuid);
		}
		
		GameAPI.log("[Instance] Checking for instance returned null. Serious shit gone down.");
		return null;
	}
	
	public static boolean inInstance(UUID uuid) {
		if (InstanceListener.instance.containsKey(uuid)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean exists(String name) {
		File world = new File(GameAPI.getInstance().getDataFolder() + "/instances/maps/" + name);
		
		if (world.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static void leaveInstance(Player pl) {
		pl.teleport(Bukkit.getWorld("world").getSpawnLocation());
		InstanceListener.instance.remove(pl.getUniqueId());
	}
	
	public static void loadWorld(String name, List<Player> players) {
		
		File world = new File(GameAPI.getInstance().getDataFolder() + "/instances/maps/" + name);
		
		try {
			FileUtils.copyDirectory(world, new File(name + amount)); // unfortunately we have to copy it into the main directory, because its the only way bukkit can read it
			GameAPI.log("[Instance] World file copied...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        if (new File(world + "/uid.dat").exists()) {
            new File(world + "/uid.dat").delete();
            // bukkit cries about this file for some reason.
        }
		
		WorldCreator wc = new WorldCreator(name + amount);
		wc.generateStructures(false);
		World w = Bukkit.createWorld(wc);
		
		Instance i = new Instance(w.getName() + amount, players);
		
		for (Player pl : players) {
			pl.teleport(w.getSpawnLocation());
			setInstance(pl.getUniqueId(), i);
		}
		
		// the reason we do w.getName() instead of name is in case there are multiple instances running, and there are now integers at the end to differentiate.
		// we need to know the name of the exact folder to delete it.

		GameAPI.log("[Instance] World creation complete.");
		
		amount++;
	}
	
	public static void setInstance(UUID uuid, Instance i) {
		InstanceListener.instance.put(uuid, i);
	}
	
	public static void deleteWorld(String name) {
		File world = new File(name);
		
		if (world.exists()) {
			try {
				FileUtils.deleteDirectory(world);
				GameAPI.log("Instance " + name + " succesfully deleted.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
