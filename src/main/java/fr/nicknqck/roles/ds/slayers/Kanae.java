package fr.nicknqck.roles.ds.slayers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import net.md_5.bungee.api.ChatColor;

public class Kanae extends RoleBase {

	public Kanae(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		for (String desc : AllDesc.Kanae) owner.sendMessage(desc);
		setCanUseBlade(true);
	}
	@Override
	public String[] Desc() {
		return AllDesc.Kanae;
	}
	private int cooldowncoup = 0;
	private boolean fire = false;
	private int timefire = 0;
	@Override
	public void resetCooldown() {
		cooldowncoup = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getEpeefleuriale())) {
			sendActionBarCooldown(owner, cooldowncoup);
		}
		if (cooldowncoup >= 1) cooldowncoup--;
		if (timefire >= 1) timefire--;
        fire = timefire != 0;
		super.Update(gameState);
	}
	@Override
	public void GiveItems() {
		Inventory inv = owner.getInventory();
		inv.addItem(Items.getLamedenichirin());
		inv.addItem(Items.getEpeefleuriale());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getEpeefleuriale()
		};
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (!item.isSimilar(Items.getEpeefleuriale()))return;
		if (victim == owner)return;
		if (fire && timefire >= 1) victim.setFireTicks(timefire);
		if (cooldowncoup !=0) return;
		int r = RandomUtils.getRandomDeviationValue(0, 100, 5);
		String d = ChatColor.GOLD+"";
		if (r == 0) {
			victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false));
			victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 0, false, false));
			victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false));
		d = "10s de Poison, Weakness et Slowness 1";
		}
		if (r >= 1 && r <= 7) {
			owner.setHealth(owner.getHealth() + 4.0);
			owner.sendMessage("Vous venez de vous faire gagner 2"+AllDesc.coeur+" non permanent");
			
		}		
		if (r >= 8 && r <= 14) {
			fire = true;
			timefire = 10;
			owner.sendMessage(ChatColor.GREEN+"Maintenant pendant "+ChatColor.GOLD+"10s"+"§r vous mettrez en feu les ennemis que vous tapez");
			
		}
		if (r >= 15 && r <= 30) {
			victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false));
			d = "10s de Poison 1";
		}
		if (r >= 31 && r <= 41) {
			victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*12, 0, false, false));
			d = "12s de Slowness 1";
		}
		if (r >= 42 && r <= 77) {
			victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*15, 0, false, false));
			d = "15s de Weakness 1";
		}
		if (r>= 78) owner.sendMessage(ChatColor.GREEN+"Malheureusement votre adversaire: "+ChatColor.RED+victim.getName()+ChatColor.GREEN+" n'a rien reçus suite à votre coup");
		cooldowncoup+=40;
		String msg = ChatColor.GREEN+"Votre coup à infliger: "+d+" au joueur: "+ChatColor.GOLD+victim.getName();
		owner.sendMessage(msg);
		super.ItemUseAgainst(item, victim, gameState);
	}
}