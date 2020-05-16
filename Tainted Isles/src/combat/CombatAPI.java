package combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import game.GameAPI;
import graphics.GraphicsAPI;

public class CombatAPI {

	public static void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (inCombat(pl)) {
						if (getCombatTimer(pl) != 0) { 
							setCombatTimer(pl, getCombatTimer(pl) - 1);
						} else {
							removeFromCombat(pl);
							GraphicsAPI.removeBossBar(pl);
						}
					}
				}
			}
		}, 20L, 20L);
	}
	
	public static void setCombatTimer(Player pl, int amount) {
			CombatListener.combat.put(pl.getUniqueId(), amount);	
	}
	
	public static boolean inCombat(Player pl) {
		if (CombatListener.combat.containsKey(pl.getUniqueId())) {
			return true;
		}
		
		return false;
	}
	
	public static int getCombatTimer(Player pl) {
		if (inCombat(pl)) {
			return CombatListener.combat.get(pl.getUniqueId());
		}
		
		return -1;
	}
	
	
	public static void removeFromCombat(Player pl) {
		if (inCombat(pl)) { // just a fail safe
			CombatListener.combat.remove(pl.getUniqueId());	
		}
	}
	
}
