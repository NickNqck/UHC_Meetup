package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class TitanSouriant extends TitansRoles {

	public TitanSouriant(Player player) {
		super(player);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
	}
	@Override
	public Roles getRoles() {
		return null;
	}
	@Override
	public String[] Desc() {
		gameState.sendTitansList(owner);
		return new String[] {
			AllDesc.bar,
			AllDesc.role+"Titan Souriant",
			"",
			AllDesc.items+"Vous possèdez un item (transformation) qui vous octroie les effets "+AllDesc.Resi+" 10% ansi que "+AllDesc.Force+" 1",
			"",
			AllDesc.bar,
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.FEATHER).addEnchant(Enchantment.ARROW_DAMAGE,1).hideAllAttributes().setName("Transformation").setLore("§fTransformation en Titan (§cAttention cette transformation est§l PERMANENTE§f)","§7 "+RandomUtils.generateRandomString(24)).toItemStack(), event -> {
					if (isTransformedinTitan)return false;
					isTransformedinTitan = true;
					setBonusResi(10);
					setForce(20);
					givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true);
					owner.sendMessage("§7Transformation en Titan");
					TransfoEclairxMessage(owner);
					owner.getInventory().remove(owner.getItemInHand());
					return true;
				}).setDespawnable(false).setDroppable(false).getItemStack()
		};
	}
	@Override
	public void resetCooldown() {
	}

	@Override
	public String getName() {
		return "§cTitan Souriant";
	}
}
