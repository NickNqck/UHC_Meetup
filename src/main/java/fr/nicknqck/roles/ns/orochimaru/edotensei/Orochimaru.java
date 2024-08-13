package fr.nicknqck.roles.ns.orochimaru.edotensei;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.Sasuke;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Orochimaru extends OrochimaruRoles {
	private EdoTenseiUser edo;
	private final List<Chakras> chakrasVoled = new ArrayList<>();
	public Orochimaru(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
		chakrasVoled.add(getChakras());
	}
	@Override
	public Roles getRoles() {
		return Roles.Orochimaru;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setResi(20);
		givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		this.edo = new EdoTenseiUser(this);
	}
	@Override
	public String[] Desc() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Chakras chakras : chakrasVoled) {
			i++;
			if (i + 1 != chakrasVoled.size()+1) {
				sb.append(chakras.getShowedName()).append("§f,");
			}else {
				sb.append(chakras.getShowedName()).append("§f.");
			}
		}
		List<Player> mates = new ArrayList<>();
		for (Player p : gameState.getInGamePlayers()) {
			if (!gameState.hasRoleNull(p)) {
				if (getTeam(p) != null && p.getUniqueId() != owner.getUniqueId()) {
					if (getTeam(p) == TeamList.Orochimaru || getPlayerRoles(p) instanceof Sasuke) {
						mates.add(p);
					}
				}
			}
		}
		if (!mates.isEmpty()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				owner.sendMessage("Voici la liste de vos coéquipier: ");
				mates.forEach(p -> owner.sendMessage("§7 - §5"+p.getName()));}, 1);
		}
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Orochimaru",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+AllDesc.Resi+"§9 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+KusanagiItem().getItemMeta().getDisplayName()+"§f: Épée en diamant tranchant 4",
				"",
				AllDesc.point+"§5Edo Tensei§f: Permet de réssusciter un joueur que vous avez précedemment tuer et de le faire rejoindre votre camp en échange de§c 3"+AllDesc.coeur+" permanent",
				"",
				AllDesc.particularite,
				"",
				"En tuant un joueur vous gagnez§e 4"+AllDesc.Coeur("§e")+"§e d'absorbtion",
				"En tuant un joueur vous aurez 25% de chance de gagner sa nature de Chakra",
				"En mangeant une§e pomme d'or§f vous obtenez§e 3"+AllDesc.Coeur("§e")+"§e d'absorbtion§f au lieu de§e 2"+AllDesc.Coeur("§e"),
				"Vous possédez une nature de Chakra aléatoire",
				"",
				"Vos natures de Chakras: "+ sb,
				AllDesc.bar
		};
	}
	private ItemStack KusanagiItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§5Kusanagi").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setLore("§7L'épée légendaire en possession du§5 Orochimaru").toItemStack();
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (killer.getUniqueId() == owner.getUniqueId()) {
			((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+4.0f);
			if (getPlayerRoles(player) instanceof NSRoles && ((NSRoles) getPlayerRoles(player)).getChakras() != null) {
				if (RandomUtils.getOwnRandomProbability(25)) {
					if (!chakrasVoled.contains(((NSRoles) getPlayerRoles(player)).getChakras())) {
						chakrasVoled.add(((NSRoles) getPlayerRoles(player)).getChakras());
						((NSRoles) getPlayerRoles(player)).getChakras().getChakra().getList().add(owner.getUniqueId());
						owner.sendMessage("§7Vous maitrisez maintenant la nature de Chakra: "+ ((NSRoles) getPlayerRoles(player)).getChakras().getShowedName());
					}
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KusanagiItem(),
				this.edo.getEdoTenseiItem()
		};
	}
	@Override
	public void onEat(ItemStack item, GameState gameState) {
		if (item.getType() == Material.GOLDEN_APPLE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				owner.removePotionEffect(PotionEffectType.ABSORPTION);
				((CraftPlayer) owner).getHandle().setAbsorptionHearts(0);
				((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+6.0f);
			}, 1);
		}
	}
	@Override
	public Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}

	@Override
	public void resetCooldown() {
	}

	@Override
	public String getName() {
		return "Orochimaru";
	}
}