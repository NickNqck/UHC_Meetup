package fr.nicknqck;

import fr.nicknqck.GameState.MDJ;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.bijus.BijuListener;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.events.custom.StartGameEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.managers.AssassinManagerV2;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.TitanListener;
import fr.nicknqck.roles.ns.Hokage;
import fr.nicknqck.utils.rank.ChatRank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class HubListener implements Listener {
	private final GameState gameState;
	@Getter
	private static HubListener instance;
	public HubListener(GameState gameState) {this.gameState = gameState; instance = this;}
	public final void StartGame() {
		gameState.updateGameCanLaunch();
		if (!gameState.gameCanLaunch) {
			System.out.println("Impossible de start la partie");
			return;
		}
		gameState.setInGamePlayers(new ArrayList<>(gameState.getInLobbyPlayers()));
		Collections.shuffle(gameState.getInGamePlayers(), Main.RANDOM);
		gameState.getInLobbyPlayers().clear();
		spawnPlatform(Main.getInstance().getWorldManager().getGameWorld(), Material.AIR);
		gameState.infected = null;
		gameState.infecteur = null;
		Main.getInstance().getEventsManager().getEventsList().forEach(event -> event.setEnable(false));
		ItemsManager.instance.clearJspList();
		gameState.t = Main.getInstance().getGameConfig().getMaxTimeDay();
		gameState.getPlayerRoles().clear();
		gameState.getPlayerKills().clear();
		Border.setActualBorderSize(Border.getMaxBorderSize());
		gameState.shrinking = false;
		Main.getInstance().getWorldManager().getGameWorld().getWorldBorder().setSize(Border.getMaxBorderSize()*2);
		gameState.JigoroV2Pacte2 = false;
		gameState.JigoroV2Pacte3 = false;
		for (Entity e : Main.getInstance().getWorldManager().getGameWorld().getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}
		// Debug Affichages des roles InGame
		int roleNmb = 0;
		List<Roles> rolesList = new ArrayList<>();
		for (Roles r : gameState.getAvailableRoles().keySet()) {
			if (Main.isDebug()){
				System.out.println("role: "+r+", nmb: "+gameState.getAvailableRoles().get(r));
			}
			roleNmb += gameState.getAvailableRoles().get(r);
			if (gameState.getAvailableRoles().get(r) == 0){
				gameState.getDeadRoles().add(r);
			} else {
				rolesList.add(r);
			}
		}
		System.out.println("lobby: "+gameState.getInLobbyPlayers().size()+", roles: "+roleNmb+", equal: "+(gameState.getInLobbyPlayers().size() == roleNmb));
		
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null) {
				System.out.println("canceled start game, because "+u.toString()+" is Player null");
				gameState.getInGamePlayers().clear();
				for (Player player : Bukkit.getOnlinePlayers()) {
					gameState.getInLobbyPlayers().add(player.getUniqueId());
				}
				return;
			}
			p.updateInventory();
			p.setMaxHealth(20.0);
			p.setHealth(20.0);
			p.setFoodLevel(20);
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
			((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0); // Supprime les fleches du joueur
			p.setAllowFlight(false);
			p.setExp(0);
			p.setLevel(0);
			p.setFallDistance(0);
			GameListener.RandomTp(p);
			gameState.addPlayerKills(p);
			p.setGameMode(GameMode.SURVIVAL);
			giveStartInventory(p);
			fr.nicknqck.player.GamePlayer gamePlayer = new GamePlayer(p);
			gameState.getGamePlayer().put(u, gamePlayer);
			System.out.println("Player "+p+" a ete ajouter a la partie");
		}
		TitanListener.getInstance().onStartGame();
		gameState.nightTime = false;
		for (Bijus b : Bijus.values()) {
			b.getBiju().setHote(null);
			b.getBiju().resetCooldown();
		}
		Main.getInstance().getWorldManager().getGameWorld().setGameRuleValue("naturalRegeneration", "false");
		BijuListener.getInstance().resetCooldown();
		Bijus.initBiju(gameState);
		Bukkit.getPluginManager().callEvent(new StartGameEvent(gameState, rolesList));
		gameState.setActualPvPTimer(gameState.getPvPTimer());
		gameState.setServerState(ServerStates.InGame);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			if (gameState.getMdj().equals(MDJ.NS)){
				if (gameState.getHokage() == null){
					gameState.setHokage(new Hokage(gameState.getTimeProcHokage()-10, gameState));
				}
				gameState.getHokage().run();
			}
		}, 220);
		new AssassinManagerV2(gameState);
		System.out.println("Ended StartGame");
	}

	public void giveStartInventory(Player p) {
		Main.getInstance().getScoreboardManager().update(p);
		ItemsManager.ClearInventory(p);
			p.getInventory().setItem(0, Items.getdiamondsword());
			p.getInventory().setItem(2, Items.getbow());
			if (GameState.pearl == 1) {
				p.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL, GameState.pearl));
			}
			p.getInventory().setItem(5, new ItemStack(Material.GOLDEN_CARROT, 64));
			p.getInventory().setItem(9, new ItemStack(Material.ARROW, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()));
			p.getInventory().setItem(20, new ItemStack(Material.ANVIL, 1));
			p.getInventory().setItem(11, Items.getironshovel());
			p.getInventory().setItem(12, Items.getironpickaxe());
		if (GameState.nmbblock == 1) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 2) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 3) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(19, new ItemStack(Material.BRICK, 64));
		} else if (GameState.nmbblock == 4) {
			p.getInventory().setItem(1, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(28, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(19, new ItemStack(Material.BRICK, 64));
			p.getInventory().setItem(10, new ItemStack(Material.BRICK, 64));
		}
		if (GameState.eau == 1) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 2) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 3) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(25, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		} else if (GameState.eau == 4) {
			p.getInventory().setItem(7, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(16, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(25, new ItemStack(Material.WATER_BUCKET, 1));
			p.getInventory().setItem(34, new ItemStack(Material.WATER_BUCKET, 1));
		}
		if (GameState.lave == 1) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 2) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 3) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		} else if (GameState.lave == 4) {
			p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(15, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET, 1));
			p.getInventory().setItem(33, new ItemStack(Material.LAVA_BUCKET, 1));
		}
		p.getInventory().setHelmet(Items.getdiamondhelmet());
		p.getInventory().setChestplate(Items.getdiamondchestplate());
		p.getInventory().setLeggings(Items.getironleggings());
		p.getInventory().setBoots(Items.getdiamondboots());
		p.getInventory().setItem(3, new ItemStack(Material.GOLDEN_APPLE, gameState.getNmbGap()));
		p.updateInventory();
		p.setWalkSpeed(0.2f);
		((CraftPlayer) p).getHandle().setAbsorptionHearts(0);
	}
	// Creer une plateforme pour le spawn des joueurs (Suppression possible grace a Material.AIR)
	public static void spawnPlatform(final World world, final Material material) {
		for (int x = -16; x <= 16; x++) {
			for (int z = -16; z <= 16; z++) {
				world.getBlockAt(new Location(world, x, 150, z)).setType(material);
			}
		}
	}
	// Gerer l'interaction avec un item
	@EventHandler
	public void OnItemInteract(PlayerInteractEvent event) {
		if (gameState.getServerState() != ServerStates.InLobby) return; // Uniquement dans le lobby
		Player player = event.getPlayer();
		if (event.hasItem()) {
			ItemStack itemstack = event.getItem();
			// Si click droit alors afficher un menu (AdminWatch)
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (itemstack.isSimilar(ItemsManager.adminWatch)) {
					if (ChatRank.isHost(player)) {
						player.openInventory(GUIItems.getAdminWatchGUI());
		    			Main.getInstance().getInventories().updateAdminInventory(player);
					} else {
						player.openInventory(GUIItems.getRoleSelectGUI());
						Main.getInstance().getInventories().updateRoleInventory(player);
					}
				}
			}
		}
	}	

	@Setter
	@Getter
	private HashMap<Roles, Integer> availableRoles = new HashMap<>();

	public final void StartGame(final Player player) {
		gameState.updateGameCanLaunch();
		if (ChatRank.isHost(player) && gameState.gameCanLaunch) {
			StartGame();
			player.closeInventory();
		} else {
			player.closeInventory();
		}
	}
}