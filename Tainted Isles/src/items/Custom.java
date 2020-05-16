package items;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Custom {

    public static ItemStack CONFIRM_TRADE = ItemAPI.create(Material.MUSIC_DISC_CAT,
            ChatColor.GREEN + "Confirm", Arrays.asList(ChatColor.GRAY + "Click here to confirm the trade."), 1);

    public static ItemStack CONFIRMED_TRADE = ItemAPI.create(Material.MUSIC_DISC_13,
                  ChatColor.GOLD + "Trade confirmed.", Arrays.asList(ChatColor.GRAY + "Click here to cancel confirmation."), 1);

    public static ItemStack MOUNT = ItemAPI.create(Material.SADDLE, ChatColor.WHITE + "Horse Prototype", Arrays.asList(ChatColor.GRAY + "Mount"), 1);

    public static ItemStack BERRY = ItemAPI.create(Material.SWEET_BERRIES, ChatColor.DARK_AQUA + "Berry", Arrays.asList(ChatColor.GRAY + "A berry with a certain healing property."
    		, ChatColor.LIGHT_PURPLE + "Survivalist Item"), 1);
    
    public static ItemStack SPRUCE_SHARD = ItemAPI.create(Material.BEETROOT_SEEDS, ChatColor.DARK_AQUA + "Spruce Trophy Shard", 
    		Arrays.asList(ChatColor.GRAY + "A low quality shard of wood.",  ChatColor.LIGHT_PURPLE + "Artisan Item"), 1);
    
    public static ItemStack CONFIRM_RENT = ItemAPI.create(Material.MUSIC_DISC_CAT, ChatColor.GREEN + "Confirm", 
    		Arrays.asList(ChatColor.GRAY + "Click here to rent this shop."), 1);
    
    public static ItemStack DENY_RENT = ItemAPI.create(Material.MUSIC_DISC_CHIRP, ChatColor.RED + "Exit", 
    		Arrays.asList(ChatColor.GRAY + "Click here to exit."), 1);
}
