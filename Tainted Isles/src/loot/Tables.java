package loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import enums.Loot;

public class Tables {

	// these are just prototype loot tables
	
	public static List<ItemStack> get(Loot loot){
		
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		if (loot.equals(Loot.ONE)) {
			items.add(new ItemStack(Material.BREAD));
		}
		
		
		if (loot.equals(Loot.TWO)) {
			items.add(new ItemStack(Material.APPLE));
		}
		
		
		if (loot.equals(Loot.THREE)) {
			items.add(new ItemStack(Material.COOKED_CHICKEN));
		}
		
		
		if (loot.equals(Loot.FOUR)) {
			items.add(new ItemStack(Material.COOKED_BEEF));
		}
		
		if (loot.equals(Loot.FIVE)) {
			items.add(new ItemStack(Material.GOLDEN_APPLE));
		}
		
		return items;
	}
	
	public static int getRespawnTime(Loot loot) {
		switch (loot) {
			case ONE:
				return 90;
			case TWO:
				return 180;
			case THREE:
				return 270;
			case FOUR:
				return 360;
			case FIVE:
				return 480;
		}
		
		return 0;
	}
	
}
