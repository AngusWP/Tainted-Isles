package database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import enums.Profession;
import game.GameAPI;

public class DatabaseAPI {

	public static void check() {
		List<File> files = new ArrayList<File>();
		List<File> yml = new ArrayList<File>();
		
		File main = GameAPI.getInstance().getDataFolder();
		File perms = new File(main + "/perms");
		File players = new File(main + "/players");
		File instances = new File(main + "/instances");
		File maps = new File(main + "/instances/maps");
		File friends = new File(main + "/friends");
		File progress = new File(main + "/enhancements");
		File quests = new File(main + "/quests");
		File gold = new File(main + "/gold");
		File factions = new File(main + "/factions");
		File professions = new File(main + "/professions");
		File banks = new File(main + "/banks");
		File shops = new File(main + "/shops");
		File collection = new File(main + "/shops/collection");
		
		files.add(main);
		files.add(perms);
		files.add(players);
		files.add(factions);
		files.add(shops);
		files.add(banks);
		files.add(progress);
		files.add(collection);
		files.add(friends);
		files.add(instances);
		files.add(maps);
		files.add(gold);
		files.add(quests);
		files.add(professions);
		
		for (Profession p : Profession.values()) {
			File f = new File(main + "/professions/" + p.toString().toLowerCase());
			files.add(f);
		}
		
		for (File file : files) {			
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		File spawners = new File(main + "/spawners.yml");
		File chest = new File(main + "/chests.yml");
		File artisan = new File(main + "/professions/artisan_data.yml");
		File npcs = new File(main + "/shops/npcs.yml");
		File hologram = new File(main + "/shops/holograms.yml");
		
		yml.add(spawners);
		yml.add(hologram);
		yml.add(artisan);
		yml.add(chest);
		yml.add(npcs);
		
		for (File file : yml) {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
		
		files.clear();
		yml.clear();
		// dont want to store this for any longer than it needs to be
		//make the rest of the folders here
	}
	
	public static String getEXPValue(String path) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);

		if (file.exists()) {
			return DatabaseAPI.getString(path, "EXP");
		}
		
		return "1,0";
	}
	
	public static String getKey(String path) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String keys = config.getKeys(false).toString();
		GameAPI.log(keys.split("/")[0]);
		
		return keys.split("/")[0];
	}

	
	public static void setEXPValue(UUID uuid, Profession p, int level, int amount) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml");
		
		if (file.exists()) {
			DatabaseAPI.setString("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml", "EXP", level + "," + amount);
		}
	}
	
	public static void setInt(String path, String key, int amount) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		config.set(key, amount);
		
		try  {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static boolean exists(String path) {
		File f = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		
		if (f.exists()) {
			return true;
		}

		return false;
	}
	
	public static void setString(String path, String key, String s) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		config.set(key, s);
		
		try  {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void setStringList(String path, String key, List<String> s) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		config.set(key, s);
		
		try  {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public static int getInt(String path, String key) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return config.getInt(key);
	}
	
	public static String getString(String path, String key) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return config.getString(key);
	}
	
	public static List<String> getStringList(String path, String key){
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return config.getStringList(key);
	}
	
	public static boolean isString(String path, String key) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (config.get(key) instanceof String) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isStringList(String path, String key) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (config.get(key) instanceof List<?>) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isInt(String path, String key) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/" + path);
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (config.get(key) instanceof Integer) {
			return true;
		}
		
		return false;
	}
	
	public static File createYMLFile(String s) {
		File f = new File(GameAPI.getInstance().getDataFolder() + "/" + s);

		try {
			f.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
	public static void createFolder(String s) {
		File f = new File(GameAPI.getInstance().getDataFolder() + "/" + s);
		
		try {
			f.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
