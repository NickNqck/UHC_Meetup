package fr.nicknqck.events.ds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;

public class AkazaVSKyojuro extends EventBase{

	@Override
	public boolean PlayEvent(int gameTime) {
		if (gameState.attributedRole.contains(Roles.Akaza)) {
			if (gameState.attributedRole.contains(Roles.Kyojuro)) {
				if (!gameState.DeadRole.contains(Roles.Akaza)) {
					if (!gameState.DeadRole.contains(Roles.Kyojuro)) {
						ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					    Bukkit.dispatchCommand(console, "nakime E4jL5cOzv0sI2XqY7wNpD3Ab");
							if (Bukkit.getWorld("AkazaVSKyojuro") != null) {
								Player kyojuro = gameState.getOwner(Roles.Kyojuro);
								Player akaza = gameState.getOwner(Roles.Akaza);
								if (akaza != null) {
									this.akaza = akaza;
									this.originalAkazaLocation = akaza.getLocation();
									akaza.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -40, 6, -24.5, -90, 0));
									akaza.playSound(akaza.getEyeLocation(), "dsmtp.avsk", 10, 1);
								}
								if (kyojuro != null) {
									this.kyojuro = kyojuro;
									this.originalKyojuroLocation = kyojuro.getLocation();
									kyojuro.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -3.5, 7, -23.5, 90, 0));
									kyojuro.playSound(kyojuro.getLocation(), "dsmtp.avsk", 10, 1);
								}
								Bukkit.getWorld("AkazaVSKyojuro").setGameRuleValue("randomTickSpeed", "3");
								Bukkit.broadcastMessage(getEvents().getName()+"§r vient de ce déclancher !");
								BattleTime = 0;
							}
					}
				}
			}
		}
		return super.PlayEvent(gameTime);
	}
	private Player akaza;
	private Player kyojuro;
	private Location originalAkazaLocation;
	private Location originalKyojuroLocation;
	
	private boolean AkazaWin = false;
	private boolean KyojuroWin = false;
	private boolean PouvoirSang = false;
	private int PouvoirSangCooldown = -1;
	private int BattleTime = 0;
	@Override
	public void OnPlayerKilled(Player player, Player victim, GameState gameState) {}
	private ItemStack Vague() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§6Vague Flamboyante")
				.setLore("§r§7Pendant§b 10s§7 quand un joueur vous frappe il y aura 10% de chance qu'il§6 brûle§7 et subisse§c 1/2"+AllDesc.coeur)
				.toItemStack();
	}
	private int cdvague = -1;
	@Override
	public void onSubDSCommand(Player sender, String[] args) {
		if (args[0].equalsIgnoreCase("compa")) {
			if (akaza != null) {
				if (sender == akaza) {
					if (AkazaWin) {
						if (PouvoirSang) {
							PouvoirSang = false;
							PouvoirSangCooldown = 60*8;
							sender.sendMessage("§7Désactivation du§b compa");
						} else {
							if (PouvoirSangCooldown <= 0) {
								PouvoirSang = true;
								PouvoirSangCooldown = 60*10;
								sender.sendMessage("§7Activation du§b compa");
							} else {
								gameState.getPlayerRoles().get(sender).sendCooldown(sender, PouvoirSangCooldown);
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onItemInteract(PlayerInteractEvent event, ItemStack item, Player user) {
		if (isActivated()) {
			if (akaza != null && kyojuro != null) {
				if (item.isSimilar(Vague())) {
					if (user == kyojuro && KyojuroWin) {
						if (cdvague <= 0) {
							user.sendMessage("§7Maintenant vous§c enflammez§7 les joueurs qui vous frappe pendant§b 10s");
							cdvague = 60*8;
						} else {
							gameState.getPlayerRoles().get(user).sendCooldown(user, cdvague);
						}
					}
				}
			}
		}
	}
	int i = 0;
	@Override
	public void onSecond() {
		if (isActivated()) {
			if (BattleTime <= 60*5 && !LocationNul()) {
				i++;
				BattleTime++;
				if (i == 60 && akaza != null && kyojuro != null) {
					i = 0;
					String msg = "§bTemps avant fin du combat:§c "+StringUtils.secondsTowardsBeautiful(300-BattleTime);
					akaza.sendMessage(msg);
					kyojuro.sendMessage(msg);
				}
			}
			if (PouvoirSangCooldown > 0) {
				PouvoirSangCooldown--;
				if (PouvoirSangCooldown == 60*8) {
					PouvoirSang = false;
					akaza.sendMessage("§7Désactivation du§b compa");
				}
			}
			if (PouvoirSangCooldown == 0 && akaza != null) {
				akaza.sendMessage("§6§l/ds compa§7 est à nouveau utilisable !");
				PouvoirSangCooldown = -5;
			}
			if (cdvague > 0) {
				cdvague--;
				if (cdvague == (60*7)+50) {
					if (kyojuro != null) {
						gameState.getPlayerRoles().get(kyojuro).setInvincible(false);
						kyojuro.sendMessage("§7Vous n'êtes plus invincible !");
					}
				}
			}
			if (cdvague == 0) {
				if (kyojuro != null) {
					kyojuro.sendMessage(Vague().getItemMeta().getDisplayName()+"§7 est maintenant utilisable !");
					cdvague--;
				}
			}
			detectWhoWin();
		}
	}
	@SuppressWarnings("deprecation")
	private void detectWhoWin() {
		if (realEnd)return;
		boolean end = false;
		if (AkazaWin) {
			end = true;
		}
		if (KyojuroWin) {
			end = true;
		}
		if (BattleTime == 60*5) {
			end = true;
		}
		String title = "§cLe combat est fini";
		String subTitle = "";
		if (AkazaWin) {
			subTitle = "§7Victoire de§c Akaza";
		}
		if (KyojuroWin) {
			subTitle = "§7Victoire de§a Kyojuro";
		}
		if (!KyojuroWin && !AkazaWin && end && BattleTime >= 60*5 && !LocationNul()) {
			subTitle = "§7§l§nMatch nul§r§7§l...";
			kyojuro.sendTitle(title, subTitle);
			akaza.sendTitle(title, subTitle);
			kyojuro.teleport(originalKyojuroLocation);
			akaza.teleport(originalAkazaLocation);
			originalAkazaLocation = null;
			originalKyojuroLocation = null;
		}
		if (originalAkazaLocation != null && akaza != null && end && AkazaWin) {
			akaza.teleport(originalAkazaLocation);
			originalAkazaLocation = null;
			akaza.sendTitle(title, subTitle);
		}
		if (originalKyojuroLocation != null && kyojuro != null && end && KyojuroWin) {
			kyojuro.teleport(originalKyojuroLocation);
			kyojuro.sendTitle(title, subTitle);
			originalKyojuroLocation = null;
			gameState.getPlayerRoles().get(kyojuro).giveItem(kyojuro, true, this.Vague());
		}
		if (end && LocationNul() && !realEnd) {
			this.endEvent = true;
			Bukkit.broadcastMessage("Fin de l'évènement "+getEvents().getName());
			realEnd = true;
		}
	}
	boolean realEnd = false;
	private boolean LocationNul() {
		if (originalAkazaLocation == null ||originalKyojuroLocation == null) {
			return true;
		}
		return false;
	}
	private boolean endEvent = false;
	@Override
	public void setupEvent() {
		setTime(GameState.getInstance().AkazaVsKyojuroTime);
	}
	@Override
	public Events getEvents() {
		return Events.AkazaVSKyojuro;
	}
	@Override
	public int getProba() {
		return GameState.getInstance().AkazaVSKyojuroProba;
	}
	@Override
	public void onPlayerKilled(Entity damager, Player player, GameState gameState) {
		if (isActivated() && !endEvent) {
			if (akaza != null && kyojuro != null) {
				if (player.getUniqueId() == kyojuro.getUniqueId()) {//donc if victim == kyojuro donc winner == Akaza
					akaza.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), true);
					AkazaWin = true;
					akaza.sendMessage("§7Vous avez gagné votre 1v1 contre§a§l Kyojuro§r§7 !");
					PouvoirSangCooldown = 0;
					PouvoirSang = false;
				}
				if (player.getUniqueId() == akaza.getUniqueId()) {//donc if victim == akaza, donc winner == Kyojuro
					kyojuro.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), true);
					KyojuroWin = true;
					kyojuro.sendMessage("§7Vous avez gagné votre 1v1 contre§c§l Akaza§r§7 !");
					cdvague = 0;
				}
			}
		}
	}
	@Override
	public void resetCooldown() {
		cdvague = -1;
		akaza = null;
		kyojuro = null;
		originalAkazaLocation = null;
		originalKyojuroLocation = null;
		BattleTime = 0;
		i = 0;
		PouvoirSang = false;
		PouvoirSangCooldown = -1;
		AkazaWin = false;
		KyojuroWin = false;
		endEvent= false;
		realEnd = false;
	}
	@Override
	public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event, Player player, Entity damageur) {
		if (isActivated()) {
			if (kyojuro != null) {
				if (cdvague >= (60*7)+50) {
						if (player == kyojuro) {
							if (damageur instanceof Player) {
								Player attacker = (Player) damageur;
								if (RandomUtils.getOwnRandomProbability(10)) {
									attacker.damage(0.0);
									if (attacker.getHealth() > 1.0) {
										attacker.setHealth(attacker.getHealth()-1.0);
									}else {
										attacker.setHealth(1.0);
									}
									attacker.setFireTicks(attacker.getFireTicks()+100);
									attacker.sendMessage("§aKyojuro§7 vous à§6§l enflammé");
									player.sendMessage("§7Vous avez§6 brulé§7§l "+attacker.getDisplayName());
								}
							}
						}
				}
			}
			if (akaza != null) {
				if (PouvoirSang && player == akaza) {
					if (PouvoirSangCooldown >= 60*8) {
						if (RandomUtils.getOwnRandomProbability(10)) {
							event.setDamage(0.0);
							player.sendMessage("§7Vous avez esquivé les dégats d'une attaque !");
						}
					}
				}
			}
		}//if isActivated		
	}
	
}