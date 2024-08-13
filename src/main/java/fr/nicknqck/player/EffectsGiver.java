package fr.nicknqck.player;

import fr.nicknqck.GameState;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EffectsGiver implements Listener {
    public EffectsGiver() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        new BukkitRunnable() {
            private final GameState gameState = GameState.getInstance();
            @Override
            public void run() {
                if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    for (Player player : gameState.getInGamePlayers()) {
                        if (!gameState.hasRoleNull(player)) {
                            RoleBase role = gameState.getPlayerRoles().get(player);
                            if (!role.getEffects().isEmpty()) {
                                for (PotionEffect effect : role.getEffects().keySet()) {
                                    if (role.getEffects().get(effect).equals(EffectWhen.PERMANENT)) {
                                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(effect.getType(), Integer.MAX_VALUE, effect.getAmplifier(), false, false), false));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20*25);
    }
    @EventHandler
    private void onDay(DayEvent event) {
        for (Player player : event.getInGamePlayersWithRole()) {
            RoleBase roleBase = event.getGameState().getPlayerRoles().get(player);
            if (!roleBase.getEffects().isEmpty()) {
                for (PotionEffect potionEffect : roleBase.getEffects().keySet()) {
                    if (roleBase.getEffects().get(potionEffect).equals(EffectWhen.DAY)) {
                        new BukkitRunnable() {
                            private int timeRemaining = event.getGameState().timeday;
                            private final UUID uuid = player.getUniqueId();
                            @Override
                            public void run() {
                                if (event.getGameState().getServerState() != GameState.ServerStates.InGame || timeRemaining == 0 || event.getGameState().isNightTime()) {
                                    cancel();
                                    return;
                                }
                                timeRemaining--;
                                Player p = Bukkit.getPlayer(uuid);
                                if (p != null) {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> p.addPotionEffect(potionEffect, true));
                                }
                            }
                        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
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
                        new BukkitRunnable() {
                            private int timeRemaining = event.getGameState().timeday;
                            private final UUID uuid = player.getUniqueId();
                            @Override
                            public void run() {
                                if (event.getGameState().getServerState() != GameState.ServerStates.InGame || timeRemaining == 0 || !event.getGameState().isNightTime()) {
                                    cancel();
                                    return;
                                }
                                timeRemaining--;
                                Player p = Bukkit.getPlayer(uuid);
                                if (p != null) {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> p.addPotionEffect(potionEffect, true));
                                }
                            }
                        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
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