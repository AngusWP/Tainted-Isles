package banks;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import enums.BankAction;
import game.GameAPI;
import gold.GoldAPI;


public class BankListener implements Listener{

	public static HashMap<Player, BankAction> action = new HashMap<Player, BankAction>();
	public static HashMap<Player, Integer> timer = new HashMap<Player, Integer>();
	
	public void onLoad() {
		Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
		BankAPI.startTimer();
	}
	
	public void onUnload() {
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (BankAPI.hasTimer(e.getPlayer())) {
			BankAPI.removeTimer(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onAddGold(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Bank")) {
			if (!e.isCancelled()) {
				
				if (e.getClickedInventory() == null) return;
				
				if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
					BankAPI.handleBankDeposit(e, false);
					return;
				}
				
				if (e.getClickedInventory().equals(e.getView().getBottomInventory()) && e.isShiftClick()) {
					BankAPI.handleBankDeposit(e, true);
					return;
				}	
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Bank")) {
			int max = BankAPI.getMaxSlots(BankAPI.getLevel(e.getWhoClicked().getUniqueId()));
			
			Player pl = (Player) e.getWhoClicked();
			
			if ((e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) && e.getSlot() >= max - 9) {
				
				if (e.isShiftClick()) {
					e.setCancelled(true);
				}
				
				e.setCancelled(true);
				
				if (e.getSlot() == max - 5) {
					if (e.isLeftClick() && !e.isShiftClick()) {
						BankAPI.setAction(pl, BankAction.WITHDRAW_COIN);
						pl.sendMessage(ChatColor.GRAY + "Please enter the amount of gold you would like to withdraw in coins.");
						pl.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to stop this.");
						pl.sendMessage(ChatColor.GRAY + "After 15 seconds, this transaction will time out.");
						pl.closeInventory();	
						return;
					}
					
					if (e.isRightClick() && !e.isShiftClick()) {
						BankAPI.setAction(pl, BankAction.WITHDRAW_NOTE);
						pl.sendMessage(ChatColor.GRAY + "Please enter the amount of gold you would like to withdraw to a note.");
						pl.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to stop this.");
						pl.sendMessage(ChatColor.GRAY + "After 15 seconds, this transaction will time out.");
						pl.closeInventory();
						return;
					}
					
					if (e.getClick().equals(ClickType.MIDDLE)) {
						
						if ((BankAPI.getLevel(pl.getUniqueId())) == BankAPI.getMaxLevel()) {
							pl.sendMessage(ChatColor.GRAY + "Your bank is already at max capacity.");
							return;
						}
						
						BankAPI.setAction(pl, BankAction.UPGRADE);
						pl.sendMessage(ChatColor.GRAY + "Please type '" + ChatColor.GREEN + "Yes" + ChatColor.GRAY + "' to confirm this upgrade.");
						pl.sendMessage(ChatColor.GRAY + "This upgrade will cost " + ChatColor.YELLOW + BankAPI.getUpgradeCost(BankAPI.getLevel(pl.getUniqueId()))
								+ ChatColor.GRAY + " gold.");
						pl.sendMessage(ChatColor.GRAY + "Type '" + ChatColor.RED + "cancel" + ChatColor.GRAY + "' to stop this.");
						pl.sendMessage(ChatColor.GRAY + "After 15 seconds, this transaction will time out.");
						pl.closeInventory();
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
				if (e.getHand().equals(EquipmentSlot.HAND)) {
					e.setCancelled(true);
					
					if (BankAPI.hasAction(e.getPlayer())) {
						e.getPlayer().sendMessage(ChatColor.GRAY + "You have a pending bank transaction.");
						return;
					}
					
					e.getPlayer().openInventory(BankAPI.loadBank(e.getPlayer().getUniqueId()));
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (BankAPI.hasAction(e.getPlayer())) {
			e.setCancelled(true);
		
			if (e.getMessage().equalsIgnoreCase("cancel")) {
				e.getPlayer().sendMessage(ChatColor.GRAY + "Transaction cancelled.");
				BankAPI.removeTimer(e.getPlayer());
				return;
			}
			
			if (BankAPI.getAction(e.getPlayer()).equals(BankAction.UPGRADE)){
				
				if (e.getMessage().equalsIgnoreCase("yes")) {
					BankAPI.removeTimer(e.getPlayer());
					int level = BankAPI.getLevel(e.getPlayer().getUniqueId());
					
					if (GoldAPI.getBalance(e.getPlayer().getUniqueId()) < BankAPI.getUpgradeCost(level)){
						e.getPlayer().sendMessage(ChatColor.GRAY + "You do" + ChatColor.RED + " not " + ChatColor.GRAY
								+ "have enough gold. " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "Make sure your gold is in your bank, and not your inventory.");
						return;
					}
					
					HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
					
					int slots = BankAPI.getMaxSlots(level);
					
					Inventory inv = Bukkit.createInventory(null, slots + 9, "Bank");
					Inventory bank = BankAPI.loadBank(e.getPlayer().getUniqueId());
					
					for (int i = 0; i < slots; i++) {
						if (bank.getItem(i) != null) {
							items.put(i, bank.getItem(i));
							
							if (i >= slots - 9) {
								items.remove(i, bank.getItem(i));
								items.put((i + 9), bank.getItem(i));
							}
						}
					}
					
					for (int i = 0; i < inv.getSize(); i++) {
						if (items.get(i) != null) {
							inv.setItem(i, items.get(i));
						}
					}
					
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					e.getPlayer().sendMessage(ChatColor.GREEN + "Bank upgrade succesful.");
					GoldAPI.take(e.getPlayer().getUniqueId(), BankAPI.getUpgradeCost(level));
					GoldAPI.sendBalanceUpdate(e.getPlayer());
					BankAPI.saveBank(inv, e.getPlayer().getUniqueId());
					return;
				}
			}
		
			if (BankAPI.getAction(e.getPlayer()).equals(BankAction.WITHDRAW_COIN)) {
				BankAPI.removeTimer(e.getPlayer());
				BankAPI.handleWithdraw(e, false);
				return;
			}
			
			if (BankAPI.getAction(e.getPlayer()).equals(BankAction.WITHDRAW_NOTE)){
				BankAPI.removeTimer(e.getPlayer());
				BankAPI.handleWithdraw(e, true);
				return;
			}
					
			// we are going to return at every valid option so this will be the default message when it stops.
			BankAPI.removeTimer(e.getPlayer());
			e.getPlayer().sendMessage(ChatColor.GRAY + "Invalid option. Transaction cancelled.");
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase("Bank")) {
			BankAPI.saveBank(e.getInventory(), e.getPlayer().getUniqueId());
			Player pl = (Player) e.getPlayer();
			
			pl.playSound(pl.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
		}
	}
	
}
