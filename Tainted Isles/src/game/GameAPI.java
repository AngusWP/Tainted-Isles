package game;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import config.Config;

public class GameAPI {

	public static Plugin getInstance() {
		return Game.instance;
	}
	
	public static void log(String s) {
		Bukkit.getConsoleSender().sendMessage(s);
	}
	
	public static void broadcast(String s) {
		Bukkit.broadcastMessage(s);
	}

	public static String getVersion() {
		return Config.version;
	}
	
	public static String getMOTD() {
		return Config.motd;
	}
	
}
