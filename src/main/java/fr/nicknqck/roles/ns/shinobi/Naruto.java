package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Naruto extends ShinobiRoles {
	private int cdRasengan = 0;
	private int AmitieKyubi = 0;
	private int cdKyubi = 0;
	private int timeVillager = 0;
	private int cdClone = 0;
	private boolean useSmell = false;
	private Villager villager = null;
	private final ItemStack kyubiItem = new ItemBuilder(Material.NETHER_STAR).setName("§6Kyubi").setLore("§7Vous donne plus ou moins d'effet en fonction de votre tôt d'amitié avec§6 Kurama").toItemStack();
	private final ItemStack rasenganItem = new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan").setLore("§7Vous permet de rassembler votre Chakra en un point").toItemStack();

	public Naruto(UUID player) {
		super(player);
		setChakraType(Chakras.FUTON);
		setCanBeHokage(true);
	}
	@Override
	public GameState.Roles getRoles() {
		return Roles.Naruto;
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aNaruto",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aRasengan§f: Lorsque vous frappez un joueur avec, crée une explosion infligeant§c 2"+AllDesc.coeur+".§7 (1x/2m)",
				"",
				AllDesc.point+"§6Kyubi§f: Vous donne plus ou moins d'effet en fonction de votre tôt d'amitié avec lui, pour l'augmenter il faudra ce battre avec des joueurs, ",
				"§8 -§f Entre§4 0%§f et§4 25%§f: Vous offre l'effet§c Force 1§f pendant 3 minutes§f ainsi qu'une perte de§c 1/2"+AllDesc.coeur+"§f toute les§c 15s§f.§7 (1x/8m)",
				"§8 -§f Entre§c 26%§f et§c 50%§f: Vous offre les effets§e Speed 1§f et§c Force 1§f pendant 2 minutes ainsi qu'une perte de§c 1/2"+AllDesc.coeur+"§f toute les§c 20s§f.§7 (1x/7m)",
				"§8 -§f Entre§e 51%§f et§e 80%§f: Vous offre les effets§e Speed 1.5§f et§c Force 1§f pendant 2 minutes ainsi que§c 2"+AllDesc.coeur+" mais également une perte de§c 1/2"+AllDesc.coeur+" toute les§c 30s§f.§7 (1x/6m)",
				"§8 -§f Entre§a 81%§f et§a 100%§f: Vous offre les effets§e Speed 2§f et§c Force 1§f pendant 5 minutes ainsi que§c 2"+AllDesc.coeur+".§7 (1x/5m)",
				"",
				"Le cooldown est de 8 minutes au départ et réduit de 1 minutes a chaque stade",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns smell <joueur>§f: Si votre amitié avec§6 Kyubi§f est a 100%, vous permet en visant un joueur d'obtenir son camp.§7 (1x/5min)",
				"",
				AllDesc.point+"§6/ns clone§f: Crée un clone à votre position exacte, pendant tout le temp ou il restera vous accumulerez de l'énergie Senjutsu",
				"",
				AllDesc.particularite,
				"",
				"Lorsque le§d Jubi§f est invoquée, votre amitié avec§6 Kyubi§f monte a§a 100%",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				rasenganItem,
				kyubiItem
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("smell")){
			if (AmitieKyubi == 100) {
				if (!useSmell) {
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target != null) {
							if (!gameState.hasRoleNull(target)) {
                                gameState.getGamePlayer().get(target.getUniqueId());
                                owner.sendMessage(target.getName() + "§a est dans le camp: " +gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeam().getName());
                                useSmell = true;
                            } else {
								owner.sendMessage("§cCe joueur ne possède aucun rôle !");
							}
						} else {
							owner.sendMessage("§cCe joueur n'est pas connectée !");
						}
					} else {
						owner.sendMessage("§cIl faut cibler un joueur !");
					}
				} else {
					owner.sendMessage("§cLimite d'utilisation de cette commande atteinte !");
				}
			} else {
				owner.sendMessage("§7Vous n'êtes pas asser proche de§6 Kyubi§7 pour faire ceci.");
			}
		}
		if (args[0].equalsIgnoreCase("clone")) {
			if (this.villager == null) {
				if (cdClone <= 0) {
					this.villager = (Villager) owner.getLocation().getWorld().spawnEntity(owner.getLocation(), EntityType.VILLAGER);
					villager.setCustomName("§aNaruto");
					villager.setCustomNameVisible(true);
					villager.setAdult();
					EntityLiving nmsEntity = ((CraftLivingEntity) villager).getHandle();
					((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
					timeVillager = 0;
					villager.setProfession(Villager.Profession.PRIEST);
					new BukkitRunnable() {
						final Location loc = villager.getLocation().clone();
						@Override
						public void run() {
							if (villager == null) {
								cancel();
								return;
							}
							if (villager.isDead()) {
								cancel();
								return;
							}
							villager.teleport(loc);
						}
					}.runTaskTimer(Main.getInstance(), 0, 1);
					this.cdClone = Integer.MAX_VALUE;
				} else {
					sendCooldown(owner, this.cdClone);
				}
			} else {
				villager.damage(999.0, owner);
				villager = null;
				this.cdClone = 60*5;
				givePotionEffet(PotionEffectType.SPEED, 20*timeVillager, 1, true);
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*timeVillager, 1, true);
				timeVillager = 0;
				owner.sendMessage("§7Vous avez sacrifié votre§a clone§7, vous obtenez donc l'énergie qu'il avait accumulé jusqu'ici");
            }
		}
	}
	@Override
	public boolean onEntityDeath(EntityDeathEvent e, LivingEntity entity) {
		if (villager != null) {
			if (entity.getUniqueId().equals(villager.getUniqueId()) && e.getEntity().getLastDamage() < 100) {
				StringBuilder sb = new StringBuilder();
				for (Player p : Loc.getNearbyPlayersExcept(villager, 15)) {
					if (gameState.hasRoleNull(p))continue;
					if (!gameState.getInGamePlayers().contains(p.getUniqueId()))continue;
					if (p.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
					RoleBase role = gameState.getPlayerRoles().get(p);
					sb.append("§7 - ").append(role.getOriginTeam().getColor()).append(role.getName()).append("\n");
				}
				Player owner = Bukkit.getPlayer(getPlayer());
				if (owner != null) {
					owner.sendMessage("§7Votre§a clone§7 a été tué, il avait cependant eu le temp d'obtenir des informations, voici la liste des§c roles§7 proche du lui de sa mort:");
					owner.sendMessage(sb.toString());
					givePotionEffet(owner, PotionEffectType.SPEED, 20*timeVillager, 1, true);
				}
				villager = null;
				timeVillager = 0;
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(kyubiItem)) {
			if (cdKyubi <= 0) {
				if (AmitieKyubi <= 25) {
					givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*180, 1, false);
					cdKyubi = 60*8;
					new BukkitRunnable() {
						int i = 0;
						@Override
						public void run() {
							i++;
							if (i == 15) {
								owner.damage(1.0, owner);
								i = 0;
							}
							if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) || owner.hasPotionEffect(PotionEffectType.SPEED)) {
								owner.sendMessage("§7Les effets de§6 Kyubi§7 s'arrête");
								cancel();
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
				} else {
					if (AmitieKyubi <= 50) {
						givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*120, 1, false);
						givePotionEffet(PotionEffectType.SPEED, 20*120, 1, false);
						cdKyubi = 60*7;
						new BukkitRunnable() {
							int i = 0;
							@Override
							public void run() {
								i++;
								if (i == 20) {
									owner.damage(0.0);
									Heal(owner, -2);
									i = 0;
								}
								if (!owner.hasPotionEffect(PotionEffectType.SPEED) && !owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
									cancel();
									owner.sendMessage("§7Les effets de§6 Kyubi§7 s'arrête");
								}
							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
					} else {
						if (AmitieKyubi <= 80) {
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*120, 1, false);
							givePotionEffet(PotionEffectType.SPEED, 20*120, 1, false);
							addSpeedAtInt(owner, 10);
							setMaxHealth(getMaxHealth()+4.0);
							cdKyubi = 60*6;
							new BukkitRunnable() {
								int i = 0;
								@Override
								public void run() {
									i++;
									if (i == 30) {
										owner.damage(0.0);
										Heal(owner, -2);
										i = 0;
									}
									if (!owner.hasPotionEffect(PotionEffectType.SPEED) && !owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
										cancel();
										owner.sendMessage("§7Les effets de§6 Kyubi§7 s'arrête");
										addSpeedAtInt(owner, -10);
										setMaxHealth(getMaxHealth()+4.0);
									}
								}
							}.runTaskTimer(Main.getInstance(), 0, 20);
						} else {
							cdKyubi = 60*5;
							givePotionEffet(PotionEffectType.SPEED, 20*120, 2, false);
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*120, 1, false);
							setMaxHealth(getMaxHealth()+10.0);
							useSmell = false;
							new BukkitRunnable() {
								int i = 0;
								@Override
								public void run() {
									i++;
									if (gameState.getServerState() != ServerStates.InGame) {
										cancel();
									}
									if (i == 60*5) {
										owner.sendMessage("§7Vous n'êtes plus sous l'effet de§9§l Kurama");
										setMaxHealth(getMaxHealth()-10.0);
										cancel();
									}
								}
							}.runTaskTimer(Main.getInstance(), 0, 20);
							return true;
						}
					}
				}
			} else {
				sendCooldown(owner, cdKyubi);
				return true;
			}
		}
		if (item.isSimilar(rasenganItem)) {
			if (cdRasengan <= 0) {
				owner.sendMessage("§7Il faut frapper un joueur pour crée une explosion.");
            } else {
				sendCooldown(owner, cdRasengan);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (owner.getItemInHand().isSimilar(rasenganItem)) {
				if (cdRasengan <= 0) {
					fr.nicknqck.utils.particles.MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, victim.getLocation());
					Heal(victim, -4);
					victim.damage(0.0);
					Location loc = victim.getLocation();
					loc.add(new Vector(0.0, 1.8, 0.0));
					victim.teleport(loc);
					cdRasengan = 60*3;
				} else {
					sendCooldown(owner, cdRasengan);
				}
			}
			if (owner.getItemInHand().getType().name().contains("SWORD")) {
				if (AmitieKyubi < 100) {
					if(RandomUtils.getOwnRandomProbability(50)) {
						AmitieKyubi += RandomUtils.getRandomInt(1, 3);
						owner.sendMessage("§7Votre§a amitié§7 avec§6 Kyubi§7 est maintenant de §c"+AmitieKyubi+"%");
					}
				}
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (villager != null) {
			if (timeVillager < 300) {//300 = 5 minutes (60*5)
				timeVillager++;
			}
			if (timeVillager == 299) {
				owner.sendMessage("§7Votre§a clone§7 à accumulé le maximum possible de l'énergie qu'il pouvait accumulé.");
			}
		}
		if (cdClone >= 0) {
			cdClone--;
			if (cdClone == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser un§a clone");
			}
		}
		if (cdKyubi >= 0) {
			cdKyubi--;
			if (cdKyubi == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser le chakra de§6 Kyubi");
			}
		}
		if (cdRasengan >= 0) {
			cdRasengan--;
			if (cdRasengan == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§a Rasengan");
			}
		}
	}
	@Override
	public void resetCooldown() {
		cdRasengan = 0;
		cdKyubi = 0;
		useSmell = false;
		cdClone = 0;
		timeVillager = 0;
		villager = null;
	}
	@Override
	public String getName() {
		return "Naruto";
	}
}