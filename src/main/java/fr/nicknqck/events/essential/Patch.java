package fr.nicknqck.events.essential;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.PatchCritical;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.ns.Chakras;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class Patch implements Listener{
	private final GameState gameState;
	public Patch(GameState gameState) {
		this.gameState = gameState;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    private void onPatchPotion(EntityDamageByEntityEvent event) {
        if (gameState.getServerState() != ServerStates.InGame)return;
		new PatchCritical(event, gameState.getCritP());
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
	  	if (Main.isDebug()){
		  System.out.println("Original Damage: "+event.getDamage());
	  }
      	for (Titans titans : Titans.values()) {
    	  titans.getTitan().onPlayerAttackAnotherPlayer(damager, victim, event);
      }
      	if (gameState.hasRoleNull(damager)) {
		  damager.sendMessage("§cPvP§r interdit avant les rôles !");
		  event.setCancelled(true);
		  return;
	  	}
		if (gameState.hasRoleNull(victim)) {
			damager.sendMessage("§cPvP§r interdit avant les rôles !");
			event.setCancelled(true);
			return;
		}
		 UHCPlayerBattleEvent battleEvent = new UHCPlayerBattleEvent(gameState.getPlayerRoles().get(victim).getGamePlayer(), gameState.getPlayerRoles().get(damager).getGamePlayer(), event);
		 battleEvent.setPatch(false);
		 battleEvent.setDamage(event.getDamage());
		 Bukkit.getPluginManager().callEvent(battleEvent);
		 event.setDamage(battleEvent.getDamage());
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            ApplyForce(event, 20, true);
        }
        if (gameState.getPlayerRoles().containsKey(damager)) {
    		ApplyForce(event, gameState.getPlayerRoles().get(damager).getBonusForce(), false);
    	}
        if (Titans.Machoire.getTitan().getOwner() != null && Titans.Machoire.getTitan().getOwner() == damager.getUniqueId() && Titans.Machoire.getTitan().isTransformedinTitan()) {
			if (Main.isDebug()){
				System.out.println(victim.getName()+" has been resi cancelled by Titan Machoire");
			}
            return;
         }
        if (gameState.getPlayerRoles().get(victim).getAllResi() >= 100) {
            event.setCancelled(true);
        }
        if (victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			ApplyResi(event, gameState.getPlayerRoles().get(victim).getAllResi(), true);
		} else {
			if (gameState.getPlayerRoles().containsKey(victim)) {
				ApplyResi(event, gameState.getPlayerRoles().get(victim).getBonusResi(), false);
			}
		}
		UHCPlayerBattleEvent battleEvent2 = new UHCPlayerBattleEvent(gameState.getPlayerRoles().get(victim).getGamePlayer(), gameState.getPlayerRoles().get(damager).getGamePlayer(), event);
		battleEvent2.setPatch(true);
		battleEvent2.setDamage(event.getDamage());
		Bukkit.getPluginManager().callEvent(battleEvent2);
		event.setDamage(battleEvent2.getDamage());
        for (Player a : gameState.getInGamePlayers()) {
        	if (!gameState.hasRoleNull(a)) {
        		if (!event.isCancelled()) {
        			gameState.getPlayerRoles().get(a).onALLPlayerDamageByEntityAfterPatch(event, victim, damager);
        		}
        	}
        }
    }
	private void ApplyForce(EntityDamageByEntityEvent event, double fPercent, boolean effect) {
		if (effect) {
			event.setDamage((event.getDamage() / 2.3f) *(1 + 20 / 100.0f));
			if (Main.isDebug()){
				System.out.println("Force Damage to "+event.getDamage());
			}
		} else {
			if (fPercent > 0){
				double rValue = (fPercent/100) +1;
				event.setDamage(event.getDamage() *rValue);
				if (Main.isDebug()){
					System.out.println("Force Damage to "+event.getDamage());
				}
			}
		}
	}
	private void ApplyResi(EntityDamageByEntityEvent event, double reiPercent, boolean effect) {
		if (effect) {
		//	event.setDamage(event.getDamage() * (100 - reiPercent)/ 80.0f); //J'ai décider de ne pas patch la l'effet de rési car il est de base dans les valeurs que je veux
			if (Main.isDebug()){
				System.out.println("Resi Damage to "+event.getDamage());
			}
		} else {
			if (reiPercent > 0){
				double reductionFactor = 1 - (reiPercent / 100);
				event.setDamage(event.getDamage() * reductionFactor);
				if (Main.isDebug()){
					System.out.println("Bonus Resi Damage to "+event.getDamage());
				}
			}
		}
	}
}