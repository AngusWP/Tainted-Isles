package player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import game.GameAPI;
import health.HealthAPI;
import perms.PermissionAPI;

public class PlayerListener implements Listener {

	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			pl.kickPlayer("Kicked over reload.");
		}
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
    public void onDrown(EntityDamageEvent e){
        if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING){
            if (e.getEntity() instanceof Player){
                Player pl = (Player) e.getEntity();
                int dmg = HealthAPI.getMaxHealth(pl) / 12;
                e.setDamage(dmg);
            }
        }

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
            if (e.getEntity() instanceof Player){
                Player pl = (Player) e.getEntity();
                Double dmg = e.getDamage();
                Double blocks = e.getDamage();
                if (blocks >= 2) {
                    dmg = (HealthAPI.getMaxHealth(pl) * 0.02D) * blocks;
                    // some maths to give fall damage actual damage that is relevant to custom hp
                }

                if (dmg > pl.getHealth()){
                    dmg = (pl.getHealth() - 1);
                    // you can't die from fall damage
                }

                e.setDamage(dmg);
            }
        }
        
        if (e.getEntity() instanceof Player) {
            Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() {
            	public void run() {
            		if (e.getEntity().isDead()) {
            			PlayerAPI.sendDeathMessage((Player) e.getEntity(), e.getCause());
            		}
            	}
            }, 1L);
        }
        

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        //e.setJoinMessage(null);

		if (!PlayerAPI.hasPlayedBefore(e.getPlayer().getUniqueId())) {
			PlayerAPI.createPlayerData(e.getPlayer());
			
			
			
			e.getPlayer().getInventory().clear(); 
			// stop any shit happening over wipe
			
			for (ItemStack i : e.getPlayer().getInventory().getArmorContents()) {
				if (i != null) {
					i.setType(Material.AIR);
				}
			}
			
			
			GameAPI.log("New player " + e.getPlayer().getName() + " joined.");
		}
    }

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDroppedExp(0);
		e.setDeathMessage(null);
	}
	
    @EventHandler
    public void onCraft(CraftItemEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Villager){
            if (e.getDamager() instanceof Player){
                Player pl = (Player) e.getDamager();

                if (pl.getGameMode() == GameMode.CREATIVE) return;
            }

            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodDrop(FoodLevelChangeEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onBreak(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player){
            if ((e.getEntity() instanceof ItemFrame || e.getEntity() instanceof Minecart)
                    && ((Player) e.getDamager()).getGameMode() != GameMode.CREATIVE){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onWheatBreak(PlayerInteractEvent e){
        if (e.getAction() == Action.PHYSICAL){
            if (e.getClickedBlock().getType() == Material.FARMLAND){
                e.setCancelled(true);
            }
        }

        
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                if (e.getClickedBlock().getType() == Material.TRAPPED_CHEST){
                    e.getPlayer().sendMessage(ChatColor.GRAY + "You cannot open this chest.");
                    e.setCancelled(true);
                }
                
                if (e.getClickedBlock().getType() == Material.LECTERN) {
                	e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        //e.setQuitMessage(null);
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        List<InventoryType> inv = new ArrayList<>();
        inv.add(InventoryType.FURNACE);
        inv.add(InventoryType.BEACON);
        
        if (!(PermissionAPI.isHighStaff(e.getPlayer().getUniqueId()) && e.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
        	inv.add(InventoryType.WORKBENCH);
        	inv.add(InventoryType.LOOM);
        }
        
        inv.add(InventoryType.HOPPER);
        inv.add(InventoryType.BREWING);
        inv.add(InventoryType.DISPENSER);
        inv.add(InventoryType.DROPPER);
        inv.add(InventoryType.ENDER_CHEST);
        inv.add(InventoryType.CARTOGRAPHY);
        inv.add(InventoryType.BLAST_FURNACE);
        inv.add(InventoryType.STONECUTTER);
        inv.add(InventoryType.GRINDSTONE);
        inv.add(InventoryType.ENCHANTING);
        inv.add(InventoryType.SMOKER);
        inv.add(InventoryType.ANVIL);
        inv.add(InventoryType.BARREL);
        // inventories that we don't want players to be able to open.

        if (inv.contains(e.getInventory().getType())){
            e.setCancelled(true);
        }

        if (e.getView().getTitle().equalsIgnoreCase("Minecart with Chest")){
            e.setCancelled(true);
        }
    }
	
}

