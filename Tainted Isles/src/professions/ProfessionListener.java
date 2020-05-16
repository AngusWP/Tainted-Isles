package professions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import enums.Profession;
import enums.Tier;
import game.GameAPI;
import graphics.GraphicsAPI;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;
import perms.PermissionAPI;

public class ProfessionListener implements Listener {

	private List<Location> harvested = new ArrayList<Location>();
	private List<Player> harvesting = new ArrayList<Player>();
	
	public static List<Player> placing_artisan = new ArrayList<Player>();
	
	public static HashMap<Location, Integer> wood = new HashMap<Location, Integer>();
	
	// this is for any respawning nodes
	public static HashMap<Location, Plant> plant_data = new HashMap<>();
	public static HashMap<Location, Wood> wood_data = new HashMap<Location, Wood>(); 
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		ProfessionAPI.respawn();
		ProfessionAPI.load();
	}
	
	public void onUnload() {
		
		for (Location loc : wood_data.keySet()) {
			loc.getBlock().setType(wood_data.get(loc).getMaterial());
			// so it regenerates.
		}
		
		for (Location loc : plant_data.keySet()) {
			loc.getBlock().setType(plant_data.get(loc).getMaterial());
            ProfessionAPI.setFullyGrown(loc, plant_data.get(loc).getTier());
		}
		
		ProfessionAPI.save();
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals("Artisan Inventory")) {
			if (e.getClickedInventory()  != null && !e.getClickedInventory().equals(e.getView().getTopInventory())) return;
		
			e.setCancelled(true);
			Player pl = (Player) e.getWhoClicked();
			
			int slot = e.getSlot();
			
			if (slot >= 11 && slot <= 15) {
				
				Tier t;
				
				switch (slot) {
				case 12:
					t = Tier.TWO;
					break;
				case 13:
					t = Tier.THREE;
					break;
				case 14:
					t = Tier.FOUR;
					break;
				case 15:
					t = Tier.FIVE;
					break;
				default:
					t = Tier.ONE;
					break;
				}
				
				if (pl.getInventory().firstEmpty() == -1) {
					pl.sendMessage(ChatColor.GRAY + "You cannot forge a trophy with a full inventory.");
					return;
				}
				
				if (!ProfessionAPI.hasRequiredShards(pl, t)) {
					pl.sendMessage(ChatColor.GRAY + "You do not have enough shards to forge this item.");
					return;
				}
				
				ProfessionAPI.takeShards(pl, ProfessionAPI.getRequiredShards(t), t);
				pl.sendMessage(ChatColor.GRAY + "You have forged a T" + ItemAPI.getTier(t) + " trophy.");
				pl.getInventory().addItem(ItemAPI.generateTrophy(t));
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		
		if (e.getHand() == null) return; // for some reason the hand check was causing an NPE
		
		if (!e.getHand().equals(EquipmentSlot.HAND)) return;
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.STONECUTTER)) {
			e.setCancelled(true);
			ProfessionAPI.openArtisanInventory(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onArtisanBlock(BlockBreakEvent e) {
		
		if (!ProfessionAPI.isArtisanBlock(e.getBlock().getLocation())) return;
		if (!ProfessionAPI.isArtisanItem(e.getPlayer().getInventory().getItemInMainHand())) return;
		
		if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
		
		if (ProfessionAPI.isArtisanRespawning(e.getBlock().getLocation())) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "This wood is currently regrowing.");
			return;
		}
		
		Tier tier = ProfessionAPI.getTierFromArtisanBlock(e.getBlock().getType());
		
		if (!ProfessionAPI.isRequiredLevel(e.getPlayer().getUniqueId(), Profession.ARTISAN, tier)) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "You do not have the skill to cut this wood.");
			return;
		}
		
		wood_data.put(e.getBlock().getLocation(), new Wood(tier, e.getBlock().getLocation(), ProfessionAPI.getRespawnTime(Profession.ARTISAN, tier), e.getBlock().getType()));
		
		int success = ProfessionAPI.getSuccessRate(e.getPlayer().getUniqueId(), tier, Profession.ARTISAN);
		int chance = new Random().nextInt(100) + 1;
		
		if (success >= chance) {
			e.getPlayer().sendMessage(ChatColor.GREEN + "You succesfully cut this wood.");
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			
			ProfessionAPI.addEXP(e.getPlayer().getUniqueId(), Profession.ARTISAN, 
					ProfessionAPI.getReturnedExp(Profession.ARTISAN, ProfessionAPI.getTierFromArtisanBlock(e.getBlock().getType())));
			e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), 
					ProfessionAPI.getReturnedItem(Profession.ARTISAN, ProfessionAPI.getTierFromArtisanBlock(e.getBlock().getType())));
		} else {
			e.getPlayer().sendMessage(ChatColor.GRAY + "You failed to cut this wood.");
		}
		
		e.getBlock().setType(Material.BLACK_WOOL);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		
		if (!PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) return;
		if (!placing_artisan.contains(e.getPlayer())) return;
		if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
		
		if (!ProfessionAPI.isArtisanBlockItem(e.getBlockPlaced().getType())) return;
		
		if (ProfessionAPI.isArtisanBlock(e.getBlock().getLocation())) {
			e.getPlayer().sendMessage(ChatColor.RED + "There is already an artisan block at this location.");
			return;
		}
		
		e.getPlayer().sendMessage(ChatColor.GRAY + "Tier " + ItemAPI.getTier(ProfessionAPI.getTierFromArtisanBlock(e.getBlockPlaced().getType())) + " artisan block placed.");
		ProfessionAPI.addArtisanBlock(e.getBlock().getLocation(), ProfessionAPI.getTierFromArtisanBlock(e.getBlockPlaced().getType()));
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (!PermissionAPI.isHighStaff(e.getPlayer().getUniqueId())) return;
		if (!ProfessionAPI.isArtisanBlockItem(e.getBlock().getType())) return;
		if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
		
		if (ProfessionAPI.isArtisanBlock(e.getBlock().getLocation())) {
		
		if (!placing_artisan.contains(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You must be in /artisan mode to remove artisan blocks.");
			e.setCancelled(true);
			return;
		}
		
		if (ProfessionAPI.isArtisanBlock(e.getBlock().getLocation())) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "Tier " + ItemAPI.getTier(ProfessionAPI.getTierFromArtisanBlock(e.getBlock().getType())) + " artisan block removed.");
			ProfessionAPI.removeArtisanBlock(e.getBlock().getLocation());

			return;
		}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (placing_artisan.contains(e.getPlayer())) {
			placing_artisan.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ProfessionAPI.updateData(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onFarm(PlayerInteractEvent e) { // farming event done.
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				
				if (e.getClickedBlock() == null) return;
				 
				if (ProfessionAPI.isSurvivalistItem(e.getClickedBlock().getType())) { // just add more items here.
					e.setCancelled(true); // stop them harvesting it in a vanilla way.
					Tier tier = ProfessionAPI.getTierFromSurvivalistBlock(e.getClickedBlock().getType());
					
					if (ProfessionAPI.isRequiredLevel(e.getPlayer().getUniqueId(), Profession.SURVIVALIST, tier))
					
					if (harvesting.contains(e.getPlayer())) {
						GraphicsAPI.sendActionBar(e.getPlayer(), ChatColor.RED + "You are already harvesting another plant.", 2);
						return;
					}
					
					if (!harvested.contains(e.getClickedBlock().getLocation())) {
						GraphicsAPI.sendActionBar(e.getPlayer(), ChatColor.DARK_AQUA + "Trying to harvest...", 2);
						harvesting.add(e.getPlayer());
						harvested.add(e.getClickedBlock().getLocation());
						
						new BukkitRunnable() {
							public void run() {
								harvesting.remove(e.getPlayer());
								harvested.remove(e.getClickedBlock().getLocation());
								int success = ProfessionAPI.getSuccessRate(e.getPlayer().getUniqueId(), tier, Profession.SURVIVALIST);
								int chance = new Random().nextInt(100) + 1;
								
								plant_data.put(e.getClickedBlock().getLocation(), 
										new Plant(ProfessionAPI.getTierFromSurvivalistBlock(e.getClickedBlock().getType()), e.getClickedBlock().getLocation(), ProfessionAPI.getRespawnTime(Profession.SURVIVALIST, 
												ProfessionAPI.getTierFromSurvivalistBlock(e.getClickedBlock().getType())), e.getClickedBlock().getType()));
								
								if (success >= chance) {
									GraphicsAPI.sendActionBar(e.getPlayer(), ChatColor.DARK_AQUA + "You have succesfully harvested this plant!", 2);
									e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
									
									ProfessionAPI.addEXP(e.getPlayer().getUniqueId(), Profession.SURVIVALIST, 
											ProfessionAPI.getReturnedExp(Profession.SURVIVALIST, ProfessionAPI.getTierFromSurvivalistBlock(e.getClickedBlock().getType())));
									e.getPlayer().getWorld().dropItem(e.getClickedBlock().getLocation(), 
											ProfessionAPI.getReturnedItem(Profession.SURVIVALIST, ProfessionAPI.getTierFromSurvivalistBlock(e.getClickedBlock().getType())));
								} else {
									GraphicsAPI.sendActionBar(e.getPlayer(), ChatColor.RED + "You failed to harvest this item.", 2);
								}
								
								e.getClickedBlock().setType(Material.AIR);
							}
						}.runTaskLater(GameAPI.getInstance(), 40L);
					} else {
						GraphicsAPI.sendActionBar(e.getPlayer(), ChatColor.RED + "This plant is already being harvested.", 2);
					}
					

				}
			}

		}
	}
	
}
