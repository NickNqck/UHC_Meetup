package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;

import java.util.UUID;

public class PetitTitan extends TitansRoles {

	public PetitTitan(UUID player) {
		super(player);
		gameState.TitansRouge.add(owner);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.PetitTitan;
	}
	@Override
	public String[] Desc() {
	Main.getInstance().getGetterList().getTitanRougeList(owner);
	return new String[] {
			AllDesc.bar,
			AllDesc.role+"Petit Titan",
			"",
			AllDesc.items,
			"",
			AllDesc.point+"§6§lTransformation§r: Vous transforme en Titan ce qui vous donne "+AllDesc.Speed+" 1 permanent",
			"",
			AllDesc.bar
	};
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		super.GiveItems();
	}

	@Override
	public String getName() {
		return "Petit Titan";
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.FEATHER).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§6§lTransformation").setLore("§fTransformation en Titan (§cAttention cette transformation est§l PERMANENTE§f)","§7 "+RandomUtils.generateRandomString(24)).toItemStack(), (event) -> {
					if (!isTransformedinTitan) {
						isTransformedinTitan = true;
						owner.sendMessage("§7Transformation en Titan");
						TransfoEclairxMessage(owner);
						owner.getInventory().remove(owner.getItemInHand());
					}
					return true;
				}).setDespawnable(true).setPosable(false).setDroppable(false).getItemStack()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (isTransformedinTitan) {
			OLDgivePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		}		
	}
}