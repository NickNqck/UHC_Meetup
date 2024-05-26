package fr.nicknqck.roles.ds.demons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Yahaba extends RoleBase {
	
	private Player lunesup;
	private Player cible;
	private boolean killcible = false;

	public Yahaba(Player player, Roles roles) {
		super(player, roles);
		setForce(20);
		owner.sendMessage(AllDesc.Yahaba);
		owner.sendMessage("Une cible vous sera attribué dans§6 10s");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (lunesup == null) {
            	if (gameState.getLuneSupPlayers().size() < 1)return;
                    lunesup = gameState.getLuneSupPlayers().get(0);
                    owner.sendMessage("Votre lune supérieure est "+lunesup.getName());      
            }
        }, 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			 if (cible == null) {
	             cible = canBeCibleYahaba.get(0);
	             owner.sendMessage("Pour connaitre votre cible faite§6 /ds me");
	            }
			}, 20*10);
		}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (lunesup != null) {
                    owner.sendMessage("§cVotre lune supérieure est:§r "+lunesup.getName());

            }
            if (cible != null && !killcible) {
            	owner.sendMessage("§cVotre cible est:§r "+cible.getName());
            }
        }, 20);
		return AllDesc.Yahaba;
	} 
	@Override
	public void Update(GameState gameState) {
		if (!killcible) {
         for (Player p:gameState.getInGamePlayers()) {
        	 if (cible != null) {
        		if (p == cible && p.getWorld().equals(owner.getWorld())) {
        			 if (p.getLocation().distance(owner.getLocation())<= 15) {
        				 owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
        			 }
        	    }
        	 }
         }
        }else {
        	givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*3, 1, true);
        }
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setMaxHealth(24.0);
		super.RoleGiven(gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim == cible) {
				killcible = true;
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), true);
				owner.sendMessage("Vous venez de tuer votre cible vous obtenez désormais l'effet§c force");
			}
		}
		super.PlayerKilled(killer, victim, gameState);
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
		return "§cYahaba";
	}
}