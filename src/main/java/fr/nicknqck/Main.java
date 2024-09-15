package fr.nicknqck;

import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.commands.*;
import fr.nicknqck.commands.roles.*;
import fr.nicknqck.commands.vanilla.Gamemode;
import fr.nicknqck.commands.vanilla.Say;
import fr.nicknqck.commands.vanilla.Whitelist;
import fr.nicknqck.config.GameConfig;
import fr.nicknqck.events.Events;
import fr.nicknqck.events.blocks.BlockManager;
import fr.nicknqck.events.blocks.BrickBlockListener;
import fr.nicknqck.events.chat.Chat;
import fr.nicknqck.events.essential.*;
import fr.nicknqck.events.essential.inventorys.HubInventory;
import fr.nicknqck.items.*;
import fr.nicknqck.managers.DeathManager;
import fr.nicknqck.managers.RoleManager;
import fr.nicknqck.managers.WorldsManager;
import fr.nicknqck.player.EffectsGiver;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.builder.GetterList;
import fr.nicknqck.roles.ds.Lame;
import fr.nicknqck.roles.ns.akatsuki.blancv2.BanquePower;
import fr.nicknqck.scenarios.impl.TimberPvP;
import fr.nicknqck.scoreboard.ScoreboardManager;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.betteritem.BetterItemListener;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.inventories.Inventories;
import fr.nicknqck.utils.itembuilder.ItemBuilderListener;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.packets.TabTitleManager;
import fr.nicknqck.worlds.WorldFillTask;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
[UHC-MTP UHC Plugin Credits]
Ideas and Game Design: NickNack, Mega02600, Egaly inspirated by goldenuhc.eu and yukanmc
Programming: NickNack, Mega02600
Roles: NickNack, Mega02600
*/

public class Main extends JavaPlugin {

	public final String PLUGIN_NAME = "UHC-Meetup";
	@Getter
	private ScoreboardManager scoreboardManager;
    @Getter
	private ScheduledExecutorService executorMonoThread;
    @Getter
	private ScheduledExecutorService scheduledExecutorService;
	@Getter
    private static WorldFillTask worldfilltask;
	@Getter
	private GetterList getterList;
	@Getter
	private static Main Instance;
	public static Random RANDOM;
	@Getter
	private Inventories inventories;
	@Getter
	private RoleManager roleManager;
	@Getter
	private WorldsManager worldManager;
	@Getter
	private GameConfig gameConfig;
	@Getter
	private DeathManager deathManager;
	@Getter
	private FileConfiguration webhookConfig;
	@Override
	public void onEnable() {
		Instance = this;
		RANDOM = new Random();
		this.worldManager = new WorldsManager();
		GameState gameState = new GameState();
		this.inventories = new Inventories(gameState);
		this.getterList = new GetterList(gameState);
		this.gameConfig = new GameConfig();
		getWorldManager().setLobbyWorld(Bukkit.getWorlds().get(0));
		spawnPlatform(getWorldManager().getLobbyWorld());
		initGameWorld();
		initPlugin(gameState);
		EnableScoreboard(gameState);
		registerEvents(gameState);
		registerCommands(gameState);
		clearMap(getWorldManager().getLobbyWorld());
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
		saveDefaultWebhookConfig();
		this.roleManager = new RoleManager();
		System.out.println("ENDING ONENABLE");
    }
	private void saveDefaultWebhookConfig() {
		File webhookFile = new File(getDataFolder(), "webhook.yml");

		if (!webhookFile.exists()) {
			getLogger().info("Creation du fichier webhook.yml par defaut...");
			saveResource("webhook.yml", false);
		} else {
			getLogger().info("Fichier webhook.yml deja present.");
		}
		updateWebHookConfig();
	}
	public void updateWebHookConfig() {
		File webhookFile = new File(getDataFolder(), "webhook.yml");
        webhookConfig = YamlConfiguration.loadConfiguration(webhookFile);
		System.out.println("WebHook updated");
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
        getScoreboardManager().onEnable();
		System.out.println("End enable scoreboard");
	}
	private void registerEvents(GameState gameState) {
		System.out.println("Starting registering events");
		getServer().getPluginManager().registerEvents(new WeatherEvents(), this);
		getServer().getPluginManager().registerEvents(new TimberPvP(), this);
		getServer().getPluginManager().registerEvents(new JoinEvents(), this);
		getServer().getPluginManager().registerEvents(new QuitEvents(), this);
		getServer().getPluginManager().registerEvents(new EntityDamageEvents()	,this);
		getServer().getPluginManager().registerEvents(new RodTridimensionnelle(gameState), this);
		getServer().getPluginManager().registerEvents(new PotionUtils(), this);
		getServer().getPluginManager().registerEvents(new HubListener(gameState), this);
		getServer().getPluginManager().registerEvents(new GameListener(gameState), this);
		getServer().getPluginManager().registerEvents(new HomingBow(gameState), this);
		getServer().getPluginManager().registerEvents(new BlockManager(gameState), this);
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
		getServer().getPluginManager().registerEvents(new WebHookListeners(gameState), this);
		DeathManager manager = new DeathManager();
		getServer().getPluginManager().registerEvents(manager, this);
		EventUtils.registerEvents(new BanquePower());
		this.deathManager = manager;
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
	private void clearMap(World world) {
		System.out.println("Starting cleaning map");
		System.out.println("Starting cleaning blocks");
		for (int x = -150; x <= 150; x++) {
			for (int z = -150; z <= 150; z++) {
				for (int y = 50; y <= 120; y++) {
					//System.out.println("Calculating Block at "+"x:"+x+", y:"+y+", z:"+z);
					Block block = world.getBlockAt(x, y, z);
					if (block.getType().name().contains("SPONGE") ||block.getType() == Material.BRICK || block.getType() == Material.COBBLESTONE || block.getType() == Material.OBSIDIAN || block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE) {
						if (isDebug()){
							System.out.println(world.getBlockAt(x, y, z).getType().name()+" -> Air at x:"+x+", y:"+y+", z:"+z);
						}
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		System.out.println("End cleaning blocks");
		System.out.println("Starting cleaning entities");
		for (Entity e : world.getEntities()) {
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
	}
	private void giveTab(GameState gameState) {
		System.out.println("Starting give tab");
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			if (gameState.getServerState() == ServerStates.InLobby) {
				for (Player p : Bukkit.getOnlinePlayers()) {
				//	NMSPacket.sendTabTitle(p, null, null);
					NMSPacket.sendTabTitle(p,
							gameState.msgBoard + "\n",
							"\n" +
									"§7Joueurs: §c" + gameState.getInLobbyPlayers().size() +"§r/§6"+gameState.getroleNMB()+ "\n"
									+ "\n§6§l TPS: "+ TPS.getAverageTPS(1)+" "
									+ "§cdiscord.gg/RF3D4Du8VN");
				}
			}
			if (gameState.getServerState() == ServerStates.InGame) {
				for (UUID u : gameState.getInGamePlayers()) {
					Player player = Bukkit.getPlayer(u);
					if (player == null)continue;
					if (gameState.roleTimer < gameState.getInGameTime()) {
		        		if (!gameState.hasRoleNull(player)) {
		        			if (gameState.getGamePlayer().get(player.getUniqueId()).getRole().getOriginTeam() != null) {
				            	TabTitleManager.sendTabTitle(player, gameState.msgBoard+ "\n", "\n" + ChatColor.GRAY + "Kills: " + ChatColor.GOLD + gameState.getPlayerKills().get(player).size() + "\n" + "\n" + "§7Plugin by§r: §bNickNqck");
				         }
		        		}		        		 
		        	} else {
		        		int time = gameState.roleTimer-gameState.getInGameTime();
				    	String trm = time/60 < 10 ? "0"+time/60 : time/60+"";
				    	String trs = time%60 < 10 ? "0"+time%60 : time%60+"";
		        		TabTitleManager.sendTabTitle(player, gameState.msgBoard + "\n", "\n" + ChatColor.GRAY + "Role: " + ChatColor.GOLD + trm +"§rm§6"+trs+"§rs"+ "\n" + "\n" + "§7Plugin by§r: §bNickNqck");
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
	private void spawnPlatform(World world) {
		System.out.println("Spawning GLASS platform");
		for (int x = -16; x <= 16; x++) {
			for (int z = -16; z <= 16; z++) {
				world.getBlockAt(new Location(world, x, 150, z)).setType(Material.GLASS);
			}
		}
		System.out.println("Ended GLASS platform");
	}
	public void initGameWorld() {
		deleteWorld("arena");
		WorldCreator creator = new WorldCreator("arena");
		creator.generatorSettings(getBase());
		World gameWorld = creator.createWorld();
		gameWorld.setTime(6000);
		gameWorld.setGameRuleValue("doMobSpawning", "false");
		gameWorld.setGameRuleValue("doDaylightCycle", "false");
		gameWorld.setGameRuleValue("spectatorsGenerateChunks", "false");
		gameWorld.setGameRuleValue("naturalRegeneration", "false");
		gameWorld.setGameRuleValue("announceAdvancements", "false");
		gameWorld.setDifficulty(Difficulty.HARD);
		gameWorld.setSpawnLocation(0, gameWorld.getHighestBlockYAt(0, 0), 0);
		gameWorld.setGameRuleValue("randomTickSpeed", "3");
		gameWorld.setGameRuleValue("doMobSpawning", "false");
		gameWorld.setGameRuleValue("doFireTick", "false");
		getWorldManager().setGameWorld(gameWorld);
		System.out.println("Created world gameWorld");
	}
	private void deleteWorld(String worldName) {
		for (World world : Bukkit.getWorlds()) {
			if (world.getName().equals(worldName)) {
				File worldFolder = world.getWorldFolder();
				Bukkit.unloadWorld(world, false);
				try {
					FileUtils.deleteDirectory(worldFolder);
					System.out.println("Deleted world "+worldFolder.getName());
				} catch (IOException e) {
					e.fillInStackTrace();
				}
			}
		}
	}
	@Override
	public void onDisable() {
		if (getScoreboardManager() != null) {
			getScoreboardManager().onDisable();
		}
		deleteWorld("arena");
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
	private String getBase() {
        return "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":" + 1000+
        		",\"depthNoiseScaleZ\":" + 1000 +
        		",\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0," +
				"\"seaLevel\":" + 60 +
        		",\"useCaves\":" + true + //S'il y aura des caves ou non
        		",\"useDungeons\":" + true + //S'il y aura des dongeons ou non
        		",\"dungeonChance\":" + 8 + //Probo dongeon 8 = vanilla
        		",\"useStrongholds\":" + true + //S'il y aura des strongholds
        		",\"useVillages\":" + true + //S'il y aura des villages ou non
        		",\"useMineShafts\":" + false + //S'il y aura des mineshafts ou non
        		",\"useTemples\":" + false + //S'il y aura des temples de la jungle
        		",\"useMonuments\":" + false+ //
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
        		",\"goldSize\":" + 11 +
        		",\"goldCount\":" + 4 +
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