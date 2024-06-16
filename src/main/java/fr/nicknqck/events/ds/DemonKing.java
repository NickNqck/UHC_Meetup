package fr.nicknqck.events.ds;

import fr.nicknqck.roles.ds.slayers.Tanjiro;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.EventBase;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.utils.StringUtils;

public class DemonKing extends EventBase{

	@Override
	public boolean PlayEvent(int gameTime) {
		if (!isActivated() && gameTime == getTime() && !gameState.demonKingTanjiro) {			
			for (Player p : gameState.getInGamePlayers()) {
				if (!gameState.hasRoleNull(p)) {
					RoleBase role = gameState.getPlayerRoles().get(p);
					if (gameState.attributedRole.contains(Roles.Tanjiro) && gameState.attributedRole.contains(Roles.Muzan)) {
						if (gameState.DeadRole.contains(Roles.Muzan) && !gameState.DeadRole.contains(Roles.Tanjiro)) {
							if (role instanceof Tanjiro) {
								setActivated(true);
								role.setTeam(TeamList.Demon);
								role.owner.sendMessage("§cLa liste des démons est : ");
					            gameState.lunesup.forEach(lambda -> role.owner.sendMessage("§7 - §c" + lambda.getName()));
								role.owner.sendMessage("Vous venez de devenir le Démon le plus puissant de toute l'éxistance !");
								role.owner.getInventory().remove(Items.getDSTanjiroDance());
								role.setLameIncassable(role.owner, true);
								role.owner.getInventory().addItem(role.getItems());
								if (role.hasLamecoeur()) {
									role.setMaxHealth(24.0);
								}else {
									role.setMaxHealth(20.0);
								}
								role.owner.sendMessage("Votre arrivé dans le camp des§c "+TeamList.Demon.name()+"s§f restera secrète jusqu'à "+StringUtils.secondsTowardsBeautiful(gameTime+60));
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
									Bukkit.broadcastMessage(AllDesc.bar+"\n§rL'évènement aléatoire "+Events.DemonKingTanjiro.getName()+" viens de ce déclancher, le rôle§c Tanjiro§f est maintenant dans le camp des Démons !\n"+AllDesc.bar);
									gameState.demonKingTanjiro = true;
									for (Player k : gameState.getInGamePlayers()) {
										if (gameState.attributedRole.contains(Roles.Kokushibo)) {
											if (!gameState.hasRoleNull(k)) {
												if (gameState.getPlayerRoles().get(k) instanceof Kokushibo) {
													RoleBase koku = gameState.getPlayerRoles().get(k);
													Kokushibo ko = (Kokushibo) koku;
													Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
														ko.owner.sendMessage("§7Vous sentez des pulsions montez en vous...");
													}, 20);
													Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
														ko.owner.sendMessage("§7Vous devennez de plus en plus aigri...");
													}, 20*5);
													Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
														ko.setTeam(TeamList.Solo);
														ko.orginalMaxHealth = ko.getMaxHealth();
														koku.owner.sendMessage(" \n§7La nouvelle de la mort de§c Muzan§7 et maintenant un simple pourfendeur devenue le chef des§c démons§7 qu'elle honte, vous explosez de rage et décidé de§l tué§7 tout le monde\n ");
														if (koku.hasLamecoeur()) {
															koku.setMaxHealth(34.0);
														}else {
															koku.setMaxHealth(30.0);
														}
														koku.Heal(role.owner, role.getMaxHealth());
														koku.owner.sendMessage("Vous posséderez maintenant l'effet "+AllDesc.Resi+" 1 pendant 3 minutes en tuant un joueur");
														ko.solo = true;
													}, 20*10);
												}
											}
										}
									}
								}, 20*60);
								return true;
							}
							
						}else {
							this.setTime(getTime()+60*5);
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
		setTime(GameState.getInstance().DKminTime);
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
	public void onSubDSCommand(Player sender, String[] args) {}
}