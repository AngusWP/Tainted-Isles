package shops;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import game.GameAPI;
import items.Custom;
import items.ItemAPI;
import net.md_5.bungee.api.ChatColor;

public class ShopAPI {

	public static void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (UUID uuid : ShopListener.shop.keySet()) {
					Shop shop = getShop(uuid);

					shop.setSeconds(shop.getSeconds() - 1);

					if (shop.getSeconds() == 0) {
						shop.closeDownShopFully();
					}
				}
			}
		}, 20L, 20L);
	}

	public static void saveCollection(UUID uuid, Inventory inv) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/collection/" + uuid + ".yml");
		YamlConfiguration config = new YamlConfiguration();

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	public static Inventory loadCollection(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/collection/" + uuid + ".yml");
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Inventory inv = Bukkit.createInventory(null, 45, "Collection Store");

		for (int i = 0; i < inv.getSize(); i++) {
			if (config.contains("" + i)) {
				inv.setItem(i, config.getItemStack("" + i));
			}
		}

		return inv;
	}

	public static boolean isAddingShopNPC(UUID uuid) {
		if (ShopListener.creating_shops.contains(uuid)) {
			return true;
		}

		return false;
	}

	public static void createCollectionData(UUID uuid) {
		
	}
	
	public static boolean hasCollectionData(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/collection/" + uuid + ".yml");
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static void addIdentifier(ItemStack item) { // so we get the exact item while looping, distinguishes from all
														// other items.
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();

			lore.add(ChatColor.GRAY + "SHOP ITEM IDENTIFIER");
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}

	public static void removeIdentifier(ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();

			for (int i = 0; i < lore.size(); i++) {
				String line = ChatColor.stripColor(lore.get(i));

				if (line.contains("SHOP ITEM IDENTIFIER")) {
					lore.remove(i);
				}
			}

			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}

	public static Location getLocationForHologram(UUID uuid) {
		return ShopListener.hologramData.get(uuid);
	}

	public static void loadHologramData() {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/holograms.yml");
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : config.getKeys(false)) {
			String[] str = config.getString(key).split(",");

			World world = Bukkit.getWorld(str[0]);

			double x = Double.valueOf(str[1]);
			double y = Double.valueOf(str[2]);
			double z = Double.valueOf(str[3]);

			Location loc = new Location(world, x, y, z);
			ShopListener.hologramData.put(UUID.fromString(key), loc);
		}
	}

	public static void saveHologramData(boolean restart) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/holograms.yml");
		YamlConfiguration config = new YamlConfiguration();

		boolean empty = true;

		for (UUID uuid : ShopListener.hologramData.keySet()) {
			empty = false;
			String key = uuid.toString();
			Location loc = ShopListener.hologramData.get(uuid);

			String value = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
			config.set(key, value);

			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (restart) {
			if (empty) {
				file.delete();
			}
		}
	}

	public static void updateHolograms() {
		List<Location> list = new ArrayList<Location>();

		for (UUID uuid : ShopListener.npcShop.keySet()) {
			Location loc = getLocationForHologram(uuid);
			list.add(loc);

			if (!ShopListener.holograms.containsValue(loc)) { // so we don't get them stacking
				Hologram h = HologramsAPI.createHologram(GameAPI.getInstance(), loc);
				h.appendTextLine(ChatColor.GREEN + "Shop");

				ShopListener.holograms.put(h, loc);
			}
		}

		List<Hologram> l = new ArrayList<Hologram>();
		l.addAll(ShopListener.holograms.keySet());

		for (Hologram h : l) {
			Location loc = ShopListener.holograms.get(h);

			if (!list.contains(loc)) {
				h.delete();
				ShopListener.holograms.remove(h);
				// so if the NPC isn't a shop anymore, get rid of the hologram.
			}
		}
	}

	public static boolean hasItemsInCollection(UUID uuid) {
		if (hasCollectionData(uuid)) {
			Inventory inv = loadCollection(uuid);
			
			boolean empty = true;
			
			for (ItemStack item : inv.getContents()) {
				if (item != null) {
					empty = false;
				}
			}
			
			if (!empty) {
				return true;
			}
		}
		
		
		return false;
	}
	
	public static Shop getShop(UUID uuid) {
		if (ShopAPI.hasShop(uuid)) {
			return ShopListener.shop.get(uuid);
		}

		GameAPI.log("SHOP NOT FOUND. ERROR.");
		return null;
	}

	public static boolean hasShop(UUID uuid) {
		if (ShopListener.shop.containsKey(uuid)) {
			return true;
		}

		return false;
	}

	public static Shop getShopFromEntity(Entity e) {
		if (isRented(e)) {
			for (UUID uuid : ShopListener.shop.keySet()) {
				Shop shop = ShopListener.shop.get(uuid);

				if (shop.getShopKeeper() == e) {
					return shop;
				}
			}
		}

		return null;
	}

	public static void open(Player pl, Entity e) {
		if (isRented(e)) {
			Shop shop = getShopFromEntity(e);
			pl.openInventory(shop.getInventory());
		}
	}

	public static void openRentMenu(Player pl, Entity e) {
		ShopListener.rentMenu.put(pl.getUniqueId(), e);

		Inventory inv = Bukkit.createInventory(null, 9, "Rent");

		ItemStack c = Custom.CONFIRM_RENT;
		ItemStack d = Custom.DENY_RENT;

		ItemStack rent = ItemAPI.create(Material.OAK_SIGN, ChatColor.WHITE + "Rent",
				Arrays.asList(
						ChatColor.GRAY + "Pay " + ChatColor.YELLOW + getRentPrice(pl.getLocation()) + ChatColor.GRAY
								+ "g to rent this shop for " + getRentTime(pl.getLocation()) + " minutes.",
						ChatColor.GRAY + "Tax: " + ChatColor.YELLOW + getTax(pl.getLocation()) + ChatColor.GRAY + "%"),
				1);

		inv.setItem(3, c);
		inv.setItem(4, rent);
		inv.setItem(5, d);
		pl.openInventory(inv);
	}

	public static void createShop(Player pl, Entity e) {
		Shop shop = new Shop(pl, ShopListener.npcShop.get(e.getUniqueId()), e, getRentTime(pl.getLocation()));
		ShopListener.rentMenu.remove(pl.getUniqueId());

		shop.setupInventory();

		pl.openInventory(shop.getInventory());
		ShopListener.shop.put(pl.getUniqueId(), shop);
	}

	public static boolean isShopInventory(Inventory i) {

		for (UUID uuid : ShopListener.shop.keySet()) {
			Inventory inv = ShopListener.shop.get(uuid).getInventory();

			if (inv == i) {
				return true;
			}
		}

		return false;
	}

	public static Shop getShopFromInventory(Inventory i) {
		if (isShopInventory(i)) {
			for (UUID uuid : ShopListener.shop.keySet()) {
				Inventory inv = ShopListener.shop.get(uuid).getInventory();

				if (inv == i) {
					return ShopListener.shop.get(uuid);
				}
			}
		}

		return null;
	}

	public static boolean isRented(Entity e) {
		for (UUID uuid : ShopListener.shop.keySet()) {
			Shop shop = ShopListener.shop.get(uuid);

			if (shop.getShopKeeper() == e) {
				return true;
			}
		}

		return false;
	}

	public static int getRentPrice(Location loc) {
		return 200;
	}

	public static int getRentTime(Location loc) {
		return 30;
	}

	public static void makeEntityNPCShop(Entity e) {
		ShopListener.npcShop.put(e.getUniqueId(), e.getLocation());

		Location loc = e.getLocation();
		loc.setY(loc.getY() + 2.8);

		ShopListener.hologramData.put(e.getUniqueId(), loc);
	}

	public static void removeEntityNPCShop(Entity e) {
		ShopListener.npcShop.remove(e.getUniqueId());
		ShopListener.hologramData.remove(e.getUniqueId());
	}

	public static void toggleAddingShopNPC(Player pl) {
		UUID uuid = pl.getUniqueId();

		if (isAddingShopNPC(uuid)) {
			ShopListener.creating_shops.remove(uuid);
			pl.sendMessage(ChatColor.GRAY + "You are no longer setting up NPC shops.");
		} else {
			ShopListener.creating_shops.add(uuid);
			pl.sendMessage(ChatColor.GRAY + "You can now set up player NPC shops.");
			pl.sendMessage(ChatColor.YELLOW + "To make an NPC hold a player shop, simply shift right click an NPC.");
			pl.sendMessage(ChatColor.YELLOW + "To remove an NPC shop " + ChatColor.RED
					+ "(DO NOT DO THIS WHEN THE SERVER IS OPEN FOR PUBLIC AND A PLAYER HAS RENTED THE NPC)"
					+ ChatColor.YELLOW + ", simply shift right click an existing shop NPC.");
			pl.sendMessage(ChatColor.GRAY + "Type /setshop to exit this mode.");
		}
	}

	public static void saveShopNPCData(boolean restart) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/npcs.yml");
		YamlConfiguration config = new YamlConfiguration();

		boolean empty = true;

		for (UUID uuid : ShopListener.npcShop.keySet()) {
			empty = false;
			String key = uuid.toString();
			Location loc = ShopListener.npcShop.get(uuid);

			String value = loc.getWorld().getName() + "," + (int) loc.getX() + "," + (int) loc.getY() + ","
					+ (int) loc.getZ();
			config.set(key, value);

			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (restart) {
			if (empty) {
				file.delete();
			}
		}

	}

	public static void loadShopNPCData() {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/shops/npcs.yml");
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key : config.getKeys(false)) {
			String[] str = config.getString(key).split(",");

			World world = Bukkit.getWorld(str[0]);

			double x = Double.valueOf(str[1]);
			double y = Double.valueOf(str[2]);
			double z = Double.valueOf(str[3]);
			Location ent = new Location(world, (int) x, (int) y, (int) z);

			ShopListener.npcShop.put(UUID.fromString(key), ent);
		}
	}

	public static int getTax(Location loc) {
		return 10; // different locations have different tax? just a thought
	}

	public static boolean isShopNPC(Entity ent) {
		if (ShopListener.npcShop.containsKey(ent.getUniqueId())) {
			return true;
		}

		return false;
	}

}
