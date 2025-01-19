package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.InfectItem;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Muzan extends DemonsRoles {
	private boolean killnez = false;
	private int regencooldown;
	public Muzan(UUID player) {
		super(player);
		regencooldown = 10;
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Muzan;
	}
	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getDemonList(owner);
		return AllDesc.Muzan;
	}
	private boolean hasBoost = false;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setResi(20);
		getGamePlayer().startChatWith("§cMuzan: ", "!", Kokushibo.class);
	}
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
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof NezukoV2) {
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
						if (gameState.hasRoleNull(target.getUniqueId()))return;
						GamePlayer gamePlayer = gameState.getGamePlayer().get(target.getUniqueId());
						if (gamePlayer.getRole().getOriginTeam().equals(TeamList.Demon) ||gamePlayer.getRole().getTeam().equals(TeamList.Demon)) {
							target.sendMessage("§cMuzan§7 vous à offert son boost de§c 10% de force");
							hasBoost = true;
							owner.sendMessage("§7Vous avez donné§c 10% de§c force§7 à§l "+target.getName());
							gamePlayer.getRole().addBonusforce(10);
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
					if (player == null) {
						owner.sendMessage("§c"+args[1]+" n'est pas connecter");
						return;
					}
						if (gameState.getInGamePlayers().contains(player.getUniqueId()) && !gameState.hasRoleNull(player.getUniqueId())) {
							if (gameState.infected == null && gameState.infecteur == null) {
								GamePlayer gamePlayer = gameState.getGamePlayer().get(player.getUniqueId());
								if (gamePlayer.getRole() instanceof DemonsSlayersRoles && gamePlayer.getRole().getOriginTeam() == TeamList.Demon || gamePlayer.getRole() instanceof NezukoV2) {
									giveItem(player, false, Items.getInfection());
									owner.sendMessage("Vous avez donné le Pouvoir de l'infection à§c "+player.getName());
									player.sendMessage("Le grand§c Muzan§r vous à donné le pouvoir de l'infection, faite s'en bonne usage...");
									gameState.infecteur = player;
									InfectItem.getInstance().setInfecteur((DemonsSlayersRoles) gamePlayer.getRole());
								} else {
									owner.sendMessage("§c"+args[1]+"§7 ne peut pas recevoir l'§cinfection");
								}
							} else {
								owner.sendMessage("Il y à déjà quelqu'un qui possède le pouvoir de l'§cinfection§r (§c"+InfectItem.getInstance().getInfecteur().owner.getName()+"§r)");
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

	@Override
	public String getName() {
		return "Muzan";
	}
}