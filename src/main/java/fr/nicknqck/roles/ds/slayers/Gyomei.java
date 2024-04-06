package fr.nicknqck.roles.ds.slayers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Gyomei extends RoleBase{

	public Gyomei(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		for (String s : AllDesc.Gyomei) owner.sendMessage(s);
		this.setForce(20);
		setMaxHealth(24.0);
		this.setCanUseBlade(true);
		this.setResi(20);
		gameState.addPillier(owner);
	}
	@Override
	public String[] Desc() {return AllDesc.Gyomei;}
	
	private boolean soufleactif = false;
	private int souflecooldown = 0;
	private boolean killkoku = false;
	private int cooldownmarque = 0;
	@Override
	public void resetCooldown() {
		cooldownmarque = 0;
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
		if (owner.getItemInHand().isSimilar(Items.getSlayerMark())) {
			sendActionBarCooldown(owner, cooldownmarque);
		}
		if (!gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
		} else if (gameState.nightTime) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);
		}
		if (soufleactif == true) {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*3, 0, false, false), true);
			} else if (gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			}
		}
		 
		if (cooldownmarque >= 1) {	cooldownmarque--;	}		
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
		if (item.isSimilar(Items.getSlayerMark())) {
			if (!killkoku) return false;
			if (killkoku) {
				if (cooldownmarque <= 0) {
					owner.sendMessage("Vous venez d'activer votre Marque des Pourfendeurs");
					owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false));
					cooldownmarque = 60*10;
				}  else {
					sendCooldown(owner, cooldownmarque);
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role.type == Roles.Kokushibo && !killkoku) {
							killkoku = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+"Kokushibo "+ChatColor.GRAY+"vous obtenez donc la marque des pourfendeurs ce qui vous donnera speed 1 pendant 3 minutes");
							owner.getInventory().addItem(Items.getSlayerMark());
						
						}
					}
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		if (killkoku) {
			return new ItemStack[] {
				Items.getSoufleDeLaRoche(),
				Items.getSlayerMark()
			};
		}
		return new ItemStack[] {
				Items.getSoufleDeLaRoche()
		};
	}
}