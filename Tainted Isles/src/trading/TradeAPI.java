package trading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import game.GameAPI;
import items.Custom;
import items.ItemAPI;

public class TradeAPI {

        public static void setTrading(Player p, Player p1){
              TradeListener.trading.put(p, p1);

              openTradeWindow(p);
              openTradeWindow(p1);

              p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
              p1.playSound(p1.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
        }

        public static Player getTradePartner(Player p){
            if (TradeListener.trading.containsKey(p)){
                return TradeListener.trading.get(p);
            } else {
                for (Player p1 : TradeListener.trading.keySet()){
                    if (TradeListener.trading.get(p1).equals(p)){
                        return p1;
                    }
                }
            }

            return p;
        }

        public static boolean isYourSide(int i){

            //make sure to use this when checking the TOP INVENTORY.

            if (i >= 0 && i <= 3){
                return true;
            }

            if (i >= 9 && i <= 12) {
                return true;
            }

            if (i >= 18 && i <= 21) {
                return true;
            }

            if (i >= 27 && i <= 29) {
                return true;
            }

            return false;
        }

    public static boolean isTheirSide(int i){

        //make sure to use this when checking the TOP INVENTORY.

        if (i >= 5 && i <= 8){
            return true;
        }

        if (i >= 14 && i <= 17) {
            return true;
        }

        if (i >= 23 && i <= 26) {
            return true;
        }

        if (i >= 32 && i <= 34) {
            return true;
        }

        return false;
    }

        public static void openTradeWindow(Player p){

            String name = "Trading with " + TradeAPI.getTradePartner(p).getName();

            Inventory trade = Bukkit.createInventory(null, 36, name);
            ItemStack pane = ItemAPI.create(Material.BLACK_STAINED_GLASS_PANE, "", Arrays.asList(""), 1);
            ItemStack confirm = Custom.CONFIRM_TRADE;

            trade.setItem(4, pane);
            trade.setItem(13, pane);
            trade.setItem(22, pane);
            trade.setItem(30, confirm);
            trade.setItem(31, pane);
            trade.setItem(35, confirm);

            p.openInventory(trade);
            TradeListener.trade_inv.put(p, trade);
        }

        public static Inventory getTradeInventory(Player p){
            return TradeListener.trade_inv.get(p);
        }

        public static boolean isTrading(Player p){
            if (TradeListener.trading.containsKey(p) || TradeListener.trading.containsValue(p)){
                return true;
            }

            return false;
        }

        public static boolean isConfirmed(Inventory inv){
            if (inv.contains(Custom.CONFIRMED_TRADE)){
                return true;
            }

            return false;
        }

        public static void stopTrading(Player p, Player p1, boolean complete){

            if (TradeListener.trading.containsKey(p)){
                TradeListener.trading.remove(p);
            }

            if (TradeListener.trading.containsKey(p1)){
                TradeListener.trading.remove(p1);
            }


            if (complete){

                int slots_left = 36;
                int slots_left1 = 36;

                for (ItemStack item : p.getInventory()){
                    if (item != null){
                        slots_left--;
                    }
                }

                for (ItemStack item : p1.getInventory()){
                    if (item != null){
                        slots_left1--;
                    }
                }

                for (int i = 0; i < 36; i++){
                    if (isTheirSide(i)){
                        if (TradeListener.trade_inv.get(p).getItem(i) != null){
                            slots_left--;
                        }
                    }
                }

                for (int i = 0; i < 36; i++){
                    if (isTheirSide(i)){
                        if (TradeListener.trade_inv.get(p1).getItem(i) != null){
                            slots_left1--;
                        }
                    }
                }


                if (slots_left < 0){
                    p.sendMessage(ChatColor.DARK_AQUA + "That trade could not be completed because your inventory was too full.");
                    p1.sendMessage(ChatColor.DARK_AQUA + p.getName() +"'s inventory was full, so the trade has been cancelled.");
                    p.closeInventory();
                    p1.closeInventory();
                    stopTrading(p, p1, false);
                    return;
                }

                if (slots_left1 < 0){
                    p1.sendMessage(ChatColor.DARK_AQUA + "That trade could not be completed because your inventory was too full.");
                    p.sendMessage(ChatColor.DARK_AQUA + p.getName() +"'s inventory was full, so the trade has been cancelled.");
                    p.closeInventory();
                    p1.closeInventory();
                    stopTrading(p, p1, false);
                    return;
                }



                p.sendMessage(ChatColor.DARK_AQUA + "Trade completed.");
                p1.sendMessage(ChatColor.DARK_AQUA + "Trade completed.");

                for (int i = 0; i < 36; i++){
                    if (isTheirSide(i)){
                        if (TradeListener.trade_inv.get(p1).getItem(i) != null){
                            p1.getInventory().addItem(TradeListener.trade_inv.get(p1).getItem(i));
                        }
                    }
                }

                for (int i = 0; i < 36; i++){
                    if (isTheirSide(i)){
                        if (TradeListener.trade_inv.get(p).getItem(i) != null){
                            p.getInventory().addItem(TradeListener.trade_inv.get(p).getItem(i));
                        }
                    }
                }

                p.closeInventory();
                p1.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(GameAPI.getInstance(), new Runnable(){
                        public void run(){
                            TradeListener.exit_success.remove(TradeListener.trade_inv.get(p));
                            TradeListener.exit_success.remove(TradeListener.trade_inv.get(p1));
                            TradeListener.trade_inv.remove(p);
                            TradeListener.trade_inv.remove(p1);
                        }
                }, 2L);
            } else {

                Inventory inv = getTradeInventory(p);
                Inventory inv2 = getTradeInventory(p1);

                for (int i = 0; i < 36; i++){
                    if (isYourSide(i)){
                        if (inv.getItem(i) != null){
                            p.getInventory().addItem(inv.getItem(i));
                        }
                    }
                }

                for (int i = 0; i < 36; i++){
                    if (isYourSide(i)){
                        if (inv2.getItem(i) != null){
                            p1.getInventory().addItem(inv2.getItem(i));
                        }
                    }
                }

                TradeListener.trade_inv.remove(p);
                TradeListener.trade_inv.remove(p1);
            }
        }

        public static Player getTarget(Player trader) {

            List<Entity> nearbyE = trader.getNearbyEntities(3.0, 3.0, 3.0);
            ArrayList<Player> livingE = new ArrayList<Player>();
            for (Entity e : nearbyE) {
                if (e.getType() == EntityType.PLAYER) {
                    livingE.add((Player) e);
                }
            }
            BlockIterator bItr = new BlockIterator(trader, 3);

            while (bItr.hasNext()) {


                Block block = bItr.next();
                int bx = block.getX();
                int by = block.getY();
                int bz = block.getZ();
                for (LivingEntity e2 : livingE) {
                    if (e2 instanceof Player && Bukkit.getOnlinePlayers().contains(e2)) {
                        Location loc = e2.getLocation();
                        double ex = loc.getX();
                        double ey = loc.getY();
                        double ez = loc.getZ();

                        if (bx - 0.75 <= ex && ex <= bx + 1.75 && bz - 0.75 <= ez && ez <= bz + 1.75 && by - 1 <= ey
                                && ey <= by + 2.5) {
                            return (Player) e2;
                        }
                }

            }
        }
            return null;
    }
}