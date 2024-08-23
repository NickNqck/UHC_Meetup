package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Deidara extends AkatsukiRoles {

	public Deidara(UUID player) {
		super(player);
		setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.RAITON));
	}
	@Override
	public Roles getRoles() {
		return Roles.Deidara;
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cDeidara",
				AllDesc.objectifteam+"§cAkatsuki",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§cArc explosif§f: Vous permet de tirée une flèche appliquant les effets du§6 Bakûton§f séléectionner précédemment",
				"",
				AllDesc.point+"§6Bakûton§f: Vous permet d'ouvrir un menu vous permettant de choisir une technique parmis§a celle-ci§f:",
				"",
				"§7     →§cC1§f: Avec ce mode d'§aactiver§f, vos flèches créeront une explosion qui à l'impacte, inflige§c 1"+AllDesc.coeur+" de dégat au joueurs proche.§7 (1x/25s)",
				"",
				"§7     →§cC2§f: La prochaine flèche que vous tirerez avec l'§cArc Explosif§f créera une explosion a l'impacte de la même puissance que le§c C1§f, de plus, il vous donnera un§a fly§f de§c 10s§f, qui, chaque§c seconde§f, lâchera une§c TNT§f en dessous de vous.§7 (1x/8m)",
				"",
				"§7     →§cC3§f: Fais apparaître une§c pluie de TNT§f d'une taille de§c 10x10§f qui apparaitront§c 15 blocs§f au dessus du sol.§7 (1x/10m)",
				"",
				"§7     →§cC4§f: Pendant§c 15s§f tout les joueurs à moins de 25 blocs de la zone d'§catterissage§f de la §c flèche§f perdront§c 1/2"+AllDesc.coeur+"/§cs§f.§7 (1x/8m)",
				"",
				"§7     →§cArt Ultime§f: Après avoir sélectionné ce mode en faisant un clique gauche avec le§6 Bakûton§f vous empêche de bouger pendant§c 10s§f puis crée une explosion énorme tuant tout joueurs dans la zone de cette dernière (dont vous).§7 (1x/partie)",
				"",
				AllDesc.particularite,
				"",
				"Vous êtes immunisé à tout dégât provenant de vos§c explosions",
				"Votre nature de chakra est sois§e Raiton§f sois "+Chakras.DOTON.getShowedName(),
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar,
		};
	}
	private ItemStack ArcExplosifItem() {
		return new ItemBuilder(Material.BOW).setName("§cArc Explosif").addEnchant(Enchantment.DEPTH_STRIDER, 1).setUnbreakable(true).hideAllAttributes().setLore("§7Vous permet d'utiliser des techniques§6 Bakûton").toItemStack();
	}
	private ItemStack BakutonItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§6Bakûton").setLore("§7Ouvre un menu pour contrôler vos§c explosions").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				ArcExplosifItem(),
				BakutonItem()
		};
	}
	@Override
	public void onLeftClick(PlayerInteractEvent event, GameState gameState) {
		if (event.getPlayer().getUniqueId().equals(owner.getUniqueId())) {
			if (event.getItem() != null) {
				if (event.getItem().isSimilar(BakutonItem())) {
					if (mode == Mode.C1) {
						mode = Mode.C2;
						owner.sendMessage("§7Vous avez séléctionnez la technique§c \"C2\"");
						event.setCancelled(true);
						return;
					}
					if (mode == Mode.C2) {
						mode = Mode.C3;
						owner.sendMessage("§7Vous avez séléctionnez la technique§c \"C3\"");
						event.setCancelled(true);
						return;
					}
					if (mode == Mode.C3) {
						mode = Mode.C4;
						owner.sendMessage("§7Vous avez séléctionnez la technique§c \"C4\"");
						event.setCancelled(true);
						return;
					}
					if (mode == Mode.C4) {
						mode = Mode.C1;
						owner.sendMessage("§7Vous avez séléctionnez la technique§c \"C1\"");
						event.setCancelled(true);
						return;
					}
					if (mode == Mode.ArtUltime) {
						if (!hasArtUltime) {
							hasArtUltime = true;
							owner.sendMessage("§7L'§cArt Ultime§7 ce déclanche des maintenants");
							owner.setAllowFlight(true);
							owner.setFlying(true);
							new BukkitRunnable() {
								int i = 10;
								final Location loc = owner.getLocation().clone();
								@SuppressWarnings("deprecation")
								@Override
								public void run() {
									if (gameState.getServerState() != ServerStates.InGame) {
										if (!map.isEmpty()) {
											setOldBlockwMap();
										}
										cancel();
									}
									if (i == 10) {
										for (Player p : Loc.getNearbyPlayers(owner, 50)) {
											playSound(p, "mob.wither.spawn");
										}
									}
									if (i == 5) {
										for (Player p : Loc.getNearbyPlayers(owner, 50)) {
											playSound(p, "mob.wither.spawn");
										}
									}
									if (i == 0) {
										Location Center = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
										for(Location loc : new MathUtil().sphere(Center, 35, false)) {
											if (loc.getBlock().getTypeId() != 32 && loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != Material.LONG_GRASS && loc.getBlock().getType() != Material.RED_ROSE && loc.getBlock().getType() != Material.YELLOW_FLOWER && loc.getBlock().getType() != Material.DOUBLE_PLANT) {
												map.put(loc.getBlock(), loc.getBlock().getTypeId());
											}
											loc.getBlock().setType(Material.AIR);
											for (Player p : Loc.getNearbyPlayers(loc, 0.85)) {
												Main.getInstance().getDeathManager().KillHandler(p, owner);
											}
										}
										GameListener.SendToEveryone("§4§lL'art est explosion !");
										cancel();
										Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> setOldBlockwMap(), 20*60*3);
										cancel();
										return;
									}
									i--;
								}
							}.runTaskTimer(Main.getInstance(), 0, 20);
						}
					}
				}
			}
		}
	}
	private final HashMap<Block, Integer> map = new HashMap<>();
	@SuppressWarnings("deprecation")
	private void setOldBlockwMap() {
		map.keySet().stream().filter(n -> n.getTypeId() != 162).filter(e -> e.getTypeId() != 161).forEach(loc -> loc.setType(Material.getMaterial(map.get(loc))));
		map.keySet().stream().filter(b -> b.getTypeId() == 162).filter(b -> b.getType() == Material.LOG_2).forEach(dob -> dob.setTypeIdAndData(map.get(dob), (byte) 1, true));
		map.keySet().stream().filter(b -> b.getTypeId() == 161).filter(b -> b.getType() == Material.LEAVES_2).forEach(r -> r.setTypeIdAndData(map.get(r), (byte) 1, true));
		map.keySet().stream().filter(b -> b.getTypeId() == 100).forEach(b -> b.setType(Material.AIR));
		map.keySet().stream().filter(b -> b.getTypeId() == 99).forEach(b -> b.setType(Material.AIR));
		map.keySet().stream().filter(b -> b.getTypeId() == 18).forEach(b -> b.setTypeIdAndData(18, (byte) 0, true));
		map.keySet().stream().filter(b -> b.getTypeId() == 17).forEach(b -> b.setTypeIdAndData(17, (byte)0, true));
		map.clear();
	}
	@Override
	public void onEndGame() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			System.out.println("Reinitialisation des degats de la map faite par Deidara");
			setOldBlockwMap();
		}, 100);
	}
	@Override
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
		if (moover.getUniqueId().equals(owner.getUniqueId()) && hasArtUltime) {
			moover.teleport(e.getFrom(), TeleportCause.PLUGIN);
		}
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	private int cdC1 = 0;
	private int cdC2 = 0;
	private int cdC3 = 0;
	private int cdC4 = 0;
	private boolean hasArtUltime = false;
	@Override
	public void resetCooldown() {
		cdC1 = 0;
		cdC2 = 0;
		cdC3 = 0;
		cdC4 = 0;
		hasArtUltime = false;
		setOldBlockwMap();
	}

	@Override
	public String getName() {
		return "Deidara";
	}

	@Getter
    private enum Mode {
		C1(new ItemBuilder(Material.SULPHUR).setName("§cC1").toItemStack()),
		C2(new ItemBuilder(Material.FEATHER).setName("§cC2").toItemStack()),
		C3(new ItemBuilder(Material.TNT).setName("§cC3").toItemStack()),
		C4(new ItemBuilder(Material.POTION).setDurability(16420).setName("§cC4").toItemStack()),
		ArtUltime(new ItemBuilder(Material.STONE).setName("§cArt Ultime").toItemStack());
		final ItemStack item;
		Mode(ItemStack e) {
			this.item = e;
		}
    }
	private Mode mode = Mode.C1;
	private Inventory BakutonInventory() {
		Inventory inv = Bukkit.createInventory(owner, 9, "§6Bakûton");
		List<Mode> inMenu = new ArrayList<>();
		for (int i = 0; i <= 8; i+=2) {
			for (Mode m : Mode.values()) {
				if (!inMenu.contains(m) && inv.getItem(i) == null) {
					inv.setItem(i, m.getItem());
					inMenu.add(m);
				}
			}
		}
		return inv;
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(BakutonItem())) {
			owner.openInventory(BakutonInventory());
			owner.updateInventory();
			return true;
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(ArcExplosifItem()) || owner.getItemInHand().isSimilar(BakutonItem())) {
			if (mode == Mode.C1) {
				sendCustomActionBar(owner, "§7(§cC1§7) Cooldown: "+ StringUtils.secondsTowardsBeautiful(cdC1));
			}
			if (mode == Mode.C2) {
				sendCustomActionBar(owner, "§7(§cC2§7) Cooldown: "+StringUtils.secondsTowardsBeautiful(cdC2));
			}
			if (mode == Mode.C3) {
				sendCustomActionBar(owner, "§7(§cC3§7) Cooldown: "+StringUtils.secondsTowardsBeautiful(cdC3));
			}
			if (mode == Mode.C4) {
				sendCustomActionBar(owner, "§7(§cC4§7) Cooldown: "+StringUtils.secondsTowardsBeautiful(cdC4));
			}
			if (mode == Mode.ArtUltime) {
				sendCustomActionBar(owner, "§7Le jour de l'§cArt Ultime§7 est arrivé !");
			}
		}
		if (cdC1 >= 0) {
			cdC1--;
			if (cdC1 == 0) {
				owner.sendMessage("§7Le§c C1§7 est à nouveau utilisable.");
			}
		}
		if (cdC2 >= 0) {
			cdC2--;
			if (cdC2 == 0) {
				owner.sendMessage("§7Le§c C2§7 est à nouveau utilisable.");
			}
		}
		if (cdC3 >= 0) {
			cdC3--;
			if (cdC3 == 0) {
				owner.sendMessage("§7Le§c C3§7 est à nouveau utilisable.");
			}
		}
		if (cdC4 >= 0) {
			cdC4--;
			if (cdC4 == 0) {
				owner.sendMessage("§7Le§c C4§7 est à nouveau utilisable.");
			}
		}
	}
	@Override
	public void onInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId().equals(owner.getUniqueId())) {
			if (inv.getTitle().equals("§6Bakûton") || inv.getName().equals("§6Bakûton")) {
				event.setCancelled(true);
				if (item != null) {
					for (Mode m : Mode.values()) {
						if (m.getItem() != null) {
							if (item.isSimilar(m.getItem())) {
								clicker.closeInventory();
								if (m != Mode.ArtUltime) {
									clicker.sendMessage("§7Vous avez séléctioner le mode \""+m.getItem().getItemMeta().getDisplayName()+"§7\"");
								} else {
									clicker.sendMessage("§7L'§cArt Ultime§7 est prêt, plus qu'à utiliser notre dernier§6 Bakûton§7...");
								}
								this.mode = m;
								break;
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onProjectileLaunch(Projectile projectile, Player shooter) {
		if (shooter.getUniqueId().equals(owner.getUniqueId())) {
			if (owner.getItemInHand().isSimilar(ArcExplosifItem())) {
				if (mode == Mode.C1) {
					if (cdC1 <= 0) {
						projectile.setMetadata("C1", new FixedMetadataValue(Main.getInstance(), owner));
					} else {
						sendCooldown(owner, cdC1);
					}
				}
				if (mode== Mode.C2) {
					if (cdC2 <=0) {
						projectile.setMetadata("C2", new FixedMetadataValue(Main.getInstance(), shooter));
					} else {
						sendCooldown(owner, cdC2);
					}
				}
				if (mode == Mode.C3) {
					if (cdC3 <= 0) {
						projectile.setMetadata("C3", new FixedMetadataValue(Main.getInstance(), owner));
					} else {
						sendCooldown(owner, cdC3);
					}
				}
				if (mode == Mode.C4) {
					if (cdC4 <= 0) {
						projectile.setMetadata("C4", new FixedMetadataValue(Main.getInstance(), owner));
					} else {
						sendCooldown(owner, cdC4);
					}
				}
			}
		}
	}
	@Override
	public void onProjectileHit(Projectile entity, Player shooter) {
		if (shooter.getUniqueId().equals(owner.getUniqueId())) {
			if (entity instanceof Arrow) {
				if (entity.hasMetadata("C1")) {
						for (Player p : Loc.getNearbyPlayers(entity, 5)) {
							if (!p.getUniqueId().equals(owner.getUniqueId())) {
								MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, p.getLocation());
								p.damage(0.0, owner);
								Heal(p, -2);
								p.sendMessage("§7Vous avez été touché par le§c C1§7 de§c Deidara");
								owner.sendMessage(p.getDisplayName()+"§7 à subit les dégats de votre§c C1");
							}
							p.playSound(p.getLocation(), Sound.EXPLODE, 1, 8);
						}
						MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, entity.getLocation());
						cdC1 = 25;
						entity.removeMetadata("C1", Main.getInstance());
				}
				if (entity.hasMetadata("C2")) {
					if (cdC2 <= 0) {
						TNTPrimed tnt = (TNTPrimed) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.PRIMED_TNT);
						tnt.setFuseTicks(50);
						tnt.setMetadata("DeidaraC2"+StringID, new FixedMetadataValue(Main.getInstance(), owner));
						owner.setAllowFlight(true);
						owner.setFlying(true);
						cdC2 = 60*8;
						new BukkitRunnable() {
							int i = 12;
							@Override
							public void run() {
								i--;
								if (gameState.getServerState() != ServerStates.InGame || !gameState.getInGamePlayers().contains(getPlayer()) || i ==0) {
									owner.setFlying(false);
									owner.setAllowFlight(false);
									owner.setFallDistance(0.0f);
									cancel();
									return;
								}
								TNTPrimed t = (TNTPrimed) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.PRIMED_TNT);
								t.setFuseTicks(100);
								t.setMetadata("DeidaraC2"+StringID, new FixedMetadataValue(Main.getInstance(), owner));
							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
						entity.removeMetadata("C2", Main.getInstance());
					}
				}
				if (entity.hasMetadata("C3")) {
					if (cdC3 <= 0) {
						entity.removeMetadata("C3", Main.getInstance());
						Location goodY = new Location(entity.getWorld(), entity.getLocation().getX(), entity.getWorld().getHighestBlockYAt(entity.getLocation())+15, entity.getLocation().getZ());
						int sizeExplo = 10;
						Location quarterB = new Location(goodY.getWorld(), goodY.getX()-sizeExplo, goodY.getY(), goodY.getZ()-sizeExplo);
						Location quarterD = new Location(goodY.getWorld(), goodY.getX()+sizeExplo, goodY.getY(), goodY.getZ()+sizeExplo);
						for (int x = quarterB.getBlockX(); x <= quarterD.getBlockX(); x+=3) {
							for (int z = quarterB.getBlockZ(); z <= quarterD.getBlockZ(); z+=3) {
								Location loc = new Location(goodY.getWorld(), x, goodY.getY(), z);
								TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
								tnt.setFuseTicks(90);
								tnt.setCustomNameVisible(true);
								tnt.setCustomName("§cC3 de Deidara");
							}
						}
						cdC3 = 60*10;
					}
				}
				if (entity.hasMetadata("C4")) {
					if (cdC4 <= 0) {
						entity.removeMetadata("C4", Main.getInstance());
						new BukkitRunnable() {
							int i = 15;
							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								if (i <= 0) {
									owner.sendMessage("§7Votre zone ne fais plus d'effet");
									cancel();
								}
								for (Player p : Loc.getNearbyPlayersExcept(entity, 25, owner)) {
									p.damage(0.0);
									Heal(p, -1.0);
									p.resetTitle();
									p.sendTitle("§7Vous subissez les effets du§c C4", "§7de §cDeidara");
									owner.sendMessage("§c"+p.getDisplayName()+"§7 à subit les dégats de votre§c C4");
								}
								i--;
								MathUtil.sendCircleParticle(EnumParticle.BARRIER, entity.getLocation(), 25, 100);
							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
						cdC4 = 60*8+15;
					}
				}
			}
		}
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity damager) {
		if (damager instanceof TNTPrimed) {
			TNTPrimed tnt = (TNTPrimed) damager;
			if (tnt.hasMetadata("DeidaraC2"+StringID)) {
				if (victim.getUniqueId() != owner.getUniqueId()) {
					event.setDamage(0);
					Heal(victim, -4);
				}
			}
			if (victim.getUniqueId().equals(owner.getUniqueId())) {
				event.setDamage(0.0);
			}
		}
	}
}