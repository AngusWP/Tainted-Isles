package trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import combat.CombatAPI;
import game.GameAPI;
import items.Custom;
import shops.ShopListener;

public class TradeListener implements Listener {

	// remember line 209

	// this probably needs recoding, making a trade oop class and do it that way.
	// this was brought over from the original tainted isles, so it's bad code, redo this at a later date
	// for now it's fine as it works but does need a bit of work

	public static HashMap<Player, Player> trading = new HashMap<Player, Player>();
	public static HashMap<Player, Inventory> trade_inv = new HashMap<Player, Inventory>();
	public static List<Inventory> exit_success = new ArrayList<Inventory>();

	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
	}

	public void onUnload() {

	}

	@EventHandler
	public void onTradeClose(InventoryCloseEvent e) {
		if (TradeAPI.isTrading((Player) e.getPlayer())) {
			if (!exit_success.contains(e.getInventory())) {
				e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Trade cancelled.");
				Player trader = TradeAPI.getTradePartner((Player) e.getPlayer());

				TradeAPI.getTradePartner((Player) e.getPlayer()).sendMessage(ChatColor.DARK_AQUA + "Trade cancelled.");
				TradeAPI.stopTrading((Player) e.getPlayer(), TradeAPI.getTradePartner(((Player) e.getPlayer())), false);

				Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() {
					public void run() {
						trader.closeInventory();
					}
				}, 1L);
			}
		}
	}

	@EventHandler
	public void onTrade(InventoryClickEvent e) {
		Player pl = (Player) e.getWhoClicked();

		if (TradeAPI.isTrading(pl)) {

			e.setCancelled(true);

			Inventory top = e.getView().getTopInventory();
			Inventory bot = e.getView().getBottomInventory();
			Inventory other_window = TradeAPI.getTradeInventory(TradeAPI.getTradePartner(pl));

			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {

				if (e.getClickedInventory().equals(bot)) {
					ItemStack item = e.getCurrentItem();

					ArrayList<String> slots_left = new ArrayList<String>();
					int slot = 0;

					for (int i = 0; i < 36; i++) {
						if (TradeAPI.isYourSide(i)) {
							if (top.getItem(i) == null) {
								slots_left.add("" + i);
							}
						}
					}

					if (slots_left.size() == 0) {
						pl.sendMessage(ChatColor.DARK_AQUA + "Your trade window is closed.");
						return;
					}

					for (int i = 0; i < 36; i++) {
						if (TradeAPI.isYourSide(i)) {
							if (top.getItem(i) == null) {
								top.setItem(i, item);
								slot = i;
								break;
							} else {
								slots_left.add("" + i);
							}
						}
					}

					e.getView().getBottomInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
					other_window.setItem(slot + 5, item);

					if (TradeAPI.isConfirmed(top)) {

						top.setItem(30, Custom.CONFIRM_TRADE);
						top.setItem(35, Custom.CONFIRM_TRADE);
						other_window.setItem(30, Custom.CONFIRM_TRADE);
						other_window.setItem(35, Custom.CONFIRM_TRADE);

						pl.sendMessage(ChatColor.DARK_AQUA + "Trade edited, confirm cancelled.");
						TradeAPI.getTradePartner(pl)
								.sendMessage(ChatColor.DARK_AQUA + "Trade edited, confirm cancelled.");
					}

					pl.updateInventory();
					TradeAPI.getTradePartner(pl).updateInventory();
				}

				if (e.getClickedInventory().equals(top)) {

					if (e.getSlot() == 30) {
						if (e.getCurrentItem().equals(Custom.CONFIRM_TRADE)) {

							if (TradeAPI.isConfirmed(top)) {
								exit_success.add(top);
								TradeAPI.stopTrading(pl, TradeAPI.getTradePartner(pl), true);
							} else {
								top.setItem(e.getSlot(), Custom.CONFIRMED_TRADE);
								pl.sendMessage(ChatColor.DARK_AQUA + "Trade confirmed.");
								TradeAPI.getTradePartner(pl)
										.sendMessage(ChatColor.DARK_AQUA + pl.getName() + " has confirmed the trade.");

								other_window.setItem(35, Custom.CONFIRMED_TRADE);

								pl.updateInventory();
								TradeAPI.getTradePartner(pl).updateInventory();
							}

							return;
						}

						if (e.getCurrentItem().equals(Custom.CONFIRMED_TRADE)) {
							top.setItem(e.getSlot(), Custom.CONFIRM_TRADE);
							pl.sendMessage(ChatColor.DARK_AQUA + "Trade unconfirmed.");
							TradeAPI.getTradePartner(pl)
									.sendMessage(ChatColor.DARK_AQUA + pl.getName() + " has unconfirmed the trade.");
							other_window.setItem(35, Custom.CONFIRM_TRADE);
							pl.updateInventory();
							TradeAPI.getTradePartner(pl).updateInventory();
						}

						return;
					}

					if (e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
						return;
					}

					if (TradeAPI.isYourSide(e.getSlot())) {

						pl.getInventory().addItem(e.getCurrentItem());
						top.setItem(e.getSlot(), new ItemStack(Material.AIR));
						other_window.setItem(e.getSlot() + 5, new ItemStack(Material.AIR));

						pl.updateInventory();
						TradeAPI.getTradePartner(pl).updateInventory();

						if (TradeAPI.isConfirmed(top)) {

							top.setItem(30, Custom.CONFIRM_TRADE);
							top.setItem(35, Custom.CONFIRM_TRADE);
							other_window.setItem(30, Custom.CONFIRM_TRADE);
							other_window.setItem(35, Custom.CONFIRM_TRADE);

							pl.sendMessage(ChatColor.DARK_AQUA + "Trade edited, confirm cancelled.");
							TradeAPI.getTradePartner(pl)
									.sendMessage(ChatColor.DARK_AQUA + "Trade edited, confirm cancelled.");

							pl.updateInventory();
							TradeAPI.getTradePartner(pl).updateInventory();
						}
					}
				}
			}

			pl.updateInventory();
			TradeAPI.getTradePartner(pl).updateInventory();
		}

	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {

		if (TradeAPI.isTrading(e.getPlayer())) {
			e.setCancelled(true);
			return;
		}

		if (TradeAPI.getTarget(e.getPlayer()) != null) {
			if (!e.isCancelled()) {
				Player trade = TradeAPI.getTarget(e.getPlayer());
				e.setCancelled(true);

				if (CombatAPI.inCombat(trade) || ShopListener.buying_item.containsKey(e.getPlayer()) || ShopListener.selling_item.containsKey(e.getPlayer())) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "This player cannot trade currently.");
					return;
				}

				if (TradeAPI.isTrading(trade)) {
					e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "That player is currently trading.");
					return;
				}

				TradeAPI.setTrading(e.getPlayer(), trade);
				e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "You are now trading with " + trade.getName() + ".");
				trade.sendMessage(ChatColor.DARK_AQUA + "You are now trading with " + e.getPlayer().getName() + ".");
			}
		}
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player && TradeAPI.isTrading((Player) e.getEntity())) {
			e.setCancelled(true);
		}
	}
}