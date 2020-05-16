package gold;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import database.DatabaseAPI;
import game.GameAPI;
import items.ItemAPI;

public class GoldAPI {

    public static ItemStack createGoldCheque(int amount){
        return ItemAPI.create(Material.PAPER, ChatColor.YELLOW + "Gold Cheque",
                Arrays.asList(ChatColor.GRAY + "This cheque is worth " + ChatColor.YELLOW + amount + ChatColor.GRAY + " gold.",
                        ChatColor.GRAY + "Take it to to a bank to cash it in."), 1);
    }

    public static ItemStack createGoldCoins(int amount) {
    	return ItemAPI.create(Material.SUNFLOWER, ChatColor.YELLOW + "Coin", Arrays.asList(ChatColor.GRAY + "The currency of <unnamed>."), amount);
    }
    
    public static Integer getChequeAmount(ItemStack cheque){
        try {
            String line = ChatColor.stripColor(cheque.getItemMeta().getLore().get(0));
            return Integer.parseInt(line.split("worth ")[1].split(" gold.")[0]);
        } catch (Exception e) {
            return 0;
        }
    }
	
    public static boolean isCoin(ItemStack item) {
    	if (item != null && item.hasItemMeta()) {
    		if (item.getType().equals(Material.SUNFLOWER)) {
    			if (item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Coin")) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    public static void sendBalanceUpdate(Player pl) {
    	pl.sendMessage(ChatColor.GRAY + "New Balance: " + ChatColor.YELLOW + getBalance(pl.getUniqueId()) + "G");
    }
    
    public static boolean isNote(ItemStack item) {
    	if (item != null && item.hasItemMeta()) {
    		if (item.getType().equals(Material.PAPER)) {
    			if (item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Gold Cheque")) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
	public static int getBalance(UUID uuid) {
		if (hasData(uuid)) {
			return DatabaseAPI.getInt("/gold/" + uuid + ".yml", "gold");
		}
		
		return 0;
	}
	
	public static void create(UUID uuid) {	
		DatabaseAPI.createYMLFile("/gold/" + uuid + ".yml");
		DatabaseAPI.setInt("/gold/" + uuid + ".yml", "gold", 0);
	}
	
	public static void set(UUID uuid, int amount) {
		DatabaseAPI.setInt("/gold/" + uuid + ".yml", "gold", amount);
	}
	
	public static void add(UUID uuid, int amount) {
		int a = getBalance(uuid);
		a += amount;
		
		set(uuid, a);
	}
	
	public static void take(UUID uuid, int amount) {
		int a = getBalance(uuid);
		
		a -= amount;
		
		if (a < 0) {
			a = 0;
		}
		
		set(uuid, a);
	}
	
	public static boolean hasData(UUID uuid) {
		File file = new File(GameAPI.getInstance().getDataFolder() + "/gold/" + uuid + ".yml");
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
}
