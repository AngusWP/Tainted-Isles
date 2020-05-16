package mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import enums.MobType;
import enums.Tier;
import enums.Type;
import items.Item;
import items.ItemAPI;
import net.minecraft.server.v1_14_R1.GenericAttributes;

@SuppressWarnings("deprecation")
public class Mob {
	
	public static void spawn(Tier tier, MobType type, Location loc) {
		
		int randX = new Random().nextInt(3 - -3 + 1) + -3;
		int randZ = new Random().nextInt(3 - -3 + 1) + -3;
		Location newLoc = new Location(loc.getWorld(), loc.getX() + randX + .5, loc.getY() + 2, loc.getZ() + randZ + .5);
		// spread it around a bit
		
		Entity e = null;
		EntityType et = MobAPI.getEntityType(type);
		e = Bukkit.getWorld("world").spawnEntity(newLoc, et);
		LivingEntity ent = (LivingEntity) e;
		int hp = 20;
		
		if (e instanceof Zombie) {
			Zombie z = (Zombie) e;
			z.setBaby(false);
		}
		
		if (type.equals(MobType.GOBLIN)) {
			ent.getEquipment().setHelmet(ItemAPI.getHead("Goblin", true));
			ent.setCustomName(ChatColor.GREEN + "Goblin");
			ent.setCustomNameVisible(true);
		}
		
		if (tier.equals(Tier.ONE)) {
			ent.getEquipment().setChestplate(new Item(1, Type.CHESTPLATE).get());
			ent.getEquipment().setLeggings(new Item(1, Type.LEGGINGS).get());
			ent.getEquipment().setBoots(new Item(1, Type.BOOTS).get());
			ent.getEquipment().setItemInMainHand(new Item(1, Type.SWORD).get());
		}

		if (tier.equals(Tier.TWO)) {
			ent.getEquipment().setChestplate(new Item(2, Type.CHESTPLATE).get());
			ent.getEquipment().setLeggings(new Item(2, Type.LEGGINGS).get());
			ent.getEquipment().setBoots(new Item(2, Type.BOOTS).get());
			ent.getEquipment().setItemInMainHand(new Item(2, Type.SWORD).get());
		}

		if (tier.equals(Tier.THREE)) {
			ent.getEquipment().setChestplate(new Item(3, Type.CHESTPLATE).get());
			ent.getEquipment().setLeggings(new Item(3, Type.LEGGINGS).get());
			ent.getEquipment().setBoots(new Item(3, Type.BOOTS).get());
			ent.getEquipment().setItemInMainHand(new Item(3, Type.SWORD).get());
		}

		if (tier.equals(Tier.FOUR)) {
			ent.getEquipment().setChestplate(new Item(4, Type.CHESTPLATE).get());
			ent.getEquipment().setLeggings(new Item(4, Type.LEGGINGS).get());
			ent.getEquipment().setBoots(new Item(4, Type.BOOTS).get());
			ent.getEquipment().setItemInMainHand(new Item(4, Type.SWORD).get());
		}
		
		if (tier.equals(Tier.FIVE)) {
			ent.getEquipment().setChestplate(new Item(5, Type.CHESTPLATE).get());
			ent.getEquipment().setLeggings(new Item(5, Type.LEGGINGS).get());
			ent.getEquipment().setBoots(new Item(5, Type.BOOTS).get());
			ent.getEquipment().setItemInMainHand(new Item(5, Type.SWORD).get());
		}
		
		for (ItemStack item : ent.getEquipment().getArmorContents()) {
			if (item != null && item.hasItemMeta()) {
				hp += ItemAPI.getHealth(item);
			}
		}
		
		
		ent.setMaxHealth(hp);
		ent.setHealth(hp);
		
		MobListener.moveSpeed.put(ent, ((CraftLivingEntity) ent).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
		MobAPI.setSpeed(ent, 0);
		
		List<LivingEntity> list = new ArrayList<LivingEntity>();
		
		if (MobListener.mobs.get(loc) != null) {
			list = MobListener.mobs.get(loc);
		}
		
		list.add(ent);
		MobListener.mobs.put(loc, list);
	}

	public static int getRespawnTime(Tier tier) {
		
		switch (tier) {
		case ONE:
			return 45;
		case TWO:
			return 90;
		case THREE:
			return 135;
		case FOUR:
			return 180;
		case FIVE:
			return 240;
		}
		
		return 0;
	}
	
}
