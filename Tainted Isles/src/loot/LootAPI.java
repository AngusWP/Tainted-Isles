package loot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import enums.Loot;
import game.GameAPI;
import items.ItemAPI;

public class LootAPI {

	public static void count() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
				public void run() {
					for (Location loc : LootListener.respawning.keySet()) {
						
							LootListener.respawning.put(loc, LootListener.respawning.get(loc) - 1);
							
							if (LootListener.respawning.get(loc) == 0) {
								LootListener.respawning.remove(loc);
								loc.getBlock().setType(Material.CHEST);
							}
						}
				}
		}, 20L, 20L);
	}
	
	public static void loadTierMenu(Player pl) {
		Inventory inv = Bukkit.createInventory(null, 9, "Choose Tier");
		
		inv.addItem(ItemAPI.create(Material.CHEST, ChatColor.WHITE + "Tier 1", Arrays.asList(ChatColor.WHITE + "Click here to choose Tier 1."), 1));
		inv.addItem(ItemAPI.create(Material.CHEST, ChatColor.WHITE + "Tier 2", Arrays.asList(ChatColor.WHITE + "Click here to choose Tier 2."), 1));
		inv.addItem(ItemAPI.create(Material.CHEST, ChatColor.WHITE + "Tier 3", Arrays.asList(ChatColor.WHITE + "Click here to choose Tier 3."), 1));
		inv.addItem(ItemAPI.create(Material.CHEST, ChatColor.WHITE + "Tier 4", Arrays.asList(ChatColor.WHITE + "Click here to choose Tier 4."), 1));
		inv.addItem(ItemAPI.create(Material.CHEST, ChatColor.WHITE + "Tier 5", Arrays.asList(ChatColor.WHITE + "Click here to choose Tier 5."), 1));
		
		pl.openInventory(inv);
	}
	
    public static void load(){
        File data = new File(GameAPI.getInstance().getDataFolder() + "/chests.yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : config.getKeys(false)) {
            int val = config.getInt(key);
            String[] str = key.split(",");
            World world = Bukkit.getWorld(str[0]);
            double x = Double.valueOf(str[1]);
            double y = Double.valueOf(str[2]);
            double z = Double.valueOf(str[3]);
            Location loc = new Location(world, x, y, z);
            LootListener.chest.put(loc, val);
        }
    }

    public static boolean isRespawning(Location loc) {
    	if (LootListener.respawning.containsKey(loc)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static void destroyChest(Location loc, Inventory inv) {
    	if (loc != null) {
            loc.getBlock().setType(Material.AIR);
            
        	LootListener.respawning.put(loc, Tables.getRespawnTime(LootAPI.getTier(loc)));
        	
        	if (isChestOpen(loc)) {
        		Chest chest = getChest(getInventory(loc));
        		
        		if (!chest.isEmpty()) {
        			for (ItemStack item : inv.getContents()) {
        				if (item != null) {
            				Bukkit.getWorld("world").dropItem(loc, item);	
        				}
        			}
        		}
        	}
        	
    	}
    }
    
    public static Chest getChest(Inventory inv) {
    	if (isLootInventory(inv)) {
    		return LootListener.data.get(getLocation(inv));
    	}
    	
    	return null;
    }
    
    public static Inventory getInventory(Location loc) {
    	if (isLootChest(loc)) {
    		
    		for (Map.Entry<Location, Chest> chest : LootListener.data.entrySet()) {
    			if (chest.getKey().equals(loc)) {
    				return chest.getValue().getInventory();
    			}
    		}
    	}
    	
    	return Bukkit.createInventory(null, 0);
    }
    
    public static boolean isLootChest(Location loc) {
    	if (LootListener.chest.containsKey(loc)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static boolean isLootInventory(Inventory inv) {
    	if (LootListener.inv.contains(inv)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static void openChest(Player pl, Location loc) {
    	Inventory inv = Bukkit.createInventory(null, 27, "Chest");
    	inv = addItems(inv, getTier(loc));
    	List<ItemStack> items = new ArrayList<ItemStack>();
    	List<Player> viewer = new ArrayList<Player>();
    	
    	for (ItemStack i : inv.getContents()) {
    		if (i != null) {
        		items.add(i);	
    		}
    	}
    	
    	viewer.add(pl);
    	
    	pl.openInventory(inv);
    	LootListener.inv.add(inv);
    	LootListener.data.put(loc, new Chest(items, getTier(loc), loc, inv, viewer));
    }
    
    public static void loadOpenChest(Player pl, Location loc) {
    		Chest chest = getChest(getInventory(loc));
    		
    		pl.openInventory(chest.getInventory());
    		chest.addViewer(pl);
    }
    
    public static boolean isChestOpen(Location loc) {
    	if (LootListener.data.containsKey(loc)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static Location getLocation(Inventory inv) {
    	if (isLootInventory(inv)) {
    		for (Location loc : LootListener.data.keySet()) {
    			return LootListener.data.get(loc).getLocation();
    		}
    	}
    	
    	return new Location(Bukkit.getWorld(""), 0, 0, 0);
    }
    
    public static void save(){
        File data = new File(GameAPI.getInstance().getDataFolder() + "/chests.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Location loc : LootListener.chest.keySet()) {
            String s = String.valueOf(loc.getWorld().getName()) + "," + (int) loc.getX() + "," + (int) loc.getY() + "," + (int) loc.getZ();
            config.set(s, LootListener.chest.get(loc));
            try {
                config.save(data);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static Loot getTier(Location loc) {
    	
    	if (LootListener.chest.containsKey(loc)) {
        	switch (LootListener.chest.get(loc)) {
    		case 2:
    			return Loot.TWO;
    		case 3:
    			return Loot.THREE;
    		case 4:
    			return Loot.FOUR;
    		case 5:
    			return Loot.FIVE;
    	} // we return one by default so don't need to check for it
    	}
    	
    	return Loot.ONE;
    }
    
    public static Inventory addItems(Inventory inv, Loot loot) {
    	for (ItemStack item : Tables.get(loot)) {
    		inv.addItem(item);
    	}
    	
    	return inv;
    }
	
}
