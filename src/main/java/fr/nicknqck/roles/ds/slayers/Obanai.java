package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Obanai extends SlayerRoles {

	public Obanai(Player player) {
		super(player);
		for (String desc : AllDesc.Obanai) owner.sendMessage(desc);
		this.setCanUseBlade(true);		
		gameState.addPillier(owner);
	}
	@Override
	public Roles getRoles() {
		return Roles.Obanai;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Obanai;
	}
	
	private boolean soufle = false;

	private int souflecooldown = 0 ;
	@Override
	public void resetCooldown() {
		souflecooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getSoufleduSerpent());
		owner.getInventory().addItem(Items.getLamedenichirin());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSoufleduSerpent()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleduSerpent())) {
			if (souflecooldown > 0) {
				NMSPacket.sendActionBar(owner, "Cooldown: "+StringUtils.secondsTowardsBeautiful(souflecooldown));
			} else {
				NMSPacket.sendActionBar(owner, getItemNameInHand(owner)+" Utilisable");
			}
		}
		if (gameState.isApoil(owner)) {
			givePotionEffet(owner, PotionEffectType.SPEED, 20*3, 2, true);
			givePotionEffet(owner, PotionEffectType.INVISIBILITY, 20*3, 1, true);
		}
		setNoFall(gameState.isApoil(owner));
		if (souflecooldown>=1) {souflecooldown--;}
		if (souflecooldown == 60*3) {
			owner.sendMessage("Désactivation du Soufle du Serpent");
			soufle = false;
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleduSerpent())) {
			if (souflecooldown <= 0) {
				owner.sendMessage("Activation du Soufle du Serpent");
				soufle = true;
				souflecooldown = 60*5;
			}  else {
				sendCooldown(owner, souflecooldown);
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item.isSimilar(Items.getdiamondsword())) {
			if (soufle) {
				if (!victim.hasPotionEffect(PotionEffectType.POISON)) {
					victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 0, false, false));
					owner.sendMessage("Vous avez empoisonné "+victim.getName());
				}
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}

	@Override
	public String getName() {
		return "§aObanai";
	}
}
