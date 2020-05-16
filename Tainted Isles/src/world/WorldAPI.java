package world;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldAPI {
	
    public static String getRegionName(Location loc) {
    	String region = "";
    	
    	RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
    	ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    	
    	for (ProtectedRegion rg : set.getRegions()) {
    		region = rg.getId();
    		//there shouldn't be overlapping
    	}
    
    	return region;
    }
    
}
