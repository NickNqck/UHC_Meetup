package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Lames;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Kyojuro extends PillierRoles {
	private int itemcooldown = 0;
	@Setter
	private boolean alliance = false;
	public Kyojuro(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		owner.setExp(owner.getExp()+4f);
		this.setCanuseblade(true);
		Lames.FireResistance.getUsers().put(getPlayer(), Integer.MAX_VALUE);
		givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		this.setResi(20);
		super.RoleGiven(gameState);
	}

	@Override
	public Roles getRoles() {
		return Roles.Kyojuro;
	}
	@Override
	public String[] Desc() {
	return AllDesc.Kyojuro;
}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		ItemStack FireAspect = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) FireAspect.getItemMeta();
		BookMeta.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, false);
		FireAspect.setItemMeta(BookMeta);
		giveItem(owner, false, FireAspect);
		ItemStack Flame = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta FlameMeta = (EnchantmentStorageMeta) Flame.getItemMeta();
		FlameMeta.addStoredEnchant(Enchantment.ARROW_FIRE, 1, false);
		Flame.setItemMeta(FlameMeta);
		giveItem(owner, false, Flame);
		super.GiveItems();
	}
	@Override
	public void onEat(ItemStack item, GameState gameState) {
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (!alliance) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*5, 1, true);
				owner.sendMessage("Vous venez de gagner l'effet "+AllDesc.Force+" 1 pendant 5 secondes");
			} else {
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*15, 1, true);
				owner.sendMessage("Vous venez de gagner l'effet "+AllDesc.Force+" 1 pendant 15 secondes");
			}
		}
		super.onEat(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (alliance) {
			givePotionEffet(PotionEffectType.SPEED, 100, 1, true);
		}
		if (itemcooldown >= 1) {itemcooldown--;}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
	}

	@Override
	public String getName() {
		return "Kyojuro";
	}
}