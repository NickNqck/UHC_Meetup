package fr.nicknqck.roles.ds.demons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Demon_Simple extends RoleBase{

	public Demon_Simple(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		setForce(20);
		owner.sendMessage(Desc());
		}
	
	@Override
	public String[] Desc() {
		return AllDesc.Demon_Simple;
	}
	
	@Override
	public void Update(GameState gameState) {
		if (gameState.nightTime) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		} else {
			givePotionEffet(PotionEffectType.WEAKNESS, 60, 1, true);
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				this.setForce(this.getForce() + 3);
				owner.sendMessage("Vous venez de tuer: "+victim.getName()+" vous obtenez donc +§c 3% de Force§f ce qui vous à fait monter jusqu'a: "+ this.getForce()+"%");
			}
		}
	}

	@Override
	public void resetCooldown() {
		
	}
}