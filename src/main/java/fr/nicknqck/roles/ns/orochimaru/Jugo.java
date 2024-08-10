package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Jugo extends OrochimaruRoles {
	private boolean kimimaroDeath = false;
	private int marqueCD = 0;
	private boolean orochimaruDeath = false;
	public Jugo(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Kimimaro)) {
				onKimimaroDeath(false);
				owner.sendMessage("§5Kimimaro§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à sa mort");
			}
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la composition de la partie vous avez reçus tout de même le bonus dû à sa mort");
			}
		}, 20*5);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
	}
	@Override
	public Roles getRoles() {
		return Roles.Jugo;
	}
	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Jugo",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§cForce 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§5Marque maudite§f: Vous offres pendant 3minutes des effets en fonction d'un pourcentage: ",
				"§7     →§a70%§f: Vous obtenez l'effet§e Speed 1",
				"§7     →§c30%§f: Vous obtenez les effets§e Speed 1§f et§9 Résistance 1§f mais vous devennez un rôle§e Solo§f pendant 2m30s",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité de§5 Kimimaro",
				"A la mort de§5 Kimimaro§f vous obtiendrez l'identité d'§5Orochimaru",
				"A la mort d'§5Orochimaru§f vous obtiendrez l'identité des rôles:§5 Karin§f et§5 Suigetsu",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				MarqueMauditeItem()
		};
	}
	private ItemStack MarqueMauditeItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Marque maudite").setLore("§7Vous permet de devenir plus puissant pendant un court instant").toItemStack();
	}
	private void onKimimaroDeath(boolean msg) {
		kimimaroDeath = true;
		if (msg) {
			owner.sendMessage("§5Kimimaro§7 est mort, vous obtenez donc l'identité de son maitre§5 Orochimaru");
		}
		getKnowedRoles().add(Orochimaru.class);
	}
	private void onOrochimaruDeath(boolean msg) {
		orochimaruDeath = true;
		if (msg) {
			owner.sendMessage("§7Maitre§5 Orochimaru§7 est mort, vous obtenez donc l'identité de vos nouveau amis,§5 Karin§f et§5 Suigetsu");
		}
		getKnowedRoles().add(Karin.class);
		getKnowedRoles().add(Suigetsu.class);
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (!gameState.hasRoleNull(player)) {
			if (getListPlayerFromRole(Roles.Kimimaro).contains(player) &&!kimimaroDeath) {
				onKimimaroDeath(true);
			}
			if (getListPlayerFromRole(Roles.Orochimaru).contains(player) && !orochimaruDeath) {
				onOrochimaruDeath(true);
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (marqueCD >= 0) {
			marqueCD--;
			if (marqueCD == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§5 Marque maudite");
			}
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(MarqueMauditeItem())) {
			if (marqueCD <= 0) {
				marqueCD = 60*8;
				if (RandomUtils.getOwnRandomProbability(70)) {
					owner.sendMessage("§7Vous obtenez l'effet§e Speed 1");
					givePotionEffet(PotionEffectType.SPEED, 20*60*3, 1, true);
                } else {
					owner.sendMessage("§7Vous obtenez l'effet§e Speed 1§7 et l'effet§9 Résistance 1");
					givePotionEffet(PotionEffectType.SPEED, 20*60*3, 1, true);
					givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 1, true);
					setResi(20);
					TeamList oldTeam = getOriginTeam();
					setTeam(TeamList.Solo);
					owner.resetTitle();
					owner.sendTitle("Vous gagnez maintenant§e Seul", "Pendant 3 minutes");
					new BukkitRunnable() {
						int i =0;
						@Override
						public void run() {
							i++;
							if (gameState.getServerState() != GameState.ServerStates.InGame) {
								cancel();
							}
							if (owner != null && !gameState.hasRoleNull(owner) && i >= 60*3) {
								setTeam(oldTeam);
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
                }
            }else {
				sendCooldown(owner, marqueCD);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
	@Override
	public void resetCooldown() {
		marqueCD = 0;
	}

	@Override
	public String getName() {
		return "Jugo";
	}
}