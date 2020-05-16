package mobs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;

import enums.MobType;
import game.GameAPI;
import items.ItemAPI;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import perms.PermissionAPI;

public class MobListener implements Listener {

	public static HashMap<Location, String> spawners = new HashMap<Location, String>();
	public static HashMap<Location, Spawner> data = new HashMap<Location, Spawner>();
	public static HashMap<Location, List<LivingEntity>> mobs = new HashMap<>();
	public static HashMap<Player, Location> placing = new HashMap<>();
	public static HashMap<LivingEntity, Double> moveSpeed = new HashMap<LivingEntity, Double>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		MobAPI.load();
		MobAPI.spawn();
	}

	public void onUnload() {
		MobAPI.save();
		MobAPI.clearMobs();
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		for (Entity ent : e.getChunk().getEntities()) {
			if (ent instanceof LivingEntity && !(ent instanceof Player)) {
				if (ent != null && MobAPI.isCustomMob((LivingEntity) ent)) {
					ent.remove();
					MobAPI.removeFromList((LivingEntity) ent, true);
				}
			}
		}
	}

	@EventHandler
	public void onMove(EntityTargetEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e.getEntity();

			if (MobAPI.isCustomMob(le)) {
				if (e.getTarget() != null) {
					MobAPI.setSpeed(le, moveSpeed.get(le));
					moveSpeed.remove(le);
				} else {
					moveSpeed.put(le, ((CraftLivingEntity) le).getHandle()
							.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
					MobAPI.setSpeed(le, 0);
				}

			}
		}

	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.NATURAL || e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (placing.containsKey(e.getPlayer())) {
			placing.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onSpawnerBreak(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.SPAWNER)) {
			if (spawners.containsKey(e.getBlock().getLocation())) {
				if (PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) {
					if (!e.isCancelled()) {
						e.getPlayer().sendMessage(ChatColor.GRAY + "Spawner removed.");
						MobAPI.removeSpawner(e.getBlock().getLocation());
					}
				}
			}
		}
	}

	@EventHandler
	public void onSpawnerPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType().equals(Material.SPAWNER)) {
			if (PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) {

				if (placing.containsKey(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.GRAY
							+ "You are already placing a spawner. Finish that process before trying to place another.");
					return;
				}

				if (spawners.containsKey(e.getBlock().getLocation())) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "There is already a spawner at this location.");
					e.setCancelled(true);
					return;
				}

				e.getPlayer().sendMessage(ChatColor.GRAY + "To place a spawner, type in chat the following format.");
				e.getPlayer().sendMessage(ChatColor.GRAY + "mob_type:tier#amount");
				e.getPlayer().sendMessage(ChatColor.YELLOW + "Example: goblin:4#1");
				e.getPlayer().sendMessage(ChatColor.GRAY + "This will make the spawner spawn 1 T4 goblin.");
				e.getPlayer().sendMessage(ChatColor.GRAY + "To add multiple types, simply add a comma with "
						+ ChatColor.UNDERLINE + "NO SPACES" + ChatColor.GRAY + " and write another mob.");
				e.getPlayer().sendMessage(ChatColor.YELLOW + "Available mob types: " + Arrays.asList(MobType.values()));
				e.getPlayer().sendMessage(ChatColor.GRAY + "Type cancel to stop this process.");

				placing.put(e.getPlayer(), e.getBlock().getLocation());
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				if (e.getClickedBlock() != null) {
					if (e.getClickedBlock().getType().equals(Material.SPAWNER)) {
						if (spawners.containsKey(e.getClickedBlock().getLocation())) {
							if (PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) {
								e.getPlayer().sendMessage(ChatColor.GRAY + "Spawner Info:");
								e.getPlayer()
										.sendMessage(ChatColor.GRAY + spawners.get(e.getClickedBlock().getLocation()));
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onSpawnerCreate(AsyncPlayerChatEvent e) {
		if (placing.containsKey(e.getPlayer())) {
			e.setCancelled(true);

			if (PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) { // just in case
				String msg = e.getMessage();

				if (msg.equalsIgnoreCase("cancel")) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "Spawner placement cancelled.");

					Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() { // CAN'T
																											// REMOVE A
																											// BLOCK
																											// ASYNC
						public void run() {
							placing.get(e.getPlayer()).getBlock().setType(Material.AIR);
							placing.remove(e.getPlayer());
						}
					}, 1L);

					return;
				}

				if (!MobAPI.isProperFormat(msg)) {
					e.getPlayer().sendMessage(ChatColor.RED
							+ "Invalid format. You are still in the placement process, just retype the correct format.");
					e.getPlayer().sendMessage(ChatColor.GRAY + "Type cancel to stop this process.");
					return;
				}

				msg += "/" + Mob.getRespawnTime(ItemAPI.getTier(MobAPI.getTierFromSpawnerString(msg)));
				MobAPI.addSpawner(placing.get(e.getPlayer()), msg);
				e.getPlayer().sendMessage(ChatColor.GRAY + "Spawner succesfully created.");

				Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() { // CAN'T REMOVE A
																										// BLOCK ASYNC
					public void run() {
						placing.get(e.getPlayer()).getBlock().setType(Material.AIR);
						placing.remove(e.getPlayer());
					}
				}, 1L);

			} else {
				placing.remove(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onSpawn(EntitySpawnEvent e) {
		if (e.getEntity().getType().equals(EntityType.WANDERING_TRADER)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			e.getDrops().clear();
			e.setDroppedExp(0);

			if (e.getEntity() instanceof LivingEntity) {
				MobAPI.removeFromList(e.getEntity(), false);
			}
		}
	}

}
