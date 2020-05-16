package game;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import admin.AdminInput;
import banks.BankListener;
import chat.ChatInput;
import chat.ChatListener;
import combat.CombatListener;
import database.Database;
import dialogue.DialogueListener;
import enhancement.EnhancementListener;
import factions.FactionListener;
import friends.FriendInput;
import friends.FriendListener;
import gold.GoldInput;
import gold.GoldListener;
import guilds.GuildListener;
import health.HealthListener;
import instances.InstanceInput;
import instances.InstanceListener;
import items.ItemListener;
import loot.LootInput;
import loot.LootListener;
import mobs.MobInput;
import mobs.MobListener;
import mounts.MountListener;
import parties.PartyInput;
import parties.PartyListener;
import perms.PermissionListener;
import player.PlayerListener;
import professions.ProfessionInput;
import professions.ProfessionListener;
import shops.ShopInput;
import shops.ShopListener;
import trading.TradeListener;
import tutorial.TutorialListener;
import world.WorldListener;

public class Game extends JavaPlugin implements Listener {

	public static Plugin instance;
	
	public Database database;
	public TradeListener tradeListener;
	public MobListener mobListener;
	public PlayerListener playerListener;
	public WorldListener worldListener;
	public InstanceListener instanceListener;
	public PermissionListener permissionListener;
	public MountListener mountListener;
	public FactionListener factionListener;
	public CombatListener combatListener;
	public FriendListener friendListener;
	public HealthListener healthListener;
	public EnhancementListener enhancementListener;
	public PartyListener partyListener;
	public ChatListener chatListener;
	public ShopListener shopListener;
	public GoldListener goldListener;
	public DialogueListener dialogueListener;
	public TutorialListener tutorialListener;
	public ItemListener itemListener;
	public GuildListener guildListener;
	public LootListener lootListener;
	public BankListener bankListener;
	public ProfessionListener professionListener;
	
	public void onEnable() {
		instance = this;
		
		database = new Database();
		tradeListener = new TradeListener();
		combatListener = new CombatListener();
		mountListener = new MountListener();
		mobListener = new MobListener();
		dialogueListener = new DialogueListener();
		tutorialListener = new TutorialListener();
		guildListener = new GuildListener();
		professionListener = new ProfessionListener();
		friendListener = new FriendListener();
		permissionListener = new PermissionListener();
		worldListener = new WorldListener();
		healthListener = new HealthListener();
		goldListener = new GoldListener();
		shopListener = new ShopListener();
		playerListener = new PlayerListener();
		itemListener = new ItemListener();
		bankListener = new BankListener();
		factionListener = new FactionListener();
		chatListener = new ChatListener();
		instanceListener = new InstanceListener();
		lootListener = new LootListener();
		enhancementListener = new EnhancementListener();
		partyListener = new PartyListener();
		
		getCommand("uuid").setExecutor(new AdminInput());
		getCommand("set").setExecutor(new AdminInput());
		getCommand("item").setExecutor(new AdminInput());
		getCommand("mount").setExecutor(new AdminInput());
		getCommand("instance").setExecutor(new InstanceInput());
		getCommand("all").setExecutor(new ChatInput());
		getCommand("list").setExecutor(new FriendInput());
		getCommand("add").setExecutor(new FriendInput());
		getCommand("delete").setExecutor(new FriendInput());
		getCommand("party").setExecutor(new PartyInput());
		getCommand("loot").setExecutor(new LootInput());
		getCommand("spawnmob").setExecutor(new MobInput());
		getCommand("showms").setExecutor(new MobInput());
		getCommand("hidems").setExecutor(new MobInput());
		getCommand("trophy").setExecutor(new AdminInput());
		getCommand("gold").setExecutor(new GoldInput());
		getCommand("note").setExecutor(new GoldInput());
		getCommand("artisan").setExecutor(new ProfessionInput());
		getCommand("prof").setExecutor(new ProfessionInput());
		getCommand("setshop").setExecutor(new ShopInput());
		
		database.onLoad();
		tradeListener.onLoad();
		mobListener.onLoad();
		partyListener.onLoad();
		friendListener.onLoad();
		mountListener.onLoad();
		itemListener.onLoad();
		shopListener.onLoad();
		guildListener.onLoad();
		goldListener.onLoad();
		bankListener.onLoad();
		healthListener.onLoad();
		chatListener.onLoad();
		combatListener.onLoad();
		professionListener.onLoad();
		factionListener.onLoad();
		tutorialListener.onLoad();
		dialogueListener.onLoad();
		enhancementListener.onLoad();
		instanceListener.onLoad();
		lootListener.onLoad();
		permissionListener.onLoad();
		worldListener.onLoad();
		playerListener.onLoad();
	}
	
	public void onDisable() {
		tradeListener.onUnload();
		mobListener.onUnload();
		instanceListener.onUnload();
		combatListener.onUnload();
		dialogueListener.onUnload();
		itemListener.onUnload();
		mountListener.onUnload();
		lootListener.onUnload();
		guildListener.onUnload();
		enhancementListener.onUnload();
		bankListener.onUnload();
		goldListener.onUnload();
		factionListener.onUnload();
		chatListener.onUnload();
		professionListener.onUnload();
		shopListener.onUnload();
		permissionListener.onUnload();
		friendListener.onUnload();
		partyListener.onUnload();
		playerListener.onUnload();
		tutorialListener.onUnload();
		healthListener.onUnload();
		worldListener.onUnload();
		database.onUnload();
		
		instance = null;
	}
	
}
