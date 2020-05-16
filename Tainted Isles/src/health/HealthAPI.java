package health;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import combat.CombatAPI;
import enhancement.EnhancementAPI;
import game.GameAPI;
import graphics.GraphicsAPI;
import items.ItemAPI;
import player.PlayerAPI;

@SuppressWarnings("deprecation")
public class HealthAPI {

	public static void resetHealth(Player pl) {
		// just for when a player respawns or joins.
		pl.setHealthScale(20);
		int base = getBaseMaxHealth(pl);
		
		setHealth(pl, base, true);
		pl.setLevel(base);
	}
	
	public static int getBaseMaxHealth(Player pl) {
		int base = 50 + EnhancementAPI.getAdditionalHealth(pl) + getBonusArmorHealth(pl);
		return base;
	}
	
	public static void regenerate() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (!CombatAPI.inCombat(pl)) {
						if (!pl.isDead()) {
							int amount = getHealth(pl) + (getMaxHealth(pl) / 10);
							setHealth(pl, amount, false);
						}
					}
				}
			}
		}, 20L, 20L);
	}
	
	public static void updateHealth(Player pl) {
		int max = getBaseMaxHealth(pl);
		setMaxHealth(pl, max);
		pl.setLevel(getHealth(pl));
	}
	
	public static int getBonusArmorHealth(Player pl) {
		int hp = 0;
		
		for (ItemStack item : pl.getInventory().getArmorContents()) {
			if (item != null) {
				hp += ItemAPI.getHealth(item);
			}
		}
		
		return hp;
	}
	
	public static void updateHealthUnderName() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
				public void run() {
					for (Player pl : Bukkit.getOnlinePlayers()) {
						
						Scoreboard sb = GraphicsAPI.getBoard(pl);
						
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (!PlayerAPI.isNPC(p)) {
								Objective o = sb.getObjective(DisplaySlot.BELOW_NAME);
								o.getScore(p.getName()).setScore((int) p.getHealth());	
							}
						}
						
						pl.setScoreboard(sb);
						GraphicsAPI.setScoreboard(pl, sb);
					}
				}
		}, 1L, 1L);
	}
	
	public static void setHealth(Player pl, int hp, boolean overlap) { // so overlap means set both the health AND max health.
		
		if (hp > pl.getMaxHealth()) {
			if (!overlap) {
				hp = getMaxHealth(pl);
			} else {
				setMaxHealth(pl, hp);
			}
		}
		
		pl.setHealth(hp);			
		pl.setLevel((int) pl.getHealth());
	}
	
	public static void setMaxHealth(Player pl, int hp) {
		pl.setMaxHealth((double) hp);
	}
	
	public static int getMaxHealth(Player pl) {
		return (int) pl.getMaxHealth();
	}
	
	public static int getHealth(Player pl) {
		return (int) pl.getHealth();
	}
}
