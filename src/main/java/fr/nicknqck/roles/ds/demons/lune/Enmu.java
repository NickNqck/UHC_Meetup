package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;

import java.util.UUID;

public class Enmu extends DemonsRoles {
	public Enmu(UUID player) {
		super(player);
		org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			for (UUID u : gameState.getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
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
		return Roles.Enmu;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Muzan, 1);
		return AllDesc.Enmu;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getPouvoirSanginaire()
		};
	}

	@Override
	public String getName() {
		return "Enmu";
	}

	@Override
	public void GiveItems() {owner.getInventory().addItem(Items.getPouvoirSanginaire());super.GiveItems();}
	private int itemcooldown = 0;
	private int timesleep = 0;
	@Override
	public void resetCooldown() {
		itemcooldown = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getPouvoirSanginaire())) {
			sendActionBarCooldown(owner, itemcooldown);
		}
		if (gameState.nightTime) owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*5, 0, false, false), true);
		if (itemcooldown >=1)itemcooldown--;
		if (timesleep >=1)timesleep--;
			if (timesleep == 0 && itemcooldown > 0) {
				for (RoleBase r : gameState.getPlayerRoles().values()) {
					if (gameState.getInSleepingPlayers().contains(r.owner)) {
						gameState.delInSleepingPlayers(r.owner);
						r.owner.sendMessage("Vous n'êtes plus endormie");
						if (r.owner.getAllowFlight())r.owner.setAllowFlight(false);
						if (r.owner.isFlying())r.owner.setFlying(false);
					}
				}
			}
		super.Update(gameState);
	}	
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getPouvoirSanginaire())) {
			if (itemcooldown <= 0) {
				for (UUID u : gameState.getInGamePlayers()) {
					Player p = Bukkit.getPlayer(u);
					if (p == null)continue;
					if (gameState.getInGamePlayers().contains(p.getUniqueId())) {
						for (RoleBase r : gameState.getPlayerRoles().values()) {
							if (r.getOriginTeam() != TeamList.Demon) {
								if (r.getRoles() != Roles.Nezuko) {
									if (p != owner) {
										double min = 10;
										Player target = null;
										for (UUID uplou : gameState.getInGamePlayers()) {
											Player plou = Bukkit.getPlayer(uplou);
											if (plou == null)continue;
											if (owner.canSee(plou) && plou != owner && plou.getWorld().equals(owner.getWorld())) {
												double dist = Math.abs(plou.getLocation().distance(owner.getLocation()));
												if (dist < min) {
													target = plou;
													min = dist;
												}
											}
										}
										if (target != null) {
											if (owner.canSee(target)) {
												if (r.owner != owner && p != owner) {
													if (r.owner == p) {
														gameState.addInSleepingPlayers(p);
														itemcooldown = 60*2;
														timesleep = 10;
														if (gameState.getInSleepingPlayers().contains(p)) p.sendMessage("Vous avez été endormi");
														owner.sendMessage("Vous avez endormi le joueur: "+ChatColor.GOLD+p.getName());
													}
												}
											}
										} else {owner.sendMessage(ChatColor.RED+"Veuiller viser un joueur"); return true;}
									}
								}
							}
						}
					}
				}
			} else { //else du itemcooldown
				sendCooldown(owner, itemcooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}	
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {gameState.SleepingPlayer.clear();
		timesleep = 0;
		Update(gameState);
		}
		super.PlayerKilled(killer, victim, gameState);
	}
}