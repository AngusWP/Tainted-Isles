package friends;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import player.PlayerAPI;

public class FriendInput implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {

			Player pl = (Player) cs;

			if (cmd.getName().equalsIgnoreCase("list")) {
				List<String> list = FriendAPI.getList(pl.getUniqueId());

				pl.sendMessage(ChatColor.GREEN + "Your friend list: " + ChatColor.GRAY + list);
			}

			if (cmd.getName().equalsIgnoreCase("add")) {
				if (args.length == 1) {

					List<String> list = FriendAPI.getList(pl.getUniqueId());
					if (!list.contains(args[0])) {
						list.add(args[0]);

						if (PlayerAPI.isOnline(args[0])) {
							Player added = PlayerAPI.getOnlinePlayer(args[0]);

							if (added.getName().equalsIgnoreCase(cs.getName())) {
								pl.sendMessage(ChatColor.GRAY
										+ "You must be lonely... you can't add yourself to your own friends list!");
							} else {
								pl.sendMessage(ChatColor.GREEN + added.getName() + ChatColor.GRAY + " has been "
										+ ChatColor.GREEN + "added" + ChatColor.GRAY + ", and is currently "
										+ ChatColor.GREEN + "online" + ChatColor.GRAY + ".");
								FriendAPI.setList(pl.getUniqueId(), list);
							}
						} else {
							pl.sendMessage(ChatColor.GRAY + "That player is not online.");
						}

					} else {
						pl.sendMessage(ChatColor.GRAY + "That player is already in your friends list. If you wish to");
						pl.sendMessage(ChatColor.GRAY + "remove them, type /delete <name>.");
					}
				} else {
					pl.sendMessage(ChatColor.GRAY + "Invalid syntax. /add <name>.");
				}
			}

			if (cmd.getName().equalsIgnoreCase("delete")) {
				if (args.length == 1) {

					List<String> list = FriendAPI.getList(pl.getUniqueId());
					if (list.contains(args[0])) {
						list.remove(args[0]);

						if (PlayerAPI.isOnline(args[0])) {
							Player removed = PlayerAPI.getOnlinePlayer(args[0]);
							pl.sendMessage(ChatColor.RED + removed.getName() + ChatColor.GRAY + " has been "
									+ ChatColor.RED + "removed " + ChatColor.GRAY + "from your friends list.");
							FriendAPI.setList(pl.getUniqueId(), list);
						} else {
							pl.sendMessage(ChatColor.GRAY
									+ "That player is not online. To remove a player, they must be online.");
						}

					} else {
						pl.sendMessage(ChatColor.GRAY + "That player is not in your friends list.");
					}
				} else {
					pl.sendMessage(ChatColor.GRAY + "Invalid syntax. /delete <name>.");
				}
			}
		}
		return true;
	}
}
