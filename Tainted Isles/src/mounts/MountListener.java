package mounts;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;

import game.GameAPI;

public class MountListener implements Listener {

	public static HashMap<UUID, Location> mountLoc = new HashMap<UUID, Location>();
	public static HashMap<UUID, Integer> mounting = new HashMap<UUID, Integer>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		
		MountAPI.start();
	}
	
	public void onUnload() {
		
	}

    @EventHandler
    public void onCancelDamager(EntityDamageByEntityEvent e) {

    		if (e.isCancelled()) {
    			return; // stop them dismounting if the damage doesn't go through
    		}
    	
            if (e.getEntity().getType() == EntityType.HORSE){
                if (e.getEntity().isInsideVehicle()){
                    e.getEntity().remove();
                } else {
                    e.setCancelled(true);
                }
            }

            if (e.getEntity() instanceof LivingEntity && e.getEntity().isInsideVehicle()){
                if (e.getEntity().getVehicle().getType() == EntityType.HORSE){
                    e.getEntity().getVehicle().eject();
                }
            }

            if (e.getDamager() instanceof Player){

                Player pl = (Player) e.getDamager();
                
                if (MountAPI.isMounted(pl)){
                    if (pl.getVehicle().getType() == EntityType.HORSE){
                        MountAPI.dismount(pl);
                    }
                }

                if (e.getEntity() instanceof LivingEntity && MountAPI.isMounting(pl)) {
                    MountAPI.stopMounting(pl);
                }
            }
    }

    @EventHandler
    public void onHit(EntityDamageEvent e) {

    	if (e.isCancelled()) {
    		return;
    	}
    	
        if (e.getEntity().getType() == EntityType.HORSE){
            if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
                e.setCancelled(true);
            }
        }

        if (e.getEntity() instanceof Player) {
            Player pl = (Player) e.getEntity();

            if (MountAPI.isMounting(pl)) {
                MountAPI.stopMounting(pl);
            }
        }

        if (e.getEntity() instanceof Horse){
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL){
                e.setCancelled(true);
            }
        }
    }
	
	  @EventHandler
	    public void onInvClick(InventoryClickEvent e) {
	        if (e.getWhoClicked().getOpenInventory().getTitle().contains("Horse")) {
	            e.setCancelled(true);
	        }
	    }

	    @EventHandler
	    public void onDismount(VehicleExitEvent e) {
	        if (e.getExited() instanceof Player && e.getVehicle() instanceof Horse) {
	            e.getVehicle().remove();
	        }
	    }

	    @EventHandler
	    public void onDeath(PlayerDeathEvent e){
	        if (e.getEntity().isInsideVehicle()){
	            if (e.getEntity().getVehicle().getType() == EntityType.HORSE){
	                e.getEntity().getVehicle().remove();
	            }
	        }
	    }
	    
	    @EventHandler
	    public void onMountSummon(PlayerInteractEvent e) {
	        if (e.getPlayer().getInventory().getItemInMainHand() != null){
	            if (e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND)) {
	                if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.SADDLE){
	                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
		                        if (!(MountAPI.isMounting(e.getPlayer()) || MountAPI.isMounted(e.getPlayer()))) {
		                        	MountAPI.setMounting(e.getPlayer(), 5, e.getPlayer().getLocation());
		                        }
	                    }
	                }
	            }
	        }
	    }

	    @EventHandler
	    public void onMove(PlayerMoveEvent e) {
	        if (MountAPI.isMounting(e.getPlayer()) &&
	                MountAPI.getMountingLoc(e.getPlayer()).distanceSquared(e.getTo()) >= 2.0) {
	            MountAPI.stopMounting(e.getPlayer());
	        }
	    }
	    
	    @EventHandler
	    public void onScrollTeleport(PlayerTeleportEvent e) {
	        if (MountAPI.isMounting(e.getPlayer())) {
	        	 MountAPI.stopMounting(e.getPlayer());
	        }

	        if (MountAPI.isMounted(e.getPlayer())) {
		        MountAPI.dismount(e.getPlayer());
	        }
	    }
	    
	    @EventHandler
	    public void onPlayerRandomHorseMount(PlayerInteractEntityEvent e){
	        if (e.getRightClicked().getType() == EntityType.HORSE){
	                e.setCancelled(true);
	        }
	    }
	    
	    @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent e) {

	        if (mounting.containsKey(e.getPlayer().getUniqueId())) {
	            mounting.remove(e.getPlayer().getUniqueId());
	            mountLoc.remove(e.getPlayer().getUniqueId());
	        }
	        if (e.getPlayer().getVehicle() != null && e.getPlayer().getVehicle().getType() == EntityType.HORSE) {
	            e.getPlayer().getVehicle().remove();
	            e.getPlayer().teleport(e.getPlayer().getVehicle().getLocation().add(0, 1, 0));
	            // move them down a block so they don't respawn in the air.
	        }
	    }
	
}
