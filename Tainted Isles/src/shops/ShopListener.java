package shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import game.GameAPI;
import gold.GoldAPI;
import items.ItemAPI;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import player.PlayerAPI;

public class ShopListener implements Listener {

	public static ArrayList<UUID> creating_shops = new ArrayList<UUID>(); // ADMIN SETTING UP NPC SHOPS
	public static HashMap<UUID, Location> npcShop = new HashMap<UUID, Location>(); // entity uuid
	public static HashMap<Hologram, Location> holograms = new HashMap<Hologram, Location>();
	public static HashMap<UUID, Entity> rentMenu = new HashMap<UUID, Entity>(); // pl uuid

	public static HashMap<UUID, Shop> shop = new HashMap<UUID, Shop>(); // pl uuid

	public static HashMap<UUID, Location> hologramData = new HashMap<UUID, Location>();

	public static HashMap<Player, ItemStack> selling_item = new HashMap<Player, ItemStack>(); // slot in respective
																								// inventory.
	public static HashMap<Player, ItemStack> buying_item = new HashMap<Player, ItemStack>();
	public static HashMap<Player, Shop> buying_from = new HashMap<Player, Shop>();
	public static HashMap<Player, Integer> slot = new HashMap<Player, Integer>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		ShopAPI.loadShopNPCData();
		ShopAPI.loadHologramData();
		ShopAPI.start();

		Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() {
			public void run() {
				ShopAPI.updateHolograms();
				// make sure NPCs have loaded.
			}
		}, 3 * 20L);

	}
	
	public void onUnload() {
		
		for (UUID uuid : shop.keySet()) {
			shop.get(uuid).closeDownShopFully();
		}
		
		ShopAPI.saveShopNPCData(true);
		ShopAPI.saveHologramData(true);
	}

	@EventHandler
	public void onRemove(NPCRemoveEvent e) {
		if (ShopAPI.isShopNPC(e.getNPC().getEntity())) {
			ShopAPI.removeEntityNPCShop(e.getNPC().getEntity());
			
			ShopAPI.updateHolograms();
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (creating_shops.contains(e.getPlayer().getUniqueId())) {
			creating_shops.remove(e.getPlayer().getUniqueId());
		}

		if (buying_item.containsKey(e.getPlayer())) {
			buying_item.remove(e.getPlayer());
			buying_from.remove(e.getPlayer());
			slot.remove(e.getPlayer());
		}

		if (selling_item.containsKey(e.getPlayer())) {
			ShopAPI.removeIdentifier(buying_item.get(e.getPlayer()));
			selling_item.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (buying_item.containsKey(e.getPlayer()) || selling_item.containsKey(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "You are in the process of completing a shop transaction.");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!ShopAPI.hasCollectionData(e.getPlayer().getUniqueId())) {
			ShopAPI.createCollectionData(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onShopClick(InventoryClickEvent e) {

		if (e.getClickedInventory() == null)
			return;
		if (!ShopAPI.isShopInventory(e.getView().getTopInventory()))
			return;

		e.setCancelled(true);

		Player pl = (Player) e.getWhoClicked();
		Shop shop = ShopAPI.getShopFromInventory(e.getView().getTopInventory());
		
		if (shop.getOwnerUUID().equals(pl.getUniqueId())) {
			if (e.getSlot() == e.getView().getTopInventory().getSize() - 1) { // the last slot, where the close button
																				// is.
				shop.toggleStatus();
			}

			if (shop.isClosed()) {

				if (e.getClickedInventory() == e.getView().getTopInventory()) {
					if (e.getCurrentItem() != null) {
						if (e.getClick() == ClickType.RIGHT
								&& !(e.getSlot() == e.getView().getTopInventory().getSize() - 1)) {

							if (pl.getInventory().firstEmpty() == -1) {
								pl.sendMessage(
										ChatColor.RED + "You cannot remove this item, as your inventory is full.");
								return;
							}

							ItemStack item = e.getCurrentItem();
							ItemAPI.removePrice(item);

							pl.sendMessage(ChatColor.GREEN + "Item removed.");
							shop.removeItem(item);
							pl.getInventory().addItem(item);
						}
					}
				}

				if (e.getClickedInventory() == e.getView().getBottomInventory()) {
					if (e.getCurrentItem() == null)
						return;

					ItemStack i = e.getCurrentItem();

					if (!i.hasItemMeta() && i.getItemMeta().hasLore()) {
						pl.sendMessage(ChatColor.GRAY + "You cannot sell this item.");
						return;
					}

					if (shop.getInventory().firstEmpty() == -1) {
						pl.sendMessage(ChatColor.GRAY + "Your shop is full.");
						return;
					}

					pl.closeInventory();
					pl.sendMessage(ChatColor.DARK_AQUA
							+ "Please enter a price for this item (NOTE: if the item is in a stack, the price is PER ITEM, NOT PER STACK.)");
					pl.sendMessage(ChatColor.RED + "Type cancel to stop this.");
					ShopAPI.addIdentifier(i);
					selling_item.put(pl, i);
				}
			}
		} else { // EVERYONE ELSE

			if (shop.isClosed()) {
				pl.closeInventory();
				pl.sendMessage(ChatColor.RED + "The shop has closed.");
				return;
			}
			
			if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
			if (e.getSlot() == e.getClickedInventory().getSize() - 1) return;
			
			if (pl.getInventory().firstEmpty() == -1) {
				pl.sendMessage(ChatColor.GRAY + "You must have space in your inventory to purchase an item.");
			}
			
			
			pl.sendMessage(ChatColor.DARK_AQUA + "Please enter the amount of this item you would like to buy (x" + e.getCurrentItem().getAmount() + ", Price (FOR ONE): " + ItemAPI.getPrice(e.getCurrentItem()) + ")");
			pl.closeInventory();
			
			buying_item.put(pl, e.getCurrentItem());
			buying_from.put(pl, shop);
			slot.put(pl, e.getSlot());
		}
	}

	
	
	@EventHandler
	public void onRentClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) return;
		
		if (e.getView().getTitle().equalsIgnoreCase("Rent")) {
			if (!e.getClickedInventory().equals(e.getView().getTopInventory())) {
				return;
			}

			e.setCancelled(true);

			if (e.getSlot() == 5) {
				rentMenu.remove(e.getWhoClicked().getUniqueId());
				e.getWhoClicked().closeInventory();
				return;
			}

			if (e.getSlot() == 3) {
				// the big one.
				e.getWhoClicked().closeInventory();

				if (ShopAPI.hasShop(e.getWhoClicked().getUniqueId())) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You already have an open shop.");
					return;
				}

				if (ShopAPI.hasItemsInCollection(e.getWhoClicked().getUniqueId())) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You currently have items in your collection store. Please retrieve these items before renting a shop.");
					return;
				}
				
				// recheck if someone's rented it in this time
				
				if (ShopAPI.isRented(rentMenu.get(e.getWhoClicked().getUniqueId()))) {
					e.getWhoClicked().sendMessage(ChatColor.RED
							+ "It appears someone else has rented the shop while you were in the inventory.");
					e.getWhoClicked().closeInventory();

					return;
				}

				if (GoldAPI.getBalance(e.getWhoClicked().getUniqueId()) < ShopAPI
						.getRentPrice(e.getWhoClicked().getLocation())) {
					e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot afford to rent this shop. Make sure the required gold is in your bank.");
					return;
				}

				Player pl = (Player) e.getWhoClicked();
				
				GoldAPI.take(pl.getUniqueId(), ShopAPI.getRentPrice(pl.getLocation()));
				ShopAPI.createShop(pl, rentMenu.get(pl.getUniqueId()));
				pl.playSound(pl.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
				GoldAPI.sendBalanceUpdate(pl);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (!e.getHand().equals(EquipmentSlot.HAND)) return;
		if (!e.getClickedBlock().getType().equals(Material.JUKEBOX)) return;
	
		if (!ShopAPI.hasItemsInCollection(e.getPlayer().getUniqueId())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You do not have any items in your collection store.");
			return;
		}
		
		e.getPlayer().openInventory(ShopAPI.loadCollection(e.getPlayer().getUniqueId()));
		e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 1F);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equalsIgnoreCase("Collection Store")) return;
		
		e.setCancelled(true);
		
		if (e.getClickedInventory() == null) return;
		if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
		if (e.getCurrentItem() == null) return;
		
		if (e.getWhoClicked().getInventory().firstEmpty() == -1) {
			e.getWhoClicked().sendMessage(ChatColor.GRAY + "You have a full inventory.");
			return;
		}
		
		e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
        e.getView().getTopInventory().removeItem(e.getCurrentItem());
        ((Player) e.getWhoClicked()).updateInventory();
        
        ShopAPI.saveCollection(e.getWhoClicked().getUniqueId(), e.getView().getTopInventory());
	}
	
    @EventHandler
    public void onCollectionClose(InventoryCloseEvent e){
    	if (!e.getView().getTitle().equalsIgnoreCase("Collection Store")) return;
    	ShopAPI.saveCollection(e.getPlayer().getUniqueId(), e.getView().getTopInventory());
    }
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getHand().equals(EquipmentSlot.HAND)) {

			if (e.getRightClicked() instanceof Player) {
				Player npc = (Player) e.getRightClicked();

				if (PlayerAPI.isNPC(npc)) {
					if (ShopAPI.isAddingShopNPC(e.getPlayer().getUniqueId())) {
						if (e.getPlayer().isSneaking()) {

							if (ShopAPI.isShopNPC(e.getRightClicked())) {
								ShopAPI.removeEntityNPCShop(e.getRightClicked());
								e.getPlayer().sendMessage(ChatColor.GRAY + "Shop NPC removed.");
							} else {
								ShopAPI.makeEntityNPCShop(e.getRightClicked());
								e.getPlayer().sendMessage(ChatColor.GRAY + "Shop NPC created.");
							}

							ShopAPI.saveShopNPCData(false);
							ShopAPI.updateHolograms();
						}
						return;
					}

					if (ShopAPI.isShopNPC(e.getRightClicked())) {

						if (buying_item.containsKey(e.getPlayer()) || selling_item.containsKey(e.getPlayer())) {

						}

						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);

						if (ShopAPI.isRented(e.getRightClicked())) {

							Shop shop = ShopAPI.getShopFromEntity(e.getRightClicked());

							if (shop.isClosed() && !shop.getOwnerUUID().equals(e.getPlayer().getUniqueId())) {
								e.getPlayer().sendMessage(ChatColor.GRAY + "This shop is currently closed.");
								return;
							}

							ShopAPI.open(e.getPlayer(), e.getRightClicked());
						} else {
							ShopAPI.openRentMenu(e.getPlayer(), e.getRightClicked());
						}
					}
				}
			}

		}

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		if (buying_item.containsKey(e.getPlayer())) {
			e.setCancelled(true);
			
			ItemStack i = buying_item.get(e.getPlayer());
			buying_item.remove(e.getPlayer());
			
			int s = slot.get(e.getPlayer());
			slot.remove(e.getPlayer());
			
			Shop shop = buying_from.get(e.getPlayer());
			buying_from.remove(e.getPlayer());
			
			String msg = e.getMessage();

			int originalPrice = ItemAPI.getPrice(i);
			
			if (shop == null) {
				e.getPlayer().sendMessage(ChatColor.RED + "That shop's timer has expired.");
				return;
			}
			
			if (msg.contains("cancel")) {
				e.getPlayer().sendMessage(ChatColor.RED + "Item purchase cancelled.");
				return;
			}
			
			if (!shop.contains(i)) {
				e.getPlayer().sendMessage(ChatColor.RED + "That item is no longer in the shop.");
				return;
			}
			
			if (shop.isClosed()) {
				e.getPlayer().sendMessage(ChatColor.RED + "That shop is now closed, so you can no longer purchase the item.");
				return;
			}
			
			try {
				int amount = Integer.parseInt(msg);
				
				if (amount < 0 || amount > i.getAmount()) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "The amount must be more than 0, and less than the item's max amount (" + i.getAmount() + ".");
					e.getPlayer().sendMessage(ChatColor.RED + "Item purchase cancelled.");
					return;
				}
				
				int price = ItemAPI.getPrice(i) * amount;
				
				if (GoldAPI.getBalance(e.getPlayer().getUniqueId()) < price) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "You cannot afford this.");
					e.getPlayer().sendMessage(ChatColor.RED + "Item purchase cancelled.");
					return;
				}
				
				GoldAPI.take(e.getPlayer().getUniqueId(), price);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Item purchase successful.");
				
				int a = shop.getInventory().getItem(s).getAmount();
				
				shop.sellItem(i, amount, e.getPlayer());
				GameAPI.log("Amount: " + shop.getInventory().getItem(s).getAmount());
				
				
				
				i.setAmount(amount);
				e.getPlayer().getInventory().addItem(i);
				
				if (a - amount <= 0) {
					shop.getInventory().setItem(s, null);
				} else {
					ItemAPI.addPrice(shop.getInventory().getItem(s), originalPrice);
				}
	
				

				
			} catch (NumberFormatException ex) {
				e.getPlayer().sendMessage(ChatColor.GRAY + "The quantity must be a number.");
				e.getPlayer().sendMessage(ChatColor.RED + "Item purchase cancelled.");
				return;
			}
		}
		
		if (selling_item.containsKey(e.getPlayer())) {
			e.setCancelled(true);
			
			ItemStack i = selling_item.get(e.getPlayer());
			selling_item.remove(e.getPlayer());
			
			String msg = e.getMessage();

			if (msg.contains("cancel")) {
				e.getPlayer().sendMessage(ChatColor.RED + "Item sale cancelled.");
				ShopAPI.removeIdentifier(i);
				return;
			}

			if (!ShopAPI.hasShop(e.getPlayer().getUniqueId())) {
				e.getPlayer().sendMessage(ChatColor.RED + "Your shop timer has expired, so you can no longer sell this item.");
				ShopAPI.removeIdentifier(i);
				return;
			}
			
			try {
				int price = Integer.parseInt(msg);
				
				if (price < 0) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "The price must be more than 0.");
					e.getPlayer().sendMessage(ChatColor.RED + "Item sale cancelled.");
					ShopAPI.removeIdentifier(i);
					return;
				}
				
				e.getPlayer().getInventory().removeItem(i);
				ShopAPI.removeIdentifier(i);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Your item is now in your shop.");
				ShopAPI.getShop(e.getPlayer().getUniqueId()).addItem(i, price);
				
			} catch (NumberFormatException ex) {
				e.getPlayer().sendMessage(ChatColor.GRAY + "The price MUST be a number.");
				e.getPlayer().sendMessage(ChatColor.RED + "Item sale cancelled.");
				ShopAPI.removeIdentifier(i);
				return;
			}
		}
	}

}
