package fr.nicknqck.roles.ds.solos;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.StringUtils;

public class JigoroV2 extends RoleBase{
GameState gameState;
	public JigoroV2(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(AllDesc.JigoroV2);
		this.gameState = gameState;
		pacte = Pacte.Non_Choisis;
		setCanUseBlade(true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p).type == Roles.Kaigaku) {
					owner.sendMessage("La personne possédant le rôle de§c Kaigaku§r est:§c "+p.getName());
				}
			}
		}, 20);
		setLameIncassable(owner, true);
	}
	
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p).type == Roles.ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p).type == Roles.Kaigaku) {
					owner.sendMessage("La personne possédant le rôle de§c Kaigaku§r est:§c "+p.getName());
				}
			}
		}, 20);
		if (pacte == Pacte.Pacte1) {
			return AllDesc.JigoroV2Pacte1;
		}
		if (pacte == Pacte.Pacte2) {
			return AllDesc.JigoroV2Pacte2;
		}
		if (pacte == Pacte.Pacte3) {
			return AllDesc.JigoroV2Pacte3;
		}
		return AllDesc.JigoroV2;
	}
	enum Pacte{
		Non_Choisis,
		Pacte1,
		Pacte2,
		Pacte3
	}
	Pacte pacte;
	private int speedCD = 0;
	private boolean killzen = false;
	private boolean killkai = false;
	private boolean killtwo = false;
	private Player kaigaku;
	private Player zen;
	@Override
		public ItemStack[] getItems() {
			return new ItemStack[] {
					Items.getVitesse()
			};
		}
	@Override
	public void resetCooldown() {
		speedCD = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getVitesse());
		super.GiveItems();
	}
	@Override
	public void OpenFormInventory(GameState gameState) {
		if (pacte == Pacte.Non_Choisis) {
			Inventory inv = Bukkit.createInventory(owner, 9, "Choix de forme");
			inv.setItem(2, GUIItems.getJigoroPacte1());
			inv.setItem(4, GUIItems.getJigoroPacte2());
			inv.setItem(6, GUIItems.getJigoroPacte3());
			owner.openInventory(inv);
		} else {
			owner.sendMessage("Vous devez regretter votre Pacte pour vouloir le changer...");
		}
		super.OpenFormInventory(gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (pacte == Pacte.Non_Choisis) {
			if (item.isSimilar(GUIItems.getJigoroPacte1())) {
				pacte = Pacte.Pacte1;
				owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
				addforce(20);
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
				owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
			}
			if (item.isSimilar(GUIItems.getJigoroPacte2())) {
				pacte = Pacte.Pacte2;
				owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
				for (Player p : gameState.getInGamePlayers()) {//p = les gens en jeux
					if (!gameState.hasRoleNull(p)) {//vérifie que p a un role
						if (gameState.getPlayerRoles().get(p).type == Roles.Kaigaku) {//si p est kaigaku
							owner.sendMessage(p.getName()+" est§c Kaigaku");
							kaigaku = p;
							gameState.getPlayerRoles().get(p).setTeam(TeamList.Jigoro);
							setTeam(TeamList.Jigoro);
							p.sendMessage("Le joueur§6 "+owner.getName()+"§r est§6 Jigoro");
							kaigaku.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
							p.sendMessage("Vous avez rejoint la team "+getPlayerRoles(p).getTeam().name());
							kaigaku.sendMessage("Votre pacte avec votre Sensei Jigoro vous à offert l'effet Speed 1 permanent");
							gameState.JigoroV2Pacte2 = true;
							owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
						}
					}
				}
			}
			if (item.isSimilar(GUIItems.getJigoroPacte3())) {
				pacte = Pacte.Pacte3;
				owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
				gameState.JigoroV2Pacte3 = true;
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
				for (Player p : gameState.getInGamePlayers()) {//p = les gens en jeux
					if (gameState.getPlayerRoles().containsKey(p)) {//vérifie que p a un role
						if (gameState.getPlayerRoles().get(p).type == Roles.ZenItsu) {//si p est ZenItsu
							owner.sendMessage(p.getName()+" est§a ZenItsu");
							zen = p;
							gameState.getPlayerRoles().get(p).setTeam(TeamList.Jigoro);
							setTeam(TeamList.Jigoro);
							p.sendMessage("Le joueur§6 "+owner.getName()+"§r est§6 Jigoro");
							p.sendMessage("Vous avez rejoint la team "+getPlayerRoles(p).getTeam().name());
							
							p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
							owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
						}
					}
				}
			}
		}
		super.FormChoosen(item, gameState);
	}
	boolean zforce = false;
	@Override
	public void Update(GameState gameState) {
		if (pacte == Pacte.Pacte3) {
			if (zen != null) {
				if (owner.getLocation().distance(zen.getLocation()) <= 20.0 && gameState.getInGamePlayers().contains(owner) && gameState.getInGamePlayers().contains(zen)) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));
					zen.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));
					if (!zforce) {
						addforce(20);
						zforce = true;
					}
				} else {
					if (zforce) {
						addforce(-20);
						zforce = false;
					}
				}
				if (!zresi) {
					addresi(20);
					zresi = true;
				}
				if (!zresi) {
					getPlayerRoles(zen).addresi(20);
					zresi = true;
				}
				if (gameState.getInGamePlayers().contains(zen)) {
					DecimalFormat df = new DecimalFormat("0");
					NMSPacket.sendActionBar(owner, "§aZenItsu§r: "+df.format(owner.getLocation().distance(zen.getLocation()))+ArrowTargetUtils.calculateArrow(owner, zen.getLocation()));
					NMSPacket.sendActionBar(zen, "§6Jigoro§r: "+df.format(zen.getLocation().distance(owner.getLocation()))+ArrowTargetUtils.calculateArrow(zen, owner.getLocation()));	
				}
			}
			if (speedCD <90*9) {
				if (!killkai) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
				} else {
					if (owner.getHealth() <= 10.0) {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 1, false, false));	
					} else {
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
					}
				}
			}
			if (speedCD >= 1)speedCD--;
		}
		if (pacte == Pacte.Pacte2) {
			if (speedCD <90*9) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
			}
			if (speedCD >= 1)speedCD--;
			if (kaigaku != null) {
				if (!gameState.hasRoleNull(kaigaku)) {
					if (gameState.getPlayerRoles().get(kaigaku).type == Roles.Kaigaku) {
						if (gameState.getPlayerRoles().get(kaigaku).getTeam() == TeamList.Jigoro) {
							if (gameState.getInGamePlayers().contains(kaigaku) && gameState.getInGamePlayers().contains(owner)) {
								if (owner.getLocation().distance(owner.getLocation()) <= 50) {
									owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
									kaigaku.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
									if (!resi) {
										addresi(20);
										resi = true;
									}
									if (!kresi) {
										getPlayerRoles(kaigaku).addresi(20);
										kresi = true;
									}
								} else {
									if (kresi) {
										getPlayerRoles(kaigaku).addresi(-20);
										kresi = false;
									}
									if (resi) {
										addresi(-20);
										resi = false;
									}
								}
							}						
						}
					}
				}
			}
		}
		if (pacte == Pacte.Pacte1) {
			if (killzen && !killtwo) {
				if (!gameState.nightTime) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
				}
			}
			if (killkai && !killtwo) {
				if (gameState.nightTime) {
					owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
				}
			}
			if (killzen && killkai && !killtwo) {
				killtwo = true;
				owner.sendMessage("On dirait que vous avez réussit à tuer vos deux disciple, vous êtes vraiment un être cruel !");
			}
			if (killtwo) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false));
			}
			if (speedCD >= 1)speedCD--;
			if (owner.getItemInHand().isSimilar(Items.getVitesse())) {
				if (speedCD > 0) {
					NMSPacket.sendActionBar(owner, "Cooldown:§6 "+StringUtils.secondsTowardsBeautiful(speedCD));
				} else {
					NMSPacket.sendActionBar(owner, owner.getItemInHand().getItemMeta().getDisplayName()+"§r Utilisable");
				}
			}
			if (speedCD <90*9) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
			}
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item != null) {
				if (item.isSimilar(Items.getVitesse())) {
					if (speedCD <= 0) {
						owner.removePotionEffect(PotionEffectType.SPEED);
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false));
						owner.sendMessage("Vous venez d'activer votre§6 Speed 3");
						speedCD = 60*10;
					} else {
						owner.sendMessage("Veuiller attendre encore "+StringUtils.secondsTowardsBeautiful(speedCD));
					}
				}
		}
		return super.ItemUse(item, gameState);
	}
	boolean resi = false;
	boolean kresi = false;
	boolean zresi = false;
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (pacte == Pacte.Pacte1) {
			if (killer == owner) {
				if (victim != owner){
					if (gameState.getInGamePlayers().contains(victim)) {
						if (gameState.getPlayerRoles().containsKey(victim)) {
							RoleBase role = gameState.getPlayerRoles().get(victim);
							if (role.type == Roles.ZenItsu) {
								if (!killzen) {
									addSpeedAtInt(owner, 10);
									owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Zen'Itsu "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"résistance 1 le jour"+ChatColor.GRAY+", ainsi que "+ChatColor.GOLD+"10% de Speed");
									killzen = true;
									if (!resi) {
										addresi(20);
										resi = true;
									}
								}					
							}
							if (role.type == Roles.Kaigaku) {
								if (!killkai) {
									killkai = true;
									owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Kaigaku "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"résistance 1 la nuit"+ChatColor.GRAY+", ainsi que "+ChatColor.GOLD+"10% de Speed");
									addSpeedAtInt(owner, 10);
									if (!resi) {
										addresi(20);
										resi = true;
									}
								}
							}
						}
					}
				}
			}	
		}
		if (pacte == Pacte.Pacte2) {
			if (gameState.getInGamePlayers().contains(victim) && gameState.getInGamePlayers().contains(killer)) {
				if (gameState.getPlayerRoles().containsKey(victim) || gameState.getPlayerRoles().containsKey(killer)) {
					if (killer == owner) {
						String msg = "Vous avez reçus 1 demi§c❤§r permanent car§6 Jigoro§r ou§6 Kaigaku à fait un kill";
						owner.sendMessage(msg);
						kaigaku.sendMessage(msg);
						getPlayerRoles(kaigaku).setMaxHealth(getPlayerRoles(kaigaku).getMaxHealth()+1.0);
						setMaxHealth(getMaxHealth()+1.0);
						owner.updateInventory();
						kaigaku.updateInventory();
					}
				}
			}
		}
		if (pacte == Pacte.Pacte3) {
			if (killer == owner || killer == zen) {
				if (!killkai) {
					if (getPlayerRoles(victim).type == Roles.Kaigaku) {
						owner.sendMessage(ChatColor.GOLD+killer.getName()+"§r à tué§c Kaigaku§r ce qui vous permet d'avoir§6 Speed 2 en dessous de 5§c❤§r");
						killkai = true;
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
}