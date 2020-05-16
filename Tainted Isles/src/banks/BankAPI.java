package banks;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import database.DatabaseAPI;
import enums.BankAction;
import game.GameAPI;
import gold.GoldAPI;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;
import player.PlayerAPI;

@SuppressWarnings("deprecation")
public class BankAPI {

	public static void startTimer() {
		new BukkitRunnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (hasTimer(pl)) {
						if (getTimer(pl) != 0) {
							setTimer(pl, getTimer(pl) - 1);
						} else {
							removeTimer(pl);
							pl.sendMessage(ChatColor.GRAY + "Your bank transaction has exceeded the time limit.");
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(GameAPI.getInstance(), 20L, 20L);
	}

	public static int getUpgradeCost(int level) {
		int price = 400; // level one

		switch (level) {
		case 2:
			price = 2000;
			break;
		case 3:
			price = 5000;
			break;
		case 4:
			price = 1000;
			break;
		}

		return price;
	}

	public static int getTimer(Player pl) {
		if (hasTimer(pl)) {
			return BankListener.timer.get(pl);
		}

		return 0;
	}

	public static boolean hasAction(Player pl) {
		if (BankListener.action.containsKey(pl)) {
			return true;
		}

		return false;
	}

	public static void setAction(Player pl, BankAction bankAction) {
		BankListener.action.put(pl, bankAction);
		setTimer(pl, 15);
	}

	public static BankAction getAction(Player pl) {
		if (hasAction(pl)) {
			return BankListener.action.get(pl);
		}

		return null;
	}

	public static void setTimer(Player pl, int amount) {
		BankListener.timer.put(pl, amount);
	}

	public static void removeTimer(Player pl) {
		BankListener.timer.remove(pl);
		BankListener.action.remove(pl);
	}

	public static boolean hasTimer(Player pl) {
		if (BankListener.timer.containsKey(pl)) {
			return true;
		}

		return false;
	}

	public static Inventory loadBank(UUID uuid) {
		if (hasBank(uuid)) {
			File file = new File(GameAPI.getInstance().getDataFolder() + "/banks/" + uuid + ".yml");
			YamlConfiguration config = new YamlConfiguration();

			try {
				config.load(file);
			} catch (Exception e) {
				e.printStackTrace();
			}

			int size = 17;

			HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

			for (int i = 0; i < getMaxSlots(getMaxLevel()); i++) {
				if (config.contains("" + i)) {
					items.put(i, config.getItemStack("" + i));
					size = i; // so at end we know the last slot to have an item, which will be our inventory
								// size.
				}
			}

			Inventory inv = Bukkit.createInventory(null, size + 1, "Bank");

			for (int i : items.keySet()) {
				inv.setItem(i, items.get(i));
			}

			inv.setItem(size - 4, getAmountItem(uuid));

			return inv;
		}

		return createBank(uuid);
	}

	public static int getLevel(UUID uuid) {
		if (hasBank(uuid)) {
			return (loadBank(uuid).getSize() / 9) - 1; // we minus one as there is the bottom bar.
		}

		return 1;
	}

	public static int getMaxLevel() {
		return 5;
	}

	public static int getMaxSlots(int level) {
		return (level * 9) + 9;
	}

	public static void saveBank(Inventory inv, UUID uuid) {
		if (hasBank(uuid)) {

			File file = new File(GameAPI.getInstance().getDataFolder() + "/banks/" + uuid + ".yml");
			YamlConfiguration config = new YamlConfiguration();

			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) != null) {
					config.set("" + i, inv.getItem(i));
				}
			}

			try {
				config.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void handleWithdraw(AsyncPlayerChatEvent e, boolean note) {

		try {
			
			if (e.getMessage().contains(" ")) {
				e.getPlayer().sendMessage(
						ChatColor.GRAY + "The amount " + ChatColor.RED + "must " + ChatColor.GRAY + "be a number.");
				return;
			}
			
			int amount = Integer.parseInt(e.getMessage());
			
			if (amount < 1) {
				e.getPlayer().sendMessage(
						ChatColor.GRAY + "The amount " + ChatColor.RED + "must " + ChatColor.GRAY + "be more than 0.");
				return;
			}

			if (amount > GoldAPI.getBalance(e.getPlayer().getUniqueId())) {
				e.getPlayer().sendMessage(ChatColor.GRAY + "You do" + ChatColor.RED + " not " + ChatColor.GRAY
						+ "have enough to withdraw this amount.");
				return;
			}

			if (!note) {
				if (amount > 64) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "When withdrawing coins, the maximum amount is "
							+ ChatColor.YELLOW + "64" + ChatColor.GRAY + ".");
					return;
				}
			}

			if (e.getPlayer().getInventory().firstEmpty() == -1) {
				e.getPlayer().sendMessage(ChatColor.GRAY + "You do" + ChatColor.RED + " not " + ChatColor.GRAY
						+ "have the avaiable inventory space.");
				return;
			}

			if (note) {
				e.getPlayer().getInventory().addItem(GoldAPI.createGoldCheque(amount));
			} else {
				e.getPlayer().getInventory().addItem(GoldAPI.createGoldCoins(amount));
			}

			GoldAPI.take(e.getPlayer().getUniqueId(), amount);
			e.getPlayer().sendMessage(ChatColor.GRAY + "Amount withdrawn: " + ChatColor.YELLOW + amount + "G");
			e.getPlayer().sendMessage(ChatColor.GRAY + "New Balance: " + ChatColor.YELLOW
					+ GoldAPI.getBalance(e.getPlayer().getUniqueId()) + "G");

		} catch (NumberFormatException ex) {
			e.getPlayer().sendMessage(
					ChatColor.GRAY + "The amount " + ChatColor.RED + "must " + ChatColor.GRAY + "be a number.");
			ex.printStackTrace();
		}
	}
	
	public static void handleBankDeposit(InventoryClickEvent e, boolean shiftClick) {
		ItemStack item = shiftClick ? e.getCurrentItem() : e.getCursor();
		
		if (item == null) {
			return;
		}
		
		if (GoldAPI.isCoin(item) || GoldAPI.isNote(item)) {
			boolean note = GoldAPI.isNote(item) ? true : false;
			
			int amount;
			
			if (note) {
				amount = GoldAPI.getChequeAmount(item);
				amount = amount * item.getAmount(); // so if its two bank notes that are stacked, give double the value.
			} else {
				amount = item.getAmount();
			}
			
			Player pl = (Player) e.getWhoClicked();
			GoldAPI.add(pl.getUniqueId(), amount);
			pl.sendMessage(ChatColor.GRAY + "Amount deposited: " + ChatColor.YELLOW + amount + "G");
			pl.sendMessage(ChatColor.GRAY + "New Balance: " + ChatColor.YELLOW
					+ GoldAPI.getBalance(pl.getUniqueId()) + "G");
			pl.playSound(pl.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			
			if (shiftClick) {
				e.setCurrentItem(null);
			} else {
				e.setCursor(null);
			}
			
			BankAPI.updateAmountInInventory(e.getView().getTopInventory(), pl.getUniqueId());
		}	
		
	}

	public static boolean hasBank(UUID uuid) {
		if (DatabaseAPI.exists("banks/" + uuid + ".yml")) {
			return true;
		}

		return false;
	}

	public static ItemStack getAmountItem(UUID uuid) {
		return ItemAPI.create(Material.SUNFLOWER,
				ChatColor.YELLOW + PlayerAPI.getOnlinePlayer(uuid).getName() + "'s account",
				Arrays.asList(
						ChatColor.GRAY + "You are currently storing " + ChatColor.YELLOW + GoldAPI.getBalance(uuid)
								+ ChatColor.GRAY + " gold.",
						ChatColor.WHITE + "Left click " + ChatColor.GRAY + "to withdraw gold coins.",
						ChatColor.WHITE + "Right click " + ChatColor.GRAY + "to withdraw notes.",
						ChatColor.WHITE + "Middle click " + ChatColor.GRAY + "to upgrade your bank."),
				1);
	}

	public static void updateAmountInInventory(Inventory inv, UUID uuid) {
		int slot = inv.getSize() - 5;

		inv.setItem(slot, getAmountItem(uuid));

		PlayerAPI.getOnlinePlayer(uuid).updateInventory();
	}

	public static Inventory createBank(UUID uuid) {
		DatabaseAPI.createYMLFile("banks/" + uuid + ".yml");
		Inventory inv = Bukkit.createInventory(null, 18, "Bank");

		for (int i = 9; i < 18; i++) {
			inv.setItem(i, ItemAPI.create(Material.IRON_BARS, " ", Arrays.asList(""), 1));
		}

		inv.setItem(13, getAmountItem(uuid));

		return inv;
	}
}
