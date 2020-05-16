package parties;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import game.GameAPI;
import graphics.GraphicsAPI;
import instances.InstanceAPI;
import player.PlayerAPI;

public class PartyAPI {

	public static void update() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					GraphicsAPI.refreshScoreboard(pl);
				}
			}
		}, 1L, 1L);

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(GameAPI.getInstance(), new Runnable() {
			public void run() {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (hasInvite(pl)) {
						setInviteTime(pl, getInviteTime(pl) - 1);

						if (getInviteTime(pl) == 0) {
							removeInvite(pl);
						}
					}
				}
			}
		}, 20L, 20L);
	}

	public static boolean hasInvite(Player pl) {
		if (PartyListener.invite.containsKey(pl.getUniqueId())) {
			return true;
		}

		return false;
	}

	public static void removeInvite(Player pl) {
		if (hasInvite((pl))) {
			Player owner = getInviter(pl);

			if (owner.isOnline()) {
				owner.sendMessage(ChatColor.YELLOW + "Your invite to " + pl.getName() + " has expired.");
			}

			PartyListener.invite.remove(pl.getUniqueId());
			PartyListener.invite_timer.remove(pl.getUniqueId());

			pl.sendMessage(ChatColor.YELLOW + "The party invite from " + owner.getName() + " has expired.");
		}
	}

	public static Player getInviter(Player pl) {
		if (hasInvite(pl)) {
			if (PlayerAPI.isOnline(PartyListener.invite.get(pl.getUniqueId()))) {
				Player inviter = PlayerAPI.getOnlinePlayer(PartyListener.invite.get(pl.getUniqueId()));
				return inviter;
			}
		}

		return null;
	}

	public static void decline(Player pl) {
		if (hasInvite(pl)) {
			Player owner = getInviter(pl);

			if (owner.isOnline()) {
				owner.sendMessage(ChatColor.YELLOW + pl.getName() + " has declined your party invitation.");
			}

			PartyListener.invite.remove(pl.getUniqueId());
			PartyListener.invite_timer.remove(pl.getUniqueId());

			pl.sendMessage(ChatColor.YELLOW + "You have declined " + owner.getName() + "'s invitation.");
		}
	}

	public static void sendInvite(Player invited, Player owner) {
		if (!hasInvite(invited)) {
			PartyListener.invite.put(invited.getUniqueId(), owner.getUniqueId());
			setInviteTime(invited, 15);
			owner.sendMessage(ChatColor.YELLOW + "You have invited " + invited.getName() + " to your party.");
			invited.sendMessage(ChatColor.YELLOW + owner.getName() + " has invited you to a party!");
			invited.sendMessage(ChatColor.YELLOW + "Type /party accept, or /party decline.");
		} else {
			owner.sendMessage(ChatColor.YELLOW + "That player has a pending invitation.");
		}
	}

	public static int getInviteTime(Player invited) {
		if (hasInvite(invited)) {
			return PartyListener.invite_timer.get(invited.getUniqueId());
		}

		return -1;
	}

	public static void setInviteTime(Player invited, int time) {
		if (hasInvite(invited)) {
			PartyListener.invite_timer.put(invited.getUniqueId(), time);
		}
	}

	public static void join(Player pl, Party party) {
		Player owner = party.getOwner();

		PartyListener.invite.remove(pl.getUniqueId());
		PartyListener.invite_timer.remove(pl.getUniqueId());

		if (isFull(party)) {
			pl.sendMessage(ChatColor.YELLOW + "That party is full.");
			owner.sendMessage(ChatColor.YELLOW + pl.getName() + " tried to join your party, but the party is full.");
			return;
		}

		for (Player members : party.getMembers()) {
			members.sendMessage(ChatColor.YELLOW + pl.getName() + " has joined the party.");
		}

		party.add(pl);
		PartyListener.party.put(pl.getUniqueId(), party);
		pl.sendMessage(ChatColor.YELLOW + "You have joined the party.");

	}

	public static boolean isFull(Party party) {
		if (party.getMembers().size() == 6) {
			return true;
		}

		return false;
	}

	public static void sendChat(Player pl, String msg) {
		Party party = getParty(pl);

		for (Player members : party.getMembers()) {
			members.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Party" + ChatColor.GRAY + "] "
					+ ChatColor.WHITE + pl.getName() + ChatColor.GRAY + ": " + msg);
		}
	}

	public static void kick(Player pl) {
		Party party = getParty(pl);
		party.remove(pl);

		pl.sendMessage(
				ChatColor.YELLOW + "You have been kicked by " + party.getOwner().getName() + ", the party leader.");
		PartyListener.party.remove(pl.getUniqueId());
		GraphicsAPI.updateScoreboard(pl);

		for (Player members : party.getMembers()) {
			if (party.getOwner().equals(members)) {
				members.sendMessage(ChatColor.YELLOW + "You have kicked " + pl.getName() + " from the party.");
			} else {
				members.sendMessage(ChatColor.YELLOW + pl.getName() + " has been kicked from the party.");
			}

			GraphicsAPI.updateScoreboard(members);
		}
	}

	public static void leave(Player pl) {
		Party party = getParty(pl);

		if (party.getOwner().equals(pl)) {
			disband(pl);
			return;
		}

		party.remove(pl);
		PartyListener.party.remove(pl.getUniqueId());
		GraphicsAPI.updateScoreboard(pl);

		pl.sendMessage(ChatColor.YELLOW + "You have left the party.");

		if (InstanceAPI.inInstance(pl.getUniqueId())) {
			InstanceAPI.leaveInstance(pl);
		}
		
		for (Player members : party.getMembers()) {
			members.sendMessage(ChatColor.YELLOW + pl.getName() + " has left your party.");
			GraphicsAPI.updateScoreboard(members);
		}
	}

	public static void disband(Player pl) {
		Party party = getParty(pl);

		ArrayList<Player> list = new ArrayList<Player>();
		list.addAll(party.getMembers());
		// this is to stop concurrent modificattion exception
		
		for (Player all : list) {
			PartyListener.party.remove(all.getUniqueId());
			GraphicsAPI.updateScoreboard(all);

			if (all.equals(party.getOwner())) {
				all.sendMessage(ChatColor.YELLOW + "You have disbanded the party.");
			} else {
				all.sendMessage(ChatColor.YELLOW + "Your party has been disbanded.");
			}
			
			if (InstanceAPI.inInstance(all.getUniqueId())) {
				InstanceAPI.leaveInstance(pl);
			}

			party.remove(all);
		}
	}

	public static boolean isInParty(Player pl) {
		if (PartyListener.party.containsKey(pl.getUniqueId())) {
			return true;
		}

		return false;
	}

	public static void createParty(Player pl) {
		Party party = new Party(pl);
		PartyListener.party.put(pl.getUniqueId(), party);
		pl.sendMessage(ChatColor.YELLOW + "You have created a party.");
	}

	public static Party getParty(Player pl) {
		if (isInParty(pl)) {
			return PartyListener.party.get(pl.getUniqueId());
		}

		return null;
	}

}
