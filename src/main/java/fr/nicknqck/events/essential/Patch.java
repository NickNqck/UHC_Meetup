package fr.nicknqck.events.essential;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.PatchCritical;
import fr.nicknqck.events.custom.ResistancePatchEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.titans.impl.MachoireV2;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.UUID;

public class Patch implements Listener{

	private final GameState gameState;

	public Patch(GameState gameState) {
		this.gameState = gameState;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    private void onPatchPotion(EntityDamageByEntityEvent event) {
        if (gameState.getServerState() != ServerStates.InGame)return;
		if (Main.isDebug()){
		System.out.println("Original Damage: "+event.getDamage());
		}
		new PatchCritical(event, Main.getInstance().getGameConfig().getCritPercent());
		if (Main.isDebug()){
			System.out.println("Original Damage (After Critical Nerf): "+event.getDamage());
		}
		for (final Chakras ch : Chakras.values()) {
			ch.getChakra().onPlayerDamageAnEntity(event, (event.getEntity()));
		}
		if (!(event.getEntity() instanceof Player)) return;
		callRoleDamage(event);
        if (!(event.getDamager() instanceof Player)) return;
        final Player damager = (Player) event.getDamager();
        final Player victim = (Player) event.getEntity();
     	if (damager.getItemInHand() == null) return;
        Arrays.stream(Titans.values()).forEach(titans -> titans.getTitan().onPlayerAttackAnotherPlayer(damager, victim, event));
		if (checkNull(damager, victim)) {
			event.setCancelled(true);
			return;
		}
		final GamePlayer gameVictim = gameState.getGamePlayer().get(victim.getUniqueId());
		final GamePlayer gameDamager = gameState.getGamePlayer().get(damager.getUniqueId());
		 UHCPlayerBattleEvent battleEvent = new UHCPlayerBattleEvent(gameVictim, gameDamager, event, false);
		 battleEvent.setDamage(event.getDamage());
		 Bukkit.getPluginManager().callEvent(battleEvent);
		 event.setDamage(battleEvent.getDamage());
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            ApplyForce(event, Main.getInstance().getGameConfig().getForcePercent(), true);
        }
		ApplyForce(event, gameDamager.getRole().getBonusForce(), false);
        if (Titans.Machoire.getTitan().getOwner() != null && Titans.Machoire.getTitan().getOwner() == damager.getUniqueId() && Titans.Machoire.getTitan().isTransformedinTitan()) {
			if (Main.isDebug()){
				System.out.println(victim.getName()+" has been resi cancelled by Titan Machoire");
			}
			event.setDamage(event.getDamage()*1.2);
            return;
		}
		if (Main.getInstance().getTitanManager().hasTitan(event.getDamager().getUniqueId())) {
			if (Main.getInstance().getTitanManager().getTitan(event.getDamager().getUniqueId()) instanceof MachoireV2) {
				if (Main.getInstance().getTitanManager().getTitan(event.getDamager().getUniqueId()).isTransformed()) {
					if (victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
						event.setDamage(event.getDamage()*1.2);
					}
					return;
				}
			}
		}
		final double allResi = gameVictim.getRole().getBonusResi() + gameVictim.getRole().getResi();
        if (allResi >= 100) {
            event.setCancelled(true);
        }
        if (victim.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			int resi = 1;
			for (final PotionEffect potionEffect : victim.getActivePotionEffects()) {
				if (potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
					resi = potionEffect.getAmplifier()+1;
					break;
				}
			}
			ApplyResi(event, (Main.getInstance().getGameConfig().getResiPercent()*resi), true);
		}
		ApplyResi(event, gameState.getGamePlayer().get(victim.getUniqueId()).getRole().getBonusResi(), false);
		final UHCPlayerBattleEvent battleEvent2 = new UHCPlayerBattleEvent(gameVictim, gameDamager, event, true);
		battleEvent2.setDamage(event.getDamage());
		Bukkit.getPluginManager().callEvent(battleEvent2);
		event.setDamage(battleEvent2.getDamage());
		callAfterRoleDamage(event);
    }

	private void callAfterRoleDamage(@NonNull final EntityDamageByEntityEvent event) {
		for (@NonNull final UUID u : gameState.getInGamePlayers()) {
			Player a = Bukkit.getPlayer(u);
			if (a == null)continue;
			if (!gameState.hasRoleNull(a.getUniqueId())) {
				if (!event.isCancelled()) {
					gameState.getGamePlayer().get(a.getUniqueId()).getRole().onALLPlayerDamageByEntityAfterPatch(event, (Player)event.getEntity(), (Player)event.getDamager());
				}
			}
		}
	}
	private boolean checkNull(@NonNull final Player victim,@NonNull final Player damager) {
		if (gameState.hasRoleNull(damager.getUniqueId())) {
			damager.sendMessage("§cPvP§r interdit avant les rôles !");
			return true;
		}
		if (gameState.hasRoleNull(victim.getUniqueId())) {
			damager.sendMessage("§cPvP§r interdit avant les rôles !");
			return true;
		}
		return false;
	}
	private void callRoleDamage(@NonNull final EntityDamageByEntityEvent event) {
		for (final UUID u : gameState.getInGamePlayers()) {
			Player a = Bukkit.getPlayer(u);
			if (a == null)continue;
			if (!gameState.hasRoleNull(a.getUniqueId())) {
				gameState.getGamePlayer().get(a.getUniqueId()).getRole().onALLPlayerDamageByEntity(event, (Player) event.getEntity(), event.getDamager());
			}
		}
	}
	private void ApplyForce(EntityDamageByEntityEvent event, double fPercent, boolean effect) {
		if (effect) {
			event.setDamage(event.getDamage()*0.5304740497679363);//Pour retirer la force
			double force = fPercent/100;
			force = force+1.0;
            BigDecimal bd = new BigDecimal(event.getDamage());
			bd = bd.setScale(2, RoundingMode.HALF_UP);
			event.setDamage(bd.doubleValue());
			event.setDamage(event.getDamage()*force);
		//	event.setDamage((event.getDamage() / 2.3f) *(1 + 20 / 100.0f));
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
	private void ApplyResi(EntityDamageByEntityEvent event, double reiPercent, boolean isEffect) {
		final ResistancePatchEvent resistancePatchEvent = new ResistancePatchEvent(reiPercent, isEffect, (Player) event.getEntity(), (Player) event.getDamager(), false);
		Bukkit.getPluginManager().callEvent(resistancePatchEvent);
		if (resistancePatchEvent.isCancelled())return;
		if (isEffect) {
			double baseDamage = event.getDamage();
			System.out.println("initDamage: " + baseDamage+", reiPercent = "+reiPercent);

// Annule la réduction de Résistance SI elle est active
			for (PotionEffect effect : ((Player) event.getEntity()).getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
					int level = effect.getAmplifier(); // Résistance I -> 0
					double vanillaReduction = (level + 1) * 0.2;
					// Spigot a déjà appliqué la réduction. On l'annule :
					baseDamage = baseDamage / (1.0 - vanillaReduction);
					break;
				}
			}

			System.out.println("AfterPatchResiDamage: " + baseDamage);

// Maintenant on applique NOTRE pourcentage de réduction perso
			double finalDamage = baseDamage * (1.0 - (reiPercent / 100.0));
			System.out.println("AfterFullPatchDamage: " + finalDamage);

			event.setDamage(finalDamage);

			/*double resi = reiPercent/100;
			final Player entity = (Player) event.getEntity();
			for (final PotionEffect potionEffect : entity.getActivePotionEffects()) {
				if (potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
					resi = (double) (20 * (potionEffect.getAmplifier() + 1)) /100;
					break;
				}
			}
			resi = 1-resi;
			event.setDamage(event.getDamage()*resi);
		//	event.setDamage(event.getDamage() * (100 - reiPercent)/ 80.0f); //J'ai décider de ne pas patch l'effet de rési car il est de base dans les valeurs que je veux
			*/
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