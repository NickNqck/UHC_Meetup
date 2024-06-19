package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.roles.builder.DemonType;
import fr.nicknqck.roles.builder.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.ZenItsu;
import fr.nicknqck.roles.ds.solos.JigoroV2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;

public class Kaigaku extends DemonsRoles {

	private boolean killzen = false;
	private int cooldownquatriememouvement = 0;
	private int cooldowntroisiememouvement = 0;
	public Kaigaku(Player player) {
		super(player);
		owner.sendMessage(AllDesc.Kaigaku);
		this.setForce(20);
		this.setCanUseBlade(true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p) instanceof ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
			}
		}, 20);
		gameState.lunesup.add(owner);
		gameState.addLuneSupPlayers(owner);
		setLameIncassable(owner, true);
	}

	@Override
	public DemonType getRank() {
		return DemonType.LuneSuperieur;
	}

	@Override
	public Roles getRoles() {
		return Roles.Kaigaku;
	}
	@Override
	public void resetCooldown() {
		cooldownquatriememouvement = 0;
		cooldowntroisiememouvement = 0;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleFoudre3iememouvement(),
				Items.getSoufleFoudre4iememouvement()
		};
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (Player p : getIGPlayers()) {
				if (getPlayerRoles(p) instanceof Muzan) {
					owner.sendMessage("La personne possédant le rôle de§c Muzan§r est:§c "+p.getName());
				}
				if (getPlayerRoles(p) instanceof ZenItsu) {
					owner.sendMessage("La personne possédant le rôle de§a ZenItsu§r est:§a "+p.getName());
				}
				if (getPlayerRoles(p) instanceof JigoroV2 && gameState.JigoroV2Pacte2) {
					owner.sendMessage("La personne possédant le rôle de§6 Jigoro§r est:§6 "+p.getName());
				}
			}
		}, 20);
		return AllDesc.Kaigaku;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSoufleFoudre3iememouvement());
		owner.getInventory().addItem(Items.getSoufleFoudre4iememouvement());
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}

	@Override
	public String getName() {
		return "§cKaigaku";
	}

	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre4iememouvement())) {
			sendActionBarCooldown(owner, cooldownquatriememouvement);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre3iememouvement())) {
			sendActionBarCooldown(owner, cooldowntroisiememouvement);
		}
		if (cooldownquatriememouvement >= 1) {
			cooldownquatriememouvement--;
		}	
		if (cooldowntroisiememouvement >= 1) {
			cooldowntroisiememouvement--;
		}
		if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false));
		}
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof ZenItsu) {
							killzen = true;						
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+role.getRoles()+" "+ChatColor.GRAY+"vous obtenez donc sa capacité qui est que quand vous mettez un coup avec votre épée il y à 1 chance sur 10 que la cible ce prenne "+ChatColor.RED+"1 coeur de dégat"+ChatColor.GRAY+" via un éclair");
						
						}
					}
				}
			}
			if (gameState.JigoroV2Pacte2) {
				if (gameState.getInGamePlayers().contains(victim) && gameState.getInGamePlayers().contains(killer)) {
					if (gameState.getPlayerRoles().containsKey(victim) || gameState.getPlayerRoles().containsKey(killer)) {
						if (killer == owner) {
							String msg = "Vous avez reçus 1 demi"+Main.RH()+" permanent car§6 Jigoro§r ou§6 Kaigaku à fait un kill";
							owner.sendMessage(msg);
							setMaxHealth(getMaxHealth()+1.0);
							owner.updateInventory();
							for (Player p : gameState.getInGamePlayers()) {
								if (gameState.getPlayerRoles().containsKey(p)) {
									if (gameState.getPlayerRoles().get(p) instanceof JigoroV2) {
										Player jigoro = gameState.getPlayerRoles().get(p).owner;
										jigoro.sendMessage(msg);
										getPlayerRoles(jigoro).setMaxHealth(getPlayerRoles(jigoro).getMaxHealth()+1.0);
										jigoro.updateInventory();
									}
								}
							}
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleFoudre3iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldowntroisiememouvement <= 0) {
				cooldowntroisiememouvement = 5*60;
				owner.sendMessage(ChatColor.GREEN+"Exécution du"+ChatColor.GOLD+" Troisème mouvement du soufle de la foudre.");
				for(Player p : gameState.getInGamePlayers()) {
					if (p != owner) {
						if (getPlayerRoles(p).getTeam() != getPlayerRoles(owner).getTeam()) {
							  if(p.getLocation().distance(owner.getLocation()) <= 30) {
								  if (p.getHealth() > 4.0) {
										p.setHealth(p.getHealth() - 4.0);
									} else {
										p.setHealth(0.5);
									}
								  if (!gameState.JigoroV2Pacte2) {
									  owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*(60*3), 0, false, false));
								  }
							    	owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + p.getName());
							    	p.sendMessage(ChatColor.GREEN+"Vous avez été touchez le Troisième mouvement du soufle de la foudre de:"+ChatColor.GOLD+" Kaigaku");
							        gameState.spawnLightningBolt(p.getWorld(), p.getLocation());
							    }	
						}
					}
				}					
			}  else {
				sendCooldown(owner, cooldowntroisiememouvement);
			}
		}
		if (item.isSimilar(Items.getSoufleFoudre4iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (cooldownquatriememouvement <= 0) {
				double min = 25;
				Player target = getTargetPlayer(owner, min);
					if (target != null) {
						if (owner.canSee(target)) {
							Location loc = target.getLocation();
							System.out.println(target.getEyeLocation());
							loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
							loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
							loc.setPitch(0);
							System.out.println(loc);
							owner.teleport(loc);
							target.getWorld().strikeLightning(target.getLocation());
							if (target.getHealth() > 4.0) {
								target.setHealth(target.getHealth() - 4.0);
							} else {
								target.setHealth(0.5);
							}
							owner.sendMessage(ChatColor.GREEN+"Exécution du"+ChatColor.GOLD+" Quatrième mouvement du soufle de la foudre");
							cooldownquatriememouvement = 60*3;
							target.sendMessage(ChatColor.WHITE+"Vous avez été touché par un soufle de la foudre");
							owner.teleport(loc);

						}	
					} else {
						owner.sendMessage("§cIl faut viser un joueur !");
					}
				} else {
					sendCooldown(owner, cooldownquatriememouvement);
				}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (killzen) {
			if (item.getType().name().contains("SWORD")) {
				if (RandomUtils.getOwnRandomProbability(10)) {
					for(Player p : gameState.getInGamePlayers()) {
						if (p != owner && p == victim) {
							Heal(victim, -4);
						    owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + victim.getName());
						    victim.sendMessage(ChatColor.GREEN+"Vous avez été foudroyez par "+ChatColor.GOLD+"Kaigaku");
						    victim.getWorld().strikeLightningEffect(victim.getLocation());
						}
					}
					
				}
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
}