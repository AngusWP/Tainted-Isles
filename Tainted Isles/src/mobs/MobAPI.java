package mobs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import enums.MobType;
import enums.Tier;
import game.GameAPI;
import items.ItemAPI;
import net.minecraft.server.v1_14_R1.GenericAttributes;

public class MobAPI {

	public static boolean hasPlayersNearby(Location loc) {
		for (Entity ent : loc.getChunk().getEntities()) {
			if (ent instanceof Player) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void spawn() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Location loc : MobListener.data.keySet()) {
					Spawner spawner = MobListener.data.get(loc);

					if (spawner == null) {
						return;
					}

					if (spawner.isRespawning()) {
						
						if (spawner.getCurrentTimer() != 0 && !spawner.isFirstSpawn()) {
							spawner.setCurrentTime(spawner.getCurrentTimer() - 1);
						} else {
							if (hasPlayersNearby(loc)) {
								String data = MobListener.spawners.get(loc);
								String originalData = data; // save it up here before it gets split
								
								if (data.contains(",")) {
									for (String s : data.split(",")) {
										s = s.split("/")[0];
										String type = s.split(":")[0];
										int tier = Integer.parseInt(s.split(":")[1].split("#")[0]);
										int amt = Integer.parseInt(s.split("#")[1]);
										for (int i = 0; i < amt; i++) {
											Mob.spawn(ItemAPI.getTier(tier), getType(type), loc);
										}

									}
								} else {
									String type = data.split(":")[0];
									data = data.split("/")[0];
									int tier = Integer.parseInt(data.split(":")[1].split("#")[0]);
									int amt = Integer.parseInt(data.split("#")[1]);

									for (int i = 0; i < amt; i++) {
										Mob.spawn(ItemAPI.getTier(tier), getType(type), loc);
									}
								}

								spawner.setCurrentTime(getRespawnTime(originalData));
								spawner.setRespawning(false);
								spawner.setFirstSpawn(false);
							}
						}
					}
				}
			}
		}, 20L, 20L);
	}
	
	public static void save() {
		File data = new File(GameAPI.getInstance().getDataFolder() + "/spawners.yml");
		YamlConfiguration config = new YamlConfiguration();

		if (!data.exists()) {
			try {
				data.createNewFile();
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
		
		if (MobListener.spawners.isEmpty()) {
			data.delete();
		}
		
		for (Location loc1 : MobListener.spawners.keySet()) {
			String s = String.valueOf(loc1.getWorld().getName()) + "," + (int) loc1.getX() + "," + (int) loc1.getY()
					+ "," + (int) loc1.getZ();
			config.set(s, MobListener.spawners.get(loc1));
			try {
				config.save(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void load() {
		File data = new File(GameAPI.getInstance().getDataFolder() + "/spawners.yml");
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : config.getKeys(false)) {
			String val = config.getString(key);
			String[] str = key.split(",");
			World world = Bukkit.getWorld(str[0]);
			double x = Double.valueOf(str[1]);
			double y = Double.valueOf(str[2]);
			double z = Double.valueOf(str[3]);
			Location loc = new Location(world, x, y, z);
			MobListener.spawners.put(loc, val);
			MobListener.data.put(loc, new Spawner(loc, val, getRespawnTime(val)));
		}
	}

	
	public static void addSpawner(Location loc, String s) {
		MobListener.spawners.put(loc, s);
		s = s.split("/")[1];
		MobListener.data.put(loc, new Spawner(loc, s, Mob.getRespawnTime(ItemAPI.getTier(MobAPI.getTierFromSpawnerString(s)))));
		save();
	}

	public static void removeSpawner(Location loc) {
		MobListener.spawners.remove(loc);
		MobListener.data.remove(loc);
		save();
	}

	public static int getTierFromSpawnerString(String s) {
		if (isProperFormat(s)) {
			return Integer.parseInt(s.split(":")[1].split("#")[0]);
		}
		
		
		return 0;
	}
	
	public static boolean isProperFormat(String s) {
		
		if (s.contains(",")) {
			for (String line : s.split(",")) {
				if (line.contains("#") && line.contains(":")) {
					
					String type = line.split(":")[0];
					
					if (MobAPI.getType(type) == null) {
						return false;
					}
					
					String tier = line.split(":")[1].split("#")[0];
					String amount = line.split("#")[1];
					
					try {
						int t = Integer.parseInt(tier);
						int a = Integer.parseInt(amount);
						
						if (t > 5 || t < 1) {
							return false;
						}
						
						if (a < 1) {
							return false;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				} else { return false; }
			}
			
		} else {
			if (s.contains("#") && s.contains(":")) {
				
				String type = s.split(":")[0];
				
				if (MobAPI.getType(type) == null) {
					return false;
				}
				
				String tier = s.split(":")[1].split("#")[0];
				String amount = s.split("#")[1];
				
				try {
					int t = Integer.parseInt(tier);
					int a = Integer.parseInt(amount);
					
					if (t > 5 || t < 1) {
						return false;
					}
					
					if (a < 1) {
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else { return false; }
		}
		
		return true;
	}
	
	public static void setSpeed(LivingEntity ent, double speed) {
		 ((CraftLivingEntity) ent).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
	}
	
	public static boolean isCustomMob(LivingEntity ent) {
		for (Location loc : MobListener.mobs.keySet()) {
			if (MobListener.mobs.get(loc).contains(ent)) {
				return true;
				
			}
		}
		return false;
	}
	
	public static void removeFromList(LivingEntity le, boolean despawned) {
		for (Location loc : MobListener.mobs.keySet()) {
			if (MobListener.mobs.get(loc).contains(le)) {
				List<LivingEntity> list = MobListener.mobs.get(loc);
				list.remove(le);
				
				if (list.isEmpty()) {
					
					if (MobListener.data.get(loc) != null) {
						Spawner spawner = MobListener.data.get(loc);
						spawner.setRespawning(true);	
						
						if (despawned) {
							spawner.setFirstSpawn(true);
						}
					}
				}
			}
		}
	}

	public static Tier getTier(LivingEntity e) {
		return ItemAPI.getTier(e.getEquipment().getBoots());
	}
	
	public static MobType getType(String s) {
		if (MobType.valueOf(s.toUpperCase()) != null) {
			return MobType.valueOf(s.toUpperCase());
		}

		return null;
	}

	public static EntityType getEntityType(MobType type) {

		if (type.equals(MobType.GOBLIN)) {
			return EntityType.ZOMBIE;
		}

		return EntityType.WITHER_SKELETON;
	}

	public static void clearMobs() {
		for (Entity e : Bukkit.getWorld("world").getEntities()) {
			if (!(e instanceof Player)) {
				if (e instanceof LivingEntity) {
					LivingEntity ent = (LivingEntity) e;
					if (ent.getCustomName() != null) {
						ent.remove();
					}
				}
			}
		}
	}
	
	private static int getRespawnTime(String s) {
		return Integer.parseInt(s.split("/")[1]);
	}
}
