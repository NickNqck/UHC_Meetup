package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.UchiwaRoles;
import fr.nicknqck.roles.ns.power.Izanami;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Itachi extends UchiwaRoles {

	private Izanami izanami;
	private int SusanoCD = 0;
	private boolean hasIzanagi = false;
	private int BowCD = -1;
	private int AmateratsuCD = 0;
	private int totsukaCD = 0;
	private boolean infectFinish = false;
	private int cdTsukuyomi = 0;
	private int cdAttaque = 0;
	
	public Itachi(UUID player) {
		super(player);
		setChakraType(Chakras.KATON);
	}

	@Override
	public UchiwaType getUchiwaType() {
		return UchiwaType.IMPORTANT;
	}

	@Override
	public Roles getRoles() {
		return Roles.Itachi;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Akatsuki;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Kisame, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cItachi",
				AllDesc.objectifteam+"l'§cAkatsuki",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§eSpeed 1§f et§6 Fire Résistance 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§c§lSusano§f: Permet d'obtenir l'effet§9 Résistance 1§f pendant 5minutes, de plus, vous donne accès à l'épée de§c Totsuka",
				"",
				AllDesc.point+"§cTotsuka§f: Épée en diamant tranchant 7, obtensible en utilisant le§c§l Susano",
				"",
				AllDesc.point+"§cAmateratsu§f: Permet en visant un joueur, d'infliger des dégats à ce dernier tout-en l'enflamment ",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack GenjutsuItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§cGenjutsu")
				.setLore("§7Permet de lancer des illusions")
				.toItemStack();
	}
	@Override
	public void resetCooldown() {
		cdAttaque = 0;
		cdTsukuyomi = 0;
		AmateratsuCD = 0;
		BowCD = 0;
		totsukaCD = 0;
		SusanoCD = 0;
	}
	private Inventory GenjutsuInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cGenjutsu");
		inv.setItem(0,new ItemBuilder(Material.ARMOR_STAND).setName("§cTsukuyomi").setLore("§7Cooldown§l "+ StringUtils.secondsTowardsBeautiful(cdTsukuyomi),"§7Permet d'immobiliser les joueurs autour de vous").toItemStack());
		inv.setItem(4,new ItemBuilder(Material.IRON_SWORD).setName("§cAttaque").setLore("§7Cooldown§l "+StringUtils.secondsTowardsBeautiful(cdAttaque),"§7Vous permez de vous téléportez sur un joueur au alentour").toItemStack());
		if (izanami == null) {
			inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous permez d'infecter quelqu'un").toItemStack());
		} else {
			if (!izanami.isAllTrue()) {
				if (infectFinish) {
					return inv;
				}else{
					inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous êtes en cours d'infection").toItemStack());
				}
			}
		}
		return inv;
	}
	private final List<Player> Tsukuyomi = new ArrayList<>();
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
				if (!this.infectFinish) {
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
						if (item.isSimilar(GUIItems.getSelectBackMenu())) {
							owner.openInventory(GenjutsuInventory());
							return;
						}
						Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
						if (clicked == null) {
							return;
						}
						if (Loc.getNearbyPlayers(owner, 30).contains(clicked)) {
							clicker.sendMessage("§cL'izanami§7 commence...");
							izanami = new Izanami(clicker.getUniqueId(), clicked.getUniqueId());
							izanami.start("§c");
							clicker.sendMessage(izanami.getStringsMission());
							clicker.closeInventory();
							new ItachiRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                        }
					}
				}
			}
		}
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}
	private void openIzanamiInventory() {
		owner.closeInventory();
		if (izanami == null) {
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
			inv.setItem(3, izanami.getResultUserMission());
			inv.setItem(5, izanami.getResultVictimMission());
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
				AmateratsuItem(),
				GenjutsuItem()
		};
	}
	private ItemStack SusanoItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§c§lSusano")
				.setLore("§7Vous donne "+AllDesc.Resi+"§7 1 pendant 5m")
				.toItemStack();
	}
	private ItemStack AmateratsuItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§cAmaterasu")
				.setLore("§7Permet en visant un joueur de le mettre en feu")
				.toItemStack();
	}
	private ItemStack TotsukaItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD)
				.setName("§cTotsuka")
				.setLore("§7Épée légendaire en possession d'§cItachi")
				.addEnchant(Enchantment.DAMAGE_ALL, 7)
				.setUnbreakable(true)
				.toItemStack();
	}
	
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (owner.getItemInHand().isSimilar(TotsukaItem())) {
				if (totsukaCD > 0) {
					sendCooldown(owner, totsukaCD);
					event.setDamage(0.0);
					event.setCancelled(true);
				}
				totsukaCD = 10;
			}
		}
		if (Tsukuyomi != null && Tsukuyomi.contains(victim)) {
			event.setDamage(0);
			event.setCancelled(true);
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(GenjutsuItem())) {
			owner.openInventory(GenjutsuInventory());
			return true;
		}
		if (item.isSimilar(AmateratsuItem())) {
			if (AmateratsuCD <= 0) {
				Player target = getTargetPlayer(owner, 25);
				if (target == null) {
					owner.sendMessage("§cIl faut viser un joueur !");
					return true;
				}
				AmateratsuCD = 60*5;
				owner.sendMessage("§cAMATERATU !");
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (i == 10) {
							owner.sendMessage("L'§cAmateratsu§f à fini de brulé");
							cancel();
							return;
						}
						target.setFireTicks(target.getFireTicks()+250);
						target.damage(1.5);
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
			} else {
				sendCooldown(owner, AmateratsuCD);
				return true;
			}
		}
		if (item.isSimilar(SusanoItem())) {
			if (hasIzanagi) {
				owner.sendMessage("§7Vous n'avez plus accès au§c§l Susano");
				return true;
			}
			if (SusanoCD <= 0) {
				setResi(20);
				SusanoCD = 60*15;
				owner.sendMessage("§7Activation du§c§l Susano");
				giveItem(owner, true, TotsukaItem());
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (i == 60*5) {
							owner.sendMessage("§7Votre§c§l Susano§7 ce désactive");
							owner.getInventory().removeItem(TotsukaItem());
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
						sendCustomActionBar(owner, "§bTemp restant de§c§l Susano§b:§c§l "+StringUtils.secondsTowardsBeautiful(SusanoCD-(60*10)));
						givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
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
		if (totsukaCD >= 0) {
			totsukaCD--;
			if (totsukaCD == 0) {
				owner.sendMessage("§cTotsuka§7 est de nouveal utilisable");
			}
		}
		if (cdTsukuyomi >= 0) {
			cdTsukuyomi -= 1;
		}
		if (cdTsukuyomi == 0) {
			owner.sendMessage("§cTsukuyomi§7 est de nouveau utilisable");
		}
		if (cdAttaque >= 0) {
			cdAttaque -= 1;
		}
		if (cdAttaque == 0) {
			owner.sendMessage("§cAttaque§7 est de nouveau utilisable");
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
		if (AmateratsuCD >= 0) {
			AmateratsuCD--;
			if (AmateratsuCD == 0) {
				owner.sendMessage("§aLa technique§c Susano§f est de nouveau utilisable");
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
		return "Itachi";
	}

	private static class ItachiRunnable extends BukkitRunnable {
		private final Itachi itachi;
		ItachiRunnable(Itachi itachi){
			this.itachi = itachi;
		}

		@Override
		public void run() {
			if (itachi.gameState.getServerState() != ServerStates.InGame || itachi.izanami == null) {
				cancel();
				return;
			}
			if (itachi.izanami.isAllTrue()) {
				Player toIzanami = Bukkit.getPlayer(itachi.izanami.getTarget());
				if (toIzanami != null) {
					itachi.infectFinish = itachi.izanami.onSuccessfullInfection(itachi, itachi.getGameState().getGamePlayer().get(toIzanami.getUniqueId()).getRole());
					if (itachi.infectFinish){
						cancel();
					}
				}
			}
		}
	}
}