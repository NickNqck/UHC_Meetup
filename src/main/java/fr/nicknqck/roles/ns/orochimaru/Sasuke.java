package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.power.Izanami;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sasuke extends OrochimaruRoles {

	private Izanami izanami;
	private boolean mortOrochimaru = false;
	private int SusanoCD = 0;
	private boolean hasIzanagi = false;
	private int BowCD = -1;
	private int amaterasuCD = 0;
	private int cdTsukuyomi = 0;
	private int cdAttaque = 0;
	private boolean hasIzanami = false;
	private boolean killItachi = false;
	private boolean infectFinish = false;
	private final List<Player> Tsukuyomi = new ArrayList<>();
	
	public Sasuke(UUID player) {
		super(player);
		setChakraType(Chakras.KATON);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à sa mort");
			}
		}, 20*10);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (!gameState.attributedRole.contains(Roles.Itachi)) {
				onItachiKill(false);
				owner.sendMessage("§cItachi§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à son kill");
			}
		}, 20*5);
		setCanBeHokage(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Sasuke;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Orochimaru, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Sasuke",
				AllDesc.objectifsolo+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§e Speed 1§f et§6 Fire Résistance 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§c§lSusano§f: Permet d'obtenir l'effet§9 Résistance 1§f pendant 5minutes, de plus, vous donne accès à l'§cArc§f du§c§l Susano",
				"",
				AllDesc.point+SusanoBowItem().getItemMeta().getDisplayName()+"§f: Arc power 7 qui peut tirer une flèche que toute les 10 secondes",
				"",
				AllDesc.point+"§cAmaterasu§f: Permet en visant un joueur, d'infliger des dégats à ce dernier tout-en l'enflamment ",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns izanagi§f: Vous permet en échange d'§c1"+AllDesc.coeur+"§f permanent, de vous remettre full vie et de vous donnez§e 5 pommes d'or",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité d'§5Orochimaru",
				"A la mort d'§5Orochimaru§f vous gagnez§c 3"+AllDesc.coeur+" ainsi que l'effet§c Force 1§f de manière permanente, également, vous obtiendrez un traqueur vers§c Itachi",
				"Si vous parvenez a tué§c Itachi§f vous gagnerez§c 2"+AllDesc.coeur+" permanent ainsi que l'item§c Genjutsu",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer.getUniqueId() == owner.getUniqueId()) {
			if (getListPlayerFromRole(Roles.Itachi).contains(victim)) {
				if (!killItachi) {
					onItachiKill(true);
				}
			}
		}
	}
	private void onItachiKill(boolean msg) {
		killItachi = true;
		if (msg) {
			owner.sendMessage("§7Vous avez réussi à tuer ce traitre du clan§4§l Uchiwa§7 de§c Itachi§7 vous obtenez donc l'accès au§c Genjutsu");
		}
		giveItem(owner, true, GenjutsuItem());
		giveHealedHeartatInt(2);
	}
	private ItemStack GenjutsuItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§cGenjutsu")
				.setLore("§7Hérité de§c Itachi§7 cette oeil permet à§e Sasuke§7 d'utiliser des§c Genjutsus")
				.toItemStack();
	}
	
	@Override
	public void resetCooldown() {
		cdAttaque = 0;
		cdTsukuyomi = 0;
		SusanoCD = 0;
		BowCD = 0;
	}
	private Inventory GenjutsuInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cGenjutsu");
		inv.setItem(0,new ItemBuilder(Material.ARMOR_STAND).setName("§cTsukuyomi").setLore("§7Cooldown§l "+cd(cdTsukuyomi),"§7Permet d'immobiliser les joueurs autour de vous").toItemStack());
		inv.setItem(4,new ItemBuilder(Material.IRON_SWORD).setName("§cAttaque").setLore("§7Cooldown§l "+cd(cdAttaque),"§7Vous permez de vous téléportez sur un joueur au alentour").toItemStack());
		if (!hasIzanami) {
			inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous permez d'infecter quelqu'un").toItemStack());
		} else {
            if (infectFinish) {
                return inv;
            } else {
                inv.setItem(8, new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous êtes en cours d'infection").toItemStack());
            }
        }
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
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() == owner.getUniqueId()) {
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
				if (!infectFinish) {
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
					if (izanami == null) {
						if (!item.hasItemMeta())return;
						if (!item.getItemMeta().hasDisplayName())return;
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							owner.openInventory(GenjutsuInventory());
							return;
						}
						Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
						if (clicked == null) {
							return;
						}
						if (Loc.getNearbyPlayers(owner, 30).contains(clicked)) {
							hasIzanami = true;
							clicker.sendMessage("§cL'izanami§7 commence...");
							this.izanami = new Izanami(clicker.getUniqueId(), clicked.getUniqueId());
							izanami.start(getTeamColor());
							clicker.sendMessage(izanami.getStringsMission());
							clicker.closeInventory();
							new SasukeRunnable(this).runTaskTimer(Main.getInstance(), 0, 20);
                        }
					}
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	private void openIzanamiInventory() {
		owner.closeInventory();
		if (this.izanami == null) {
			Inventory toOpen = Bukkit.createInventory(owner, 54, "§cIzanami");
			for (int i = 0; i <= 8; i++) {
				if (i == 4) {
					toOpen.setItem(i, GUIItems.getSelectBackMenu());
				} else {
					toOpen.setItem(i, GUIItems.getOrangeStainedGlassPane());
				}
			}
			
			for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
				toOpen.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((3)).setName(p.getDisplayName()).toItemStack());
			}
			owner.openInventory(toOpen);
		} else {
			Inventory inv = Bukkit.createInventory(owner, 9, "§cIzanami");
			inv.setItem(2, this.izanami.getResultUserMission());
			inv.setItem(4, this.izanami.getResultVictimMission());
			inv.setItem(8, GUIItems.getSelectBackMenu());
			owner.openInventory(inv);
		}
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				SusanoItem(),
				amaterasuItem(),
		};
	}
	private ItemStack SusanoItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§c§lSusano")
				.setLore("§7Vous donne "+AllDesc.Resi+"§7 1 pendant 5m")
				.toItemStack();
	}
	private ItemStack SusanoBowItem() {
		return new ItemBuilder(Material.BOW)
				.setName("§cArc§f du§c§l Susano")
				.setLore("§7L'arc du§c Susano§7 de§e Sasuke")
				.addEnchant(Enchantment.ARROW_DAMAGE, 7)
				.setUnbreakable(true)
				.toItemStack();
	}
	private ItemStack amaterasuItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§cAmaterasu")
				.setLore("§7Permet en visant un joueur de le mettre en feu")
				.toItemStack();
	}
	private void onOrochimaruDeath(boolean msg) {
		mortOrochimaru = true;
		setMaxHealth(getMaxHealth()+6.0);
		owner.setMaxHealth(getMaxHealth());
		if (msg) {
			owner.sendMessage("§7Enfin...§5 Orochimaru§7 est§c mort§7, vous pouvez enfin exprimez votre plein potentielle");
		}
		owner.setHealth(owner.getHealth()+6.0);
		setTeam(TeamList.Sasuke);
		if (gameState.attributedRole.contains(Roles.Itachi) && !killItachi) {
			new BukkitRunnable() {
				int i = 0;
				final Player itachi = getPlayerFromRole(Roles.Itachi);
				@Override
				public void run() {
					i++;
					if (itachi == null) {
						cancel();
						return;
					}
					if (!itachi.isOnline()) {
						cancel();
						return;
					}
					if (gameState.getServerState() != ServerStates.InGame) {
						cancel();
					}
					if (i == 60*5) {
						cancel();
					}
					if (itachi.getGameMode() != GameMode.SURVIVAL) {
						cancel();
					}
					if (i < 60*5) {
						sendCustomActionBar(owner, "§cItachi: "+Loc.getDirectionMate(owner, itachi, false));
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (gameState.attributedRole.contains(Roles.Orochimaru)) {
			if (getListPlayerFromRole(Roles.Orochimaru).contains(player) && !mortOrochimaru) {
				onOrochimaruDeath(true);
			}
		}
	}
	
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (Tsukuyomi != null && Tsukuyomi.contains(victim)) {
			event.setDamage(0);
			event.setCancelled(true);
		}
		if (entity instanceof Arrow) {
			Arrow arrow = (Arrow) entity;
			if (victim != null) {
				if (arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();
					if (shooter == owner) {
						if (arrow.hasMetadata("Susano BOW")) {
							if (BowCD <= 0) {
								victim.setFireTicks(victim.getFireTicks()+100);
								BowCD = 10;
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onProjectileLaunch(ProjectileLaunchEvent event, Player shooter) {
		if (shooter.getUniqueId() == owner.getUniqueId()) {
			if (shooter.getItemInHand().isSimilar(SusanoBowItem())) {
				if (BowCD <= 0) {
					if (SusanoCD < 60*10) {
						owner.getInventory().removeItem(SusanoBowItem());
					}
					event.getEntity().setMetadata("Susano BOW", new FixedMetadataValue(Main.getInstance(), shooter.getLocation()));
				}else {
					if (SusanoCD < 60*10) {
						owner.getInventory().removeItem(SusanoBowItem());
					}
					event.setCancelled(true);
				}
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(GenjutsuItem())) {
			owner.openInventory(GenjutsuInventory());
			return true;
		}
		if (item.isSimilar(amaterasuItem())) {
			if (amaterasuCD <= 0) {
				Player target = getTargetPlayer(owner, 25);
				if (target == null) {
					owner.sendMessage("§cIl faut viser un joueur !");
					return true;
				}
				amaterasuCD = 60*5;
				owner.sendMessage("§cAMATERASU !");
				target.sendMessage("§7Vous avez été toucher par l'§cAmaterasu");
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (i == 10) {
							owner.sendMessage("L'§cAmaterasu§f à fini de brulé");
							cancel();
							return;
						}
						target.setFireTicks(target.getFireTicks()+250);
						target.damage(1.5);
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
			} else {
				sendCooldown(owner, amaterasuCD);
				return true;
			}
		}
		if (item.isSimilar(SusanoBowItem())) {
			if (BowCD > 0) {
				sendCooldown(owner, BowCD);
				return true;
			}
		}
		if (item.isSimilar(SusanoItem())) {
			if (hasIzanagi) {
				owner.sendMessage("§7Vous n'avez plus accès au§c§l Susano");
				return true;
			}
			if (SusanoCD <= 0) {
				giveItem(owner, true, SusanoBowItem());
				setResi(20);
				SusanoCD = 60*15;
				owner.sendMessage("§7Activation du§c§l Susano");
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (i == 60*5) {
							owner.sendMessage("§7Votre§c§l Susano§7 ce désactive");
							owner.getInventory().removeItem(SusanoBowItem());
							cancel();
							return;
						}
						if (gameState.getServerState() != ServerStates.InGame) {
							cancel();
							return;
						}
						if (gameState.getInSpecPlayers().contains(owner)) {
							cancel();
							return;
						}
						if (hasIzanagi) {
							cancel();
							return;
						}
						sendCustomActionBar(owner, "§bTemp restant de§c§l Susano§b:§c§l "+cd(SusanoCD-(60*10)));
						givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
				return true;
			}else {
				sendCooldown(owner, SusanoCD);
			}
			return true;
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (mortOrochimaru) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
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
		if (SusanoCD >= 0) {
			SusanoCD--;
			if (SusanoCD == 0) {
				owner.sendMessage("§aLa technique§c Susano§a est à nouveau utilisable");
			}
		}
		if (BowCD >= 0) {
			BowCD--;
			if (BowCD == 0) {
				owner.sendMessage("§aLa technique§c Susano§f (§cArc§f)§a est de nouveau utilisable");
			}
		}
		if (amaterasuCD >= 0) {
			amaterasuCD--;
			if (amaterasuCD == 0) {
				owner.sendMessage("§aLa technique§c Amaterasu§a est de nouveau utilisable");
			}
		}
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("izanagi")) {
			if (!hasIzanagi) {
				setMaxHealth(getMaxHealth()-2);
				owner.setMaxHealth(getMaxHealth());
				owner.getInventory().remove(SusanoItem());
				owner.sendMessage("§7Vous utilisez§c l'izanagi");
				giveItem(owner, false, new ItemStack(Material.GOLDEN_APPLE, 5));
				hasIzanagi = true;
				owner.setHealth(getMaxHealth());
			} else {
				owner.sendMessage("§7Vous avez déjà trop utiliser ce maudit pouvoir...");
			}
		}
	}
	@Override
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
		if (Tsukuyomi.contains(moover)) {
			e.setCancelled(true);
			moover.teleport(e.getFrom());
		}
	}

	@Override
	public String getName() {
		return "Sasuke";
	}

	private static class SasukeRunnable extends BukkitRunnable {
		private final Sasuke sasuke;
		public SasukeRunnable(Sasuke sasuke) {
			this.sasuke = sasuke;
		}
		
		@Override
		public void run() {
			if (sasuke.getGameState().getServerState() != ServerStates.InGame) {
				cancel();
				sasuke.izanami = null;
				return;
			}
			if (sasuke.izanami == null){
				return;
			}
			if (sasuke.izanami.isAllTrue()) {
				Player owner = Bukkit.getPlayer(sasuke.izanami.getUser());
				Player toIzanami = Bukkit.getPlayer(sasuke.izanami.getTarget());
				if (owner != null && toIzanami != null) {
					boolean reussite = sasuke.izanami.onSuccessfullInfection(sasuke, sasuke.getPlayerRoles(toIzanami));
					if (reussite) {
						sasuke.infectFinish = true;
						cancel();
					}
				}
			}
		}
	}
}