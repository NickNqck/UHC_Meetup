package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
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
	public @NonNull TeamList getOriginTeam() {
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
				OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 2, true);
				OLDgivePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
			}else {
				OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			}
		}else {
			if (gameState.nightTime) {
				OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				OLDgivePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
			}else {
				OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
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
						final GamePlayer gamePlayer = gameState.getGamePlayer().get(player.getUniqueId());
						if (gamePlayer.getRole() instanceof DemonsSlayersRoles && gamePlayer.getRole().getOriginTeam() == TeamList.Demon
								|| gamePlayer.getRole() instanceof NezukoV2) {
							getGamePlayer().sendMessage("Vous avez donné le Pouvoir de l'infection à§c "+player.getName());
							player.sendMessage("Le grand§c Muzan§r vous à donné le pouvoir de l'infection, faite s'en bonne usage...");
							gamePlayer.getRole().addPower(new InfectPower((DemonsRoles) gamePlayer.getRole()));
						} else {
							owner.sendMessage("§c"+args[1]+"§7 ne peut pas recevoir l'§cinfection");
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
	public void resetCooldown() {}

	@Override
	public String getName() {
		return "Muzan";
	}
	private static class InfectPower extends CommandPower {

		public InfectPower(@NonNull DemonsRoles role) {
			super("/ds infection <joueur>", "infection", new Cooldown(-500), role, CommandType.DS,
					"§7Cette§c commande§7 vous permet d'§cinfecter§7 le joueur cibler dans le camp des§c Démons§7 à condition");
			setMaxUse(1);
		}

		@Override
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
			final String[] args = (String[]) map.get("args");
			if (args.length == 2) {
				final Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					player.sendMessage("§cLe joueur viser n'existe pas ou n'est pas connecté");
					return false;
				}
				if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
					player.sendMessage("§cLe joueur viser n'a pas de rôle, impossible de l'infecter");
					return false;
				}
				final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
				final GameState gameState = role.getGameState();
				new InfectionRunnable(gameState, (DemonsRoles) getRole(), role);
				return true;
			}
			return false;
		}
		private static final class InfectionRunnable extends BukkitRunnable {

			private final GameState gameState;
			private final DemonsRoles roleInfecteur;
			private final RoleBase roleTarget;
			private int timeRemaining;

            private InfectionRunnable(GameState gameState, DemonsRoles roleInfecteur, RoleBase roleTarget) {
                this.gameState = gameState;
                this.roleInfecteur = roleInfecteur;
                this.roleTarget = roleTarget;
				this.timeRemaining = Main.getInstance().getGameConfig().getInfectionTime();
            }

            @Override
			public void run() {
				if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				if (timeRemaining <= 0) {
					final Player target = Bukkit.getPlayer(roleTarget.getPlayer());
					if (target == null)return;
					if (!target.isOnline())return;
					procInfection(target);
					cancel();
					return;
				}
				timeRemaining--;
			}
			@SuppressWarnings("deprecation")
			private void procInfection(final Player target) {
				target.resetTitle();
				target.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
				target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false));

				target.sendMessage(target.getName()+" à été infecté");
				for (final UUID u : gameState.getInGamePlayers()) {
					final Player z = Bukkit.getPlayer(u);
					if (z == null)continue;
					if (gameState.hasRoleNull(u)) continue;
					if (gameState.getGamePlayer().get(u).getRole().getOriginTeam().equals(TeamList.Demon)) {
						z.sendMessage("§4Un joueur à été infecté et à rejoins le camp des§c Démons");
					}
				}
				this.roleInfecteur.getGamePlayer().sendMessage(target.getName()+" à été infecté");
				if (gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeam() != TeamList.Slayer) {
					target.sendMessage("Vous avez été §cinfecté§f mais comme vous n'étiez pas du camp§a Slayer§r vous n'avez pas pus être§c infecté§f, vous restez donc dans votre camp d'origine");
				}
				if (gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeam() == TeamList.Slayer) {
					gameState.getGamePlayer().get(target.getUniqueId()).getRole().setTeam(TeamList.Demon);
				}
				target.resetTitle();
				target.sendMessage("Voici l'identité de votre§c infecteur§f:§c§l "+this.roleInfecteur.getGamePlayer().getPlayerName());
				target.sendTitle("§cVous avez été infecté", "Vous gagnez maintenant avec les Démons");
				this.roleTarget.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
			}
		}
	}
}