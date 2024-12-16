package fr.nicknqck.roles.ns.orochimaru.edotensei;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.Jugo;
import fr.nicknqck.roles.ns.orochimaru.Karin;
import fr.nicknqck.roles.ns.orochimaru.Kimimaro;
import fr.nicknqck.roles.ns.orochimaru.Sasuke;
import fr.nicknqck.roles.ns.solo.jubi.Madara;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kabuto extends OrochimaruRoles implements Listener {

	private TextComponent desc;
	private int ninjutsuCD = 0;
	private boolean mortOrochimaru = false;
	private boolean karinDeath = false;
	private boolean kimimaroDeath = false;
	private boolean jugoDeath = false;
	private final ItemStack dashItem = new ItemBuilder(Material.NETHER_STAR).setName("§aDash").toItemStack();
	private boolean solo = false;
	private final ItemStack ermiteItem = new ItemBuilder(Material.NETHER_STAR).setName("§5Mode Ermite").toItemStack();
	private int dashCd;
	private int cdErmite;
	private boolean obitoTeam = false;
	private boolean lastAlive = false;
	private EdoTenseiUser edo;
	private boolean kabutoSend = false, obitoSend = false;
    public Kabuto(UUID player) {
		super(player);
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setChakraType(Chakras.SUITON);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		getKnowedRoles().add(Orochimaru.class);
		AutomaticDesc automaticDesc = new AutomaticDesc(this);
		automaticDesc.addEffects(getEffects());
		automaticDesc.setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Soigne quelqu'un en fonction du clique:\n\n" +
				"§7     → Clique droit: En visant un joueur, celà permet de le§d soigner§7 de§c 2"+AllDesc.coeur+"\n\n" +
				"§7     → Clique gauche: Vous§d soigne§7 de§c 2"+AllDesc.coeur)}), "§aNinjutsu Médical", 60*3));
		automaticDesc.addParticularites(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez la nature de chakra: "+getChakras().getShowedName())}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort d'§5Orochimaru§7 vous obtenez son item d'§5Edo Tensei§7.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de §5Karin§7 votre§a Ninjutsu Médical§d soignera§7 de§c 5"+AllDesc.coeur)}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§5 Jugo§7 vous obtiendrez un§a dash§7 qui vous propulsera§c 10blocs§7 en avant et infligera§c 2"+AllDesc.coeur+"§7 aux joueurs proche.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§5 Kimimaro§7 vous obtiendrez une épée en diamant nommé \"§fManipulation des os§7\" enchanter tranchant IV.")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A la mort de§5 Sasuke§7 s'il était encore dans le camp§5 Orochimaru§7 vous gagnez§a +§c2"+AllDesc.coeur+"§c permanent")}),
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Si vous êtes le dernier membre du camp§5 Orochimaru§7 en§a vie§7 vous obtiendrez l'effet§c Force I§7 de manière§c permanente§7.\n\n" +
						"§7De plus, vous aurez le choix de gagner ou non avec§d Obito§7 en duo (uniquement si§d Madara§7 est§c mort§7)\n" +
						"§7Si§d Obito§7 accepte vous rejoindrez son camp mais perdrez votre§5 Edo Tensei§7.")})
		);
		this.desc = automaticDesc.getText();
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Karin)) {
				onKarinDeath(false);
				owner.sendMessage("§5Karin§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.attributedRole.contains(Roles.Kimimaro)) {
				onKimimaroDeath(false);
				owner.sendMessage("§5Kimimaro§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.getAttributedRole().contains(Roles.Sasuke)) {
				onSasukeDeath(false);
				owner.sendMessage("§5Sasuke§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
			}
			if (!gameState.getAttributedRole().contains(Roles.Jugo)) {
				owner.sendMessage("§5Jugo§7 n'étend pas dans la partie, vous recevez donc les bonus dû à sa mort");
				onJugoDeath(false);
			}
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la partie vous avez tout de même reçus les bonus dû à sa mort !");
			}
			verifyAliveOrochimaru(getGameState());
		}, 20*10);
	}
	@Override
	public Roles getRoles() {
		return Roles.Kabuto;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		List<ItemStack> toReturn = new ArrayList<>();
		toReturn.add(this.NinjutsuMedicalItem());
		if (mortOrochimaru && !obitoTeam) {
			toReturn.add(this.edo.getEdoTenseiItem());
		}
		if (jugoDeath) {
			toReturn.add(this.dashItem);
		}
		if (solo) {
			toReturn.add(this.ermiteItem);
		}
		return toReturn.toArray(new ItemStack[0]);
	}
	@Override
	public String[] Desc() {
		return new String[0];
	}
	@Override
	public TextComponent getComponent() {
		return desc;
	}
	@Override
	public void Update(GameState gameState) {
		if (ninjutsuCD >= 0) {
			ninjutsuCD--;
			if (ninjutsuCD == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau§d soigner§7 quelqu'un.");
			}
		}
		if (cdErmite >= 0) {
			cdErmite--;
			if (cdErmite == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser le§5 Mode Ermite");
			}
		}
		if (dashCd >= 0) {
			dashCd--;
			if (dashCd == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau§a Dash");
			}
		}
	}
	@Override
	public void resetCooldown() {
		ninjutsuCD = 0;
		dashCd = 0;
		cdErmite = 0;
	}
	private ItemStack NinjutsuMedicalItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aNinjutsu Médical").setLore("§7Permet de vous soignez vous ou un autre joueur").toItemStack();
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (gameState.getGamePlayer().get(player.getUniqueId()).getRole() instanceof Orochimaru) {
			if (!mortOrochimaru) {
				onOrochimaruDeath(true);
			}
		}
	}
	private void onOrochimaruDeath(boolean msg) {
		owner.getInventory().removeItem(getItems());
		mortOrochimaru = true;
		this.edo = new EdoTenseiUser(this);
		giveItem(owner, true, getItems());
		if (msg) {
			owner.sendMessage("§7C'est terrible !§5 Orochimaru§7 est§c mort§7, en son homage vous récupérez sa plus puissante technique, l'§5Edo Tensei§7.");
		}
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}
	@Override
	public String getName() {
		return "Kabuto";
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		HandlerList.unregisterAll(this);
	}
	@EventHandler
	private void onUHCDeath(@NonNull UHCDeathEvent event) {
		if (event.getRole() != null) {
			if (event.getRole() instanceof Karin && !this.karinDeath) {
				onKarinDeath(true);
			}
			if (event.getRole() instanceof Kimimaro && !this.kimimaroDeath) {
				onKimimaroDeath(true);
			}
			if (event.getRole() instanceof Sasuke) {
				Sasuke role = (Sasuke) event.getRole();
				if (!role.isMortOrochimaru()) {
					onSasukeDeath(true);
				}
			}
			if (event.getRole() instanceof Jugo) {
				onJugoDeath(true);
			}
			if (!solo && !obitoTeam && getGamePlayer().isAlive()) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> verifyAliveOrochimaru(event.getGameState()), 5);
			}
		}
	}
	@EventHandler
	private void onInteract(PlayerInteractEvent event) {
		if (event.getItem() != null) {
			Player player = event.getPlayer();
			if (player.getUniqueId() == getPlayer()) {
				if (event.getItem().isSimilar(NinjutsuMedicalItem())) {
					event.setCancelled(true);
					if (ninjutsuCD > 0) {
						sendCooldown(owner, ninjutsuCD);
						return;
					}
					if (event.getAction().name().contains("LEFT")) {
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
						player.sendMessage("§dSoins !");
						Heal(owner, this.karinDeath ? 10 : 4);
					} else {
						Player target = getTargetPlayer(owner, 30);
						if (target == null) {
							owner.sendMessage("§cIl faut viser un joueur !");
							return;
						}
						Heal(target, this.karinDeath ? 10 : 4);
						player.sendMessage("§dSoins !");
						target.sendMessage("§5Kabuto§f vous à§d soigner§f !");
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
					}
				}
				if (event.getItem().isSimilar(this.dashItem)) {
					if (dashCd >= 1) {
						sendCooldown(event.getPlayer(), dashCd);
						event.setCancelled(true);
						return;
					}
					player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(5));
					player.setNoDamageTicks(16);
					new BukkitRunnable() {
						private int tick = 16;
						private final List<UUID> damaged = new ArrayList<>();
						@Override
						public void run() {
							tick--;
							for (Block block : getSurroundingBlocks(player)) {
								if (block.getType() != Material.BEDROCK && block.getType() != Material.BARRIER) {
									Bukkit.getScheduler().runTask(Main.getInstance(), () -> block.setType(Material.AIR));
								}
							}
							for (Player p : Loc.getNearbyPlayersExcept(player, 5)) {
								if (!damaged.contains(p.getUniqueId())) {
									Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
										if (p.getHealth() - 4.0 <= 0)  {
											p.setHealth(1.0);
										} else {
											p.setHealth(p.getHealth()-4.0);
										}
										damaged.add(p.getUniqueId());
									});
								}
							}
							if (tick == 0) {
								cancel();
							}
						}
					}.runTaskTimerAsynchronously(Main.getInstance(), 1, 0);
					dashCd = 60*6;
					event.setCancelled(true);
				}
				if (event.getItem().isSimilar(this.ermiteItem)) {
					if (cdErmite >= 1) {
						sendCooldown(player, cdErmite);
						event.setCancelled(true);
						return;
					}
					if (this.solo) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), true);
						HealingRunnable runnable = new HealingRunnable(this);
						runnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
					}
				}
			}
		}
	}
	@EventHandler
	private void onUHCKill(UHCPlayerKillEvent event) {
		if (event.getGamePlayerKiller() != null) {
			if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
				if (this.cdErmite > 60) {
					this.cdErmite-=60;
					event.getPlayerKiller().sendMessage("§7Le cooldown actuel de votre§5 Mode Ermite§7 a été réduit de§c 60 secondes§7.");
				}
			}
		}
	}
	private List<Block> getSurroundingBlocks(Player player) {
		List<Block> blocks = new ArrayList<>();
		Location playerLocation = player.getLocation();

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				Block block = playerLocation.clone().add(x, 0, z).getBlock();
				Block block2 = playerLocation.clone().add(x, 1, z).getBlock();
				Block block3 = playerLocation.clone().add(x, 2, z).getBlock();
				blocks.add(block);
				blocks.add(block2);
				blocks.add(block3);
			}
		}

		return blocks;
	}
	@Override
	public void onNsCommand(String[] args) {
		if (!lastAlive)return;
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("kabuto")) {
				if (this.kabutoSend)return;
				if (args[1].equalsIgnoreCase("send")) {
					Player obito = getPlayerFromRole(Roles.Obito);
					if (obito != null) {
						proposeObito(obito);
						Player owner = Bukkit.getPlayer(getPlayer());
						if (owner != null) {
							owner.sendMessage("§7Vous venez de proposer une alliance à§d Obito");
							kabutoSend = true;
						}
					}
				}
				if (args[1].equalsIgnoreCase("dont")) {
					Player owner = Bukkit.getPlayer(getPlayer());
					if (owner != null) {
						onKabutoDeny(owner);
						owner.sendMessage("§7Vous avez refuser de proposer une alliance à§d Obito");
						kabutoSend = true;
					}
				}
			}
		}
	}
	private void verifyAliveOrochimaru(GameState gameState) {
		int amountDeath = 0;
		int amountIG = 0;
		for (RoleBase role : gameState.getPlayerRoles().values()) {
			if (role.getOriginTeam().equals(TeamList.Orochimaru)) {
				amountIG++;
				if (!role.getGamePlayer().isAlive()) {
					amountDeath++;
				}
			}
		}
		if (amountDeath == amountIG-1) {//donc si tout les orochimarus sont mort (sauf kabuto)
			onLastAlive();
		}
	}
	private void onKarinDeath(boolean msg) {
		this.karinDeath = true;
		if (msg) {
			Player owner = Bukkit.getPlayer(getPlayer());
			if (owner != null) {
				owner.sendMessage("§5Karin§7 est malheuresement morte, celà vous a donnez des idées pour votre§a Ninjutsu Médical§7 il§d soignera§7 à partir de maintenant§c 5"+AllDesc.coeur+"§7 au lieu de§c 2§7.");
			}
		}
	}
	private void onKimimaroDeath(boolean msg) {
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner == null)return;
		this.kimimaroDeath = true;
		giveItem(owner, false, new ItemBuilder(Material.DIAMOND_SWORD).setName("§fManipulation des os").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setDroppable(false).toItemStack());
		if (msg) {
			owner.sendMessage("§5Kimimaro§7 est mort, sa vie n'aura pas été inutile, vous récupérez sa capaciter de la §n§fManipulation des os");
		}
	}
	private void onSasukeDeath(boolean msg) {
		Player owner = Bukkit.getPlayer(getPlayer());
		setMaxHealth(getMaxHealth()+4.0);
		if (owner != null) {
			owner.setMaxHealth(getMaxHealth());
			owner.setHealth(owner.getHealth()+4.0);
			if (msg) {
				owner.sendMessage("§7Cette saleté de§5 Sasuke§7 est mort, malgré ceci sa vie n'a pas été inutile, sa grande vitalité vous à inspirez et vous a donnez§a +§c2"+AllDesc.coeur+"§7 permanent");
			}
		}
	}
	private void onJugoDeath(boolean msg) {
		this.jugoDeath = true;
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner != null) {
			giveItem(owner, false, this.dashItem);
			if (msg) {
				owner.sendMessage("§5Jugo§7 est malencontreusement mort, heureusement que vous aviez eu le temp de faire des expériences sur lui, vous avez§c récupérer§7 sa faculté de§a Dash");
			}
		}
	}
	private void onLastAlive() {
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		Player owner = Bukkit.getPlayer(getPlayer());
		if (owner != null) {
			boolean obitoAlive = !getListPlayerFromRole(Obito.class).isEmpty();
			boolean madaraAlive = !getListPlayerFromRole(Madara.class).isEmpty();
			lastAlive = true;
			if (!madaraAlive) {
				if (obitoAlive){
					proposeKabuto(owner);
				} else {
					onKabutoDeny(owner);
				}
			} else {
				onKabutoDeny(owner);
			}
		}
	}
	private void proposeKabuto(@NonNull Player owner) {
		TextComponent proposition = new TextComponent("§7Vous n'avez plus aucun allier en ce bas monde, voulez vous proposer à§d Obito§7 une alliance ?\n\n");
		TextComponent accept = new TextComponent("§a§lACCEPTER");
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns kabuto send"));
		proposition.addExtra(accept);
		proposition.addExtra("\n\n");
		TextComponent deny = new TextComponent("§c§lREFUSER");
		deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns kabuto dont"));
		proposition.addExtra(deny);
		owner.spigot().sendMessage(proposition);
	}
	private void proposeObito(@NonNull Player obito) {
		TextComponent proposition = new TextComponent("§5Kabuto§7 vous propose une alliance, celà lui fera rejoindre votre camp, souhaitez vous §aaccepter§7 son invitation ?\n\n");
		TextComponent accept = new TextComponent("§a§lACCEPTER");
		accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns obito accept"));
		proposition.addExtra(accept);
		proposition.addExtra("\n\n");
		TextComponent deny = new TextComponent("§c§lREFUSER");
		deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns obito deny"));
		proposition.addExtra(deny);
		obito.spigot().sendMessage(proposition);
	}
	private void onKabutoDeny(@NonNull Player owner) {
		this.solo = true;
		owner.sendMessage("§7Vous êtes maintenant un rôle§e Solitaire§7.");
		setTeam(TeamList.Kabuto);
		this.edo.setCanEdoTensei(true);
	}
	private void onObitoDeny(@NonNull Player owner) {
		owner.sendMessage("§7Cette saleté d'§dObito§7 a refuser de s'allier à vous, vous avez intérêt à lui faire regretter");
		onKabutoDeny(owner);
	}
	private void onObitoAccept(@NonNull Player owner, @NonNull Obito obito) {
		owner.sendMessage("§dObito§7 a accepter de vous rejoindre, ses éxigences vous force à ne plus utiliser votre§5 Edo Tensei§7, vous avez gagner un allier de taille");
		setTeam(obito.getTeam());
		this.obitoTeam = true;
		Player player = Bukkit.getPlayer(obito.getPlayer());
		if (player == null)return;
		player.sendMessage("§5Kabuto§7 rejoint maintenant votre camp");
		this.edo.setCanEdoTensei(false);

	}
	public void onObitoCommand(String[] args, Obito role) {
		if (!lastAlive)return;
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("obito")) {
				if (this.obitoSend)return;
				if (args[1].equalsIgnoreCase("deny")) {
					Player owner = Bukkit.getPlayer(getPlayer());
					if (owner != null) {
						onObitoDeny(owner);
						obitoSend = true;
					}
				}
				if (args[1].equalsIgnoreCase("accept")) {
					Player owner = Bukkit.getPlayer(getPlayer());
					if (owner != null) {
						onObitoAccept(owner, role);
						obitoSend = true;
					}
				}
			}
		}
	}
	private static class HealingRunnable extends BukkitRunnable {

		private final Kabuto kabuto;
		private int timeRemaining = 60*5;
		private int timeHealthRemaining = 10;
		private HealingRunnable(Kabuto owner) {
			this.kabuto = owner;
		}
		@Override
		public void run() {
			if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame) || !kabuto.getGamePlayer().isAlive()){
				cancel();
				return;
			}
			timeRemaining--;
			timeHealthRemaining--;
			if (timeRemaining == 0) {
				cancel();
				return;
			}
			Player owner = Bukkit.getPlayer(kabuto.getPlayer());
			if (owner != null) {
				if (timeHealthRemaining <= 0) {
					timeHealthRemaining = 10;
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.setHealth(Math.min(owner.getHealth() + 1.0, owner.getMaxHealth())));
				}
			}
		}
	}
}