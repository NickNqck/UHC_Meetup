package fr.nicknqck.roles.aot.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Gabi extends AotRoles {
	private boolean killshifter = false;
	public Gabi(Player player) {
		super(player);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
		setCanVoleTitan(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Gabi;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	private	List<Player> inList = new ArrayList<>();
	@Override
	public String[] Desc() {
			if (!killshifter) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					List<Player> canBeinList = new ArrayList<>();
					for (Player p : gameState.getInGamePlayers()) {
						if (!gameState.hasRoleNull(p)) {
							if (getPlayerRoles(p) instanceof AotRoles && ((AotRoles) getPlayerRoles(p)).canShift) {
								if (!inList.contains(p)) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t)) {
										inList.add(t);
									}
								}
							}else {
								canBeinList.remove(p);
							}
						}else {
							canBeinList.remove(p);
						}
					}
			}, 11);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					List<Player> canBeinList = new ArrayList<>();
					for (Player p : gameState.getInGamePlayers()) {
						if (!gameState.hasRoleNull(p)) {
							if (getPlayerRoles(p) instanceof AotRoles && ((AotRoles) getPlayerRoles(p)).canShift) {
								if (!inList.contains(p)) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t)) {
										inList.add(t);
										System.out.println("added "+t.getName());
										
									}
								}
							}else {
								canBeinList.remove(p);
								System.out.println("removed "+p.getName());
							}
						}else {
							canBeinList.remove(p);
							System.out.println("remove "+p.getName());
						}
					}
			}, 14);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					List<Player> canBeinList = new ArrayList<>();
					for (Player p : gameState.getInGamePlayers()) {
						if (!gameState.hasRoleNull(p)) {
							if (getPlayerRoles(p) instanceof AotRoles && ((AotRoles) getPlayerRoles(p)).canShift) {
								if (!inList.contains(p)) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t)) {
										inList.add(t);
										System.out.println("added "+t.getName());
										
									}
								}
							}else {
								canBeinList.remove(p);
							}
						}else {
							canBeinList.remove(p);
						}
					}
			}, 15);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					owner.sendMessage("§7Liste des cibles: ");
					inList.forEach(p -> owner.sendMessage("§7 - §c"+p.getName()));
				}, 20);
			}
			return new String[] {
					AllDesc.bar,
					AllDesc.role+"Gabi",
					"",
					AllDesc.point+"Vous devez gagner seul mais celà peux changer en fonction de quel rôle vous tuez",
					"",
					AllDesc.point+"Si vous parvenez un tuez un joueur possèdant un role dans le camp§9 Shifter§f, vous volerez le rôle de ce dernier et devrez gagner avec le camp§9 Shifter",
					"",
					AllDesc.point+"Vous obtenez une liste de 3 joueurs dans laquelle se trouve 3 personnes possédant un titan pouvant Shifter",
					"",
					AllDesc.point+"Pour vous aidez à votre tâche, vous possèderez 5 "+AllDesc.coeur+" suplémentaire ainsi que l'effet "+AllDesc.Force+" 1 à moins de 15blocs de l'une des personnes de votre list",
					"",
					AllDesc.bar,
			};
	}

	@Override
	public String getName() {
		return "§eGabi";
	}

	@Override
	public void RoleGiven(GameState gameState) {
		giveHealedHeartatInt(owner, 5);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void Update(GameState gameState) {//Update s'actualise toute les secondes
		if (!killshifter) {
			if (inList.size() > 0) {
				for (Player p : inList) {
					if (p != null) {
						if (gameState.getInSpecPlayers().contains(p))inList.remove(p);
						if (p.getWorld().equals(owner.getWorld())) {
							if (owner.getLocation().distance(p.getLocation()) <= 20) {
								givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
								setForce(20);
							}
						}
						if (!owner.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
							setForce(0);
						}
					}
				}
			}
		}
		super.Update(gameState);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (inList.contains(player)) {
			inList.remove(player);
		}
	}
	@Override
	public void resetCooldown() {
	}
}