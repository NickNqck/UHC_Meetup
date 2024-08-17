package fr.nicknqck.events.ds.dkt;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DemonKing extends EventBase{

	@Override
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == getMinTime() && !gameState.demonKingTanjiro) {
			for (UUID u : gameState.getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
				if (!gameState.hasRoleNull(p)) {
					RoleBase roleBase = gameState.getPlayerRoles().get(p);
					if (gameState.attributedRole.contains(Roles.Tanjiro) && gameState.attributedRole.contains(Roles.Muzan)) {
						if (gameState.DeadRole.contains(Roles.Muzan) && !gameState.DeadRole.contains(Roles.Tanjiro) && roleBase instanceof DemonsSlayersRoles) {
							DemonsSlayersRoles role = (DemonsSlayersRoles) roleBase;
							if (role instanceof Tanjiro) {
								setActivated(true);
								gameState.delInPlayerRoles(p);
								gameState.addInPlayerRoles(p, new DemonKingTanjiroRole(p.getUniqueId()));
								RoleBase newRole = gameState.getPlayerRoles().get(p);
								Main.getInstance().getGetterList().getDemonList(p);
								if (role.getLames().equals(Lames.Coeur)) {
									newRole.setMaxHealth(24.0);
								}else {
									newRole.setMaxHealth(20.0);
								}
								if (newRole instanceof DemonsSlayersRoles){
									((DemonsSlayersRoles) newRole).setLames(role.getLames());
								}
								newRole.setGamePlayer(role.getGamePlayer());
								newRole.getGamePlayer().setRole(newRole);
								p.sendMessage("Votre arrivé dans le camp des§c "+TeamList.Demon.name()+"s§f restera secrète jusqu'à "+StringUtils.secondsTowardsBeautiful(gameTime+60));
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
									Bukkit.broadcastMessage(AllDesc.bar+"\n§rL'évènement aléatoire "+Events.DemonKingTanjiro.getName()+" viens de ce déclancher, le rôle§c Tanjiro§f est maintenant dans le camp des Démons !\n"+AllDesc.bar);
									gameState.demonKingTanjiro = true;
									if (RandomUtils.getOwnRandomProbability(50)) {
										for (UUID uk : gameState.getInGamePlayers()) {
											Player k = Bukkit.getPlayer(uk);
											if (k == null)continue;
											if (gameState.attributedRole.contains(Roles.Kokushibo)) {
												if (!gameState.hasRoleNull(k)) {
													if (gameState.getPlayerRoles().get(k) instanceof Kokushibo) {
														RoleBase koku = gameState.getPlayerRoles().get(k);
														Kokushibo ko = (Kokushibo) koku;
														Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> ko.owner.sendMessage("§7Vous sentez des pulsions montez en vous..."), 20);
														Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> ko.owner.sendMessage("§7Vous devennez de plus en plus aigri..."), 20*5);
														Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
															ko.setTeam(TeamList.Solo);
															ko.orginalMaxHealth = ko.getMaxHealth();
															koku.owner.sendMessage(" \n§7La nouvelle de la mort de§c Muzan§7 et maintenant un simple pourfendeur devenue le chef des§c démons§7 qu'elle honte, vous explosez de rage et décidé de§l tué§7 tout le monde\n ");
															if (((Kokushibo) koku).getLames().equals(Lames.Coeur)) {
																koku.setMaxHealth(34.0);
															}else {
																koku.setMaxHealth(30.0);
															}
															koku.Heal(k, role.getMaxHealth());
															k.sendMessage("Vous posséderez maintenant l'effet "+AllDesc.Resi+" 1 pendant 3 minutes en tuant un joueur");
															ko.solo = true;
														}, 20*10);
													}
												}
											} else {
												break;
											}
										}
									}
								}, 20*60);
								return true;
							}
							
						}else {
							this.setMinTime(getMinTime()+60*5);
							setActivated(false);
							return false;
						}
					}					
				}
			}			
		}
		return super.PlayEvent(gameTime);
	}
	@Override
	public void OnPlayerKilled(Player player, Player victim, GameState gameState) {}
	@Override
	public void setupEvent() {
		setMinTime(GameState.getInstance().DKminTime);
	}
	@Override
	public Events getEvents() {
		return Events.DemonKingTanjiro;
	}
	@Override
	public int getProba() {
		return GameState.getInstance().DKTProba;
	}
	@Override
	public void onItemInteract(PlayerInteractEvent event, ItemStack itemstack, Player player) {}
	@Override
	public void onPlayerKilled(Entity damager, Player player, GameState gameState2) {}
	@Override
	public void onSecond() {}
	@Override
	public void resetCooldown() {}
	@Override
	public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event, Player player, Entity damageur) {}
	@Override
	public boolean onSubDSCommand(Player sender, String[] args) {return false;}
}