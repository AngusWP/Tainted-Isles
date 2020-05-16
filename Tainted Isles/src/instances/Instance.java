package instances;

import java.util.List;

import org.bukkit.entity.Player;

public class Instance {

	String name;
	String path;
	List<Player> players;
	
	public Instance(String name, List<Player> players) {
		this.name = name;
		this.players = players;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Player> getPlayers(){ 
		return players;
	}
	
	public void add(Player pl) {
		players.add(pl);
	}
	
	public void remove(Player pl) {
		players.remove(pl);
	}
	
	public boolean contains(Player pl) {
		if (players.contains(pl)) {
			return true;
		}
		
		return false;
	}
	
}
