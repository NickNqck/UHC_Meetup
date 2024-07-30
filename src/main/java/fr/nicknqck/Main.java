package fr.nicknqck;

import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.commands.*;
import fr.nicknqck.commands.roles.*;
import fr.nicknqck.commands.vanilla.Gamemode;
import fr.nicknqck.commands.vanilla.Say;
import fr.nicknqck.commands.vanilla.Whitelist;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.blocks.BlockManager;
import fr.nicknqck.events.blocks.BrickBlockListener;
import fr.nicknqck.events.chat.Chat;
import fr.nicknqck.events.essential.*;
import fr.nicknqck.items.*;
import fr.nicknqck.player.EffectsGiver;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.builder.GetterList;
import fr.nicknqck.roles.ds.Lame;
import fr.nicknqck.scenarios.impl.TimberPvP;
import fr.nicknqck.scoreboard.ScoreboardManager;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.SchedulerRunnable;
import fr.nicknqck.utils.betteritem.BetterItemListener;
import fr.nicknqck.utils.inventories.Inventories;
import fr.nicknqck.utils.itembuilder.ItemBuilderListener;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.packets.TabTitleManager;
import fr.nicknqck.worlds.WorldFillTask;
import fr.nicknqck.worlds.WorldGenerator;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
[UHC-MTP UHC Plugin Credits]
Ideas and Game Design: NickNack, Mega02600, Egaly inspirated by goldenuhc.eu and yukanmc
Programming: NickNack, Mega02600
Roles: NickNack, Mega02600
*/

public class Main extends JavaPlugin implements Listener{
	public final String PLUGIN_NAME = "UHC-Meetup";
	public World gameWorld;
	public World nakime;
	@Getter
	private ScoreboardManager scoreboardManager;
    @Getter
	private ScheduledExecutorService executorMonoThread;
    @Getter
	private ScheduledExecutorService scheduledExecutorService;
	@Getter
    private static WorldFillTask worldfilltask;
	public static List<Chunk> keepChunk = new ArrayList<>();
	@Getter
	private GetterList getterList;

	@Getter
	private static Main Instance;
	public static Random RANDOM;
	@Getter
	private Inventories inventories;
	@Getter
	private RoleManager roleManager;

	@Override
	public void onEnable() {
		Instance = this;
		RANDOM = new Random();
		this.roleManager = new RoleManager();
	//	this.databaseManager = new DatabaseManager();
		GameState gameState = new GameState();
		this.inventories = new Inventories(gameState);
		this.getterList = new GetterList(gameState);
		this.gameWorld = Bukkit.getWorld(getConfig().getString("gameworld"));
		gameState.world = gameWorld;

		gameWorld.setGameRuleValue("randomTickSpeed", "3");
		gameWorld.setGameRuleValue("doMobSpawning", "false");
		gameWorld.setGameRuleValue("doFireTick", "false");
		initPlugin(gameState);
		EnableScoreboard(gameState);
		registerEvents(gameState);
		registerCommands(gameState);
		spawnPlatform();
		nakime = Bukkit.getWorld("nakime");
		clearMap();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
			p.setPlayerListName(p.getName());
			p.setDisplayName(p.getName());
			for (PotionEffect effect : p.getActivePotionEffects()){
				p.removePotionEffect(effect.getType());
			}
			p.getActivePotionEffects().clear();
			gameState.addInLobbyPlayers(p);
			ItemsManager.GiveHubItems(p);
			p.teleport(new Location(p.getWorld(), 0, 151, 0));
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
		}
		
		System.out.println("["+PLUGIN_NAME+"] Enabled");
		giveTab(gameState);

		saveDefaultConfig();
        registerRecipes();
		System.out.println("ENDING ONENABLE");
    }
	private void registerRecipes() {
		System.out.println("Starting registering recipes");
		ItemStack result = Items.getLamedenichirin();
        ShapedRecipe recipe = new ShapedRecipe(result);
        recipe.shape("OId", "GPG", "dIO");
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('d', Material.DIAMOND);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('P', Material.IRON_SWORD);
        Bukkit.addRecipe(recipe);
		System.out.println("Ending registering recipes");
	}
	private void EnableScoreboard(GameState gameState) {
		System.out.println("Enabling scoreboard");
		scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager(gameState);
        SchedulerRunnable.register(this);
        getScoreboardManager().onEnable();
		System.out.println("End enable scoreboard");
	}
	private void registerEvents(GameState gameState) {
		System.out.println("Starting registering events");
		getServer().getPluginManager().registerEvents(new WeatherEvents(), this);
		getServer().getPluginManager().registerEvents(new TimberPvP(), this);
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new JoinEvents(), this);
		getServer().getPluginManager().registerEvents(new QuitEvents(), this);
		getServer().getPluginManager().registerEvents(new EntityDamageEvents()	,this);
		getServer().getPluginManager().registerEvents(new RodTridimensionnelle(gameState), this);
		getServer().getPluginManager().registerEvents(new PotionUtils(), this);
		getServer().getPluginManager().registerEvents(new HubListener(gameState), this);
		getServer().getPluginManager().registerEvents(new GameListener(gameState), this);
		getServer().getPluginManager().registerEvents(new HomingBow(gameState), this);
		getServer().getPluginManager().registerEvents(new BlockManager(gameState), this);
		getServer().getPluginManager().registerEvents(new WorldGenerator(gameState), this);
		getServer().getPluginManager().registerEvents(new ItemsManager(gameState), this);
		getServer().getPluginManager().registerEvents(new Chat(gameState), this);
		getServer().getPluginManager().registerEvents(new BrickBlockListener(), this);
		getServer().getPluginManager().registerEvents(new BetterItemListener(), this);
		getServer().getPluginManager().registerEvents(new InfectItem(gameState), this);
		getServer().getPluginManager().registerEvents(new BulleGyokko(gameState), this);
		getServer().getPluginManager().registerEvents(new Lame(), this);
		getServer().getPluginManager().registerEvents(new Arctridi(gameState), this);
		getServer().getPluginManager().registerEvents(new Whitelist(gameState), this);
		getServer().getPluginManager().registerEvents(new BijuListener(), this);
		getServer().getPluginManager().registerEvents(new TitanListener(), this);
		getServer().getPluginManager().registerEvents(new Patch(gameState), this);//Patch effet de potion
		getServer().getPluginManager().registerEvents(new AttackUtils(), this);
		getServer().getPluginManager().registerEvents(new HubInventory(gameState), this);
		getServer().getPluginManager().registerEvents(new ItemBuilderListener(), this);
		new EffectsGiver();
		System.out.println("Ending registering events");
	}
	private void registerCommands(GameState gameState) {
		System.out.println("Starting registering commands");
		getCommand("ds").setExecutor(new DSmtpCommands(gameState));
		getCommand("a").setExecutor(new AdminCommands(gameState));
		getCommand("c").setExecutor(new CRolesCommands(gameState));
		getCommand("aot").setExecutor(new AotCommands(gameState));
		getCommand("ns").setExecutor(new NsCommands(gameState));
		getCommand("drop").setExecutor(new DropCommand());
		getCommand("claim").setExecutor(new ClaimCommand(gameState));
		getCommand("mumble").setExecutor(new Mumble());
		getCommand("say").setExecutor(new Say());
		getCommand("gamemode").setExecutor(new Gamemode());
		getCommand("gm").setExecutor(new Gamemode());
		getCommand("wl").setExecutor(new Whitelist(gameState));
		getCommand("whitelist").setExecutor(new Whitelist(gameState));
		getCommand("mc").setExecutor(new McCommands(gameState));
		getCommand("discord").setExecutor(new Discord());
		System.out.println("Ending registering commands");
	}
	private void clearMap() {
		System.out.println("Starting cleaning map");
		System.out.println("Starting cleaning blocks");
		for (int x = -150; x <= 150; x++) {
			for (int z = -150; z <= 150; z++) {
				for (int y = 60; y <= 120; y++) {
					//System.out.println("Calculating Block at "+"x:"+x+", y:"+y+", z:"+z);
					Block block = gameWorld.getBlockAt(x, y, z);
					if (block.getType() == Material.BRICK || block.getType() == Material.COBBLESTONE || block.getType() == Material.OBSIDIAN || block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE) {
						if (isDebug()){
							System.out.println(gameWorld.getBlockAt(x, y, z).getType().name()+" -> Air at x:"+x+", y:"+y+", z:"+z);
						}
						gameWorld.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		System.out.println("End cleaning blocks");
		System.out.println("Starting cleaning entities");
		for (Entity e : gameWorld.getEntities()) {
			if (e instanceof Player) continue;
			System.out.println("removing Entity "+e.getName()+" at "+e.getLocation());
			e.remove();
		}
		System.out.println("Ending cleaning entities");
		System.out.println("Ending cleaning map");
	}
	private void initPlugin(GameState gameState) {
		System.out.println("init Roles");
		for (GameState.Roles r : GameState.Roles.values()) {
			gameState.addInAvailableRoles(r, 0);
		}
		System.out.println("init Events");
		for (Events e : Events.values()) {
			gameState.addInAvailableEvents(e);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
		}
		System.out.println("init FastInv");
	}
	private void giveTab(GameState gameState) {
		System.out.println("Starting give tab");
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			if (gameState.getServerState() == ServerStates.InLobby) {
				for (Player p : gameState.getInLobbyPlayers()) {
				//	NMSPacket.sendTabTitle(p, null, null);
					NMSPacket.sendTabTitle(p,
							gameState.msgBoard + "\n",
							"\n" +
									"§7Joueurs: §c" + gameState.getInLobbyPlayers().size() +"§r/§6"+gameState.getroleNMB()+ "\n"
									+ "\n"
									+ "§cdiscord.gg/RF3D4Du8VN");
				}
			}
			if (gameState.getServerState() == ServerStates.InGame) {
				for (Player player : gameState.getInGamePlayers()) {
					if (gameState.roleTimer < gameState.getInGameTime()) {
		        		if (!gameState.hasRoleNull(player)) {
		        			if (gameState.getPlayerRoles().get(player).getOriginTeam() != null) {
				            	TabTitleManager.sendTabTitle(player, gameState.msgBoard+ "\n", "\n" + ChatColor.GRAY + "Kills: " + ChatColor.GOLD + gameState.getPlayerKills().get(player).size() + "\n" + "\n" + "§7Plugin by§r: §bNickNqck");
				         }
		        		}		        		 
		        	} else {
		        		int time = gameState.roleTimer-gameState.getInGameTime();
				    	String trm = time/60 < 10 ? "0"+time/60 : time/60+"";
				    	String trs = time%60 < 10 ? "0"+time%60 : time%60+"";
		        		TabTitleManager.sendTabTitle(player, gameState.msgBoard + "\n", "\n" + ChatColor.GRAY + "Role: " + ChatColor.GOLD + trm +"§rm§6"+trs+"§rs"+ "\n" + "\n" + "§7Plugin by§r: §bNickNqck");
		        	}
					if (!gameState.hasRoleNull(player)) {
		        		gameState.getPlayerRoles().get(player).onTick();
		        	}
				}
			}
			if (gameState.getServerState() == ServerStates.GameEnded) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					NMSPacket.clearTitle(p);
				}
			}
		}, 1, 1);
		System.out.println("Ending give tab");
	}
	private void spawnPlatform() {
		System.out.println("Spawning GLASS platform");
		for (int x = -16; x <= 16; x++) {
			for (int z = -16; z <= 16; z++) {
				gameWorld.getBlockAt(new Location(gameWorld, x, 150, z)).setType(Material.GLASS);
			}
		}
		System.out.println("Ended GLASS platform");
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		if (keepChunk.contains(e.getChunk()))
			e.setCancelled(true); 
   }
	@Override
	public void onDisable() {
		if (getScoreboardManager() != null) {
			getScoreboardManager().onDisable();
		}
		System.out.println("["+PLUGIN_NAME+"] Disabled");
		for (int x = -16; x <= 16; x++) {
			for (int z = -16; z <= 16; z++) {
				World world = Bukkit.getWorlds().get(0);
				world.getBlockAt(new Location(world, x, 150, z)).setType(Material.AIR);				
			}
		}
		System.out.println("["+PLUGIN_NAME+"] "+getBase());
	}
	public static boolean isDebug(){
		boolean debug = getInstance().getConfig().getBoolean("debug");
		if (getInstance().getConfig().getString("debug").equalsIgnoreCase("ultra")){
			System.out.println("Debug is enable");
		}
		return debug;
	}
	public static String getBase() {
        return "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":" + 600+
        		",\"depthNoiseScaleZ\":" + 600 +
        		",\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":" + 63 + 
        		",\"useCaves\":" + true + //S'il y aura des caves ou non
        		",\"useDungeons\":" + true + //S'il y aura des dongeons ou non
        		",\"dungeonChance\":" + 8 + //Probo dongeon 8 = vanilla
        		",\"useStrongholds\":" + true + //S'il y aura des strongholds
        		",\"useVillages\":" + true + //S'il y aura des villages ou non
        		",\"useMineShafts\":" + true + //S'il y aura des mineshafts ou non
        		",\"useTemples\":" + true + //S'il y aura des temples de la jungle
        		",\"useMonuments\":" + true+ //
        		",\"useRavines\":" + true + //Si il y aura des failles ou non
        		",\"useWaterLakes\":" + true + //S'il y aura des lacs d'eau
        		",\"waterLakeChance\":" + 4 + //La proba d'avoir des lacs d'eau 4 = vanilla
        		",\"useLavaLakes\":" + true + //S'il y aura des lacs de lave ou non
        		",\"lavaLakeChance\":" + 80 + //S'il y aura des lacs de lave 80 = vanilla
        		",\"useLavaOceans\":" + false + //Si on remplace les Océans d'eau par de la lave ou non
        		",\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":" + 4 + //Taille des rivières 4 = vanilla
        		",\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80"+
        		",\"coalSize\":"+ 17 +
        		",\"coalCount\":" + 20 + 
        		",\"coalMinHeight\":" + 0 +
        		",\"coalMaxHeight\":"+ 128 +
        		",\"ironSize\":" + 9+
        		",\"ironCount\":" + 20 + 
        		",\"ironMinHeight\":" + 0 + 
        		",\"ironMaxHeight\":" + 64 + 
        		",\"goldSize\":" + 9 +
        		",\"goldCount\":" + 2 + 
        		",\"goldMinHeight\":" + 0 + 
        		",\"goldMaxHeight\":" + 32 + 
        		",\"redstoneSize\":" + 8 + 
        		",\"redstoneCount\":" + 20 + 
        		",\"redstoneMinHeight\":" + 0 + 
        		",\"redstoneMaxHeight\":" + 16+ 
        		",\"diamondSize\":" + 8 + ",\"diamondCount\":" + 1 +
        		",\"diamondMinHeight\":" + 0 + ",\"diamondMaxHeight\":" + 22 +
        		",\"lapisSize\":" + 7 + ",\"lapisCount\":" + 1 + ",\"lapisCenterHeight\":" + 16 +",\"lapisSpread\":" + 16+ "}";
    }
}