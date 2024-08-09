package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class HantenguV2 extends DemonsRoles {

	public HantenguV2(UUID player) {
		super(player);
		clone = Clone.Hantengu;
		owner.getInventory().addItem(Items.getMaterialisationEmotion());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
			}
		}, 20);
	}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.HantenguV2;
	}
	enum Clone {
		Hantengu, Karaku, Sekido, Urogi, Urami, Aizetsu, Zohakuten
	}

	@Override
	public String getName() {
		return "Hantengu§7 (§6V2§7)";
	}

	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
			}
		}, 20);
		return AllDesc.HantenguV2;
	}
	Clone clone;
	private boolean firstchoice = false;
	private boolean secondchoice = false;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
			Items.getMaterialisationEmotion()	
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (clone == Clone.Hantengu) {
			if (!owner.hasPotionEffect(PotionEffectType.WEAKNESS)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
			}
			if (gameState.isApoil(owner)) {
				if (!owner.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
				}
				if (!owner.hasPotionEffect(PotionEffectType.SPEED)) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
				}
				if (!isHasNoFall()) {
					setNoFall(true);
				}
			} else {
				if (owner.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
				if (owner.hasPotionEffect(PotionEffectType.SPEED)) {
					owner.removePotionEffect(PotionEffectType.SPEED);
				}
				if (isHasNoFall()) {
					setNoFall(false);
				}
			}
		}
		if (clone == Clone.Karaku) {
			if (!owner.hasPotionEffect(PotionEffectType.SPEED)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
			}
			if (timeinKaraku == 0) {
				clone = Clone.Hantengu;
				owner.removePotionEffect(PotionEffectType.SPEED);
				owner.getInventory().remove(Items.getHantenguKarakuVent());
				owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
			} else if (timeinKaraku >= 1){
				timeinKaraku-=1;
				NMSPacket.sendActionBar(owner, "§bTemp restant en temp que§6 "+clone.name()+"§r "+StringUtils.secondsTowardsBeautiful(timeinKaraku));
			}
		}
		if (clone == Clone.Sekido) {
			if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
			}
			if (timeinSekido >= 1) {
				timeinSekido-=1;
				NMSPacket.sendActionBar(owner, "§b Temp restant en temp que§6 "+clone.name()+"§r "+StringUtils.secondsTowardsBeautiful(timeinSekido));
			} else {
				clone = Clone.Hantengu;
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.getInventory().remove(Items.getHantenguSekidoKakkhara());
				owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
			}
		}
		if (clone == Clone.Urami) {
			if (!owner.hasPotionEffect(PotionEffectType.WEAKNESS)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
			}
			if (!owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
			}
			if (timeinUrami >= 1) {
				timeinUrami-=1;
				NMSPacket.sendActionBar(owner, "§b Temp restant en temp que§6 "+clone.name()+"§r "+StringUtils.secondsTowardsBeautiful(timeinUrami));
			} else {
				clone = Clone.Hantengu;
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
				if (getResi() != 0) {
					setResi(0);
				}
			}			
		}
		if (clone == Clone.Urogi) {
			if (!owner.hasPotionEffect(PotionEffectType.SPEED)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
			}
			if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
			}
			if (urogi) {
				givePotionEffet(owner, PotionEffectType.JUMP, 20, 4, true);
			}
			if (!isHasNoFall()) {
				setNoFall(true);
			}
			if (timeinUrogi >= 1) {
				timeinUrogi--;
				NMSPacket.sendActionBar(owner, "§b Temp restant en temp que§6 "+clone.name()+"§r "+StringUtils.secondsTowardsBeautiful(timeinUrogi));
			} else {
				if (timeinUrogi == 0) {
					clone = Clone.Hantengu;
					setNoFall(false);
					owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
					owner.removePotionEffect(PotionEffectType.SPEED);
					owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					owner.getInventory().remove(Items.getHantenguUrogi());
					owner.getInventory().remove(Items.getHantenguUrogiCri());
					}
			}
			if (fly) {
				if (flytime >= 1) {
					owner.setAllowFlight(true);
					flytime--;
					owner.sendMessage("Temp de Fly restant: "+flytime);
				}else {
					fly = false;
					owner.setAllowFlight(false);
					owner.sendMessage("Désactivation de votre Fly");
				}
			}
		}
		if (clone == Clone.Aizetsu) {
			if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
			}
			if (invincibletime >= 1) {
				invincibletime-=1;
				owner.sendMessage("Vous êtes§6 invincible§r pendant encore§6 "+StringUtils.secondsTowardsBeautiful(invincibletime));
			} else {
				if (isInvincible()) {
					setInvincible(false);
					owner.sendMessage("Vous n'êtes plus invincible");
				}
			}
			if (timeinAizetsu >= 1) {
				timeinAizetsu-=1;
				NMSPacket.sendActionBar(owner, "§b Temp restant en temp que§6 Aizetsu§r "+StringUtils.secondsTowardsBeautiful(timeinAizetsu));
			} else {
				clone = Clone.Hantengu;
				owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.getInventory().remove(Items.getHantenguAizetsuEpee());
				setMaxHealth(getMaxHealth()-4.0);
			}
		}
		if (clone == Clone.Zohakuten) {
			if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
			}
			if (!owner.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
			}
			if (getResi() != 20) {
				setResi(20);
			}
			if (regentime == 0) {
				if (owner.getHealth() <= (getMaxHealth() - 1.0)) {
					owner.setHealth(owner.getHealth()+1.0);
				} else {
					owner.setHealth(getMaxHealth());
				}
				regentime = 15;
			} else {
				if (regentime >= 1) {
					regentime-=1;
				}
			}
			if (timeinZohakuten >= 1) {
				timeinZohakuten--;
				NMSPacket.sendActionBar(owner, "§b Temp restant en temp que§6 "+clone.name()+"§r "+StringUtils.secondsTowardsBeautiful(timeinZohakuten));
			} else {
				clone = Clone.Hantengu;
				owner.sendMessage("Vous êtes redevenue le pitoyable§6 Hantengu");
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				setResi(0);
				ItemStack giveitem = null;
				if (fcKaraku) {
					giveitem  = Items.getHantenguKarakuVent();
					owner.getInventory().remove(giveitem);
					owner.sendMessage("Vous avez perdu l'item: "+giveitem.getItemMeta().getDisplayName());
					fcKaraku = false;
				}
				if (fcSekido) {
					giveitem = Items.getHantenguSekidoKakkhara();
					owner.getInventory().remove(giveitem);
					owner.sendMessage("Vous avez perdu l'item: "+giveitem.getItemMeta().getDisplayName());						
					fcSekido = false;
				}
				if (scUrogi) {
					giveitem = Items.getHantenguUrogiCri();
					owner.getInventory().remove(giveitem);
					owner.sendMessage("Vous avez perdu l'item: "+giveitem.getItemMeta().getDisplayName());
					scUrogi = false;
				}
				if (scUrami) {
					setMaxHealth(getMaxHealth()-4.0);
					owner.sendMessage("Vous avez perdu 2"+AllDesc.coeur+" permanent");
					scUrami = false;
				}
				if (scAizetsu) {
					giveitem = Items.getHantenguAizetsuEpee();
					owner.getInventory().remove(giveitem);
					owner.sendMessage("Vous avez perdu l'item: "+giveitem.getItemMeta().getDisplayName());
					scAizetsu = false;
				}
				firstchoice = false;
				secondchoice = false;
				fcKaraku = false;
				fcSekido = false;
				timeinUrami = 0;
				hasrespawnasUrami = true;
				setCanRespawn(false);
			}
		}
		
		if (cdkaraku >= 1) {cdkaraku--;}
		if (cdkaraku == 0) {
			owner.sendMessage(Items.getHantenguKarakuVent().getItemMeta().getDisplayName()+"§f est à nouveau utilisable !");
			cdkaraku--;
		}
		if (cdyari >=1)cdyari--;
		if (cdyari == 0) {
			owner.sendMessage(Items.getHantenguAizetsuEpee().getItemMeta().getDisplayName()+"§f est à nouveau utilisable !");
			cdyari--;
		}
		if (cdcri >= 0) cdcri-=1;
		if (cdcri == 0)owner.sendMessage(Items.getHantenguUrogiCri().getItemMeta().getDisplayName()+"§f est à nouveau utilisable !");
		if (cdAizetsu >= 0) cdAizetsu-=1;
		if (cdAizetsu == 0) owner.sendMessage(Items.getHantenguAizetsu().getItemMeta().getDisplayName()+"§f est à nouveau utilisable !");
		if (invincibletime > 0) {
			invincibletime--;
			owner.sendMessage("Temp avant fin d'invincibilité:§6 "+cd(invincibletime));
		}else if (invincibletime == 0) {
			owner.sendMessage("Vous n'êtes plus invincible");
			setInvincible(false);
			invincibletime-=1;
		}
		super.Update(gameState);
	}
	private int regentime = 0;
	private int cdkaraku = 0;
	private int cdsekido = 0;
	private boolean fcKaraku = false;
	private boolean fcSekido = false;
	private boolean scUrogi = false;
	private boolean scUrami = false;
	private boolean scAizetsu = false;
	private int flytime = 8;
	@Override
	public void resetCooldown() {
		flytime = 8;
		scAizetsu = false;
		scUrami = false;
		scUrogi = false;
		fcKaraku = false;
		fcSekido = false;
		clone = Clone.Hantengu;
		cdyari = 0;
		invincibletime = 0;
		cdcri = 0;
		timeinKaraku = 0;
		timeinSekido = 0;
		timeinUrami = 0;
		timeinUrogi = 0;
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getMaterialisationEmotion())) {
			if (clone == Clone.Hantengu) {
				Inventory inv = Bukkit.createInventory(owner, 9, "Choix de forme");
				if (!firstchoice) {
					inv.setItem(3, Items.getHantenguKaraku());
					inv.setItem(5, Items.getHantenguSekido());
					owner.openInventory(inv);
				} else {
					if (!secondchoice) {
						if (fcKaraku) {
							inv.setItem(3, Items.getHantenguUrogi());
							if (getMaxHealth() > 7.0) {
								inv.setItem(5, Items.getHantenguUrami());
							}
							owner.openInventory(inv);
						}
						if (fcSekido) {
							if (getMaxHealth() > 7.0) {
								inv.setItem(3, Items.getHantenguUrami());
							}
							inv.setItem(5, Items.getHantenguAizetsu());
							owner.openInventory(inv);
						}
					} else {
						inv.setItem(4, Items.getHantenguZohakuten());
						owner.openInventory(inv);
					}
				}	
			} else {
				owner.sendMessage("Il faut être§6 Hantengu§r pour changer de clone");
			}
		}
		if (item.isSimilar(Items.getHantenguKarakuVent())) {
			if (firstchoice) {
				if (clone == Clone.Karaku || clone == Clone.Zohakuten) {
					if (cdkaraku <= 0) {
						for (Player player : gameState.getInGamePlayers()) {
							if (player != owner && player.getWorld().equals(owner.getWorld())) {
								if (player.getLocation().distance(owner.getLocation()) <= 30) {
									Location ploc1 = player.getLocation();
									Location spawn1 = new Location(player.getWorld(), ploc1.getX(),
									ploc1.getY() + 30, ploc1.getZ());
									Location loc = player.getLocation();
									System.out.println(player.getEyeLocation());
									loc.setX(loc.getX()+ Math.cos(Math.toRadians(-player.getEyeLocation().getYaw() + 90)));
									loc.setZ(loc.getZ()+ Math.sin(Math.toRadians(player.getEyeLocation().getYaw() - 90)));
									loc.setPitch(0);
									System.out.println(loc);
									System.out.println(spawn1);
									player.teleport(spawn1);
									player.sendMessage("Vous venez d'être téléporter par Karaku");
									cdkaraku = 60;
								}
							}
						}
					} else {
						owner.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cdkaraku));
					}
				} else {
					owner.sendMessage("Vous n'êtes pas avec le bon clone pour utiliser: "+item.getItemMeta().getDisplayName());
				}
			}
		}
		if (item.isSimilar(Items.getHantenguSekidoKakkhara())) {
			if (firstchoice) {
				if (clone == Clone.Sekido || clone == Clone.Zohakuten) {
					if (cdsekido <= 0) {
						for (Player p : gameState.getInGamePlayers()) {
							if (p != owner) {
								if (getPlayerRoles(p).getOriginTeam() != TeamList.Demon) {
									if (p.getLocation().distance(owner.getLocation()) <= 25) {
										if (p.getHealth() > 4.0) {
											p.setHealth(p.getHealth() - 4.0);
										} else {
											p.setHealth(0.5);
										}
										cdsekido = 90;
										p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*4, 0));
										p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*4, 3));
										gameState.spawnLightningBolt(p.getWorld(), p.getLocation());
										p.sendMessage("Vous avez été toucher par le§6 Kakkhara§r de§6 Sekido");
										owner.sendMessage("Vous avez toucher le joueur:§a "+p.getName()+"§r avec votre§6 Kakkhara");
									}
								}
							}
						}
					} else {
						owner.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cdsekido));
					}
				}
			}
		}
		if (item.isSimilar(Items.getHantenguUrogi())) {
			if (firstchoice && secondchoice) {
				if (clone == Clone.Urogi) {
					if (owner.isSneaking()) {
						if (urogi) {
							if (!fly) {
								if (flytime>0) {
									fly = true;
									owner.sendMessage("Vous avez§6 "+flytime+"§rs pour voler");
								}else {
									owner.sendMessage("Vous n'avez plus la permission de fly");
								}
							} else {
								fly = false;
								owner.sendMessage("Vous avez désactivé votre fly");
								owner.setAllowFlight(false);
							}
						}else {
							owner.sendMessage("Il faut avoir activé§6 Urogi§r pour utiliser cette capacité");
						}
					}else {
						if (!urogi) {
							owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, false, false));
							owner.sendMessage("Vous avez activé votre§a Jump Boost 4");
							urogi = true;
						} else {
							owner.removePotionEffect(PotionEffectType.JUMP);
							owner.sendMessage("Vous avez désactivé votre§a Jump Boost 4");
							urogi = false;
						}	
					}					
				}
			}
		}
		if (item.isSimilar(Items.getHantenguUrogiCri())) {
			if (firstchoice && secondchoice) {
				if (clone == Clone.Urogi || clone == Clone.Zohakuten) {
					if (cdcri <= 0) {
						Player t = getRightClicked(30, 1);
						if (t == null || getPlayerRoles(t).getOriginTeam() == TeamList.Demon) {
							owner.sendMessage("§cVeuiller viser un joueur");
						} else {
							Player player = (Player) t;
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*30, 0, false, false), true);
							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*30, 0, false, false), true);
							player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*30, 0, false, false), true);
							cdcri = 120;
							owner.sendMessage("Vous avez crier sur§6 "+player.getName());
						}	
					} else {
						owner.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cdcri));
					}
				}
			}
		}
		if (item.isSimilar(Items.getHantenguAizetsuEpee())) {
			if (firstchoice && secondchoice) {
				if (clone == Clone.Aizetsu || clone == Clone.Zohakuten) {
					if (cdyari <= 0) {
						invincibletime = 5;
						setInvincible(true);
						owner.sendMessage("Vous êtes maintenant invincible pendant§6 5s");
						cdyari = 30;
					} else {
					owner.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cdyari));	
					}			
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	private int cdyari = 0;
	private int invincibletime = 0;
	private int cdcri = 0;
	private int timeinKaraku =0;
	private int timeinSekido = 0;
	private int timeinUrami = 0;
	private int timeinUrogi = 0;
	private boolean hasrespawnasUrami = false;
	private boolean urogi = false;
	private boolean fly = false;

	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (!firstchoice) {
			if (item.isSimilar(Items.getHantenguKaraku())) {
				clone = Clone.Karaku;
				firstchoice = true;
				owner.closeInventory();
				owner.getInventory().addItem(Items.getHantenguKarakuVent());
				owner.updateInventory();
				timeinKaraku = 60*2;
				owner.removePotionEffect(PotionEffectType.SPEED);
				owner.removePotionEffect(PotionEffectType.WEAKNESS);
				owner.removePotionEffect(PotionEffectType.INVISIBILITY);
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				setNoFall(false);
				fcKaraku = true;
				owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinKaraku)+"§r de temp en temp que§c Karaku");
			}
			if (item.isSimilar(Items.getHantenguSekido())) {
				clone = Clone.Sekido;
				firstchoice = true;
				owner.closeInventory();
				fcSekido = true;
				owner.getInventory().addItem(Items.getHantenguSekidoKakkhara());
				owner.updateInventory();
				timeinSekido = 60*2;
				setNoFall(false);
				owner.removePotionEffect(PotionEffectType.SPEED);
				owner.removePotionEffect(PotionEffectType.WEAKNESS);
				owner.removePotionEffect(PotionEffectType.INVISIBILITY);
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinSekido)+"§r de temp en temp que§c Sekido");
			}
		} else {
			if (!secondchoice) {
				if (item.isSimilar(Items.getHantenguUrami())) {
					clone = Clone.Urami;
					secondchoice = true;
					scUrami = true;
					owner.closeInventory();
					if (getResi() != 40) {
						setResi(40);
					}
					setNoFall(false);
					owner.removePotionEffect(PotionEffectType.SPEED);
					owner.removePotionEffect(PotionEffectType.WEAKNESS);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					if (hasrespawnasUrami) {
						hasrespawnasUrami = false;
					}
					setCanRespawn(true);
					timeinUrami = 60*3;
					owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinUrami)+"§r de temp en temp que§c Urami");
					owner.updateInventory();
				}
				if (item.isSimilar(Items.getHantenguUrogi())) {
					clone = Clone.Urogi;
					secondchoice = true;
					scUrogi = true;
					setNoFall(false);
					owner.removePotionEffect(PotionEffectType.SPEED);
					owner.removePotionEffect(PotionEffectType.WEAKNESS);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					owner.closeInventory();
					timeinUrogi = 60*3;
					owner.getInventory().addItem(Items.getHantenguUrogi());
					owner.getInventory().addItem(Items.getHantenguUrogiCri());
					owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinUrogi)+"§r de temp en temp que§c Urogi");
					owner.updateInventory();
				}
				if (item.isSimilar(Items.getHantenguAizetsu())) {
					clone = Clone.Aizetsu;
					secondchoice = true;
					scAizetsu = true;
					setMaxHealth(getMaxHealth() + 4.0);
					setNoFall(false);
					owner.removePotionEffect(PotionEffectType.SPEED);
					owner.removePotionEffect(PotionEffectType.WEAKNESS);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					owner.closeInventory();
					timeinAizetsu = 60*3;
					owner.getInventory().addItem(Items.getHantenguAizetsuEpee());
					owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinAizetsu)+"§r de temp en temp que§c Aizetsu");
					owner.updateInventory();
				}
			} else {
				if (item.isSimilar(Items.getHantenguZohakuten())) {
					clone = Clone.Zohakuten;
					setNoFall(false);
					owner.removePotionEffect(PotionEffectType.SPEED);
					owner.removePotionEffect(PotionEffectType.WEAKNESS);
					owner.removePotionEffect(PotionEffectType.INVISIBILITY);
					owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					timeinZohakuten = 60*5;
					owner.sendMessage("Vous avez§6 "+StringUtils.secondsTowardsBeautiful(timeinZohakuten)+"§r de temp en temp que§c Zohakuten");
					owner.updateInventory();
					ItemStack giveitem = null;
					if (fcKaraku) {
						giveitem  = Items.getHantenguKarakuVent();
						owner.getInventory().addItem(giveitem);
						owner.sendMessage("Vous avez reçus l'item: "+giveitem.getItemMeta().getDisplayName());
					}
					if (fcSekido) {
						giveitem = Items.getHantenguSekidoKakkhara();
						owner.getInventory().addItem(giveitem);
						owner.sendMessage("Vous avez reçus l'item: "+giveitem.getItemMeta().getDisplayName());						
					}
					if (scUrogi) {
						giveitem = Items.getHantenguUrogiCri();
						owner.getInventory().addItem(giveitem);
						owner.sendMessage("Vous avez reçus l'item: "+giveitem.getItemMeta().getDisplayName());	
					}
					if (scUrami) {
						setMaxHealth(getMaxHealth()+4.0);
						owner.sendMessage("Vous avez reçus 2§c❤§r permanent§r supplémentaire pendant§6 "+StringUtils.secondsTowardsBeautiful(timeinZohakuten));
						setCanRespawn(false);
					}
					if (scAizetsu) {
						giveitem = Items.getHantenguAizetsuEpee();
						owner.getInventory().addItem(giveitem);
						owner.sendMessage("Vous avez reçus l'item: "+giveitem.getItemMeta().getDisplayName());	
					}
				}
			}
		}
		super.FormChoosen(item, gameState);
	}
	int timeinZohakuten = 0;
	int timeinAizetsu = 0;
	int cdAizetsu = 0;
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (clone == Clone.Urami) {
			if (victim == owner) {
				if (isCanRespawn()) {
					clone = Clone.Hantengu;
					firstchoice = false;
					secondchoice = false;
					fcKaraku = false;
					fcSekido = false;
					timeinUrami = 0;
					hasrespawnasUrami = true;
					setCanRespawn(false);
					if (getMaxHealth() > 6.0) {
						setMaxHealth(getMaxHealth() - 6.0);
					} else {
						setMaxHealth(2.0);
					}
					owner.sendMessage("Vous avez réussis à mourir en temp qu'§6Urami§r vous réssuciter donc en temp qu'§cHantengu§r mais vous avez perdu§c 3❤§r permanent");
					GameListener.RandomTp(victim, Main.getInstance().getWorldManager().getGameWorld());
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
}