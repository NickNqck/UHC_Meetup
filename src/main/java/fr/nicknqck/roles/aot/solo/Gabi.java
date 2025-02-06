package fr.nicknqck.roles.aot.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Gabi extends AotRoles {
	private boolean killshifter = false;
	private final List<UUID> inList = new ArrayList<>();
	public Gabi(UUID player) {
		super(player);
		setCanVoleTitan(true);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Gabi;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public void GiveItems() {
		gameState.GiveRodTridi(owner);
	}
	@Override
	public String[] Desc() {
			if (!killshifter) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
					List<Player> canBeinList = new ArrayList<>();
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (!gameState.hasRoleNull(p.getUniqueId())) {
							GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
							if (gamePlayer.getRole() instanceof AotRoles && ((AotRoles) gamePlayer.getRole()).canShift) {
								if (!inList.contains(p.getUniqueId())) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t.getUniqueId())) {
										inList.add(t.getUniqueId());
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
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (!gameState.hasRoleNull(p.getUniqueId())) {
							GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
							if (gamePlayer.getRole() instanceof AotRoles && ((AotRoles) gamePlayer.getRole()).canShift) {
								if (!inList.contains(p.getUniqueId())) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t.getUniqueId())) {
										inList.add(t.getUniqueId());
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
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (!gameState.hasRoleNull(p.getUniqueId())) {
							GamePlayer gamePlayer = gameState.getGamePlayer().get(p.getUniqueId());
							if (gamePlayer.getRole() instanceof AotRoles && ((AotRoles) gamePlayer.getRole()).canShift) {
								if (!inList.contains(p.getUniqueId())) {
									canBeinList.add(p);
									Player t = canBeinList.get(0);
									if (inList.size() < 3 && !inList.contains(t.getUniqueId())) {
										inList.add(t.getUniqueId());
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
					inList.stream().filter(u -> Bukkit.getPlayer(u) != null).forEach(p -> owner.sendMessage("§7 - §c"+Bukkit.getPlayer(p).getName()));
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
		return "Gabi";
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
			if (!inList.isEmpty()) {
				for (UUID u : inList) {
					Player p = Bukkit.getPlayer(u);
					if (p != null) {
						if (gameState.getInSpecPlayers().contains(p))inList.remove(u);
						if (Loc.getNearbyPlayersExcept(p, 20).contains(owner)) {
							OLDgivePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						}
					}
				}
			}
		}
		super.Update(gameState);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (killer.getUniqueId().equals(getPlayer())) {
			if (inList.contains(player.getUniqueId())) {
				this.killshifter = true;
			}
		}
        inList.remove(player.getUniqueId());
	}
	@Override
	public void resetCooldown() {
	}
}