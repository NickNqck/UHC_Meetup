package fr.nicknqck.roles.ns.solo.jubi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.power.Izanami;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.KamuiUtils;
import fr.nicknqck.utils.powers.KamuiUtils.Users;
import fr.nicknqck.utils.particles.MathUtil;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
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


public class Obito extends JubiRoles {
	public List<Player> Tsukuyomi = new ArrayList<>();
	private fr.nicknqck.roles.ns.power.Izanami izanami;
	private int cdNinjutsu = 0;
	public Obito(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		super.RoleGiven(gameState);
		System.out.println("Called RoleGiven in "+this);
		setChakraType(Chakras.KATON);
		if (!gameState.attributedRole.contains(Roles.Kakashi)) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			Bukkit.dispatchCommand(console, "nakime Gh6Iu2YjZl8A9Bv3Tn0Pq5Rm");
		}
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public @NonNull GameState.Roles getRoles() {
		return Roles.Obito;
	}
	@Override
	public String getName() {
		return "Obito";
	}
     @Override
	public String[] Desc() {
    	 KnowRole(owner, Roles.Madara, 20);
		return new String[] {
				AllDesc.bar,
			    AllDesc.role+"Obito",
			    "§fVotre objectif est de gagner avec le camp "+getRoles().getTeam().getColor()+getRoles().getTeam().name(),
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
				}else {
					if (target.getWorld().equals(Bukkit.getWorld("Kamui"))) {
						KamuiUtils.end(target);
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
	public ItemStack[] getItems() {
		return new ItemStack[] {
				GenjutsuItem(),
				ninjutsuSpatioTemporelItem(),
				KamuiItem()
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
		inv.setItem(0,new ItemBuilder(Material.ARMOR_STAND).setName("§cTsukuyomi").setLore("§7Cooldown§l "+StringUtils.secondsTowardsBeautiful(cdTsukuyomi),"§7Permet d'immobiliser les joueurs autour de vous").toItemStack());
		inv.setItem(4,new ItemBuilder(Material.IRON_SWORD).setName("§cAttaque").setLore("§7Cooldown§l "+StringUtils.secondsTowardsBeautiful(cdAttaque),"§7Vous permez de vous téléportez sur un joueur au alentour").toItemStack());
		if (!Izanami) {
			inv.setItem(8,new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous permez d'infecter quelqu'un").toItemStack());
		} else {
            if (hasIzanami) {
                return inv;
            } else {
                inv.setItem(8, new ItemBuilder(Material.NETHER_STAR).setName("§dIzanami").setLore("§7Vous êtes en cours d'infection").toItemStack());
            }
        }
		return inv;
	}
	private int cdSonohoka = 0;
    private int cdArimasu = 0;
    private int cdSusano = -1;
	private Inventory KamuiInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cKamui");
		inv.setItem(3, new ItemBuilder(Material.EYE_OF_ENDER).setName("§dArimasu").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(cdArimasu),
				"§7Permet de vous téléportez dans le Kamui").toItemStack());
		inv.setItem(7, new ItemBuilder(Material.ENDER_PEARL).setName("§dSonohaka").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(cdSonohoka),
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
			inv.setItem(3, izanami.getResultUserMission());
			inv.setItem(5, izanami.getResultVictimMission());
			inv.setItem(8, GUIItems.getSelectBackMenu());
			owner.openInventory(inv);
		}
	}
	private boolean hasIzanami = false;
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}
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
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() == owner.getUniqueId()) {
			if (inv != null && item != null && item.getType() != Material.AIR) {
				if (inv.getTitle().equals("§cKamui§7 ->§d Sonohaka")) {
					if (item.getType() != Material.AIR) {
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
										if (!owner.getWorld().getName().equals("Kamui")) {
											cancel();
											return;
										}
										if (i == 60*3) {
											owner.sendMessage("§7Vous sortez du§d Kamui§7 !");
											KamuiUtils.end(owner);
											cancel();
                                        } else {
											sendCustomActionBar(owner, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful((60*3)-i));
										}
									}
								}.runTaskTimer(Main.getInstance(), 0, 20);
                        } else {
							sendCooldown(clicker, cdArimasu);
                        }
                        return;
                    }
					if (event.getSlot() == 7) {
						event.setCancelled(true);
						owner.closeInventory();
						if (cdSonohoka <= 0) {
								openSonohakaInventory();
                        } else {
							sendCooldown(clicker, cdSonohoka);
                        }
                        return;
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
					if (!hasIzanami) {
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
								Izanami izanami1 = new Izanami(clicker.getUniqueId(), clicked.getUniqueId());
								izanami1.start("§d");
								this.izanami = izanami1;
								clicker.sendMessage(izanami1.getStringsMission());
								new ObitoRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
							}
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player Madara = getPlayerFromRole(Roles.Madara);
				owner.sendMessage(("§dObito: "+msg.substring(1)));
				if (Madara != null) {
					Madara.sendMessage(("§dObito: "+msg.substring(1)));
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
	}
	@Override
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
		if (Tsukuyomi.contains(moover)) {
			e.setCancelled(true);
			moover.teleport(e.getFrom());
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
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
                        } else {
							sendCooldown(owner, cdSusano);
                        }
                        return true;
                    }
				}
			}
		}
		if (item.isSimilar(ninjutsuSpatioTemporelItem())) {
			if (cdNinjutsu <= 0) {
				for (UUID u : gameState.getInGamePlayers()) {
					if (u != getPlayer()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
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
                        }
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
				cdNinjutsu = 60*8;
            } else {
				sendCooldown(owner, cdNinjutsu);
            }
            return true;

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
	private static class ObitoRunnable extends BukkitRunnable{
		private final Obito obito;
		ObitoRunnable(Obito owner){
			this.obito = owner;
		}
		@Override
		public void run() {
			if (obito.gameState.getServerState() != ServerStates.InGame || obito.izanami == null) {
				cancel();
				return;
			}
			if (obito.izanami.isAllTrue()) {
				Player toIzanami = Bukkit.getPlayer(obito.izanami.getTarget());
				if (toIzanami != null) {
					obito.hasIzanami = obito.izanami.onSuccessfullInfection(obito, GameState.getInstance().getGamePlayer().get(toIzanami.getUniqueId()).getRole());
					if (obito.hasIzanami){
						cancel();
					}
				}
			}
		}
	}
}
