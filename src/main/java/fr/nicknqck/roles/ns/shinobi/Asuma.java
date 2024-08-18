package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Asuma extends ShinobiRoles {

	public Asuma(UUID player) {
		super(player);
		setChakraType(Chakras.FUTON);
		setCanBeHokage(true);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public GameState.Roles getRoles() {
		return Roles.Asuma;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aAsuma",
				AllDesc.objectifteam+"§aShinobi",
				"",
				"Effet: ",
				"",
				AllDesc.point+"§cForce I§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aLame de chakra§f: Épée en fer§l Tranchant IV",
				"",
				AllDesc.point+"§cNuées Ardentes§f: Vous permet d'infliger§1 Blindness 1§f au joueur à moins de§c 25 blocs§f de vous, également,§6 brûle§f les joueurs touchées (sauf§c vous§f)§7 (1x/3m30)",
				"",
				AllDesc.particularite,
				"",
				"Lorsque vous frapperez un §cjoueur§f avec votre§a Lame de chakra§f vous aurez§c 10%§f de chance d'§cenflammer§f la cible",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack LameItem() {
		return new ItemBuilder(Material.IRON_SWORD).setName("§aLame de chakra").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setLore("§7Lame remplit de chakra§a Futon§7 de§a Asuma").toItemStack();
	}
	private ItemStack NueesItem() {
		return new ItemBuilder(Material.SULPHUR).setName("§cNuées Ardentes").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore("§7Vous permet d'infliger§1 Blindness 1§7 au joueurs proche").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				LameItem(),
				NueesItem()
		};
	}
	private int cdNuees = 0;
	@Override
	public void resetCooldown() {
		cdNuees = 0;
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		if (cdNuees >= 0) {
			cdNuees--;
			if (cdNuees == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre §cNuees Ardentes");
			}
		}
	}
	@Override
	public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {
		if (damager.getUniqueId().equals(owner.getUniqueId())) {
			if (owner.getItemInHand().isSimilar(LameItem())) {
				if (RandomUtils.getOwnRandomProbability(5)) {
					victim.setFireTicks(150);
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.CONNUE;
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(NueesItem())) {
			if (cdNuees <= 0) {
				owner.sendMessage("§cNuées Ardentes !");
				for (Player p : Loc.getNearbyPlayersExcept(owner, 25)) {
					if (owner.canSee(p)) {
						givePotionEffet(p, PotionEffectType.BLINDNESS, 20*20, 1, true);
						cdNuees = 60*3+30;
						p.setFireTicks(150);
						p.sendMessage("Vous venez d'être touche par la §8Nuées Ardentes§f de§a Asuma");
						owner.sendMessage("§7§l"+p.getName()+"§7 à été touchée");
					}
				}
            } else {
				sendCooldown(owner, cdNuees);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "Asuma";
	}
}