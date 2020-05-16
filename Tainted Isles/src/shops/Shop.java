package shops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import game.GameAPI;
import gold.GoldAPI;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;

public class Shop {

	Player pl;
	UUID uuid;
	Location loc;
	int seconds;
	Entity shopKeep;
	Inventory inv;
	int tax;
	boolean closed;
	
	public Shop(Player pl, Location loc, Entity shopKeep, int minutes) {
		this.pl = pl;
		this.uuid = pl.getUniqueId();
		this.loc = loc;
		this.shopKeep = shopKeep;
		this.seconds = minutes * 60;
		this.inv = Bukkit.createInventory(null, 9, pl.getName() + "'s Shop");
		this.tax = 10;
		this.closed = true;
	}
	
	public boolean contains(ItemStack item) {
		if (inv.contains(item)) {
			return true;
		}
		
		return false;
	}
	
	public void closeDownShopFully() {
		// so save any remaining items etc.
		
		if (pl.isOnline()) {
			pl.sendMessage(ChatColor.GRAY + "Your shop has closed. Any remaining items will be sent to your collection store.");
		}
		
		inv.setItem(inv.getSize() - 1, null); // get rid of the toggle item.
		
		boolean empty = true;
		
		for (ItemStack item : inv.getContents()) {
			if (item != null) {
				ItemAPI.removePrice(item);
				empty = false;
			}
		}
		
		if (!empty) {
			ShopAPI.saveCollection(pl.getUniqueId(), inv);	
		}
		
		ShopListener.shop.remove(pl.getUniqueId());
	}
	
	public void setupInventory() {
		ItemStack toggle = ItemAPI.create(Material.MUSIC_DISC_CHIRP, ChatColor.GREEN + "Click here to open your shop.", Arrays.asList(ChatColor.GRAY + 
				"You will not be able to remove or add items to your shop while it is open.", ChatColor.GRAY + "To remove an item, right click it. To add an item, drag it into the inventory."), 1);
		inv.setItem(inv.getSize() - 1, toggle);
		// use inv.getSize() just in case we add a way to increase your shop space.
	}
	
	public void toggleStatus() {
		pl.playSound(pl.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
		
		if (isClosed()) {
			openShop();
			ItemStack toggle = ItemAPI.create(Material.MUSIC_DISC_CAT, ChatColor.RED + "Click here to close your shop.", Arrays.asList(ChatColor.GRAY + 
					"While closed, you can add or remove items."), 1);
			pl.sendMessage(ChatColor.GREEN + "Your shop is now open.");
			inv.setItem(inv.getSize() - 1, toggle);
			
		} else {
			closeShop();
			pl.sendMessage(ChatColor.RED + "Your shop is now closed.");
			ItemStack toggle = ItemAPI.create(Material.MUSIC_DISC_CHIRP, ChatColor.GREEN + "Click here to open your shop.", Arrays.asList(ChatColor.GRAY + 
					"You will not be able to remove or add items to your shop while it is open."), 1);
			inv.setItem(inv.getSize() - 1, toggle);
		}
	}
	
	public void openShop() {
		closed = false;
	}
	
	public void closeShop() {
		closed = true;
		List<HumanEntity> list = new ArrayList<HumanEntity>();
		list.addAll(getViewers()); // stop CME (concurrent modification exception)
		
		for (HumanEntity pl : list) {
			if (uuid != pl.getUniqueId()) {
				pl.closeInventory();
			}
		}
		
		for (Player pl : ShopListener.buying_from.keySet()) {
			Shop s = ShopListener.buying_from.get(pl);
			
			if (s == this) {
				ShopListener.buying_from.remove(pl);
				pl.sendMessage(ChatColor.RED + "The shop you were buying an item from has been closed, so you will not be able to buy this item.");
			}
		}
	}
	
	public void addItem(ItemStack item, int price) {
		ItemAPI.addPrice(item, price);
		inv.addItem(item);
	}
	
	public void changePrice(ItemStack item, int price) {
		ItemAPI.removePrice(item);
		ItemAPI.addPrice(item, price);
	}
	
	public void removeItem(ItemStack item) {
		inv.removeItem(item);
	}
	
	public void sellItem(ItemStack item, int itemAmount, Player buyer) {
		int amount = ItemAPI.getPrice(item) * itemAmount;
		ItemAPI.removePrice(item);
		
		GameAPI.log("Amount before tax: " + amount);
		
		float taxModifier =  (1 - (tax / 100));
		
		GameAPI.log("Tax: " + taxModifier);
		
		amount *= taxModifier;
		
		GameAPI.log("Amount after tax: " + amount);
		
		if (pl.isOnline()) {
			pl.sendMessage(ChatColor.GREEN + "You have sold x" + itemAmount + " " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + ", and recieved " + 
					ChatColor.YELLOW + amount + ChatColor.GREEN + "g for this transaction.");
		}
		
		GoldAPI.add(uuid, amount);
	}
	
	
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public boolean isOwner(Player pl) {
		if (this.pl == pl) {
			return true;
		}
		
		return false;
	}

	public int getSeconds() {
		return seconds;
	}
	
	public List<HumanEntity> getViewers() {
		return inv.getViewers();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Entity getShopKeeper() {
		return shopKeep;
	}
	
	public Player getOwner() {
		return pl;
	}
	
	public UUID getOwnerUUID() {
		return uuid;
	}
	
	public int getTax() {
		return tax;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public boolean isClosed() {
		return closed;
	}
}
