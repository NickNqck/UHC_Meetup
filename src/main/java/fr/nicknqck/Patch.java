package fr.nicknqck;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.ns.Chakras;

public class Patch implements Listener{
	GameState gameState;
	public Patch(GameState gameState) {
		this.gameState = gameState;
	}
	@EventHandler(priority = EventPriority.HIGHEST)
    private void onPatchPotion(EntityDamageByEntityEvent event) {
        if (gameState.getServerState() != ServerStates.InGame)return;
		new PatchCritical(event, gameState.critP);
		for (Chakras ch : Chakras.values()) {
			ch.getChakra().onPlayerDamageAnEntity(event, (event.getEntity()));
		}
		if (!(event.getEntity() instanceof Player)) return;
        for (Player a : gameState.getInGamePlayers()) {
        	if (!gameState.hasRoleNull(a)) {
        		gameState.getPlayerRoles().get(a).onALLPlayerDamageByEntity(event, (Player) event.getEntity(), event.getDamager());
        	}
        }
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
      if (damager.getItemInHand() == null) return;
      if (damager.getItemInHand().getType() == Material.AIR)return;
      System.out.println("Original Damage: "+event.getDamage());
      for (Titans titans : Titans.values()) {
    	  titans.getTitan().onPlayerAttackAnotherPlayer(damager, victim, event);
      }
        if (!gameState.getPlayerRoles().containsKey(damager)) {
        	damager.sendMessage("§cPvP§r interdit avant les rôles !");
        	event.setCancelled(true);
        }
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            ApplyForce(event, gameState.getPlayerRoles().get(damager).getForce(), damager, true);
        }
        if (gameState.getPlayerRoles().containsKey(damager)) {
    		ApplyForce(event, gameState.getPlayerRoles().get(damager).getBonusForce(), damager, false);
    	}
        if (Titans.Machoire.getTitan().getOwner() != null && Titans.Machoire.getTitan().getOwner() == damager.getUniqueId() && Titans.Machoire.getTitan().isTransformedinTitan()) {
        	System.out.println(victim.getName()+" has been resi cancelled by Titan Machoire");
            return;
         }
        if (gameState.getPlayerRoles().get(victim).getAllResi() >= 100) {
            event.setCancelled(true);
        }
        	if (victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                ApplyResi(event, gameState.getPlayerRoles().get(victim).getAllResi(), victim, true);
            } else {
            	if (gameState.getPlayerRoles().containsKey(victim)) {
            		ApplyResi(event, gameState.getPlayerRoles().get(victim).getBonusResi(), victim, false);
            	}
            }
        if (event.getDamage() <= 0) event.setDamage(0.5);
        if (event.getDamage() > 100.0) event.setDamage(20.0);
        if (event.getDamage() > 30)event.setDamage(30);
        for (Player a : gameState.getInGamePlayers()) {
        	if (!gameState.hasRoleNull(a)) {
        		if (!event.isCancelled()) {
        			gameState.getPlayerRoles().get(a).onALLPlayerDamageByEntityAfterPatch(event, victim, damager);
        		}
        	}
        }
    }
	private void ApplyForce(EntityDamageByEntityEvent event, double fPercent, Player damager, boolean effect) {
		if (effect) {
			event.setDamage((event.getDamage() / 2.3f) *(1 + gameState.getPlayerRoles().get(damager).getForce() / 100.0f));
			System.out.println("Force Damage to "+event.getDamage());
		} else {
			if (fPercent > 0){
				double rValue = (double)(fPercent/100)+1;
				event.setDamage(event.getDamage() *rValue);
				System.out.println("Force Damage to "+event.getDamage());
			}
		}
	}
	private void ApplyResi(EntityDamageByEntityEvent event, double reiPercent, Player victim, boolean effect) {
		if (effect) {
			event.setDamage(event.getDamage() * (100 - reiPercent)/ 80.0f);
			System.out.println("Resi Damage to "+event.getDamage());
		} else {
			if (reiPercent > 0){
				double rValue = (double)(reiPercent/100)+1;
				event.setDamage(event.getDamage() *rValue);
				System.out.println("Bonus Resi Damage to "+event.getDamage());
			}
		}
	}
}