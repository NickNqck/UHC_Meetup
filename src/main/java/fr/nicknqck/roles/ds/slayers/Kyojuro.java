package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.ItemsManager;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Kyojuro extends SlayerRoles {

	public Kyojuro(Player player) {
		super(player);
		owner.sendMessage(AllDesc.Kyojuro);
		this.setForce(20);
		this.setCanuseblade(true);
		this.setLameFr(true);
		this.setResi(20);
		gameState.addPillier(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.Kyojuro;
	}
	@Override
	public String[] Desc() {
	return AllDesc.Kyojuro;
}	private int itemcooldown = 0;

	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		super.GiveItems();
	}
	@Override
	public void onEat(ItemStack item, GameState gameState) {
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (!Events.Alliance.getEvent().isActivated()) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*5, 1, true);
				owner.sendMessage("Vous venez de gagner l'effet "+AllDesc.Force+" 1 pendant 5 secondes");
			}else {
				givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 20*15, 1, true);
				owner.sendMessage("Vous venez de gagner l'effet "+AllDesc.Force+" 1 pendant 15 secondes");
			}
		}
		super.onEat(item, gameState);
	}
	private boolean hasdsfire = false;
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if(args[0].equalsIgnoreCase("fire")) {
			if (!hasdsfire) {
				for (int slot = 0; slot < 9; slot++) {
		            ItemStack item = owner.getInventory().getItem(slot);
		            if (item != null && item.getType() == Material.BOW) {
		            	item.addEnchantment(Enchantment.ARROW_FIRE, 1);
	            		owner.sendMessage("Enchantement de votre Arc");
	            		ItemsManager.instance.jsp.add(item);
		            }
		            if (item != null && item.getType() == Material.DIAMOND_SWORD) {
		            	item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
	            		owner.sendMessage("Enchantement de votre Épée");
	            		ItemsManager.instance.jsp.add(item);
		            }
		            hasdsfire = true;
		        }
			}			
		}
	}

	@Override
	public void Update(GameState gameState) {
		if (Events.Alliance.getEvent().isActivated()) {
			givePotionEffet(PotionEffectType.SPEED, 100, 1, true);
		}
		givePotionEffet(owner, PotionEffectType.FIRE_RESISTANCE, 20*3, 1, true);
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
		return "§aKyojuro";
	}
}