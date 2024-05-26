package fr.nicknqck.roles.ds.demons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.TeamList;
import fr.nicknqck.roles.desc.AllDesc;

public class Muzan extends RoleBase {
	boolean killnez = false;
	int regencooldown = 0;
	public Muzan(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(AllDesc.Muzan);
		regencooldown = 10;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			owner.sendMessage("§cLa liste des démons est : ");
            gameState.lunesup.forEach(lambda -> owner.sendMessage("§c" + lambda.getName()));
		}, 20);
		addforce(20);//pour contre balancer le onday qui retire 20% de force
		}
	@Override
	public String[] Desc() {
		List<Player> a = new ArrayList<>();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			owner.sendMessage("§cLa liste des démons est : ");
			for (Player p : gameState.lunesup) {
				if (!gameState.getInSpecPlayers().contains(p)) {
					 a.add(p);
				}
			} //gameState.lunesup.forEach(lambda -> owner.sendMessage("§c" + lambda.getName()));
			a.forEach(ee -> owner.sendMessage("§c"+ee.getName()));
		}, 20);
		return AllDesc.Muzan;
	}
	private boolean hasBoost = false;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void RoleGiven(GameState gameState) {setResi(20);}
	@Override
	public void onNight(GameState gameState) {addforce(20);if(killnez)setResi(40);}
	@Override
	public void onDay(GameState gameState) {if (!killnez)addforce(-20);}
	@Override
	public void Update(GameState gameState) {
		if (killnez) {
			if (gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 2, true);
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
			}else {
				givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			}
		}else {
			if (gameState.nightTime) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
			}else {
				givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			}
		}
		if ((owner.getHealth() != owner.getMaxHealth())) {
			if (owner.getHealth() != this.getMaxHealth()) {
				if (regencooldown >= 1){regencooldown--;}
				if (regencooldown == 0) {
					//owner.sendMessage(ChatColor.GREEN+"Vous venez de régénérer 1 demi-coeur suite à votre pouvoir de régénération");
					regencooldown = 10;
					Heal(owner, 1.0);
				}
			}
			
		}
		super.Update(gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role.type == Roles.Nezuko) {
							killnez = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Nezuko "+ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"force 1 le jour ainsi que speed 1 la nuit"+ChatColor.GRAY+", votre "+ChatColor.GOLD+"PouvoirSanginaire "+ChatColor.GRAY+"c'est également amélioré vous offrant"+ChatColor.RED+" Speed 1 le jour et Résistance 2 la nuit");
						}
					}
				}
			}
		}
	super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("boost")) {
			if (args.length == 2 && args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					if (!hasBoost) {
						if (gameState.hasRoleNull(target))return;
						if (getPlayerRoles(target).getOldTeam().equals(TeamList.Demon)) {
							target.sendMessage("§cMuzan§7 vous à offert son boost de§c 10% de force");
							hasBoost = true;
							owner.sendMessage("§7Vous avez donné§c 10% de force§7 à§l "+target.getName());
							getPlayerRoles(target).addBonusforce(10);
						}else {
							owner.sendMessage("Vous ne pouvez pas boost un humain !");
						}
					} else {
						owner.sendMessage("§7Vous avez déjà boost...");
					}
				}else {
					owner.sendMessage("§7Veuiller cibler un joueur connecter...");
				}
			} else {
				owner.sendMessage("§7Veuiller cibler un joueuer...");
			}
		}
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length == 1) {
				owner.sendMessage("Cette commande prend un joueur en compte");
			}
			if (args.length == 2) {
				if (args[1] != null) {
					Player player = Bukkit.getPlayer(args[1]);
						if (gameState.getInGamePlayers().contains(player) && !gameState.hasRoleNull(player)) {
							if (gameState.infected == null && gameState.infecteur == null) {
								if (getPlayerRoles(player).getOldTeam() == TeamList.Demon || getPlayerRoles(player).type == Roles.Nezuko) {
									giveItem(player, false, Items.getInfection());
									owner.sendMessage("Vous avez donné le Pouvoir de l'infection à§c "+player.getName());
									player.sendMessage("Le grand§c Muzan§r vous à donné le pouvoir de l'infection, faite s'en bonne usage...");
									gameState.infecteur = player;
								}		
							} else {
								owner.sendMessage("Il y à déjà quelqu'un qui possède le pouvoir de l'§cinfection§r (§c"+gameState.infecteur.getName()+"§r)");
							}
						} else {
							owner.sendMessage("La personne visée n'a pas de rôle ou n'est pas en jeu");
						}
				} else {
					owner.sendMessage("Il faut renseigner le nom d'un joueur");
				}
			}
		}
	}
	@Override
	public void resetCooldown() {
	}
}