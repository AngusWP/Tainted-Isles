package items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import enums.Tier;
import enums.Type;

public class Item {

	ItemStack item;
	int tier;
	Type type;

	public Item(int tier, Type type) {
		this.tier = tier;
		this.type = type;

		generate(tier, type);
	}

	public void generate(int tier, Type type) {

		String s = "";
		ItemStack i = null;
		Material mat = null;

		if (type.equals(Type.BOW)) {
			mat = Material.BOW;
		} else {

			switch (tier) {
			case 1:
				s = ItemAPI.isArmor(type) ? "LEATHER_" : "WOODEN_";
				break;
			case 2:
				s = ItemAPI.isArmor(type) ? "CHAINMAIL_" : "STONE_";
				break;
			case 3:
				s = "GOLDEN_";
				break;
			case 4:
				s = "DIAMOND_";
				break;
			case 5:
				s = "IRON_";
				break;
			}

			s += type.toString().toUpperCase();
			mat = Material.valueOf(s);
		}

		i = new ItemStack(mat);
		ItemMeta meta = i.getItemMeta();
		
		int stats;
		
		if (tier <= 2) {
			stats = new Random().nextInt(2) + 1;
		} else {
			stats = new Random().nextInt(2) + 2;
		}

		String dmg = getDamage(ItemAPI.getTier(tier), type); // its a string because the damage is a range (5-6)
		int hp = getHealth(ItemAPI.getTier(tier), type);

		if (ItemAPI.isArmor(type)) {

			meta.setDisplayName(getName(ItemAPI.getTier(tier), type));
			meta.setLore(Arrays.asList(ChatColor.GRAY + "Health: " + ChatColor.WHITE + "+" + hp));

		} else if (ItemAPI.isWeapon(type)) {

			// hp values
			meta.setDisplayName(getName(ItemAPI.getTier(tier), type));
			meta.setLore(Arrays.asList(ChatColor.GRAY + "Damage: " + ChatColor.WHITE + dmg));

		}

		meta = addStats(type, meta, stats);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		i.setItemMeta(meta);
		this.item = i;
	}

	public ItemStack get() {
		return item;
	}

	private ItemMeta addStats(Type type, ItemMeta meta, int amountOfStats) {
		List<String> lore = new ArrayList<String>();
		int chance = new Random().nextInt(100) + 1;

		List<String> armorStats = new ArrayList<String>();
		List<String> wepStats = new ArrayList<String>();
		// so we can remove them when they've already been put on.

		wepStats.add(ChatColor.GRAY + "Crit Chance: ");
		wepStats.add(ChatColor.GRAY + "Accuracy: ");
		wepStats.add(ChatColor.GRAY + "Lifesteal: ");
		
		armorStats.add(ChatColor.GRAY + "Dodge: ");
		armorStats.add(ChatColor.GRAY + "Placeholder: ");
		armorStats.add(ChatColor.GRAY + "Placeholder: ");
		
		for (int i = 0; i < amountOfStats; i++) {
			if (ItemAPI.isArmor(type)) {
				int stat = new Random().nextInt(armorStats.size());
				String s = armorStats.get(stat);

				if (armorStats.size() == 1 && lore.isEmpty()) {
					if (isPercent(s)) {
						lore.add(armorStats.get(stat) + ChatColor.WHITE.toString() + generateRandomStat(ItemAPI.getTier(tier), true, false) + "%");
					} else {
						lore.add(armorStats.get(stat) + ChatColor.WHITE.toString() + "+" + generateRandomStat(ItemAPI.getTier(tier), false, false));
					}
				} else {
					if (chance <= 65) {
						if (isPercent(s)) {
							lore.add(armorStats.get(stat) + ChatColor.WHITE.toString() + generateRandomStat(ItemAPI.getTier(tier), true, false) + "%");
						} else {
							lore.add(armorStats.get(stat) + ChatColor.WHITE.toString() + "+" + generateRandomStat(ItemAPI.getTier(tier), false, false));
						}
					}

					armorStats.remove(stat);
				}
			}

			if (ItemAPI.isWeapon(type)) {
				int stat = new Random().nextInt(wepStats.size());
				String s = wepStats.get(stat);

				if (wepStats.size() == 1 && lore.isEmpty()) {
					
					if (tier != 1 || tier != 2) { // t1 and t2 can have no added bonuses
						if (isPercent(s)) {
							lore.add(wepStats.get(stat) + ChatColor.WHITE.toString() + generateRandomStat(ItemAPI.getTier(tier), true, true) + "%");
						} else {
							lore.add(wepStats.get(stat) + ChatColor.WHITE.toString() + "+" + generateRandomStat(ItemAPI.getTier(tier), false, true));
						}	
					}
				} else {
					if (chance <= 50) {
						if (isPercent(s)) {
							lore.add(wepStats.get(stat) + ChatColor.WHITE.toString() + generateRandomStat(ItemAPI.getTier(tier), true, true) + "%");
						} else {
							lore.add(wepStats.get(stat) + ChatColor.WHITE.toString() + "+" + generateRandomStat(ItemAPI.getTier(tier), false, true));
						}
					}

					wepStats.remove(stat);		
				}
			}
		}

		List<String> finalLore = new ArrayList<String>();
		finalLore.addAll(meta.getLore());
		finalLore.addAll(lore);
		meta.setLore(finalLore);
		return meta;
	}

	private boolean isPercent(String s) {
		s = ChatColor.stripColor(s);

		if (s.contains("Crit Chance") || s.contains("Dodge") || s.contains("Accuracy") || s.contains("Lifesteal")) {
			return true;
		}

		return false;
	}
	
	public static int generateRandomStat(Tier tier, boolean percent, boolean weapon) {
		int stat = 0;
		int t = ItemAPI.getTier(tier);
		
		int armorLow = 1;
		int armorCap = 5;
		int armorAmount = new Random().nextInt(5) + 6;
		int wepLow = 1;
		int wepCap = 5;
		int wepAmount = new Random().nextInt(4) + 3;
		
		switch (t) {
		case 2:
			armorLow = 3;
			armorCap = 8;
			armorAmount = new Random().nextInt(15) + 11;
			wepLow = 3;
			wepCap = 8;
			wepAmount = new Random().nextInt(6) + 7;
			break;
		case 3:
			armorLow = 5;
			armorCap = 10;
			armorAmount = new Random().nextInt(20) + 21;
			wepLow = 5;
			wepCap = 10;
			wepAmount = new Random().nextInt(10) + 13;
			break;
		case 4:
			armorLow = 6;
			armorCap = 12;
			armorAmount = new Random().nextInt(30) + 51;
			wepLow = 6;
			wepCap = 12;
			wepAmount = new Random().nextInt(15) + 21;
			break;
		case 5:
			armorLow = 8;
			armorCap = 16;
			armorAmount = new Random().nextInt(40) + 91;
			wepLow = 8;
			wepCap = 16;
			wepAmount = new Random().nextInt(20) + 41;
			break;
		}

		if (percent) {
			int p;

			if (weapon) {
				p = new Random().nextInt(wepCap - wepLow) + (wepLow + 1);
			} else {
				p = new Random().nextInt(armorCap - armorLow) + (armorLow + 1);
			}
			
			stat = p;
		} else {
			
			if (weapon) {
				stat = wepAmount;
			} else {
				stat = armorAmount;
			}
			
		}
		

		return stat;
	}
	
	private String getDamage(Tier tier, Type type) { // it's a string because it's a range
		String dmg = "";
		int min = 1;
		int max = 1;
		
		switch (tier) {
		case ONE:
			min = new Random().nextInt(3) + 3;
			max = new Random().nextInt(5) + 4;
			break;
		case TWO:
			min = new Random().nextInt(5) + 9;
			max = new Random().nextInt(10) + 15;
			break;
		case THREE:
			min = new Random().nextInt(20) + 31;
			max = new Random().nextInt(30) + 61;
			break;
		case FOUR:
			min = new Random().nextInt(40) + 81;
			max = new Random().nextInt(60) + 131;
			break;
		case FIVE:
			min = new Random().nextInt(50) + 201;
			max = new Random().nextInt(80) + 351;
			break;
		}
		
		if (min >= max) {
			min = max;
		}

		// if type.equals bow min and max * 20%
		
		dmg = min + "-" + max;
		
		return dmg;
	}

	private int getHealth(Tier tier, Type type) {
		int hp = 1;
		
		switch (tier) {
		case ONE:
			hp = new Random().nextInt(20) + 11;
			break;
		case TWO:
			hp = new Random().nextInt(30) + 51;
			break;
		case THREE:
			hp = new Random().nextInt(300) + 151;
			break;
		case FOUR:
			hp = new Random().nextInt(600) + 1201;
			break;
		case FIVE:
			hp = new Random().nextInt(2000) + 2001;
			break;
		}
		
		if (type.equals(Type.HELMET) || type.equals(Type.BOOTS)) {
			hp *= 0.5; // half it
		}

		return hp;
	}

	private String getName(Tier tier, Type type) {
		String name = "";
		String item_name = type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();

		switch (tier) {
		case ONE:
			name = ChatColor.WHITE + item_name;
			break;
		case TWO:
			name = ChatColor.GREEN + item_name;
			break;
		case THREE:
			name = ChatColor.YELLOW + item_name;
			break;
		case FOUR:
			name = ChatColor.AQUA + item_name;
			break;
		case FIVE:
			name = ChatColor.LIGHT_PURPLE + item_name;
			break;
		}

		return name;
	}

}
