package fr.nicknqck.roles.ds;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;

public class Lame implements Listener{
	@EventHandler
	public void ItemUse(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (item == null)return;
		if (p == null)return;
		if (item.isSimilar(Items.getLamedenichirin())) {
			GameState gameState = GameState.getInstance();
			if(gameState.getServerState() != ServerStates.InGame)return;
			if (gameState.getPlayerRoles().containsKey(p)) {
				if (gameState.getPlayerRoles().get(p).isCanUseBlade()) {
					RoleBase role = gameState.getPlayerRoles().get(p);
					int rint = RandomUtils.getRandomInt(0, 6);
					if (rint == 0 && !role.hasLamecoeur()){
						role.owner.getInventory().removeItem(Items.getLamedenichirin());
						role.owner.getInventory().addItem(Items.getLamedenichirincoeur());
						role.giveHealedHeartatInt(p, 2);
						role.owner.sendMessage("Vous avez obtenu la lame de "+AllDesc.coeur);
						role.setLamecoeur(true);
						role.actualduralame+=40;
						role.hasblade = true;
					}
					if (rint == 1 && !role.owner.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE) && !role.hasLameFr()) {
							role.owner.getInventory().removeItem(Items.getLamedenichirin());
							role.owner.getInventory().addItem(Items.getLamedenichirinfireresi());
							role.owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), true);
							role.owner.sendMessage("Vous avez obtenu la lame de "+AllDesc.fireResi);
							role.setLameFr(true);
							role.actualduralame+=40;
							role.hasblade = true;
					} 
					if (rint == 2 && !role.hasLameSpeed()) {
						role.owner.getInventory().removeItem(Items.getLamedenichirin());
						role.owner.getInventory().addItem(Items.getLamedenichirinspeed());
						role.addSpeedAtInt(role.owner, 10);
						role.owner.sendMessage("Vous avez obtenu la lame de "+AllDesc.Speed);
						role.setLameSpeed(true);
						role.actualduralame+=40;
						role.hasblade = true;
					}
					if (rint == 3 && !role.hasLameresi()) {
						role.owner.getInventory().removeItem(Items.getLamedenichirin());
						role.owner.getInventory().addItem(Items.getLamedenichirinresi());					
						role.addBonusResi(10);
						role.owner.sendMessage("Vous avez obtenu la lame de "+AllDesc.Resi);
						role.setLameresi(true);
						role.actualduralame+=40;
						role.hasblade = true;
					} 
					if (rint == 4 && !role.isHasNoFall()) {
						role.setNoFall(true);
						role.owner.sendMessage("Vous avez obtenu la lame"+ChatColor.GREEN+" No Fall");
						role.owner.getInventory().removeItem(Items.getLamedenichirin());
						role.owner.getInventory().addItem(Items.getLamedenichirinnofall());
						role.actualduralame+=40;
						role.hasblade = true;
					} 
					if (rint == 5 && !role.hasLameForce()) {
						role.addBonusforce(10);
						role.owner.sendMessage("Vous avez obtenu la lame de "+AllDesc.Force);
						role.owner.getInventory().removeItem(Items.getLamedenichirin());
						role.owner.getInventory().addItem(Items.getLamedenichirinforce());
						role.setLameForce(true);
						role.actualduralame+=40;
						role.hasblade = true;
					}
					System.out.println("Random = "+rint);
				} else {
					p.sendMessage("Vous n'avez pas accès à la lame de Nichirin");
				}
			} else {
				p.sendMessage("Il faut avoir un rôle pour obtenir une lame");
			}
		}
	}
}