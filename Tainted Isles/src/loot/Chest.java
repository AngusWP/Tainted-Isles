package loot;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import enums.Loot;

public class Chest {

	List<ItemStack> items;
	Loot loot;
	Location loc;
	Inventory inv;
	List<Player> viewers;
	
	public Chest(List<ItemStack> items, Loot loot, Location loc, Inventory inv, List<Player> viewers) {
		this.items = items;
		this.loot = loot;
		this.loc = loc;
		this.inv = inv;
		this.viewers = viewers;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public Loot getTier() {
		return loot;
	}
	
	public boolean hasViewers() {
		if (!viewers.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	public boolean isEmpty() {
		if (items.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public List<Player> getViewers() {
		return viewers;
	}
	
	public void addViewer(Player pl) {
		viewers.add(pl);
	}
	
	public void removeViewer(Player pl) {
		viewers.remove(pl);
	}

}
