package graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import combat.CombatListener;
import de.Herbystar.TTA.TTA_Methods;
import game.GameAPI;
import parties.PartyAPI;
import player.PlayerAPI;

@SuppressWarnings("deprecation")
public class GraphicsAPI {

	// this is for stuff like barapis, titles, actionbars, tab, holographic displays etc.

	public static HashMap<UUID, Scoreboard> boards = new HashMap<>();
	
	public static void sendTitle(Player pl, String title, String sub) {
		TTA_Methods.sendTitle(pl, title, 10, 60, 10, sub, 10, 60, 10);
		pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	}
	
	public static void sendActionBar(Player pl, String title, int duration) { 
		TTA_Methods.sendActionBar(pl, title, duration);
	}

	public static void move(Hologram h) {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
				public void run() {
					if (!h.isDeleted()) {
						h.teleport(h.getLocation().getWorld(), h.getX(), h.getY() + 0.1, h.getZ());
						
					}
				}
		}, 1L, 1L);
	}
	
	public static void sendCombatHologram(Player pl, LivingEntity ent, double damage) {
		Location loc = ent.getLocation();
		
		float radiusX = (new Random().nextInt(50) + 16);
		float radiusZ = (new Random().nextInt(50) + 16);
		
		loc.setX(loc.getX() + (radiusX / 100));
		loc.setY(loc.getY() + 1);
		loc.setZ(loc.getZ() + (radiusZ / 100));
		
		Hologram h = HologramsAPI.createHologram(GameAPI.getInstance(), loc);
		h.appendTextLine(ChatColor.RED.toString() +  "-" + (int) damage + ChatColor.RED + "\u2764");
		move(h);
		
		Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable() {
				public void run() {
					h.delete();
				}
		}, 20L);
	}
	
	public static void updateCombatBar(Player pl, LivingEntity ent) {
		BossBar bar;
	
		// make sure to do the damage FIRST.
		
		if (hasBossBar(pl)) {
			bar = getBossBar(pl);
		} else {
			bar = Bukkit.createBossBar(ent.getName(), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
		}
		
		double equation = (ent.getHealth()) / ent.getMaxHealth();
		
		double progress = (equation >= 0) ? equation : 0;	

		int hp = (int) (ent.getHealth());
		
		if (hp < 0) hp = 0;
		
		bar.setTitle(ent.getName() + ChatColor.GRAY + " [" + hp + " HP" + ChatColor.GRAY + "]");
		bar.setProgress(progress);
		setBossBar(pl, bar);
	}

	public static void setBossBar(Player pl, BossBar bar) {
		bar.addPlayer(pl);
		CombatListener.bar.put(pl.getUniqueId(), bar);
	}
	
	public static BossBar getBossBar(Player pl) {
		BossBar bar = null;
		
		if (hasBossBar(pl)) {
			bar = CombatListener.bar.get(pl.getUniqueId());
		}
		
		return bar;
	}
	
	public static boolean hasBossBar(Player pl) {
		if (CombatListener.bar.containsKey(pl.getUniqueId())) {
			return true;
		}
		
		return false;
	}
	
	public static void removeBossBar(Player pl) {
		if (hasBossBar(pl)) {
			BossBar bar = getBossBar(pl);
			bar.removePlayer(pl);
			CombatListener.bar.remove(pl.getUniqueId());
		}
	}
	

	public static boolean hasScoreboard(Player pl) {
		if (boards.containsKey(pl.getUniqueId())) {
			return true;
		}

		return false;
	}

	public static Scoreboard getBoard(Player pl) {
		if (!hasScoreboard(pl)) {
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o;
			
			o = sb.registerNewObjective("showHealth", "health");
			o.setDisplaySlot(DisplaySlot.BELOW_NAME);
			o.setDisplayName(ChatColor.RED + "\u2764");			

			setScoreboard(pl, sb);
			updateScoreboard(pl);
			return sb;
		}

		return boards.get(pl.getUniqueId());
	}

	public static void setScoreboard(Player pl, Scoreboard board) {
		boards.put(pl.getUniqueId(), board);
	}

	public static void refreshScoreboard(Player pl) {

		if (PartyAPI.isInParty(pl)) {
			List<Player> mem = PartyAPI.getParty(pl).getMembers();
			Scoreboard sb = getBoard(pl);

			if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
				Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
				for (Player p : mem) {
					if (PartyAPI.getParty(pl).getOwner().equals(p)) {
						String name = ChatColor.BOLD.toString() + p.getName();
						if (name.length() > 16)
							name = name.substring(0, 16);
						o.getScore(name).setScore((int) p.getHealth());
					} else {
						String name = p.getName();
						if (name.length() > 16)
							name = name.substring(0, 16);
						o.getScore(name).setScore((int) p.getHealth());
					}
				}
				pl.setScoreboard(sb);
				setScoreboard(pl, sb);

			} else {
				updateScoreboard(pl);
			} 
		} 
	}

	public static void removeScoreboard(Player pl) {
		if (PlayerAPI.isNPC(pl)) {
			boards.remove(pl.getUniqueId());
		}
	}
	
	public static void updateScoreboard(Player pl) {
		if (PartyAPI.isInParty(pl)) {
			List<Player> mem = PartyAPI.getParty(pl).getMembers();
			Scoreboard sb = getBoard(pl);

			if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
				sb.getObjective(DisplaySlot.SIDEBAR).unregister();
			}

			Objective o = sb.registerNewObjective("party_data", "dummy");
			o.setDisplayName(ChatColor.RED + "Party");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);

			for (Player p : mem) {

				if (PartyAPI.getParty(pl).getOwner().equals(p)) {
					String name = ChatColor.BOLD.toString() + p.getName();
					if (name.length() > 16)
						name = name.substring(0, 16);
					o.getScore(name).setScore((int) p.getHealth());
				} else {
					String name = p.getName();
					if (name.length() > 16)
						name = name.substring(0, 16);
					o.getScore(name).setScore((int) p.getHealth());
				}
			}
			pl.setScoreboard(sb);
			setScoreboard(pl, sb);
		} else {
			Scoreboard sb = getBoard(pl);

			if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
				sb.getObjective(DisplaySlot.SIDEBAR).unregister();
			}
		}
	}
	
}
