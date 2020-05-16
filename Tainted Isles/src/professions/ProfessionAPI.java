package professions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.block.impl.CraftSweetBerryBush;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import database.DatabaseAPI;
import enums.Profession;
import enums.Tier;
import game.GameAPI;
import items.Custom;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;
import player.PlayerAPI;

public class ProfessionAPI {

	public static void updateData(UUID uuid) {
		for (Profession p : Profession.values()){
			
			File f = new File(GameAPI.getInstance().getDataFolder() + "/professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml");
			
			if (!f.exists()) {
				DatabaseAPI.createYMLFile("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml");
				DatabaseAPI.setEXPValue(uuid, p, 1, 0);
			}
		}

	}
	
    public static void load(){
        File data = new File(GameAPI.getInstance().getDataFolder() + "/professions/artisan_data.yml");
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
            ProfessionListener.wood.put(loc, val);
        }
    }
	
    public static int getArtisanShards(Player pl, Tier tier) {
    	int am = 0;
    	// when we code higher tiers, check the tier
    	
    	for (ItemStack item : pl.getInventory().getContents()) {
    		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
    			if (item.getType().equals(Material.BEETROOT_SEEDS) && item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Spruce")) {
    				am += item.getAmount();
    			}
    		}
    	}
    	
    	return am;
    }
    
    public static boolean isArtisanShard(Player pl, ItemStack item, Tier tier) {
    	
    	// add a switch statement when we code more tiers
    	if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
    		if (item.getType().equals(Material.BEETROOT_SEEDS) && item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Spruce")) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public static void takeShards(Player pl, int amount, Tier tier) {
    	int plShards = getArtisanShards(pl, tier);
    	plShards -= amount; // we have already checked if they have enough, so we don't need to worry about doing that.
    	
    	for (ItemStack item : pl.getInventory().getContents()) {
    		if (item != null) {
        		if (isArtisanShard(pl, item, tier)) {
        			pl.getInventory().remove(item);
        		}	
    		}
    	}
    	
    	ItemStack item = getShard(tier);
    	item.setAmount(plShards);
    	pl.getInventory().addItem(item);
   }
    
   public static boolean isCapLevel(UUID uuid, Profession prof) {
	   if (getLevel(uuid, prof) >= getCapLevel()) {
		   return true;
	   }
	   
	   return false;
   }
    
    public static boolean hasRequiredShards(Player pl, Tier tier) {
    	int am = getArtisanShards(pl, tier);
    	
    	if (am >= getRequiredShards(tier)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static void openArtisanInventory(Player pl) {
    	Inventory inv = Bukkit.createInventory(null, 27, "Artisan Inventory");
    	
    	ItemStack blank = ItemAPI.create(Material.BLACK_STAINED_GLASS_PANE, "", Arrays.asList(" "), 1);
    	
    	for (int i = 0; i < inv.getSize(); i++) {
    		inv.setItem(i, blank);
    	}
    	
    	int tier = 1;
    	
    	for (int i = 11; i < 16; i++) {
    		
    		ItemStack item = ItemAPI.create(Material.TRIPWIRE_HOOK, ChatColor.LIGHT_PURPLE + "Tier " + tier, 
    				Arrays.asList(ChatColor.GRAY + "Click here to forge this trophy.", 
    						ChatColor.YELLOW + "Required Shards: " + getRequiredShards(ItemAPI.getTier(tier)) + " T" + tier + " shards."), 1);
    		
    		inv.setItem(i, item);
    		
    		tier++;
    	}
    	
    	pl.openInventory(inv);
    }
    
    public static ItemStack getShard(Tier tier) {
    	switch (tier) {
    		default:
    			return Custom.SPRUCE_SHARD;
    	}
    }
    
    public static ItemStack getPlant(Tier tier) {
    	switch (tier) {
    		default:
    			return Custom.BERRY;
    	}
    }
    
    public static int getRequiredShards(Tier tier) {
    	return 32; // t1
    }
    
    public static void save(){
        File data = new File(GameAPI.getInstance().getDataFolder() + "/professions/artisan_data.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Location loc : ProfessionListener.wood.keySet()) {
            String s = String.valueOf(loc.getWorld().getName()) + "," + (int) loc.getX() + "," + (int) loc.getY() + "," + (int) loc.getZ();
            config.set(s, ProfessionListener.wood.get(loc));
            try {
                config.save(data);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
	public static void respawn() {
		new BukkitRunnable() {
			public void run() {
				if (ProfessionListener.plant_data != null) {
					for (Location loc : ProfessionListener.plant_data.keySet()) {
						if (loc != null) {
							Plant plant = ProfessionListener.plant_data.get(loc);
							plant.setRespawnTime(plant.getRespawnTime() - 1);
							
							if (plant.getRespawnTime() == 0) {
								loc.getBlock().setType(plant.getMaterial());
								setFullyGrown(loc, plant.getTier());
							}	
						}
					}
				}
				
				if (ProfessionListener.wood_data != null) {
					for (Location loc : ProfessionListener.wood_data.keySet()) {
						if (loc != null) {
							Wood wood = ProfessionListener.wood_data.get(loc);
							wood.setRespawnTime(wood.getRespawnTime() - 1);
							
							if (wood.getRespawnTime() == 0) {
								loc.getBlock().setType(wood.getMaterial());
								ProfessionListener.wood_data.remove(loc);
							}	
						}
					}
				}

			}
		}.runTaskTimer(GameAPI.getInstance(), 20L, 20L);
	}
	
	public static void addArtisanBlock(Location loc, Tier tier) {
		ProfessionListener.wood.put(loc, ItemAPI.getTier(tier));
	}
	
	public static void removeArtisanBlock(Location loc) {
		ProfessionListener.wood.remove(loc);
	}
	
	public static boolean isArtisanBlock(Location loc) {
		if (ProfessionListener.wood.containsKey(loc)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isArtisanBlockItem(Material m) {
		if (m.equals(Material.OAK_LOG) || m.equals(Material.JUNGLE_LOG) || m.equals(Material.BIRCH_LOG) || m.equals(Material.ACACIA_LOG) || m.equals(Material.SPRUCE_LOG)) {
			return true;
		}
			
		return false;
	}
	
	public static boolean isSurvivalistItem(Material m) {
		if (m.equals(Material.SWEET_BERRY_BUSH)) {
			return true;
		}
			
		return false;
	}
	
	public static Tier getTierFromArtisanBlock(Material m) {
		switch (m) {
		case SPRUCE_LOG:
			return Tier.ONE;
		case BIRCH_LOG:
			return Tier.TWO;
		case OAK_LOG:
			return Tier.THREE;
		case JUNGLE_LOG:
			return Tier.FOUR;
		case ACACIA_LOG:
			return Tier.FIVE;
		default:
			break;
		}
		
		GameAPI.log("Failed to retrieve tier from artisan block.");
		return null;
	}
	
	public static boolean isArtisanRespawning(Location loc) {
		if (isArtisanBlock(loc)) {
			if (ProfessionListener.wood_data.containsKey(loc)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isArtisanItem(ItemStack i) {
		
		// this is temp
		
		if (i.getType().equals(Material.BAMBOO)) {
			return true;
		}
		
		return false;
	}
	
	public static void setFullyGrown(Location loc, Tier tier) {
		
		/*
		 * 
		 * switch tier
		 */
		
		CraftSweetBerryBush c = (CraftSweetBerryBush) loc.getBlock().getBlockData();	
        c.setAge(3);
        loc.getBlock().setBlockData(c);
		ProfessionListener.plant_data.remove(loc);
	}
	
	public static int getCapLevel() {
		return 50;
	}
	
	public static int getRespawnTime(Profession p, Tier tier) { // just add profession as a parameter incase we want different respawn times over professions
		switch (tier) {
		case ONE:
			return 30;
		case TWO:
			return 60;
		case THREE:
			return 90;
		case FOUR:
			return 120;
		case FIVE:
			return 150;
		}
		
		return 0;
	}
	
	public static boolean isRequiredLevel(UUID uuid, Profession p, Tier tier) {
		int t = ItemAPI.getTier(tier);
		int level = getLevel(uuid, p);
		int required = 1;
		
		switch (t) {
		case 2:
			required = 10;
			break;
		case 3:
			required = 20;
			break;
		case 4:
			required = 30;
			break;
		case 5:
			required = 40;
			break;
		}
		
		if (level >= required) {
			return true;
		}
		
		return false;
	}
	
	public static int getSuccessRate(UUID uuid, Tier t, Profession p) {
		int success = 60; // base level one success rate
		
		if (!hasData(uuid, p)) {
			updateData(uuid);
		}

		int level = getLevel(uuid, p);
		int tier = ItemAPI.getTier(t);
		
		if (tier == 1) {
			success += (getLevel(uuid, p)) * 4;
			
			if (level >= 10) {
				success = 100;
			}
		}
		
		if (tier == 2) {
			success = 50; // base
			success += (getLevel(uuid, p) - 10) * 5;
			
			if (level >= 20) {
				success = 100;
			}
		}
		
		if (tier == 3) {
			success = 40;
			success += (getLevel(uuid, p) - 20) * 6;
			
			if (level >= 30) {
				success = 100;
			}
		}
		
		if (tier == 4) {
			success = 30;
			success += (getLevel(uuid, p) - 30) * 7;
			
			if (level >= 40) {
				success = 100;
			}
		}
		
		if (tier == 5) {
			success = 20;
			success += (getLevel(uuid, p) - 40) * 8;
			
			if (level >= 50) {
				success = 100;
			}
		}
		return success;
	}
	
	public static int getReturnedExp(Profession p, Tier tier) {
		switch (tier) {
		case ONE:
			return new Random().nextInt(50) + 51;
		case TWO:
			return new Random().nextInt(100) + 151;
		case THREE:
			return new Random().nextInt(150) + 301;
		case FOUR:
			return new Random().nextInt(200) + 501;
		case FIVE: 
			return new Random().nextInt(400) + 701;
		}
		
		return 0;
	}
	
	public static Tier getTierFromSurvivalistBlock(Material m) {
		switch (m) {
		case SWEET_BERRY_BUSH:
			return Tier.ONE; // we only know one atm
		default:
			break;
		}
		
		GameAPI.log("Failed to retrieve tier from survivalist plant.");
		return null;
	}
	
	public static ItemStack getReturnedItem(Profession p, Tier tier) {
		/*
		 * 
		 * another switch statement in future
		 */
		/*
		if (p.equals(Profession.FARMING)) {
			
		}
		*/
		
		if (p.equals(Profession.SURVIVALIST)) {
			return getPlant(tier);
		}
		
		if (p.equals(Profession.ARTISAN)) {
			return getShard(tier);
		}
		
		
		GameAPI.log("Failed to retrieve returned item from profession.");
		return null;
	}
	
	public static boolean hasData(UUID uuid, Profession p) {
		if (DatabaseAPI.exists("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml")) {
			return true;
		}
		
		
		return false;
	}
	
	public static int getLevel(UUID uuid, Profession p) {
		if (hasData(uuid, p)) {
			String s = DatabaseAPI.getEXPValue("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml");
			
			return Integer.parseInt(s.split(",")[0]);
		}
		
		return 1;
	}
	
	public static void setLevel(UUID uuid, Profession p, int level) {
		DatabaseAPI.setString("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml", "EXP", level + ",0");
	}
	
	public static int getEXP(UUID uuid, Profession p) {
		if (hasData(uuid, p)) {
			String s = DatabaseAPI.getEXPValue("professions/" + p.toString().toLowerCase() + "/" + uuid + ".yml");
			
			return Integer.parseInt(s.split(",")[1]);
		}
		
		return 0;
	}
	
	public static void addEXP(UUID uuid, Profession p, int amount) {
		
		if (isCapLevel(uuid, p)) {
			return;
		}
		
		int req_exp = getRequiredEXP(getLevel(uuid, p), p);
		DatabaseAPI.setEXPValue(uuid, p, getLevel(uuid, p), (getEXP(uuid, p) + amount));	
		
		PlayerAPI.getOnlinePlayer(uuid).sendMessage(ChatColor.DARK_AQUA + "+" + amount + " EXP [" + getEXP(uuid, p) + "/" + getRequiredEXP(getLevel(uuid, p), p) + "]");
		 
		if (getEXP(uuid, p) > req_exp){
			int leftover = getEXP(uuid, p) - req_exp;
			levelUp(uuid, p, leftover);
		}
	}
	
	public static int getRequiredEXP(int level, Profession p) {
		return 100 + (level * 35); // temporary
	}
	
	public static void levelUp(UUID uuid, Profession p, int leftover) {
		int level = getLevel(uuid, p) + 1;
		
		Player pl = PlayerAPI.getOnlinePlayer(uuid);
		
		DatabaseAPI.setEXPValue(uuid, p, level, leftover);
		pl.sendMessage(ChatColor.DARK_AQUA + "Your " + p.toString().toLowerCase() + " level has increased to " + level + "!");
		pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	}
}
