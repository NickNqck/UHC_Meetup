package fr.nicknqck.roles.ds.slayers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;

public class ZenItsu extends RoleBase{

	private int cooldownpremiermouvement = 0;
	private int cooldowntroisiememouvement = 0;
	public ZenItsu(Player player, Roles roles) {
		super(player, roles);
		for (String desc : AllDesc.ZenItsu) owner.sendMessage(desc);
		this.setCanUseBlade(true);
		this.setForce(20);
	}
	@Override
		public String[] Desc() {
		if (gameState.JigoroV2Pacte3) {
			for (Player p : getIGPlayers()) {
				if (gameState.getPlayerRoles().containsKey(p)) {
					if (getPlayerRoles(p).type == Roles.JigoroV2) {
						sendMessageAfterXseconde(owner, "§6Jigoro§r: "+p.getName(), 1);
					}
				}
			}
		}
			return AllDesc.ZenItsu;
		}
	@Override
	public void onDSCommandSend(String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("eclair")) {
			if (eclair) {
				eclair = false;
				owner.sendMessage("§7Vous venez de désactivé vos§e éclair");
			}else {
				eclair = true;
				owner.sendMessage("§7Vous venez d'activé vos§e éclair");
			}
		}
		super.onDSCommandSend(args, gameState);
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		owner.getInventory().addItem(Items.getJoueurZenItsuSpeed());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getJoueurZenItsuSpeed()
		};
	}
	@Override
	public void resetCooldown() {
		cooldownpremiermouvement = 0;
		cooldowntroisiememouvement = 0;
	}
	private int cdeclair = 0;
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getJoueurZenItsuSpeed())) {
			sendActionBarCooldown(owner, cooldownpremiermouvement);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleFoudre3iememouvement())) {
			sendActionBarCooldown(owner, cooldowntroisiememouvement);
		}
		if (cooldownpremiermouvement < 60*3) {
			if (owner.getHealth() <= getMaxHealth()/2) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 2, true);
			}
		} else {
			if (owner.getHealth() <= getMaxHealth()/2) {
				givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			}
			givePotionEffet(owner, PotionEffectType.SPEED, 60, 3, true);
		}
		if (cooldownpremiermouvement >= 1) {cooldownpremiermouvement--;}
		if (cooldowntroisiememouvement >= 1) {cooldowntroisiememouvement--;}
		if (cooldownpremiermouvement == 60*3) {
			if (!gameState.JigoroV2Pacte3) {
				givePotionEffet(owner, PotionEffectType.SLOW, 20*60, 2, true);
				givePotionEffet(owner, PotionEffectType.WEAKNESS, 20*60, 2, true);
			}
			if (!gameState.JigoroV2Pacte3) {
				givePotionEffet(owner, PotionEffectType.SLOW, 20*60, 2, true);
				givePotionEffet(owner, PotionEffectType.WEAKNESS, 20*60, 2, true);
			}
		}
		if (cdeclair >= 1) cdeclair--;
		super.Update(gameState);
	}
	public boolean eclair = false;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getJoueurZenItsuSpeed())) {
			if (cooldownpremiermouvement <= 0) {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 2, true);
				cooldownpremiermouvement = 60*4;
		}  else {
			sendCooldown(owner, cooldownpremiermouvement);
		}
		}
		if (item.isSimilar(Items.getSoufleFoudre3iememouvement())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (item != null) {
			if (eclair) {
				if (RandomUtils.getRandomProbability(10)) {
					if (cdeclair <= 0) {
							if (owner != victim) {
								if (victim.getHealth() > 2.0) {
									victim.setHealth(victim.getHealth() - 2.0);
								} else {
									victim.setHealth(0.1);
								}
									cdeclair = 7;
							    	owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + victim.getName());
							    	victim.sendMessage("§aZenItsu§7 vous à fait perdre 1"+AllDesc.coeur+"§7 suite à votre§e Foudroyage");
							        victim.getWorld().strikeLightningEffect(victim.getLocation());
							}
					}
				}
			}
		}
		super.ItemUseAgainst(item, victim, gameState);
	}
}