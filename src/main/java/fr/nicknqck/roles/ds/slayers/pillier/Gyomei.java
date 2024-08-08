package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Gyomei extends PillierRoles {

	public Gyomei(UUID player) {
		super(player);
		setMaxHealth(24.0);
		this.setCanuseblade(true);
		this.setResi(20);
	}

	@Override
	public Roles getRoles() {
		return Roles.Gyomei;
	}
	@Override
	public String[] Desc() {return AllDesc.Gyomei;}
	
	private boolean soufleactif = false;
	private int souflecooldown = 0;
	@Override
	public void resetCooldown() {
		souflecooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getSoufleDeLaRoche());
		super.GiveItems();
	}
	
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleDeLaRoche())) {
			sendActionBarCooldown(owner, souflecooldown);
		}
		if (!gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		} else {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);
		}
		if (soufleactif) {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);
			} else {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			}
		}
		if (souflecooldown >= 1) {	souflecooldown--;}
		if (souflecooldown == 60*4) {
			soufleactif = false;
			owner.sendMessage("Désactivation du Soufle de la Roche");
		}
		super.Update(gameState);
	}
	
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleDeLaRoche())) {
			if (!soufleactif) {
				if (souflecooldown <= 0) {
					owner.sendMessage(ChatColor.WHITE+"Activation du Soufle de la Roche");
					soufleactif = true;
					souflecooldown = 60*7;
				}  else {
					sendCooldown(owner, souflecooldown);
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleDeLaRoche()
		};
	}

	@Override
	public String getName() {
		return "Gyomei";
	}
}