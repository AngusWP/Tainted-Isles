package mobs;

import org.bukkit.Location;

public class Spawner {

	Location loc;
	String entities;
	int respawnTime;
	int currentTimer;
	boolean respawning;
	boolean firstSpawn;
	
	public Spawner(Location loc, String entities, int respawnTime) {
		this.loc = loc;
		this.entities = entities;
		this.respawnTime = respawnTime;
		this.currentTimer = respawnTime;
		this.respawning = true;
		this.firstSpawn = true;
	}
	
	public boolean isRespawning() {
		return respawning;
	}
	
	public boolean isFirstSpawn() {
		return firstSpawn;
	}
	
	public void setFirstSpawn(boolean b) {
		this.firstSpawn = b;
	}
	
	public void setRespawning(boolean b) {
		this.respawning = b;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public String getEntities(){
		return entities;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
	public int getCurrentTimer() {
		return currentTimer;
	}
	
	public void setCurrentTime(int t) {
		this.currentTimer = t;
	}
	
}
