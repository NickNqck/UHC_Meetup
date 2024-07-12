package fr.nicknqck.player;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.DayEvent;
import fr.nicknqck.events.custom.NightEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

public class EffectsGiver implements Listener {
    public EffectsGiver() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }
    @EventHandler
    private void onDay(DayEvent event) {
        for (Player player : event.getInGamePlayersWithRole()) {
            RoleBase roleBase = event.getGameState().getPlayerRoles().get(player);
            if (!roleBase.getEffects().isEmpty()) {
                for (PotionEffect potionEffect : roleBase.getEffects().keySet()) {
                    if (roleBase.getEffects().get(potionEffect).equals(EffectWhen.DAY)) {
                        player.addPotionEffect(potionEffect, true);
                    }
                }
            }
        }
    }
    @EventHandler
    private void onNight(NightEvent event) {
        for (Player player : event.getInGamePlayersWithRole()) {
            RoleBase roleBase = event.getGameState().getPlayerRoles().get(player);
            if (!roleBase.getEffects().isEmpty()) {
                for (PotionEffect potionEffect : roleBase.getEffects().keySet()) {
                    if (roleBase.getEffects().get(potionEffect).equals(EffectWhen.NIGHT)) {
                        player.addPotionEffect(potionEffect, true);
                    }
                }
            }
        }
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() != null) {
            if (event.getGamePlayerKiller().getRole() == null)return;
            for (PotionEffect potionEffect : event.getGamePlayerKiller().getRole().getEffects().keySet()) {
                if (event.getGamePlayerKiller().getRole().getEffects().get(potionEffect).equals(EffectWhen.AT_KILL)) {
                    event.getPlayerKiller().addPotionEffect(potionEffect, true);
                }
            }
        }
    }
}