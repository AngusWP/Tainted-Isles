package professions;

import org.bukkit.Location;
import org.bukkit.Material;

import enums.Tier;

public class Plant {
	
	Tier tier;
	Location loc;
	int respawn;
	Material mat;
	
	public Plant(Tier tier, Location loc, int respawn, Material mat) {
		this.tier = tier;
		this.loc = loc;
		this.respawn = respawn;
		this.mat = mat;
	}
	
	public Tier getTier() {
		return tier;
	}
	
	public Material getMaterial() {
		return mat;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public int getRespawnTime() {
		return respawn;
	}
	
	public void setRespawnTime(int respawn) {
		this.respawn = respawn;
	}

}
