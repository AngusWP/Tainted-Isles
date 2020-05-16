package items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import enums.Rarity;
import enums.Tier;
import enums.Type;
import graphics.GraphicsAPI;
import health.HealthAPI;

@SuppressWarnings("deprecation")
public class ItemAPI {

	public static ItemStack getHead(String s, boolean name) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		if (name) {
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(s));
		} else {
			UUID uuid = UUID.fromString(s);
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack generateTrophy(Tier tier) {
		ItemStack is = null;
		
		int t = getTier(tier);
		
		is = create(Material.TRIPWIRE_HOOK, ChatColor.WHITE + "Trophy", Arrays.asList(ChatColor.GRAY + "+" + (t * 10) + " HP"), 1);
		
		return is;
	}
	
	public static int getPrice(ItemStack item) {
		
		if (item.hasItemMeta() && item.getItemMeta().hasLore()){
			List<String> lore = item.getItemMeta().getLore();
			
			for (int i = 0; i < lore.size(); i++) {
				String line = ChatColor.stripColor(lore.get(i));
				
				if (line.contains("Price: ")) {
					return Integer.parseInt(line.split(": ")[1].split("g")[0]);
				}
			}
		}
		
		return 0;
	}
	
	public static void addPrice(ItemStack item, int amount) {
		if (item.hasItemMeta() && item.getItemMeta().hasLore()){
			List<String> lore = item.getItemMeta().getLore();
			
			lore.add(ChatColor.GRAY + "Price: " + ChatColor.YELLOW + amount + "g");
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
	public static void removePrice(ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasLore()){
			List<String> lore = item.getItemMeta().getLore();
			
			for (int i = 0; i < lore.size(); i++) {
				String line = ChatColor.stripColor(lore.get(i));
				
				if (line.contains("Price: ")) {
					lore.remove(i);
				}
			}
			
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
	public static List<String> getTrophyStats(Player pl){
		List<String> s = new ArrayList<String>();
		
		ItemStack trophy = pl.getInventory().getItemInOffHand();
		
		if (trophy != null) {
			if (trophy.hasItemMeta() && trophy.getItemMeta().hasLore()) {
				
				List<String> lore = trophy.getItemMeta().getLore();
				
				for (int i = 0; i < lore.size(); i++) {
					if (lore.get(i).contains("+")) {
						s.add(ChatColor.stripColor(lore.get(i)));
					}
				}
				
			}
		}
		
		return s;
	}
	
	public static void updateTrophyStats(Player pl) {
		
		if (pl.getInventory().getItemInOffHand().getType().equals(Material.TRIPWIRE_HOOK)){
			
			for (String s : getTrophyStats(pl)) {
				if (s.contains("+") && s.contains("HP")) {
					int hp = Integer.parseInt(s.split("\\+")[1].split(" HP")[0]);
					HealthAPI.setMaxHealth(pl, HealthAPI.getMaxHealth(pl) + hp);
				}	
			}
		} else {
			resetStats(pl);
		}
	}
	
	public static void resetStats(Player pl) {
		HealthAPI.setMaxHealth(pl, HealthAPI.getBaseMaxHealth(pl));
		// add any other base stat resets here
	}
	
	public static int getDamage(ItemStack item) {

		int dmg = 1;

		if (item != null && item.hasItemMeta()) {
			for (int i = 0; i < item.getItemMeta().getLore().size(); i++) {
				String line = item.getItemMeta().getLore().get(i);
				line = ChatColor.stripColor(line);

				if (line.contains("Damage: ")) {
					int min = Integer.parseInt(line.split(": ")[1].split("-")[0]);
					int max = Integer.parseInt(line.split("-")[1]);

					dmg = new Random().nextInt((max - min) + 1) + min;
				}
			}
		}

		return dmg;
	}

	public static void handleStats(LivingEntity ent, LivingEntity d, EntityDamageByEntityEvent e) {
		int chance = new Random().nextInt(100) + 1;
		int dmg = getDamage(d.getEquipment().getItemInMainHand());
		boolean dodged = false;
		int dodge = 0;

		for (ItemStack item : ent.getEquipment().getArmorContents()) {
			if (item != null) {
				if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
					dodge += getStat(item, "Dodge", true);
				}

				// add all other armor stats here.
			}
		}

		if (dodge >= chance) {
			dodged = true;
		}

		if (d.getEquipment().getItemInMainHand().hasItemMeta()
				&& d.getEquipment().getItemInMainHand().getItemMeta().hasLore()) {

			for (String stat : d.getEquipment().getItemInMainHand().getItemMeta().getLore()) {
				stat = ChatColor.stripColor(stat);

				if (stat.contains("Crit")) {
					int crit = ItemAPI.getStat(d.getEquipment().getItemInMainHand(), "Crit Chance", true);
					chance = new Random().nextInt(100) + 1;

					if (crit >= chance && !dodged) {
						dmg *= 1.5;
						d.sendMessage(ChatColor.DARK_AQUA + "[Critical Strike!]");
					}

				}

				if (stat.contains("Accuracy")) {
					chance = new Random().nextInt(100) + 1;
					int accuracy = 0;

					accuracy = ItemAPI.getStat(d.getEquipment().getItemInMainHand(), "Accuracy", true);

					if (accuracy >= chance) {
						ent.sendMessage(
								ChatColor.DARK_AQUA + "[Your dodge was countered by your opponent's accuracy!]");
						d.sendMessage(ChatColor.DARK_AQUA + "[Your accuracy countered your opponent's dodge!]");
						dodged = false;
					}
				}

				if (stat.contains("Lifesteal")) { // have this at the bottom so we handle all appliance of any
													// additional damage first
					int lifesteal = ItemAPI.getStat(d.getEquipment().getItemInMainHand(), "Lifesteal", true);

					int amount = (dmg / 100) * lifesteal;

					if (amount < 1) {
						amount = 1;
					}

					if (!dodged && d instanceof Player) {
						HealthAPI.setHealth((Player) d, HealthAPI.getHealth((Player) d) + (int) amount, false);
						d.sendMessage(ChatColor.DARK_AQUA + "+" + amount + " HP [" + HealthAPI.getHealth((Player) d)
								+ "/" + HealthAPI.getMaxHealth((Player) d) + "]");
					}
				}
			}
		}

		if (!dodged) {
			ent.damage(dmg);

			if (d instanceof Player) {
				GraphicsAPI.updateCombatBar((Player) d, ent);
				GraphicsAPI.sendCombatHologram((Player) d, ent, dmg);
			}
		} else {
			e.setCancelled(true);
			ent.sendMessage(ChatColor.DARK_AQUA + "[You dodged an incoming attack!]");
			d.sendMessage(ChatColor.DARK_AQUA + "[Your attack was dodged!]");

			if (d instanceof Player) {
				((Player) d).playSound(d.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1F, 1F);
			}

			if (ent instanceof Player) {
				((Player) ent).playSound(ent.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1F, 1F);
			}
		}
	}

	public static int getStat(ItemStack item, String stat, boolean percent) {
		if (item != null) {
			if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
				for (int i = 0; i < item.getItemMeta().getLore().size(); i++) {
					String line = item.getItemMeta().getLore().get(i);
					line = ChatColor.stripColor(line);
					stat = ChatColor.stripColor(stat);

					if (line.contains(stat)) {
						if (percent) {
							return Integer.parseInt(line.split(": ")[1].split("%")[0]);
						} else {
							return Integer.parseInt(line.split("\\+")[1]);
						}
					}

				}
			}
		}

		return 0;
	}

	public static Rarity getRarity(ItemStack item) {
		return Rarity.POOR;
		// code this properly soon
	}

	public static Tier getTier(int tier) {
		switch (tier) {
		case 2:
			return Tier.TWO;
		case 3:
			return Tier.THREE;
		case 4:
			return Tier.FOUR;
		case 5:
			return Tier.FIVE;
		}

		return Tier.ONE;
	}

	public static int getTier(Tier tier) {
		switch (tier) {
		case ONE:
			return 1;
		case TWO:
			return 2;
		case THREE:
			return 3;
		case FOUR:
			return 4;
		case FIVE:
			return 5;
		}

		return 0;
	}

	public static Tier getTier(ItemStack item) {
		String name = item.getType().toString();

		if (name.contains("LEATHER") || name.contains("WOODEN")) {
			return Tier.FIVE;
		}

		if (name.contains("CHAINMAIL") || name.contains("STONE")) {
			return Tier.TWO;
		}

		if (name.contains("GOLDEN")) {
			return Tier.THREE;
		}

		if (name.contains("DIAMOND")) {
			return Tier.FOUR;
		}

		if (name.contains("IRON")) {
			return Tier.FIVE;
		}

		return null;
	}

	public static ItemStack create(Material m, String n, List<String> l, int a) {
		ItemStack i = new ItemStack(m);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(n);
		meta.setLore(l);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		i.setItemMeta(meta);
		i.setAmount(a);
		return i;
	}

	public static int getHealth(ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if (meta.hasLore()) {
			for (int i = 0; i < meta.getLore().size(); i++) {
				String line = meta.getLore().get(i);
				line = ChatColor.stripColor(line);
				// we have this so if we change the colour of the text it will still work

				if (line.contains("Health: +")) {
					return Integer.parseInt(line.split("\\+")[1]); // reason for the \\ is thats its a regex
				}
			}
		}

		return 0;
	}

	public static Type getType(ItemStack item) {

		String mat = item.getType().toString().toUpperCase();

		if (item.getType() == Material.BOW) {
			return Type.BOW;
		}

		if (mat.contains("SWORD")) {
			return Type.SWORD;
		}

		if (mat.contains("AXE")) {
			return Type.AXE;
		}

		if (mat.contains("HELMET")) {
			return Type.HELMET;
		}

		if (mat.contains("CHESTPLATE")) {
			return Type.CHESTPLATE;
		}

		if (mat.contains("LEGGINGS")) {
			return Type.LEGGINGS;
		}

		if (mat.contains("BOOTS")) {
			return Type.BOOTS;
		}

		return Type.NONE;
	}

	public static boolean isArmor(Type type) {
		if (type.equals(Type.HELMET) || type.equals(Type.CHESTPLATE) || type.equals(Type.LEGGINGS)
				|| type.equals(Type.BOOTS)) {
			return true;
		}

		return false;
	}

	public static boolean isWeapon(Type type) {
		if (type.equals(Type.BOW) || type.equals(Type.SWORD) || type.equals(Type.AXE)) {
			return true;
		}

		return false;
	}

}
