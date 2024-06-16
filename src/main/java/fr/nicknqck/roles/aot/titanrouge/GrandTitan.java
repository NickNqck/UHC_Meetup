package fr.nicknqck.roles.aot.titanrouge;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;

public class GrandTitan extends RoleBase{

	public GrandTitan(Player player) {
		super(player);
		gameState.TitansRouge.add(owner);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.GrandTitan;
	}
	@Override
	public String[] Desc() {
		gameState.sendTitansList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Grand Titan",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6§lTransformation§r: Vous transforme en Titan ce qui offre l'effet "+AllDesc.Resi+" 1 permanent",
				"",
				AllDesc.bar
		};
	}

	@Override
	public String getName() {
		return "§cGrand Titan";
	}

	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		super.GiveItems();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.FEATHER).setName("§6§lTransformation").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setLore("§fTransformation en Titan (§cAttention cette transformation est§l PERMANENTE§f)","§7 "+RandomUtils.generateRandomString(24)).toItemStack(), event -> {
					if (isTransformedinTitan)return false;
					isTransformedinTitan = true;
					setResi(20);
					givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 800, 1, true);
					owner.sendMessage("§7Transformation en Titan");
					TransfoEclairxMessage(owner);
					owner.getInventory().remove(owner.getItemInHand());
					return true;
				}).setDroppable(false).setDespawnable(true).getItemStack()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (isTransformedinTitan) {
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
		}
	}
	@Override
	public void resetCooldown() {
		
	}
}