package fr.nicknqck.events.ds;

import fr.nicknqck.GameListener;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.ds.demons.lune.Akaza;
import fr.nicknqck.roles.ds.slayers.pillier.KyojuroV2;
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
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;

public class AkazaVSKyojuro extends EventBase{
	private int i = 0;
	private Akaza akaza;
	private KyojuroV2 kyojuro;
	private Location originalAkazaLocation;
	private Location originalKyojuroLocation;
	private boolean AkazaWin = false;
	private boolean KyojuroWin = false;
	private boolean PouvoirSang = false;
	private int PouvoirSangCooldown = -1;
	private int BattleTime = 0;
	private int cdvague = -1;
	private boolean endEvent = false;
	private boolean realEnd = false;
	@Override
	public boolean PlayEvent(int gameTime) {
		if (gameState.attributedRole.contains(Roles.Akaza)) {
			if (gameState.attributedRole.contains(Roles.Kyojuro)) {
				if (!gameState.DeadRole.contains(Roles.Akaza)) {
					if (!gameState.DeadRole.contains(Roles.Kyojuro)) {
						ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					    Bukkit.dispatchCommand(console, "nakime E4jL5cOzv0sI2XqY7wNpD3Ab");
							if (Bukkit.getWorld("AkazaVSKyojuro") != null) {
								Player pkyojuro = gameState.getOwner(Roles.Kyojuro);
								Player pAkaza = gameState.getOwner(Roles.Akaza);
								if (pAkaza != null && pkyojuro != null) {
									Akaza akaza = (Akaza) gameState.getPlayerRoles().get(pAkaza);
									KyojuroV2 kyojuro = (KyojuroV2) gameState.getPlayerRoles().get(pkyojuro);
									this.akaza = akaza;
									this.kyojuro = kyojuro;
									this.originalAkazaLocation = pAkaza.getLocation();
									pAkaza.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -40, 6, -24.5, -90, 0));
									pAkaza.playSound(pAkaza.getEyeLocation(), "dsmtp.avsk", 10, 1);
									this.originalKyojuroLocation = pkyojuro.getLocation();
									pkyojuro.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -3.5, 7, -23.5, 90, 0));
									pkyojuro.playSound(pkyojuro.getLocation(), "dsmtp.avsk", 10, 1);
									Bukkit.getWorld("AkazaVSKyojuro").setGameRuleValue("randomTickSpeed", "3");
									Bukkit.broadcastMessage(getEvents().getName()+"§r vient de ce déclancher !");
									BattleTime = 0;
									return true;
								}
							}
					}
				}
			}
		}
		return super.PlayEvent(gameTime);
	}
	private ItemStack Vague() {
		return new ItemBuilder(Material.NETHER_STAR)
				.setName("§6Vague Flamboyante")
				.setLore("§r§7Pendant§b 10s§7 quand un joueur vous frappe il y aura 10% de chance qu'il§6 brûle§7 et subisse§c 1/2"+AllDesc.coeur)
				.toItemStack();
	}
	@Override
	public boolean onSubDSCommand(Player sender, String[] args) {
		if (args[0].equalsIgnoreCase("compa")) {
			if (akaza != null) {
				if (sender.getUniqueId().equals(akaza.getPlayer())) {
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
			return true;
		}
		return false;
	}
	@Override
	public void onItemInteract(PlayerInteractEvent event, ItemStack item, Player user) {
		if (isActivated()) {
			if (akaza != null && kyojuro != null) {
				if (item.isSimilar(Vague())) {
					if (user.getUniqueId() == kyojuro.getPlayer()&& KyojuroWin) {
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
	@Override
	public void onSecond() {
		if (isActivated()) {
			if (BattleTime <= 60*5 && !LocationNull()) {
				i++;
				BattleTime++;
				if (i == 60 && akaza != null && kyojuro != null) {
					i = 0;
					String msg = "§bTemps avant fin du combat:§c "+StringUtils.secondsTowardsBeautiful(300-BattleTime);
					Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
					if (akaza != null) {
						akaza.sendMessage(msg);
					}
					Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
					if (kyojuro != null) {
						kyojuro.sendMessage(msg);
					}
				}
			}
			if (realEnd && !LocationNull() && this.akaza != null && this.kyojuro != null) {
				Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
				Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
				if (kyojuro != null) {
					Location loc = GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld());
					kyojuro.teleport(loc);
					this.originalKyojuroLocation = null;
				}
				if (akaza != null) {
					Location loc = GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld());
					akaza.teleport(loc);
					this.originalAkazaLocation = null;
				}
			}
			if (PouvoirSangCooldown > 0) {
				PouvoirSangCooldown--;
				if (PouvoirSangCooldown == 60*8 && this.akaza != null) {
					PouvoirSang = false;
                    Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
					if (akaza != null) {
						akaza.sendMessage("§7Désactivation du§b compa");
					}
				}
			}
			if (PouvoirSangCooldown == 0 && akaza != null) {
				Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
				if (akaza != null) {
					akaza.sendMessage("§6§l/ds compa§7 est à nouveau utilisable !");
				}
				PouvoirSangCooldown = -5;
			}
			if (cdvague > 0) {
				cdvague--;
				if (cdvague == (60*7)+50) {
					if (kyojuro != null) {
						kyojuro.setInvincible(false);
						Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
						if (kyojuro != null) {
							kyojuro.sendMessage("§7Vous n'êtes plus invincible !");
						}
					}
				}
			}
			if (cdvague == 0) {
				if (kyojuro != null) {
					Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
					if (kyojuro != null) {
						kyojuro.sendMessage(Vague().getItemMeta().getDisplayName()+"§7 est maintenant utilisable !");
					}
					cdvague--;
				}
			}
			detectWhoWin();
		}
	}
	@SuppressWarnings("deprecation")
	private void detectWhoWin() {
		if (realEnd)return;
		boolean end = AkazaWin;
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
		if (!KyojuroWin && !AkazaWin && end && !LocationNull()) {
			subTitle = "§7§l§nMatch nul§r§7§l...";
			Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
			Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
			if (akaza == null)return;
			if (kyojuro == null) return;
			kyojuro.sendTitle(title, subTitle);
			akaza.sendTitle(title, subTitle);
			kyojuro.teleport(originalKyojuroLocation);
			akaza.teleport(originalAkazaLocation);
			originalAkazaLocation = null;
			originalKyojuroLocation = null;
		}
		if (originalAkazaLocation != null && akaza != null && end && AkazaWin) {
			Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
			if (akaza == null)return;
			akaza.teleport(originalAkazaLocation);
			originalAkazaLocation = null;
			akaza.sendTitle(title, subTitle);
		}
		if (originalKyojuroLocation != null && kyojuro != null && end && KyojuroWin) {
			Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
			if (kyojuro == null) return;
			kyojuro.teleport(originalKyojuroLocation);
			kyojuro.sendTitle(title, subTitle);
			originalKyojuroLocation = null;
			gameState.getPlayerRoles().get(kyojuro).giveItem(kyojuro, true, this.Vague());
		}
		if (end && !LocationNull() && !realEnd) {
			this.endEvent = true;
			Bukkit.broadcastMessage("Fin de l'évènement "+getEvents().getName());
			realEnd = true;
		}
	}
	private boolean LocationNull() {
        return originalAkazaLocation == null && originalKyojuroLocation == null;
    }
	@Override
	public void setupEvent() {
		setMinTime(GameState.getInstance().AkazaVsKyojuroTime);
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
				if (player.getUniqueId() == kyojuro.getPlayer()) {//donc if victim == kyojuro donc winner == Akaza
					akaza.givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
					AkazaWin = true;
					PouvoirSangCooldown = 0;
					PouvoirSang = false;
					Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
					if (akaza == null)return;
					akaza.sendMessage("§7Vous avez gagné votre 1v1 contre§a§l Kyojuro§r§7 !");
				}
				if (player.getUniqueId() == akaza.getPlayer()) {//donc if victim == akaza, donc winner == Kyojuro
					KyojuroWin = true;
					cdvague = 0;
					Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
					if (kyojuro == null)return;
					this.kyojuro.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
					kyojuro.sendMessage("§7Vous avez gagné votre 1v1 contre§c§l Akaza§r§7 !");

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
						if (player.getUniqueId() == kyojuro.getPlayer()) {
							if (damageur instanceof Player) {
								Player attacker = (Player) damageur;
								if (RandomUtils.getOwnRandomProbability(10)) {
									attacker.damage(0.0);
									if (attacker.getHealth() > 1.0) {
										attacker.setHealth(attacker.getHealth()-1.0);
									} else {
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
				if (PouvoirSang && player.getUniqueId().equals(akaza.getPlayer())) {
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
