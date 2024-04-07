package fr.nicknqck.roles.ns.solo.jubi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.bijus.Bijus;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.KamuiUtils;
import fr.nicknqck.utils.KamuiUtils.Users;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;


public class Obito extends RoleBase {
	public List<Player> Tsukuyomi = new ArrayList<>();

	public Obito(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		setChakraType(Chakras.KATON);
		owner.sendMessage(Desc());
		giveItem(owner, false, getItems());
		if (!gameState.attributedRole.contains(Roles.Kakashi)) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			Bukkit.dispatchCommand(console, "nakime Gh6Iu2YjZl8A9Bv3Tn0Pq5Rm");
		}
		missionsO.clear();
		missionI = new HashMap<Integer, MissionInfecte>();
		for (MissionObito mo : MissionObito.values()) {
			mo.setFinished(false);
		}
		for (MissionInfecte mi : MissionInfecte.values()) {
			mi.setFinished(false);
		}
	}
	public class ObitoRunnable extends BukkitRunnable{

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if (gameState.getServerState() != ServerStates.InGame) {
				cancel();
			}
			if (toIzanami != null) {
				if (missionI.get(1) == MissionInfecte.Bouger && !Tsukuyomi.contains(toIzanami)) {
					Location loc = toIzanami.getLocation().clone();
					loc.setPitch(0);
					loc.setYaw(0);
					if (sansbougerLoc != null) {
						if (loc.equals(sansbougerLoc)) {
							timesansbouger++;
						}
					}
				}
				if (missionsO.get(1) == MissionObito.Rester || missionsO.get(2) == MissionObito.Rester) {
					for(Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
						if (p.getUniqueId() == toIzanami.getUniqueId()) {
							timepassedp++;
						}
					}
					if (timepassedp == 60*5) {
						owner.sendMessage("§7Vous avez fini l'une de vos mission !");
						MissionObito.Rester.setFinished(true);
					}
				}
			}
			if (timesansbouger == 5 && missionI.get(1) == MissionInfecte.Bouger) {
				owner.sendMessage("§7Votre cible à accomplie sa mission !");
				missionI.get(1).setFinished(true);
			}
			if (missionsO.size() == 2) {
				if (missionsO.get(1).getifFinished()) {
					if (missionsO.get(2).getifFinished()) {
						if (missionI.size() == 1) {
							if (missionI.get(1).getifFinished()) {
								if (!b) {
									owner.sendMessage("§7L'infection est terminé§c "+toIzanami.getName()+"§7 rejoint maintenant le camp§d Jubi");
									getPlayerRoles(toIzanami).setOldTeamList(getPlayerRoles(toIzanami).getTeam());
									getPlayerRoles(toIzanami).setTeam(getPlayerRoles(owner).getTeam());
									toIzanami.resetTitle();
									b = true;
									toIzanami.sendTitle("§cVous êtes sous l'effet de l'§lIzanami", "§cVous êtes maintenant dans le camp§d Jubi");
									toIzanami.sendMessage("§7Voici l'identité de vos coéquipier§d Obito: "+(getPlayerFromRole(Roles.Obito) != null ? getPlayerFromRole(Roles.Obito).getName() : "§cMort")+"§f et§d Madara: "+(getPlayerFromRole(Roles.Madara) != null ? getPlayerFromRole(Roles.Madara).getName() : "§cMort"));
									System.out.println("Obito izanami finished");
									cancel();
								}
							}
						}
					}
				}
			}
		}
	}
     @Override
	public String[] Desc() {
    	 KnowRole(owner, Roles.Madara, 20);
		return new String[] {
				AllDesc.bar,
			    AllDesc.role+"Obito",
			    "§fVotre objectif est de gagner avec le camp "+type.getTeam().getColor()+type.getTeam().name(),
				"",
				AllDesc.items,
				"",
				AllDesc.point+KamuiItem().getItemMeta().getDisplayName()+"§f:  Vous offre le choix entre:",
				"§7     →§d Arimasu§f: Vous permet de vous téléportez dans la dimension \"§dKamui\"§r",
				"§7     →§d Sonohaka§f: En choisissant un joueur, vous permet de le téléportez dans la dimension \"§dKamui\"§r",
				"",
				AllDesc.point+GenjutsuItem().getItemMeta().getDisplayName()+"§f: Vous offre le choix entre:",
				"§7     →§cTsukuyomi§f: Vous permet pendant 8s d'empêcher tout joueur étant à moins de 30 blocs de vous de bouger",
				"§7     →§cAttaque§f: En choisissant un joueur, vous permet de vous téléportez derrière lui",
				"§7     →§dIzanami§f: En choisissant un joueur, vous permet de l'infecter via des missions",
				"",
				AllDesc.point+ninjutsuSpatioTemporelItem().getItemMeta().getDisplayName()+"§f: Vous permet de vous rendre invincible et invisible au yeux de tous pendant 1m, des particules de feu apparaitront à vos pied",
				"",
				AllDesc.point+"Vous connaissez le pseudo du joueur possédant le rôle§d Madara§r et pouvez parler avec ce dernier en mettant un§c !§r avant votre message.",
				"",
				AllDesc.point+"§dTraqueur§f: Permet via un menu de traquer le Biju voulu",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns izanagi§f: Vous permet de vous remettre full vie et de vous donnez§e 5 pommes d'or§f, cependant vous fais perdre l'accès au§c Susano§f et vous retire§c 1"+AllDesc.coeur+" permanent",
				"",
				AllDesc.point+"§6/ns yameru <joueur>§f: Permet en ciblant un joueur, de s'il est dans le§d Kamui§f de le téléporter dans le monde normal",
				"",
				AllDesc.capacite,
				"",
				AllDesc.point+"Lorsque vous tuez un joueur vous régénérez 3"+AllDesc.coeur,
				"",
				AllDesc.point+"Vous possédez la nature de Chakra : "+getChakras().getShowedName(),
				AllDesc.bar
		};
	    
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != null) {
				Heal(owner, 6.0);
			}
		}
	}
	private boolean izanagi = false;
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("izanagi")) {
			if (!izanagi) {
				giveItem(owner, false,new ItemStack(Material.GOLDEN_APPLE,5));
				setMaxHealth(getMaxHealth()-2.0);
				owner.setMaxHealth(getMaxHealth());
				owner.sendMessage("§cVous venez d'utiliser votre izanagi vous ne pourrez donc plus utiliser de susano");
				izanagi = true;
				owner.setHealth(getMaxHealth());
			} else {
				owner.sendMessage("§cVous avez déjà utiliser votre izanagi vous ne pouvez donc plus l'utiliser");
			}
		}
		if (args[0].equalsIgnoreCase("yameru")) {
			if (args.length == 2) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					owner.sendMessage("§CVeuiller cibler un joueur éxistant !");
					return;
				}else {
					if (target.getWorld().equals(Bukkit.getWorld("Kamui"))) {
						KamuiUtils.end(target);
						return;
					}
				}
			}
		}
	}
	private ItemStack GenjutsuItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu").setLore("§7Permet d'utiliser les pouvoirs de Obito").toItemStack();
	}
	private ItemStack ninjutsuSpatioTemporelItem(){
		return new ItemBuilder(Material.NETHER_STAR).setName("§cNinjutsu Spatio-Temporel").setLore("§7Vous rend invisible avec votre armure").toItemStack();
	}
	private ItemStack KamuiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dKamui").setLore("§7Permet de vous téléportez ou de téléporter un joueur dans le Kamui").toItemStack();
	}
	private ItemStack TraqueurItem() {
		return new ItemBuilder(Material.COMPASS)
				.setName("§dTraqueur")
				.setLore("§7Permet de traquer les bijus")
				.toItemStack();
	}
	public ItemStack[] getItems() {
		return new ItemStack[] {
				GenjutsuItem(),
				ninjutsuSpatioTemporelItem(),
				KamuiItem(),
				TraqueurItem()
		};
	}
	private int cdTsukuyomi = 0;
	private int cdAttaque = 0;
	private boolean Izanami = false;
	@Override
	public void resetCooldown() {
		cdArimasu = 0;
		cdSonohoka = 0;
		cdAttaque = 0;
		cdNinjutsu = 0;
		cdSusano = 0;
		cdTsukuyomi = 0;
	}
	private Inventory GenjutsuInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cGenjutsu");
		inv.setItem(0,new ItemBuilder(Material.ARMOR_STAND).setName("§cTsukuyomi").setLore("§7Cooldown§l "+cd(cdTsukuyomi),"§7Permet d'immobiliser les joueurs autour de vous").toItemStack());
		inv.setItem(4,new ItemBuilder(Material.IRON_SWORD).setName("§cAttaque").setLore("§7Cooldown§l "+cd(cdAttaque),"§7Vous permez de vous téléportez sur un joueur au alentour").toItemStack());
		if (!Izanami) {
			inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous permez d'infecter quelqu'un").toItemStack());
		} else {
			if (Izanami) {
				if (b) {
					return inv;
				} else {
					inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous êtes en cours d'infection").toItemStack());
				}
			}
		}
		return inv;
	}
	private int cdSonohoka = 0;
    private int cdArimasu = 0;
    private int cdSusano = -1;
	private Inventory KamuiInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cKamui");
		inv.setItem(3, new ItemBuilder(Material.EYE_OF_ENDER).setName("§dArimasu").setLore("§7Cooldown "+cd(cdArimasu),
				"§7Permet de vous téléportez dans le Kamui").toItemStack());
		inv.setItem(7, new ItemBuilder(Material.ENDER_PEARL).setName("§dSonohaka").setLore("§7Cooldown "+cd(cdSonohoka),
				"§7Permet de téléporter un joueur dans le Kamui").toItemStack());
		return inv;
	}
	private void useTsukuyomi() {
		owner.sendMessage("Vous venez d'utiliser votre Tsukuyomi");
		for (Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
			Tsukuyomi.add(p);
			p.sendMessage("§7Vous êtes maintenant sous l'effet du tsukuyomi");
			p.setAllowFlight(true);
			p.setFlying(true);
		}
		new BukkitRunnable() {
			int s = 0;
			@Override
			public void run() {
				if (s != 8)s++;
				if (s == 8) {
					owner.sendMessage("Votre Tsukuyomi ne fais plus effet");
					Tsukuyomi.forEach(p -> p.setFlying(false));
					Tsukuyomi.forEach(p -> p.setAllowFlight(false));
					Tsukuyomi.clear();
					cancel();
				}
				if (s > 8) {
					System.out.println("runnable must be cancelled ???");
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}
	private void openAttaqueInventory() {
		Inventory toOpen = Bukkit.createInventory(owner, 54, "§cAttaque");
		for (int i = 0; i <= 8; i++) {
			if (i == 4) {
				toOpen.setItem(i, GUIItems.getSelectBackMenu());
			} else {
				toOpen.setItem(i, GUIItems.getOrangeStainedGlassPane());
			}
		}
		for (Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
			toOpen.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setName(p.getDisplayName()).toItemStack());
		}
		owner.openInventory(toOpen);
	}
	private Player toIzanami = null;
	private void openIzanamiInventory() {
		owner.closeInventory();
		if (toIzanami == null) {
			Inventory toOpen = Bukkit.createInventory(owner, 54, "§cIzanami");
			for (int i = 0; i <= 8; i++) {
				if (i == 4) {
					toOpen.setItem(i, GUIItems.getSelectBackMenu());
				} else {
					toOpen.setItem(i, GUIItems.getOrangeStainedGlassPane());
				}
			}
			
			for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
				toOpen.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setName(p.getDisplayName()).toItemStack());
			}
			owner.openInventory(toOpen);
		} else {
			Inventory inv = Bukkit.createInventory(owner, 9, "§cIzanami");
			String l1 = "§eVotre missions \"§f"+missionsO.get(1).getMission()+"\"§e est "+(missionsO.get(1).getifFinished() ? "§aTerminé" : "§cInachevé§f"+(missionsO.get(1) == MissionObito.Fraper ? " (§c"+fraper+"§f/§615§f)" : (missionsO.get(1) == MissionObito.Rester ? " (§c"+StringUtils.secondsTowardsBeautiful(timepassedp)+"§f/§65m§f)" : missionsO.get(1) == MissionObito.Taper ? " (§c"+taper+"§f/§615§f)" :"")));
			String l2 = "§eVotre missions \"§f"+missionsO.get(2).getMission()+"\"§e est "+(missionsO.get(2).getifFinished() ? "§aTerminé" : "§cInachevé§f"+(missionsO.get(2) == MissionObito.Fraper ? " (§c"+fraper+"§f/§615§f)" : (missionsO.get(2) == MissionObito.Rester ? " (§c"+StringUtils.secondsTowardsBeautiful(timepassedp)+"§f/§65m§f)" : (missionsO.get(2) == MissionObito.Taper ? " (§c"+taper+"§f/§615§f)" :""))));
			inv.setItem(3, new ItemBuilder(GlobalUtils.getPlayerHead(owner.getUniqueId())).setName("§eVos objectifs").setLore("",l1,l2).toItemStack());
			String l3 = "§eSa mission \"§f"+missionI.get(1).getMission()+"\"§e est "+(missionI.get(1).getifFinished() ? "§aTerminé" : "§cInachevé§f"+(missionI.get(1) == MissionInfecte.Gap ? "(§c"+gapEated+"§f/§65§f)" : ""));
			inv.setItem(5, new ItemBuilder(Material.NETHER_STAR).setName("§eSon objectif").setLore("",l3).toItemStack());
			inv.setItem(8, GUIItems.getSelectBackMenu());
			owner.openInventory(inv);
		}
	}
	private int timesansbouger = 0;
	private Location sansbougerLoc = null;
	private int taper = 0;
	boolean b = false;
	private int fraper = 0;
	private int timepassedp = 0;
	private enum MissionObito {
		Taper(0, "Tapée la cible 15x", false),
		Lave(1, "Mettre de la§6 lave§7 sous la cible", false),
		Fraper(2, "Être frappé par l'épée de la cible 15x", false),
		Gap(3, "Donner une pomme d'or à la cible", false),
		Rester(4, "Rester proche de la cible pendant 5m", false);
		
		private String mission;
		private int nmb;
		private boolean finished;
		private MissionObito(int nmb, String string, boolean fini) {
			this.nmb=nmb;
			this.mission = string;
			this.finished = fini;
		}
		public String getMission() {
			return mission;
		}
		public int getNMB() {
			return nmb;
		}
		public boolean getifFinished() {
			return finished;
		}
		public void setFinished(boolean b) {
			this.finished = b;
		}
	}
	private enum MissionInfecte {
		Tuer(0, "Tuer un joueur", false),
		Gap(1, "Manger 5§e pommes d'or", false),
		Bouger(2, "Ne pas bouger pendant 5s (§nsans§c Tsukuyomi§f)", false);
		private String mission;
		private int nmb;
		private boolean finished;
		private MissionInfecte(int nmb, String string, boolean fini) {
			this.nmb=nmb;
			this.mission = string;
			this.finished = fini;
		}
		public String getMission() {
			return mission;
		}
		public int getNMB() {
			return nmb;
		}
		public boolean getifFinished() {
			return finished;
		}
		public void setFinished(boolean b) {
			this.finished = b;
		}
	}
	private HashMap<Integer, MissionInfecte> missionI = new HashMap<>();
	private Location lavaLoc = null;
	private Bijus traqued = null;
	@Override
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (player.getUniqueId() == owner.getUniqueId()) {
				if (event.getItem().isSimilar(TraqueurItem())) {
					if (traqued != null) {
						if (traqued.getBiju().getMaster() == null) {
							Location loc = traqued.getBiju().getSpawn();
		                    owner.sendMessage(CC.prefix("&a" + loc.getBlockX() + "&f, &a" + loc.getBlockY() + "&f, &a" + loc.getBlockZ()));
		                    owner.setCompassTarget(loc);
		                    owner.sendMessage(CC.translate("&a"+StringUtils.secondsTowardsBeautiful(traqued.getBiju().getTimeSpawn())));
						} else {
							owner.sendMessage(CC.prefix("&fVous traquez désormais " + Bukkit.getPlayer(traqued.getBiju().getMaster()).getName() + " &fqui se situe en :"));
		                    Location loc = Bukkit.getPlayer(traqued.getBiju().getMaster()).getLocation();
		                    owner.sendMessage(CC.prefix("&a" + loc.getBlockX() + "&f, &a" + loc.getBlockY() + "&f, &a" + loc.getBlockZ()));
		                    owner.setCompassTarget(loc);
		                    owner.sendMessage(CC.translate("&a"+StringUtils.secondsTowardsBeautiful(traqued.getBiju().getTimeSpawn())));
						}
					}
				}
			}
		}
	}
	@Override
	public boolean onBucketEmpty(Material bucket, Block block, GameState gameState, Player player) {
		if (bucket == Material.LAVA_BUCKET) {
				this.lavaLoc = block.getLocation();
		}
		return false;
	}
	@Override
	public void onBucketFill(PlayerBucketFillEvent e, Material bucket) {
		Block block = e.getBlockClicked().getRelative(e.getBlockFace());
		if (lavaLoc != null) {
			if (block.getLocation() == lavaLoc) {
				lavaLoc = null;
			}
		}
	}
	private Map<Integer, MissionObito> missionsO = new HashMap<>();
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(owner, PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		givePotionEffet(owner, PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (cdSusano >=0) {
			cdSusano--;
			if (cdSusano == 0) {
				owner.sendMessage("§7Votre§c Susano§7 est à nouveau utilisable !");
			}
		}
		if (cdNinjutsu >= 0) {
			cdNinjutsu -= 1;
		}
		if (cdNinjutsu == 0) {
			owner.sendMessage("§cVotre "+ninjutsuSpatioTemporelItem().getItemMeta().getDisplayName()+"§c est de nouveau utilisable");
		}
		if (cdTsukuyomi >= 0) {
			cdTsukuyomi -= 1;
		}
		if (cdTsukuyomi == 0) {
			owner.sendMessage("§cVotre Tsukuyomi est de nouveau utilisable");
		}
		if (cdAttaque >= 0) {
			cdAttaque -= 1;
		}
		if (cdAttaque == 0) {
			owner.sendMessage("§cVotre Attaque est de nouveau utilisable");
		}
		if (cdArimasu >= 0) {
			cdArimasu -= 1;
		}
		if (cdArimasu == 0) {
			owner.sendMessage("§cVous pouvez de nouveau vous téléportez dans le kamui");
		}
		if (cdSonohoka >= 0) {
			cdSonohoka -= 1;
		}
		if (cdSonohoka == 0) {
			owner.sendMessage("§cVous pouvez de nouveau téléporter quelqu'un dans le kamui");
		}
	}
	private void openSonohakaInventory(){
		Inventory inv = Bukkit.createInventory(owner, 54, "§cKamui§7 ->§d Sonohaka");
		for (int i = 0; i <= 8; i++) {
			if (i == 4) {
				inv.setItem(i, GUIItems.getSelectBackMenu());
			} else {
				inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
			}
		}
		for (Player p : Loc.getNearbyPlayersExcept(owner, 30)) {
			inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setName(p.getDisplayName()).toItemStack());
		}
		owner.openInventory(inv);
	}
	private int gapEated = 0;
	private boolean toIzanaKillqql = false;
	@Override
	public void onALLPlayerEat(PlayerItemConsumeEvent e, ItemStack item, Player eater) {
		if (toIzanami != null) {
			if (missionI.get(1).equals(MissionInfecte.Gap) && !missionI.get(1).getifFinished() && eater.getUniqueId() == toIzanami.getUniqueId()) {
				gapEated++;
				if (gapEated == 5) {
					owner.sendMessage("§7Votre victime à accomplie sa mission !");
					missionI.get(1).setFinished(true);
				}
			}
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (toIzanami != null) {
			if (killer.getUniqueId() == toIzanami.getUniqueId() && missionI.get(1) == MissionInfecte.Tuer && !toIzanaKillqql) {
				toIzanaKillqql = true;
				owner.sendMessage("§7Votre victime à accomplie sa mission !");
				missionI.get(1).setFinished(true);
			}
		}
	}
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() == owner.getUniqueId()) {
			if (inv != null && item != null && item.getType() != Material.AIR) {
				if (inv.getTitle().equalsIgnoreCase("§7Traqueur de bijus")) {
					event.setCancelled(true);
					for (Bijus bijus : Bijus.values()) {
						if (item.isSimilar(bijus.getBiju().getItem())) {
							this.traqued = bijus;
							owner.sendMessage("§7Vous traquez maintenant "+bijus.getBiju().getName());
							if (bijus.getBiju().getMaster() == null) {
								Location loc = bijus.getBiju().getSpawn();
			                    owner.sendMessage(CC.prefix("&a" + loc.getBlockX() + "&f, &a" + loc.getBlockY() + "&f, &a" + loc.getBlockZ()));
			                    owner.setCompassTarget(loc);
			                    owner.sendMessage(CC.translate("&a"+StringUtils.secondsTowardsBeautiful(traqued.getBiju().getTimeSpawn())));
							} else {
								owner.sendMessage(CC.prefix("&fVous traquez désormais " + Bukkit.getPlayer(bijus.getBiju().getMaster()).getName() + " &fqui se situe en :"));
			                    Location loc = Bukkit.getPlayer(bijus.getBiju().getMaster()).getLocation();
			                    owner.sendMessage(CC.prefix("&a" + loc.getBlockX() + "&f, &a" + loc.getBlockY() + "&f, &a" + loc.getBlockZ()));
			                    owner.setCompassTarget(loc);
			                    owner.sendMessage(CC.translate("&a"+StringUtils.secondsTowardsBeautiful(traqued.getBiju().getTimeSpawn())));
							}
							break;
						}
					}
				}
				if (inv.getTitle().equals("§cKamui§7 ->§d Sonohaka")) {
					if (item != null && item.getType() != Material.AIR) {
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							owner.openInventory(KamuiInventory());
							event.setCancelled(true);
						}
						if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
							Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
							if (clicked != null) {
								clicker.closeInventory();
								if (Loc.getNearbyPlayersExcept(owner, 30).contains(clicked)) {
									clicked.closeInventory();
									clicked.sendMessage("§7Vous avez été téléportez dans le§d Kamui !");
									KamuiUtils.start(clicked.getLocation(), Users.cibleObito, clicked, true);
									new BukkitRunnable() {
										int i = 0;
										@Override
										public void run() {
											i++;
											if (i == 60*3) {
												KamuiUtils.end(clicked);
											}else {
												sendCustomActionBar(clicked, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful((60*3)-i));
											}
											if (clicked.getWorld() != Bukkit.getWorld("Kamui")) {
												cancel();
												return;
											}
										}
									}.runTaskTimer(Main.getInstance(), 0, 20);
									cdSonohoka = 60*15;
									event.setCancelled(true);
								}
							}
						}
					}
				}
				if (inv.getTitle().equalsIgnoreCase("§cKamui")) {
					if (item.getType() == Material.AIR)return;
					if (Bukkit.getWorld("Kamui") == null) {
						clicker.sendMessage("§7La map Kamui n'existe pas");
						clicker.closeInventory();
						event.setCancelled(true);
						return;
					}
					if (event.getSlot() == 3) {
						owner.closeInventory();
						event.setCancelled(true);
						if (cdArimasu <= 0) {
								cdArimasu = 60*10;
								KamuiUtils.start(owner.getLocation(), Users.obito, owner, true);
								new BukkitRunnable() {
									int i = 0;
									@Override
									public void run() {
										if (Bukkit.getWorld("Kamui") == null) {
											cancel();
											return;
										}
										if (gameState.getServerState() != ServerStates.InGame) {
											cancel();
											return;
										}
										i++;
										if (owner.getWorld().getName() != "Kamui") {
											cancel();
											return;
										}
										if (i == 60*3) {
											owner.sendMessage("§7Vous sortez du§d Kamui§7 !");
											KamuiUtils.end(owner);
											cancel();
											return;
										} else {
											sendCustomActionBar(owner, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful((60*3)-i));
										}
									}
								}.runTaskTimer(Main.getInstance(), 0, 20);
								return;
						} else {
							sendCooldown(clicker, cdArimasu);
							return;
						}
					}
					if (event.getSlot() == 7) {
						event.setCancelled(true);
						owner.closeInventory();
						if (cdSonohoka <= 0) {
								openSonohakaInventory();
								return;
						} else {
							sendCooldown(clicker, cdSonohoka);
							return;
						}
					}
				}
				if (inv.getTitle().equalsIgnoreCase("§cGenjutsu")) {
					event.setCancelled(true);
					if (event.getSlot()== 0) {
						if (cdTsukuyomi <= 0) {
							useTsukuyomi();
							cdTsukuyomi = 60*5;
						} else {
							sendCooldown(owner, cdTsukuyomi);
						}
					}
					if (event.getSlot() == 4) {
						if (cdAttaque <= 0) {
							owner.closeInventory();
							openAttaqueInventory();
						} else {
							sendCooldown(owner, cdAttaque);
						}
					}
					if (!b) {
						if (event.getSlot()== 8) {
							openIzanamiInventory();
							event.setCancelled(true);
						}
					}
				}
				if (inv.getTitle().equalsIgnoreCase("§cAttaque")) {
					if (clicker == owner) {
						event.setCancelled(true);
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							owner.openInventory(GenjutsuInventory());
							return;
						}
						Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
						if (clicked == null) {
							return;
						}
						if (Loc.getNearbyPlayersExcept(owner, 30).contains(clicked)) {
							clicker.sendMessage("§cAttaque !");
							clicker.closeInventory();
							Loc.teleportBehindPlayer(clicker, clicked);
							clicked.sendMessage("§7Vous ressentez une présence derrière vous...");
							cdAttaque = 60*5;
							return;
						}
					}
				}
				if (inv.getTitle().equalsIgnoreCase("§cIzanami")) {
					if (clicker.getUniqueId() == owner.getUniqueId()) {
						event.setCancelled(true);
						if (toIzanami == null) {
							if (item.isSimilar(GUIItems.getSelectBackMenu())) {
								owner.openInventory(GenjutsuInventory());
								return;
							}
							Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
							if (clicked == null) {
								return;
							}
							if (Loc.getNearbyPlayers(owner, 30).contains(clicked)) {
								Izanami = true;
								toIzanami = clicked;
								clicker.sendMessage("§cL'izanami§7 commence...");
								clicker.closeInventory();
								missionsO.clear();
								int rdm1 = RandomUtils.getRandomInt(0, 4);
								int rdm2 = rdm1;
								while (rdm2 == rdm1) {
									rdm2 = RandomUtils.getRandomInt(0, 4);
								}
								int rdm3 = RandomUtils.getRandomInt(0, 2);
								for (MissionObito mo : MissionObito.values()) {
										if (mo.getNMB() == rdm1) {
											missionsO.put(1, mo);
										}
										if (mo.getNMB() == rdm2) {
											missionsO.put(2, mo);
										}
								}
								for (MissionInfecte mi : MissionInfecte.values()) {
									if (mi.getNMB() == rdm3) {
										missionI.put(1, mi);
									}
								}
								clicker.sendMessage(new String[] {
										"",
										"§7Votre première mission est \"§n"+missionsO.get(1).getMission()+"§7\",",
										"§7Votre deuxième mission est \"§n"+missionsO.get(2).getMission()+"§7\""
										,"",
										"§7La mission de \"§l"+clicked.getDisplayName()+"§7\"§7 est \"§n"+missionI.get(1).getMission()+"§7\"",
										""
								});
								new ObitoRunnable().runTaskTimer(Main.getInstance(), 0, 20);
								return;
							}
						}
					}
				}
			}
		}
	}
	private Map<UUID, ItemStack> drop = new HashMap<>();
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player Madara = getPlayerFromRole(Roles.Madara);
				owner.sendMessage(CC.translate("&dObito: "+msg.substring(1)));
				if (Madara != null) {
					Madara.sendMessage(CC.translate("&dObito: "+msg.substring(1)));
				}
			}
		}
	}
	@Override
	public void onALLPlayerDropItem(PlayerDropItemEvent e, Player dropper, ItemStack item) {
		if (dropper.getUniqueId() == owner.getUniqueId()) {
			if (missionsO.get(1) == MissionObito.Gap || missionsO.get(2) == MissionObito.Gap) {
				if (item.getType() == Material.GOLDEN_APPLE) {
					drop.put(owner.getUniqueId(), item);
				}
			}
		}
	}
	@Override
	public void onALLPlayerRecupItem(PlayerPickupItemEvent e, ItemStack s) {
		if (toIzanami != null) {
				if (missionsO.get(1) == MissionObito.Gap || missionsO.get(2) == MissionObito.Gap) {
					if (e.getPlayer() == toIzanami && !MissionObito.Gap.getifFinished()) {
						if (!drop.containsKey(owner.getUniqueId())) {
							return;
						}
						if (drop.get(owner.getUniqueId()) == null) {
							return;
						}
						if (drop.get(owner.getUniqueId()).isSimilar(s)) {
							owner.sendMessage("§7Vous venez d'accomplir l'une de vos mission");
							MissionObito.Gap.setFinished(true);
							drop.remove(owner.getUniqueId(), s);
						}
					}
				}
		}
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (!victim.canSee(owner)) {
				event.setCancelled(true);
				return;
			}
		}
		if (victim == owner && cdNinjutsu >= 60*7) {
			event.setCancelled(true);
			return;
		}
		if (Tsukuyomi != null && Tsukuyomi.contains(victim)) {
			event.setDamage(0);
			event.setCancelled(true);
		}
		if (toIzanami != null) {
			if (toIzanami.getUniqueId() == victim.getUniqueId()) {
				if (entity.getUniqueId() == owner.getUniqueId()) {
					if (missionsO.get(1) ==MissionObito.Taper || missionsO.get(2) == MissionObito.Taper) {
						if (!MissionObito.Taper.getifFinished()) {
								taper++;
								if (taper == 15) {
									owner.sendMessage("§7Vous avez terminé l'une de vos mission !");
									MissionObito.Taper.setFinished(true);
								}
						}
					}
				}
			}
			if (owner.getUniqueId() == victim.getUniqueId()) {
				if (entity.getUniqueId() == toIzanami.getUniqueId()) {
					if (missionsO.get(1) ==MissionObito.Fraper || missionsO.get(2) == MissionObito.Fraper) {
						if (!MissionObito.Fraper.getifFinished()) {
								fraper++;
								if (fraper == 15) {
									owner.sendMessage("§7Vous avez terminé l'une de vos mission !");
									MissionObito.Fraper.setFinished(true);
								}
						}
					}
				}
			}
		}
	}
	@Override
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
		if (Tsukuyomi.contains(moover)) {
			e.setCancelled(true);
			moover.teleport(e.getFrom());
		}
		if (toIzanami != null) {
			if (!Tsukuyomi.contains(toIzanami)) {
				if (moover == toIzanami) {
					if (missionI.get(1) == MissionInfecte.Bouger && !missionI.get(1).getifFinished()) {
						Location loc = moover.getLocation().clone();
						loc.setPitch(0);
						loc.setYaw(0);
						this.sansbougerLoc = loc;
						timesansbouger = 0;
					}
				}
			}
		}
		if (lavaLoc != null && toIzanami != null) {
			if (missionsO.get(1) == MissionObito.Lave || missionsO.get(2) == MissionObito.Lave) {
				if (toIzanami.getUniqueId() == moover.getUniqueId()) {
					if (lavaLoc.getWorld() != moover.getWorld()) {
						return;
					}
					if (moover.getLocation().distance(lavaLoc) < 1 && !MissionObito.Lave.getifFinished() && lavaLoc.getBlock().getType().name().contains("LAVA")) {
						MissionObito.Lave.setFinished(true);
						owner.sendMessage("§7Une mission viens de s'accomplir");
					}
				}
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(TraqueurItem())) {
			if (!gameState.BijusEnable) {
				owner.sendMessage("§7Les Bijus sont désactivé...");
				return true;
			}
			Inventory inv = Bukkit.createInventory(owner, 9, "§7Traqueur de bijus");
			for (Bijus b : Bijus.values()) {
				if (b.isEnable()) {
					inv.addItem(b.getBiju().getItem());
				}
			}
			owner.openInventory(inv);
			return true;
		}
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase("§cSusano")) {
					if (!izanagi) {
						if (cdSusano <= 0) {
							cdSusano = 60*20;
							owner.sendMessage("§7Activation du§c Susano");
							setResi(20);
							new BukkitRunnable() {
								int i = 0;
								@Override
								public void run() {
									i++;
									givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
									if (i == 60*5) {
										owner.sendMessage("§7Désactivation du§c Susano");
										setResi(0);
										cancel();
									}
								}
							}.runTaskTimer(Main.getInstance(), 0, 20);
							return true;
						} else {
							sendCooldown(owner, cdSusano);
							return true;
						}
					}
				}
			}
		}
		if (item.isSimilar(ninjutsuSpatioTemporelItem())) {
			if (cdNinjutsu <= 0) {
				for (Player p : getIGPlayers()) {
					if (p.getUniqueId() != owner.getUniqueId()) {
						p.hidePlayer(owner);
					}
				}
				new BukkitRunnable() {
					int i = 0; 
					@Override
					public void run() {
						i++;
						MathUtil.sendParticle(EnumParticle.FLAME, owner.getLocation().clone());
						if (i == 60) {
							owner.sendMessage("§7Vous n'êtes plus invisible !");
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (!p.canSee(owner)) {
									p.showPlayer(owner);
								}
							}
							cancel();
							return;
						}
						if (i>= 60) {
							cancel();
							return;
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
				cdNinjutsu = 60*8;
				return true;
			} else {
				sendCooldown(owner, cdNinjutsu);
				return true;
			}
			
		}
		if (item.isSimilar(GenjutsuItem())) {
			owner.openInventory(GenjutsuInventory());
			return true;
		}
		if (item.isSimilar(KamuiItem())) {
			owner.openInventory(KamuiInventory());
			return true;
		}
		return false;
	}
	private int cdNinjutsu = 0;
}