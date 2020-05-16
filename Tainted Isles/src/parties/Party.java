package parties;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Party {

	Player owner;
	List<Player> members;
	
	
	public Party(Player pl) {
		owner = pl;
		members = new ArrayList<Player>();
		
		members.add(pl);
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public List<Player> getMembers(){
		return members;
	}
	
	public void add(Player pl) {
		this.members.add(pl);
	}
	
	public void remove(Player pl) {
		this.members.remove(pl);
	}
	
	public boolean hasMembers() {
		if (members.size() == 1) {
			return false; // so if only the owner is in the party.
		}
		
		return true;
	}
	
}
