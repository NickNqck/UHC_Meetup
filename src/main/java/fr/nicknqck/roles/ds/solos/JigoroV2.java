package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.roles.JigoroV2ChoosePacteEvent;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.roles.ds.slayers.ZenItsu;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.UUID;

public class JigoroV2 extends DemonsSlayersRoles {

	public JigoroV2(UUID player) {
		super(player);
		pacte = Pacte.Non_Choisis;
		setCanuseblade(true);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p) instanceof Kaigaku) {
					owner.sendMessage("La personne possédant le rôle de§c Kaigaku§r est:§c "+p.getName());
				}
			}
		}, 20);
		setLameIncassable(owner, true);
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}
	@Override
	public Roles getRoles() {
		return Roles.JigoroV2;
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : gameState.getInGamePlayers()) {
				if (getPlayerRoles(p) instanceof ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p) instanceof Kaigaku) {
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

	@Override
	public String getName() {
		return "Jigoro§7 (§6V2§7)";
	}

	public enum Pacte{
		Non_Choisis,
		Pacte1,
		Pacte2,
		Pacte3
	}
	@NonNull
	private Pacte pacte;
	private int speedCD = 0;
	private boolean killzen = false;
	private boolean killkai = false;
	private boolean killtwo = false;
	private Kaigaku kaigaku;
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
			JigoroV2ChoosePacteEvent choosePacteEvent = new JigoroV2ChoosePacteEvent(pacte);
			if (item.isSimilar(GUIItems.getJigoroPacte1())) {
				pacte = Pacte.Pacte1;
				choosePacteEvent.setPacte(pacte);
			}
			if (item.isSimilar(GUIItems.getJigoroPacte2())) {
				pacte = Pacte.Pacte2;
				choosePacteEvent.setPacte(pacte);
			}
			if (item.isSimilar(GUIItems.getJigoroPacte3())) {
				pacte = Pacte.Pacte3;
				choosePacteEvent.setPacte(pacte);
			}
			Bukkit.getPluginManager().callEvent(choosePacteEvent);
			if (!choosePacteEvent.isCancelled()) {
				switch (choosePacteEvent.getPacte()) {
					case Pacte1:
						owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
						owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
						break;
					case Pacte2:
						owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
						for (Player p : gameState.getInGamePlayers()) {//p = les gens en jeux
							if (!gameState.hasRoleNull(p)) {//vérifie que p a un role
								if (gameState.getPlayerRoles().get(p) instanceof Kaigaku) {//si p est kaigaku
									owner.sendMessage(p.getName()+" est§c Kaigaku");
									kaigaku = (Kaigaku) gameState.getPlayerRoles().get(p);
									gameState.getPlayerRoles().get(p).setTeam(TeamList.Jigoro);
									setTeam(TeamList.Jigoro);
									p.sendMessage("Le joueur§6 "+owner.getName()+"§r est§6 Jigoro");
									kaigaku.getEffects().put(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
									p.sendMessage("Vous avez rejoint la team "+getPlayerRoles(p).getOriginTeam().name());
									kaigaku.owner.sendMessage("Votre pacte avec votre Sensei Jigoro vous à offert l'effet Speed 1 permanent");
									gameState.JigoroV2Pacte2 = true;
									owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
									break;
								}
							}
						}
						break;
					case Pacte3:
						owner.sendMessage("Vous avez choisis le Pacte§6 "+pacte.name());
						gameState.JigoroV2Pacte3 = true;
						owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
						for (Player p : gameState.getInGamePlayers()) {//p = les gens en jeux
							if (gameState.getPlayerRoles().containsKey(p)) {//vérifie que p a un role
								if (gameState.getPlayerRoles().get(p) instanceof ZenItsu) {//si p est ZenItsu
									owner.sendMessage(p.getName()+" est§a ZenItsu");
									zen = p;
									gameState.getPlayerRoles().get(p).setTeam(TeamList.Jigoro);
									setTeam(TeamList.Jigoro);
									p.sendMessage("Le joueur§6 "+owner.getName()+"§r est§6 Jigoro");
									p.sendMessage("Vous avez rejoint la team "+getPlayerRoles(p).getOriginTeam().name());

									p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
									owner.sendMessage("La commande§6 /ds me§r à été mis-à-jour !");
								}
							}
						}
						break;
					default:
						break;
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
						zforce = true;
					}
				} else {
					if (zforce) {
						zforce = false;
					}
				}
				if (!zresi) {
					addresi(20);
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
				if (kaigaku.getTeam() == TeamList.Jigoro) {
					if (kaigaku.getGamePlayer().isAlive() && gameState.getInGamePlayers().contains(owner)) {
						if (owner.getLocation().distance(owner.getLocation()) <= 50) {
							owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false));
							kaigaku.givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
							if (!resi) {
								addresi(20);
								resi = true;
							}
							if (!kresi) {
								kaigaku.addresi(20);
								kresi = true;
							}
						} else {
							if (kresi) {
								kaigaku.addresi(-20);
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
							if (role instanceof ZenItsu) {
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
							if (role instanceof Kaigaku) {
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
						kaigaku.owner.sendMessage(msg);
						kaigaku.setMaxHealth(kaigaku.getMaxHealth()+1.0);
						setMaxHealth(getMaxHealth()+1.0);
						owner.updateInventory();
						kaigaku.owner.updateInventory();
					}
				}
			}
		}
		if (pacte == Pacte.Pacte3) {
			if (killer == owner || killer == zen) {
				if (!killkai) {
					if (getPlayerRoles(victim) instanceof Kaigaku) {
						owner.sendMessage(ChatColor.GOLD+killer.getName()+"§r à tué§c Kaigaku§r ce qui vous permet d'avoir§6 Speed 2 en dessous de 5§c❤§r");
						killkai = true;
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
}