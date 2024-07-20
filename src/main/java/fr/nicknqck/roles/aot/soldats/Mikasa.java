package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Mikasa extends SoldatsRoles {

	public Mikasa(Player player) {
		super(player);
		gameState.GiveRodTridi(owner);
		setAckerMan(true);
	}
	@Override
	public Roles getRoles() {
		return null;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Mikasa",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"Vous possèdez un item sucre qui à son activation vous octroie les effets "+AllDesc.Speed+" 1 ansi que "+AllDesc.Force+" 30%",
				"",
				AllDesc.point+"Si vous faîtes un click gauche sur un joueur avec votre§7§l Arc Tridimentionnel§f vous serez téléporter au joueur visé",
				"",
				AllDesc.bar,
		};
	}

	@Override
	public String getName() {
		return "§aMikasa";
	}

	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
		super.GiveItems();
	}
	int cd =0;
	int cdtp =0;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
		BetterItem.of(new ItemBuilder(Material.SUGAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("Sucre").toItemStack(), event ->{
			if (cd <= 0) {
			givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 1, true);
			setForce(30);
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*(60*3), 1, true);
			cd = 180;
			} else {
				sendCooldown(owner, cd);
			}
			return true;
		}).setDespawnable(false).setDroppable(false).getItemStack(),
		BetterItem.of(Items.ArcTridi(), event -> {
			if (event.isLeftClick()) {
				Player target = event.getLeftClicked(30);
				if (target != null) {
					if (cdtp <= 0) {
						owner.teleport(target);
						cdtp = 60;
						owner.sendMessage("§7Téléportation sur§l "+target.getName());
					} else {
						sendCooldown(owner, cdtp);
					}
				} else {
					owner.sendMessage("Veulliez viser quelqu'un");
				}
			}
			return true;
		}).setDroppable(false).setMovableOther(false).setDespawnable(false).getItemStack()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (cd >= 0) {
			cd -= 1;
			}
		if (cdtp >= 0) {
			cdtp -= 1;
		}
		super.Update(gameState);
	}
	@Override
	public void resetCooldown() {
		cdtp = 0;
		cd = 0;
	}

}
