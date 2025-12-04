package fr.nicknqck.player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.*;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectsGiver implements Listener {

    private static final Map<GamePlayer, Map<Class<? extends RoleBase>, PotionEffect>> killGiver = new HashMap<>();

    public EffectsGiver() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        new BukkitRunnable() {
            private final GameState gameState = GameState.getInstance();
            @Override
            public void run() {
                if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    for (final UUID u : gameState.getInGamePlayers()) {
                        final Player player = Bukkit.getPlayer(u);
                        if (player == null)continue;
                        if (!gameState.hasRoleNull(player.getUniqueId())) {
                            final RoleBase role = gameState.getGamePlayer().get(player.getUniqueId()).getRole();
                            if (!role.getEffects().isEmpty()) {
                                for (final PotionEffect effect : role.getEffects().keySet()) {
                                    if (role.getEffects().get(effect).equals(EffectWhen.PERMANENT)) {
                                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                            final PotionEffect potionEffect = new PotionEffect(effect.getType(), Integer.MAX_VALUE, effect.getAmplifier(), false, false);
                                            final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, role, potionEffect, EffectWhen.DAY);
                                            Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                            if (!effectGiveEvent.isCancelled()){
                                                player.addPotionEffect(potionEffect);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20*10);//une fois toute les 10 secondes
    }
    @EventHandler
    private void onDay(DayEvent event) {
        for (final UUID u : event.getInGamePlayersWithRole()) {
            Player player = Bukkit.getPlayer(u);
            if (player == null)continue;
            final RoleBase roleBase = event.getGameState().getGamePlayer().get(player.getUniqueId()).getRole();
            if (!roleBase.getEffects().isEmpty()) {
                for (final PotionEffect potionEffect : roleBase.getEffects().keySet()) {
                    if (roleBase.getEffects().get(potionEffect).equals(EffectWhen.DAY)) {
                        new BukkitRunnable() {
                            private int timeRemaining = Main.getInstance().getGameConfig().getMaxTimeDay();
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
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, roleBase, potionEffect, EffectWhen.DAY);
                                        Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                        if (!effectGiveEvent.isCancelled()){
                                            p.addPotionEffect(potionEffect, true);
                                        }
                                    });
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
        for (final UUID u : event.getInGamePlayersWithRole()) {
            final Player player = Bukkit.getPlayer(u);
            if (player == null)continue;
            final RoleBase roleBase = event.getGameState().getGamePlayer().get(player.getUniqueId()).getRole();
            if (!roleBase.getEffects().isEmpty()) {
                for (PotionEffect potionEffect : roleBase.getEffects().keySet()) {
                    if (roleBase.getEffects().get(potionEffect).equals(EffectWhen.NIGHT)) {
                        new BukkitRunnable() {
                            private int timeRemaining = Main.getInstance().getGameConfig().getMaxTimeDay();
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
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, roleBase, potionEffect, EffectWhen.NIGHT);
                                        Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                        if (!effectGiveEvent.isCancelled()){
                                            p.addPotionEffect(potionEffect, true);
                                        }
                                    });
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
            for (PotionEffect potionEffect : event.getGamePlayerKiller().getRole().getEffects().keySet()) {
                if (event.getGamePlayerKiller().getRole().getEffects().get(potionEffect).equals(EffectWhen.AT_KILL)) {
                    event.getPlayerKiller().addPotionEffect(potionEffect, true);
                }
            }
            if (killGiver.isEmpty())return;
            if (killGiver.containsKey(event.getGamePlayerKiller())) {
                if (event.getGameState().getGamePlayer().containsKey(event.getVictim().getUniqueId())) {
                    GamePlayer gamePlayer = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
                    if (gamePlayer.getRole() == null)return;
                    PotionEffect potionEffect = killGiver.get(gamePlayer).get(gamePlayer.getRole().getClass());
                    CustomKillEffectGiveEvent e = new CustomKillEffectGiveEvent(event.getGamePlayerKiller(), gamePlayer, gamePlayer.getRole(), potionEffect, event.getGameState());
                    Bukkit.getPluginManager().callEvent(e);
                    if (e.isCancelled())return;
                    event.getPlayerKiller().addPotionEffect(potionEffect);
                }
            }
        }
    }
    @EventHandler
    private void onEndGame(GameEndEvent event) {
        killGiver.clear();
    }
    public static void addCustomOnKill(final GamePlayer gamePlayer, final Class<? extends RoleBase> roleToKill, PotionEffect potionEffect) {
        if (!killGiver.containsKey(gamePlayer)) {
            Map<Class<? extends RoleBase>, PotionEffect> one = new HashMap<>();
            one.put(roleToKill, potionEffect);
            killGiver.put(gamePlayer, one);
        } else {
            final Map<Class<? extends RoleBase>, PotionEffect> one = new HashMap<>(killGiver.get(gamePlayer));
            killGiver.remove(gamePlayer, one);
            one.put(roleToKill, potionEffect);
            killGiver.put(gamePlayer, one);
        }
    }

}