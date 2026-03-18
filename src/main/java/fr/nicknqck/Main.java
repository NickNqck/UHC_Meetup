package fr.nicknqck;

import fr.nicknqck.commands.completer.AdminTabCompletor;
import fr.nicknqck.commands.completer.KrystalTabCompletor;
import fr.nicknqck.commands.completer.NSCompleter;
import fr.nicknqck.entity.bijus.BijuListener;
import fr.nicknqck.commands.*;
import fr.nicknqck.commands.Color;
import fr.nicknqck.commands.roles.*;
import fr.nicknqck.commands.vanilla.Gamemode;
import fr.nicknqck.commands.vanilla.Say;
import fr.nicknqck.commands.vanilla.Whitelist;
import fr.nicknqck.config.GameConfig;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.blocks.BlockManager;
import fr.nicknqck.events.blocks.BrickBlockListener;
import fr.nicknqck.events.chat.Chat;
import fr.nicknqck.events.essential.*;
import fr.nicknqck.events.essential.inventorys.HubInventory;
import fr.nicknqck.events.essential.inventorys.WorldConfig;
import fr.nicknqck.items.*;
import fr.nicknqck.managers.*;
import fr.nicknqck.player.EffectsGiver;
import fr.nicknqck.roles.builder.GetterList;
import fr.nicknqck.roles.ds.Lame;
import fr.nicknqck.roles.krystal.KrystalManager;
import fr.nicknqck.roles.krystal.MDJConfig;
import fr.nicknqck.roles.ns.akatsuki.blancv2.BanquePower;
import fr.nicknqck.roles.ns.power.KatsuyuManager;
import fr.nicknqck.runnables.PubRunnable;
import fr.nicknqck.scenarios.impl.TimberPvP;
import fr.nicknqck.scoreboard.ScoreboardManager;
import fr.nicknqck.titans.TitanManager;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.betteritem.BetterItemListener;
import fr.nicknqck.utils.biome.BiomeChanger;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInvManager;
import fr.nicknqck.utils.inventories.Inventories;
import fr.nicknqck.utils.itembuilder.ItemBuilderListener;
import fr.nicknqck.managers.TabManager;
import fr.nicknqck.utils.rank.ChatRank;
import fr.nicknqck.worlds.WorldListener;
import fr.nicknqck.worlds.worldloader.WorldFillTask;
import hm.zelha.particlesfx.util.ParticleSFX;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
[UHC-MTP UHC Plugin Credits]
Ideas and Game Design: NickNqck, Mega02600, Egaly inspirated by goldenuhc.eu and yukanmc
Programming: NickNqck, Mega02600
Roles: NickNqck
*/
@Getter
public class Main extends JavaPlugin {

	public final String PLUGIN_NAME = "UHC-Meetup";
	private final String NAME = "§7[§6UHC-Meetup§7]";
	private ScoreboardManager scoreboardManager;
	private ScheduledExecutorService executorMonoThread;
	private ScheduledExecutorService scheduledExecutorService;
	@Getter
    private static WorldFillTask worldfilltask;
	private GetterList getterList;
	@Getter
	private static Main Instance;
	public static Random RANDOM;
	private Inventories inventories;
	private RoleManager roleManager;
	private WorldsManager worldManager;
	private GameConfig gameConfig;

	private DeathManager deathManager;
	private FileConfiguration webhookConfig;

	private WorldConfig worldConfig;

	private WorldListener worldListener;

	private boolean goodServer;

	private EventsManager eventsManager;

	private KrystalBeastManager krystalBeastManager;
	private TitanManager titanManager;
	private BijuManager bijuManager;
	private TabManager tabManager;
	private HokageManager hokageManager;
	private InfoManager infoManager;
	private PlayersNameManager playersNameManager;
    private KatsuyuManager katsuyuManager;
	private KrystalManager krystalManager;

    @Override
	public void onEnable() {
		Instance = this;
		RANDOM = new Random();
		goodServer = ip();
		this.worldManager = new WorldsManager();
		GameState gameState = new GameState();
		this.inventories = new Inventories(gameState);
		this.getterList = new GetterList(gameState);
		this.gameConfig = new GameConfig();
		getWorldManager().setLobbyWorld(Bukkit.getWorlds().get(0));
		spawnPlatform(getWorldManager().getLobbyWorld());
		saveDefaultConfig();
		registerEvents(gameState);
		World arena = Bukkit.getWorld("arena");
		if (arena == null) {
			initGameWorld();
		} else {
			getWorldManager().setGameWorld(arena);
		}
		registerEvents2(gameState);
		initPlugin(gameState);
		EnableScoreboard(gameState);
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
		saveDefaultConfig();
        registerRecipes();
		saveDefaultWebhookConfig();
		this.roleManager = new RoleManager();
		BiomeChanger.init();
		this.krystalBeastManager = new KrystalBeastManager();
		this.titanManager = new TitanManager();
		this.bijuManager = new BijuManager();
		this.tabManager = new TabManager();
		this.infoManager = new InfoManager(getDataFolder());
		this.playersNameManager = new PlayersNameManager(getDataFolder());
        this.katsuyuManager = new KatsuyuManager();
        new UpdateChecker(this, "NickNqck/UHC_Meetup");
		this.krystalManager = new KrystalManager();
		ParticleSFX.setPlugin(this);
		saveResource("wing.png", false);
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
		getServer().getPluginManager().registerEvents(new HomingBow(gameState), this);
		getServer().getPluginManager().registerEvents(new BlockManager(gameState), this);
		getServer().getPluginManager().registerEvents(new ItemsManager(gameState), this);
		getServer().getPluginManager().registerEvents(new Chat(gameState), this);
		getServer().getPluginManager().registerEvents(new BrickBlockListener(), this);
		getServer().getPluginManager().registerEvents(new BetterItemListener(), this);
		getServer().getPluginManager().registerEvents(new Lame(), this);
		getServer().getPluginManager().registerEvents(new Arctridi(gameState), this);
		getServer().getPluginManager().registerEvents(new Whitelist(gameState), this);
		getServer().getPluginManager().registerEvents(new BijuListener(), this);
		getServer().getPluginManager().registerEvents(new Patch(gameState), this);//Patch effet de potion
		getServer().getPluginManager().registerEvents(new AttackUtils(), this);
		getServer().getPluginManager().registerEvents(new HubInventory(gameState), this);
		getServer().getPluginManager().registerEvents(new ItemBuilderListener(), this);
		new EffectsGiver();
		new fr.nicknqck.utils.event.EventUtils();
		getServer().getPluginManager().registerEvents(new WebHookListeners(gameState), this);
		DeathManager manager = new DeathManager();
		getServer().getPluginManager().registerEvents(manager, this);
		EventUtils.registerEvents(new BanquePower());
		WorldConfig worldConfig = new WorldConfig(gameState);
		getServer().getPluginManager().registerEvents(worldConfig, this);
		this.worldConfig = worldConfig;
		this.deathManager = manager;
		final EventsManager manager2 = new EventsManager();
		EventUtils.registerEvents(manager2);
		this.eventsManager = manager2;
		this.hokageManager = new HokageManager(gameState);
		new PubRunnable().start();
        FastInvManager.register(this);
		System.out.println("Ending registering events");
	}
	private void registerEvents2(GameState gameState) {
		getServer().getPluginManager().registerEvents(new GameListener(gameState), this);
		WorldListener worldListener = new WorldListener();
		getServer().getPluginManager().registerEvents(worldListener, this);
		getServer().getPluginManager().registerEvents(new InfoListener(), this);
		this.worldListener = worldListener;
	}
	private void registerCommands(GameState gameState) {
		System.out.println("Starting registering commands");
		getCommand("ds").setExecutor(new DSmtpCommands(gameState));
		getCommand("a").setExecutor(new AdminCommands(gameState));
		getCommand("a").setTabCompleter(new AdminTabCompletor());
		getCommand("kr").setExecutor(new KrystalCommands(gameState));
        getCommand("kr").setTabCompleter(new KrystalTabCompletor());
		getCommand("aot").setExecutor(new AotCommands(gameState));
		getCommand("ns").setExecutor(new NsCommands(gameState));
		getCommand("ns").setTabCompleter(new NSCompleter());
		getCommand("drop").setExecutor(new DropCommand());
		getCommand("claim").setExecutor(new ClaimCommand(gameState));
		getCommand("mumble").setExecutor(new Mumble());
		getCommand("say").setExecutor(new Say());
		getCommand("gamemode").setExecutor(new Gamemode());
		getCommand("gm").setExecutor(new Gamemode());
		getCommand("wl").setExecutor(new Whitelist(gameState));
		getCommand("whitelist").setExecutor(new Whitelist(gameState));
		getCommand("discord").setExecutor(new Discord());
		getCommand("color").setExecutor(new Color(gameState));
		getCommand("pack").setExecutor(new PackCommand());
		SettingsCommand settingsCommand = new SettingsCommand();
		getCommand("setting").setExecutor(settingsCommand);
		getCommand("settings").setExecutor(settingsCommand);
		Bukkit.getPluginManager().registerEvents(settingsCommand, this);
		getCommand("info").setExecutor(new InfoCommand());
	/*	getCommand("role").setExecutor(new RoleCommand());
		getCommand("role").setTabCompleter(new RoleTabComplete());
		getCommand("team").setExecutor(new TeamCommand());
		getCommand("team").setTabCompleter(new TeamTabComplete());*/
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
		for (Roles r : Roles.values()) {
			gameState.addInAvailableRoles(r, 0);
		}
		System.out.println("init Players");
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
		}
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
	public boolean initGameWorld() {
		if (!deleteWorld("arena"))return false;
		WorldCreator creator = new WorldCreator("arena");
		System.out.println("Seed: "+creator.seed());
		System.out.println("Original Base Settings: "+creator.generatorSettings());
		System.out.println("Original Base : "+creator.generator());
		creator.generatorSettings(getBase());
		System.out.println("After Config Base Settings: "+creator.generatorSettings());
        Bukkit.broadcastMessage("§f[§6UHC-Meetup§f]§a Génération en cours d'une nouvelle§c arène§a.");
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
		return true;
	}
	public boolean deleteWorld(final String worldName) {
		for (World world : Bukkit.getWorlds()) {
			if (world.getName().equals(worldName)) {
				File worldFolder = world.getWorldFolder();
				if (!world.getPlayers().isEmpty()) {
					System.out.println("Can't delete world \""+ worldName +"\" because "+world.getPlayers().size()+" is inside");
					return false;
				}
				Bukkit.unloadWorld(world, false);
				try {
					FileUtils.deleteDirectory(worldFolder);
					System.out.println("Deleted world "+worldFolder.getName());
				} catch (IOException e) {
					e.fillInStackTrace();
				}
			}
		}
		return true;
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
	}
	public static boolean isDebug(){
		boolean debug = getInstance().getConfig().getBoolean("debug");
		if (getInstance().getConfig().getString("debug").equalsIgnoreCase("ultra")){
			System.out.println("Debug is enable");
		}
		return debug;
	}
	private String getBase() {
        return "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":" + (600+getWorldConfig().getCaveBooster())+
        		",\"depthNoiseScaleZ\":" + (600+getWorldConfig().getCaveBooster()) +
        		",\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0," +
				"\"seaLevel\":" + 62 +
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
        		",\"goldSize\":" + 10 +
        		",\"goldCount\":" + (3+getWorldConfig().getGoldBooster()) +
        		",\"goldMinHeight\":" + 0 + 
        		",\"goldMaxHeight\":" + 32 + 
        		",\"redstoneSize\":" + 8 + 
        		",\"redstoneCount\":" + 20 + 
        		",\"redstoneMinHeight\":" + 0 + 
        		",\"redstoneMaxHeight\":" + 16+ 
        		",\"diamondSize\":" + 8 + ",\"diamondCount\":" + (1+getWorldConfig().getDiamondBooster()) +
        		",\"diamondMinHeight\":" + 0 + ",\"diamondMaxHeight\":" + 22 +
        		",\"lapisSize\":" + 7 + ",\"lapisCount\":" + 1 + ",\"lapisCenterHeight\":" + 16 +",\"lapisSpread\":" + 16+ "}";
    }
	private boolean ip() {
		if (getPlayerConnectIP().equalsIgnoreCase("62.210.100.59") || getAmazonIP().equalsIgnoreCase("62.210.100.59")) {
			return true;
		}
		if (getPlayerConnectIP().equalsIgnoreCase("127.0.0.1") || getPlayerConnectIP().equalsIgnoreCase("localhost") || getPlayerConnectIP().equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			return true;
		}
		if (isLocalServer()) {
			return true;
		}
		return getAmazonIP().equalsIgnoreCase("127.0.0.1") || getAmazonIP().equalsIgnoreCase("localhost") || getAmazonIP().equalsIgnoreCase("0:0:0:0:0:0:0:1");
	}
	private boolean isLocalServer() {
		String serverIP = Bukkit.getServer().getIp();
        return serverIP.isEmpty() || "127.0.0.1".equals(serverIP) || "localhost".equals(serverIP);
    }

	private static String getPlayerConnectIP() {
		try {
			URL url = new URL("https://api.ipify.org");
			Scanner scanner = new Scanner(url.openStream());
			String ipAddress = scanner.useDelimiter("\\A").next();
			scanner.close();
			return ipAddress;
		} catch (Exception e) {
			e.printStackTrace();
			return "Erreur lors de la récupération de l'adresse IP du joueur";
		}
	}
	private static String getAmazonIP() {
		try {
			URL url = new URL("http://checkip.amazonaws.com");
			Scanner scanner = new Scanner(url.openStream());
			String ipAddress = scanner.useDelimiter("\\A").next();
			scanner.close();
			return ipAddress;
		} catch (Exception e) {
			e.printStackTrace();
			return "Ip Introuvable";
		}
	}
	public void sendMessageToHosts(@NonNull final String message) {
		for (@NonNull final Player onlinePlayer : getServer().getOnlinePlayers()) {
			if (!ChatRank.isHost(onlinePlayer))continue;
			onlinePlayer.sendMessage(message);
		}
	}
	private void initRoles() {
		Collection<Class<?>> classesOf = getClassesOf(Main.getInstance());
		for (Class<?> aClass : classesOf) {
			if(!aClass.isAnnotationPresent(MDJConfig.class)) continue;

		}
	}
	private Collection<Class<?>> getClassesOf(Plugin plugin) {
		try {
			Objects.requireNonNull(plugin, "Plugin is null!");
			Validate.isTrue(JavaPlugin.class.isAssignableFrom(plugin.getClass()), "Plugin must be a JavaPlugin");

			final Method m = JavaPlugin.class.getDeclaredMethod("getFile");
			m.setAccessible(true);

			final File pluginFile = (File) m.invoke(plugin);
			final Collection<Class<?>> classes = new ArrayList<>();

			try (JarFile jarFile = new JarFile(pluginFile)) {
				Enumeration<JarEntry> entries = jarFile.entries();

				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();

					if (name.endsWith(".class")) {
						name = name.replace("/", ".").replaceFirst(".class", "");

						Class<?> clazz;

						try {
							clazz = Class.forName(name);
                            if (!clazz.getPackage().getName().contains("role")) {
                                clazz = null;
                            }
						} catch (NoClassDefFoundError ex) {
							continue;
						}
						if (clazz == null)continue;
						classes.add(clazz);
					}
				}
			}
			return classes;
		} catch (ReflectiveOperationException | IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}