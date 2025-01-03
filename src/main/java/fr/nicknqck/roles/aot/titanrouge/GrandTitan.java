package fr.nicknqck.roles.aot.titanrouge;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GrandTitan extends TitansRoles {

	public GrandTitan(UUID player) {
		super(player);
		gameState.TitansRouge.add(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.GrandTitan;
	}
	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getTitanRougeList(owner);
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
		return "Grand Titan";
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