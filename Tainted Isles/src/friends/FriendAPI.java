package friends;

import java.io.File;
import java.util.List;
import java.util.UUID;

import database.DatabaseAPI;
import game.GameAPI;

public class FriendAPI {

    public static List<String> getList(UUID uuid){
        return DatabaseAPI.getStringList("/friends/" + uuid + ".yml", "friends");
    }

    public static void setList(UUID uuid, List<String> list){
        DatabaseAPI.setStringList("/friends/" + uuid + ".yml", "friends", list);
    }
    
    public static boolean hasData(UUID uuid) {
    	File file = new File(GameAPI.getInstance() + "/friends/" + uuid + ".yml");
    	
    	if (file.exists()) {
    		return true;
    	} 
    	
    	return false;
    }
}
