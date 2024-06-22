package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Karin extends OrochimaruRoles {

	public Karin(Player player) {
		super(player);
		setChakraType(getRandomChakras());
		owner.sendMessage(Desc());
		timePassedNearby.clear();
	}
	@Override
	public Roles getRoles() {
		return Roles.Karin;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Orochimaru, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Karin",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.items,
				"",
				AllDesc.point+KaguraShinganItem().getItemMeta().getDisplayName()+"§f: Vous permet d'obtenir la liste de toute les personnes étant à moins de 100blocs de vous",
				"",
				AllDesc.point+MorsureItem().getItemMeta().getDisplayName()+"§f: Vous permet de vous remettre full vie, si vous utilisez plusieurs fois votre item dans une période de§c 60s§f vous perdez§c 0.5"+AllDesc.coeur+" permanent",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns kagurashingan§f: Pendant 1m30, traque le joueur visée",
				"",
				AllDesc.particularite,
				"",
				"Au bout de§c 2 minutes§f passé proche d'un joueur du camp§5 Orochimaru§f vous obtenez l'information qu'il est avec vous",
				"Au bout de§c 5 minutes§f passé proche d'un joueur qui n'est pas dans le camp§5 Orochimaru§f vous obtiendrez son camp",
				"",
				"Votre nature de Chakras aléatoire est "+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KaguraShinganItem(),
				MorsureItem()
		};
	}
	private ItemStack KaguraShinganItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Kagura Shingan").setLore("§7Permet de d'obtenir une liste des joueurs étant à moins de 50blocs").toItemStack();
	}
	private ItemStack MorsureItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§fMorsure").setLore("§7Permet de§d soigner§7 un joueur").toItemStack();
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("kagurashingan")) {
			if (kaguraCD <= 0) {
				if (args.length == 2) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						owner.sendMessage("§cCe joueur n'est pas connectée !");
					}else {
						if (Loc.getNearbyPlayers(owner, 100).contains(target)) {
							new BukkitRunnable() {
								int i = 0;
								@Override
								public void run() {
									i++;
									if (gameState.getServerState() != ServerStates.InGame) {
										cancel();
									}
									if (i < 90 && target.isOnline()) {
										sendCustomActionBar(owner, Loc.getDirectionMate(owner, target, 100));
									}
									if (i == 90) {
										owner.sendMessage("§7Vous n'arrivez plus à vous concentrez sur le Chakra de §6"+target.getName());
										cancel();
									}
								}
							};
						}else {
							owner.sendMessage("§cCe joueur n'est pas asser proche de vous !");
						}
					}
				}else {//else args.length
					owner.sendMessage("§7La commande est §l"+args[0]+" <joueur>§7 !");
				}
			}else {
				sendCooldown(owner, kaguraCD);
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public void resetCooldown() {
		morsureCD = 0;
		kaguraCD = 0;
	}
	private int morsureCD = 0;
	private int kaguraCD = 0;
	private final Map<UUID, Integer> timePassedNearby = new HashMap<>();
	@Override
	public void Update(GameState gameState) {
		if (timeLastUseHeal > 0)timeLastUseHeal--;
		if (morsureCD >= 0) {
			morsureCD--;
			if (morsureCD == 0) {
				owner.sendMessage(MorsureItem().getItemMeta().getDisplayName()+"§7 est de nouveau utilisable");
			}
		}
		for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
			if (gameState.hasRoleNull(p)) {
				return;
			}
			if (timePassedNearby.containsKey(p.getUniqueId())) {
				int i = timePassedNearby.get(p.getUniqueId());
				timePassedNearby.remove(p.getUniqueId(), i);
				timePassedNearby.put(p.getUniqueId(), i+1);
				if (getPlayerRoles(p).getTeam() == TeamList.Orochimaru) {
					if (timePassedNearby.get(p.getUniqueId()) == 60*2) {
						owner.sendMessage("§5"+p.getDisplayName()+"§f est dans le camp§5 Orochimaru");
					}
				}else {
					if (timePassedNearby.get(p.getUniqueId()) == 60*5) {
						owner.sendMessage(getTeamColor(p)+p.getDisplayName()+"§f est dans le camp "+getTeamColor(p)+getTeam(p).name());
					}
				}
			}else {
				timePassedNearby.put(p.getUniqueId(), 1);
			}
		}
		if (kaguraCD >= 0) {
			kaguraCD--;
			if (kaguraCD == 0) {
				owner.sendMessage(KaguraShinganItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable");
			}
		}
	}
	private int timeLastUseHeal = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(MorsureItem())) {
			if (morsureCD >0) {
				sendCooldown(owner, morsureCD);
				return true;
			}
			if (timeLastUseHeal > 0) {
				setMaxHealth(getMaxHealth()-1.0);
				owner.setMaxHealth(getMaxHealth());
				owner.sendMessage("§7Vous venez de perdre §c0.5"+AllDesc.coeur+" permanent suite à votre "+MorsureItem().getItemMeta().getDisplayName());
			}
			owner.setHealth(getMaxHealth());
			morsureCD = 5;
			timeLastUseHeal = 60;
			return true;
		}
		if (item.isSimilar(KaguraShinganItem())) {
			if (kaguraCD > 0) {
				sendCooldown(owner, kaguraCD);
				return true;
			}
			owner.sendMessage("§6Voici la liste des joueurs étant à moins de 100blocs de vous:");
			Loc.getNearbyPlayersExcept(owner, 100).forEach(p -> owner.sendMessage("§7 - §6"+p.getName()+"§7 - §6"+ArrowTargetUtils.calculateArrow(owner, p.getLocation())+" "+((int)owner.getLocation().distance(p.getLocation()))));
			kaguraCD = 60;
			return true;
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "§5Karin";
	}
}