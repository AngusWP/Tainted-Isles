package friends;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import game.GameAPI;

public class FriendListener implements Listener{


    public void onLoad(){
        Bukkit.getServer().getPluginManager().registerEvents(this, GameAPI.getInstance());
    }

    public void onUnload(){

    }
	
    @EventHandler
    public void onJoin(PlayerJoinEvent e){

    	if (!FriendAPI.hasData(e.getPlayer().getUniqueId())) {
    		FriendAPI.setList(e.getPlayer().getUniqueId(), new ArrayList<String>());
    	}

        for (Player all : Bukkit.getOnlinePlayers()){
            if (FriendAPI.hasData(all.getUniqueId())){
                if (FriendAPI.getList(all.getUniqueId()).contains(e.getPlayer().getUniqueId().toString())){
                    all.sendMessage(ChatColor.GREEN + e.getPlayer().getName() + ChatColor.GRAY + " is now online.");
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        for (Player all : Bukkit.getOnlinePlayers()){
            if (FriendAPI.getList(all.getUniqueId()).contains(e.getPlayer().getUniqueId().toString())){
                all.sendMessage(ChatColor.RED + e.getPlayer().getName() + ChatColor.GRAY + " is now offline.");
            }
        }
    }



}
