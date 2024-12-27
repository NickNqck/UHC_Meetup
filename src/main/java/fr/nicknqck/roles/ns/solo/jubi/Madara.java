package fr.nicknqck.roles.ns.solo.jubi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.builders.JubiRoles;
import fr.nicknqck.roles.ns.Intelligence;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;

public class Madara extends JubiRoles {

	private int BenshoCD = 0;
	private int ShinraCD = 0;
	private int MeteoriteUse =  0;
	private int SusanoCD = 0;
	private boolean hasIzanagi = false;

	public Madara(UUID player) {
		super(player);
		givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, true);
		setChakraType(Chakras.KATON);
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public @NonNull GameState.Roles getRoles() {
		return Roles.Madara;
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			System.out.println(event.getDamage());
		}
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Obito, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+getRoles().getTeam().getColor()+"Madara",
				"§fVotre objectif est de gagner avec le camp "+getRoles().getTeam().getColor()+getRoles().getTeam().name(),
				"",
				AllDesc.effet,
				"",
				AllDesc.point+AllDesc.fireResi+" permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+MadaraItem().getItemMeta().getDisplayName()+"§f: Permet d'activer/désactiver vos effet "+AllDesc.Speed+"§e 2§f et"+AllDesc.Force+"§c 1",
				"",
				AllDesc.point+ChibakuTenseiItem().getItemMeta().getDisplayName()+"§f: Ouvre un menu ayant 3 possibilité: ",
				"",
				"§7→ \"§cBenshô Ten'in\":§f Vous ouvre un menu permettant de choisir un joueur à téléporter à votre position",
				"",
				"§7→ \"§cShinra Tensei\":§f Vous permet de repousser toute entité étant à moins de 20blocs de vous",
				"",
				"§7→ \"§7Météorite\":§f Après 10secondes, fais spawn une météorite à l'endroit ou vous avez activé cette dernière, tuant au passage tout joueur dans un rayon de 50blocs autours de la météorite",
				"",
				AllDesc.point+SusanoItem().getItemMeta().getDisplayName()+"§f: Vous donne l'effet "+AllDesc.Resi+" 1 pendant 5minutes",
				"",
				AllDesc.point+"§dTraqueur§f: Permet via un menu de traquer le Biju voulu",
				"",
				AllDesc.commande,
				"",
				"§6/ns izanagi§f: Vous remet full vie et vous donne§e 5 pommes en or§f, mais, vous retire§c 1"+AllDesc.coeur+"§f permanent et vous retire l'accès au "+SusanoItem().getItemMeta().getDisplayName(),
				"",
				AllDesc.particularite,
				"",
				"En commencant un message avec§c !§f vous parlez avec§d Obito,",
				"Vous possédez la nature de Chakra : §cKaton",
				"",
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				new ItemBuilder(Material.DIAMOND_SWORD).setName("§dGunbaï").setLore("§7").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).toItemStack(),
				MadaraItem(),
				ChibakuTenseiItem(),
				SusanoItem(),
		};
	}
	private ItemStack MadaraItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§dMadara")
				.setLore("§7Permet d'obtenir la puissance de l'illustre§d Madara§4§l Uchiwa")
				.toItemStack();
	}
	private ItemStack ChibakuTenseiItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§cChibaku Tensei")
				.setLore("§7Permet d'ouvrir un menu")
				.toItemStack();
	}
	private ItemStack SusanoItem() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§c§lSusano")
				.setLore("§7Vous donne "+AllDesc.Resi+"§7 1 pendant 5m")
				.toItemStack();
	}
	private boolean MadaraUse = false;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(MadaraItem())) {
			if (!MadaraUse) {
				owner.sendMessage("§7Vous obtenez votre puissance d'entant");
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true);
				givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true);
				MadaraUse = true;
            } else {
				owner.sendMessage("§7Vous perdez votre puissance...");
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.removePotionEffect(PotionEffectType.SPEED);
				MadaraUse = false;
            }
            return true;
        }
		if (item.isSimilar(ChibakuTenseiItem())) {
			openChibakuTenseiInventory();
			return true;
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
				new BukkitRunnable() {
					int i = 0;
					@Override
					public void run() {
						i++;
						if (i == 60*5) {
							owner.sendMessage("§7Votre§c§l Susano§7 ce désactive");
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
						sendCustomActionBar(owner, "§bTemp restant de§c§l Susano§b:§c§l "+StringUtils.secondsTowardsBeautiful(SusanoCD-(60*10)));
						givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
			}else {
				sendCooldown(owner, SusanoCD);
			}
			return true;
		}
		return false;
	}
	@Override
	public void resetCooldown() {
		MadaraUse = false;
		BenshoCD = 0;
		ShinraCD = 0;
		MeteoriteUse = 0;
		hasIzanagi = false;
		NF.clear();
		owner.getInventory().removeItem(getItems());
		giveItem(owner, true, getItems());
	}
	private void openChibakuTenseiInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§cChibaku Tensei");
		inv.setItem(0, new ItemBuilder(Material.IRON_SWORD).setName("§cBenshô Ten'in").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(BenshoCD),"§7Permet d'attirer un joueur proche à votre position").toItemStack());
		inv.setItem(4, new ItemBuilder(Material.IRON_CHESTPLATE).setName("§cShinra Tensei").setLore("§7Cooldown "+StringUtils.secondsTowardsBeautiful(ShinraCD),"§7Permet de repousser toute entité étant à moins de 20blocs de vous").toItemStack());
		if (MeteoriteUse == 0) {
			inv.setItem(8, new ItemBuilder(Material.STONE).setName("§7Météorite").setLore("§7Utilisation:§c "+MeteoriteUse+"§7/§61","§7Permet de créer un énorme trou tuant tout joueur à l'intérieur").toItemStack());
		}
		owner.openInventory(inv);
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		if (MadaraUse) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
			givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false);
		}
		if (BenshoCD > 0)BenshoCD--;
		if (BenshoCD == 0) {
			owner.sendMessage("§cBensho Ten'in§7 est à nouveau utilisable !");
			BenshoCD--;
		}
		
		if (owner.getOpenInventory().getTitle().equalsIgnoreCase("§cChibaku Tensei")) {
			openChibakuTenseiInventory();
		}
		if (ShinraCD > 0)ShinraCD--;
		if (ShinraCD == 0) {
			owner.sendMessage("§cShinra Tensei§7 est à nouveau utilisable !");
			ShinraCD--;
		}
		if (SusanoCD > 0) SusanoCD--;
		if (SusanoCD == 0) {
			owner.sendMessage("§c§lSusano§7 est à nouveau utilisable !");
			SusanoCD--;
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
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player obito = getPlayerFromRole(Roles.Obito);
				owner.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dMadara: "+msg.substring(1)));
				if (obito != null) {
					obito.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dMadara: "+msg.substring(1)));
				}
			}
		}
	}
	private final List<UUID> NF = new ArrayList<>();
	@Override
	public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {
		if (NF.contains(victim.getUniqueId())) {
			if (e.getCause() == DamageCause.FALL) {
				NF.remove(victim.getUniqueId());
				e.setCancelled(true);
			}
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player == owner) {
			NF.clear();
		}
	}
	@Override
	public void onEndGame() {
		NF.clear();
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}

	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (inv.getTitle().equalsIgnoreCase("§cChibaku Tensei")) {
			if (event.getSlot() == 0) {
				if (BenshoCD <= 0) {
					owner.closeInventory();
					openBenshoTenIn();
				} else {
					sendCooldown(owner, BenshoCD);
				}
				event.setCancelled(true);
			}
			if (event.getSlot() == 4) {
				event.setCancelled(true);
				owner.closeInventory();
				if (ShinraCD <= 0) {
					PropulserUtils pu = new PropulserUtils(owner, 20).soundToPlay("nsmtp.shinratensei");
					NF.addAll(pu.getPropulsedUUID());
					pu.applyPropulsion();
					ShinraCD = 60*5;
				} else {
					sendCooldown(owner, ShinraCD);
				}
			}
			if (event.getSlot() == 8) {
				event.setCancelled(true);
				if (MeteoriteUse == 0) {
					MeteoriteUse++;
					owner.closeInventory();
					owner.sendMessage("§7Votre météorite attérira dans 10s");
					Loc.getNearbyPlayers(owner, 50).stream().filter(p -> gameState.getInGamePlayers().contains(p.getUniqueId())).filter(p -> !gameState.hasRoleNull(p.getUniqueId())).forEach(e -> playSound(e, "mob.wither.death"));
					new BukkitRunnable() {
						private final Location loc = owner.getLocation();
						private int s = 0;
						@Override
						public void run() {
							s++;
							if (s == 5) {
								owner.sendMessage("§7Votre météorite attérira dans 5s");
								for (UUID u : gameState.getInGamePlayers()) {
									Player p = Bukkit.getPlayer(u);
									if (p == null)continue;
									if (p.getWorld() == loc.getWorld()) {
										if (p.getLocation().distance(loc) <= 50) {
											playSound(p, "mob.wither.death");
										}
									}
								}
							}
							if (s == 10) {
								Location Center = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
								for(Location loc : new MathUtil().sphere(Center, 30, false)) {
									loc.getBlock().setType(Material.AIR);
									for (Player p : Loc.getNearbyPlayers(loc, 0.9)) {
										if (p.getUniqueId() != owner.getUniqueId()) {
											if (p.getHealth() - 10.0 <= 0) {
												p.setHealth(4.0);
											}else {
												p.setHealth(p.getHealth()-10.0);
											}
											p.damage(0.0);
										}
									}
								}
								new MathUtil().spawnFallingBlocks(new Location(loc.getWorld(), loc.getX(), loc.getY()+10, loc.getZ()), Material.STONE, 8, false, false, 60);
								cancel();
							}
							if (gameState.getInSpecPlayers().contains(owner)) {
								cancel();
							}
							if (gameState.getServerState() != ServerStates.InGame) {
								cancel();
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
				}
			}
		}
		if (inv.getTitle().equalsIgnoreCase("§cBenshô Ten'in")) {
			event.setCancelled(true);
			if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
				if (!item.hasItemMeta())return;
				if (!item.getItemMeta().hasDisplayName())return;
				Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
				if (player == null)return;
				if (Loc.getNearbyPlayers(owner, 30).contains(player)) {
					player.teleport(owner);
					player.sendMessage("§7Vous avez été touché par le "+inv.getTitle());
					owner.sendMessage(inv.getTitle()+"§c !");
					owner.closeInventory();
					BenshoCD = 60*5;
				} else {
					owner.sendMessage("§cVeuillez visé un joueur proche !");
					owner.closeInventory();
				}
			} else {
				owner.closeInventory();
				openChibakuTenseiInventory();
				owner.updateInventory();
			}
		}
	}
	private void openBenshoTenIn() {
		Inventory toOpen = Bukkit.createInventory(owner, 54, "§cBenshô Ten'in");
		for (int i = 0; i <= 8; i++) {
			if (i == 4) {
				toOpen.setItem(i, GUIItems.getSelectBackMenu());
			} else {
				toOpen.setItem(i, GUIItems.getOrangeStainedGlassPane());
			}
		}
		for (Player p : Loc.getNearbyPlayers(owner, 30)) {
			toOpen.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setSkullOwner(p.getName()).setName(p.getDisplayName()).toItemStack());
		}
		owner.openInventory(toOpen);
	}

	@Override
	public String getName() {
		return "Madara";
	}
}