package fr.nicknqck.roles.ns.akatsuki;

import java.util.ArrayList;
import java.util.List;

import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.particles.WingsEffect;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Konan extends NSRoles {

	public Konan(Player player) {
		super(player);
		setChakraType(Chakras.SUITON);
		setTeam(TeamList.Akatsuki);
		owner.sendMessage(Desc());
		setNoFall(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Konan;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, true, getItems());
	}
	@Override
	public String[] Desc() {
		List<Player> mates = new ArrayList<>();
		for (Player p : getIGPlayers()) {
			if (!gameState.hasRoleNull(p)) {
				if (getOldTeam(p) != null && p.getUniqueId() != owner.getUniqueId()) {
					if (getOldTeam(p) == TeamList.Akatsuki || getPlayerRoles(p) instanceof Obito) {
						mates.add(p);
					}
				}
			}
		}
		if (!mates.isEmpty()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				owner.sendMessage("Voici la liste de vos coéquipier: ");
				mates.forEach(p -> owner.sendMessage("§7 - §c"+p.getName()));}, 1);
		}
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cKonan",
				AllDesc.objectifteam+getTeam().getColor()+getTeam().name(),
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6Ailes de papier§f: Possède plusieurs utilisation en fonction du clique",
				"§7     →§fClique droit: Permet en visant un joueur, de l'envoyer valser dans les airs",
				"§7     →§fClique gauche: Permet d'obtenir un§a fly§f d'une durée de 10s",
				"",
				AllDesc.point+"§6Diversion de papier§f: Permet de vous téleportez la ou vous visez, également inflige l'effet "+AllDesc.blind+"§1 1§f pendant§c 8s",
				"",
				AllDesc.particularite,
				"",
				"§fVous avez§a NoFall§f jusu'à la fin de la partie",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				DiversionItem(),
				AileItem()
		};
	}
	private ItemStack DiversionItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§6Diversion de papier").toItemStack();
	}
	private ItemStack AileItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aAile").toItemStack();
	}
	private int diversionCD = 0;
	private int aileDroiteCD = 0;
	private int aileGaucheCD = 0;
	@Override
	public void resetCooldown() {
		diversionCD = 0;
		aileDroiteCD = 0;
		aileGaucheCD = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (diversionCD >= 0) {
			diversionCD--;
			if (diversionCD == 0) {
				owner.sendMessage(DiversionItem().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable");
			}
		}
		if (aileDroiteCD >= 0) {
			aileDroiteCD--;
			if (aileDroiteCD == 0) {
				owner.sendMessage(AileItem().getItemMeta().getDisplayName()+"§7 sont à nouveau capable d'envoyer quelqu'un en l'air");
			}
		}
		if (aileGaucheCD >= 0) {
			aileGaucheCD--;
			if (aileGaucheCD == 0) {
				owner.sendMessage("§7Vos "+AileItem().getItemMeta().getDisplayName()+"§7 sont à nouveau capable de voler");
			}
		}
	}
	@Override
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
		if (player.getUniqueId() == owner.getUniqueId()) {
			if (event.getItem() == null)return;
			if (event.getAction().name().contains("LEFT")) {
				if (event.getItem().isSimilar(AileItem())) {
					if (aileGaucheCD > 0) {
						sendCooldown(owner, aileGaucheCD);
						event.setCancelled(true);
						return;
					}
					owner.setAllowFlight(true);
					owner.setFlying(true);
					new BukkitRunnable() {
						int i = 0;
						@Override
						public void run() {
							i++;
							new WingsEffect(21, EnumParticle.FLAME).start(owner);
							if (i == 8) {
								owner.setFlying(false);
								owner.setAllowFlight(false);
								cancel();
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
					aileGaucheCD = 60*6;
					event.setCancelled(true);
                }
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(AileItem())) {
			if (aileDroiteCD > 0) {
				sendCooldown(owner, aileDroiteCD);
				return true;
			}
			Player target = getTargetPlayer(owner, 30);
			if (target == null) {
				owner.sendMessage("§cIl faut viser un joueur !");
				return true;
			}
			new PropulserUtils(owner, 30).applyPropulsion(target);
            aileDroiteCD = 60*5;
			return true;
		}
		if (item.isSimilar(DiversionItem())) {
			if (diversionCD > 0) {
				sendCooldown(owner, diversionCD);
				return true;
			}
			Location toTP = getTargetLocation(owner, 50);
			if (toTP == null) {
				owner.sendMessage("§cIl faut visée un endroit sans lave !");
				return true;
			}
			for (Player p : Loc.getNearbyPlayersExcept(owner, 25)) {
				givePotionEffet(p, PotionEffectType.BLINDNESS, 20*10, 1, true);
			}
			owner.teleport(toTP);
			diversionCD = 60*5;
			return true;
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "§cKonan";
	}
}