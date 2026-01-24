package fr.nicknqck.player;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.*;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EffectsGiver implements Listener {

    private static final Map<GamePlayer, Map<Class<? extends RoleBase>, PotionEffect>> killGiver = new HashMap<>();

    public EffectsGiver() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        new EffectRunnable().runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
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
    private static final class EffectRunnable extends BukkitRunnable {

        @Override
        public void run() {
            if (GameState.inGame()) {
                for (final GamePlayer gamePlayer : new ArrayList<>(GameState.getInstance().getGamePlayer().values())){
                    if (gamePlayer.getRole() == null) continue;
                    if (!gamePlayer.isAlive())continue;
                    if (!gamePlayer.isOnline())continue;
                    final RoleBase role = gamePlayer.getRole();
                    final Player player = Bukkit.getPlayer(role.getPlayer());
                    if (player == null)continue;
                    if (!role.getEffects().isEmpty()){
                        @NonNull final Map<PotionEffect, EffectWhen> map = new HashMap<>(role.getEffects());
                        @NonNull final List<PotionEffect> permaEffects = new ArrayList<>();
                        map.keySet().stream().filter(potion -> map.get(potion).equals(EffectWhen.PERMANENT)).forEach(permaEffects::add);
                        permaEffects.forEach(map::remove);
                        if (!permaEffects.isEmpty()) {
                            for (@NonNull final PotionEffect potionEffect : permaEffects) {
                                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                    final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, role, potionEffect, EffectWhen.PERMANENT);
                                    Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                    if (!effectGiveEvent.isCancelled()){
                                        player.addPotionEffect(potionEffect, true);
                                    }
                                });
                            }
                        }
                        if (GameState.getInstance().isNightTime()) {
                            for (@NonNull PotionEffect potionEffect : map.keySet()) {
                                @NonNull final EffectWhen effectWhen = map.get(potionEffect);
                                if (effectWhen.equals(EffectWhen.NIGHT)) {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, role, potionEffect, effectWhen);
                                        Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                        if (!effectGiveEvent.isCancelled()){
                                            player.addPotionEffect(potionEffect, true);
                                        }
                                    });
                                }
                            }
                        } else {
                            for (@NonNull PotionEffect potionEffect : map.keySet()) {
                                @NonNull final EffectWhen effectWhen = map.get(potionEffect);
                                if (effectWhen.equals(EffectWhen.DAY)) {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        final EffectGiveEvent effectGiveEvent = new EffectGiveEvent(player, role, potionEffect, effectWhen);
                                        Bukkit.getPluginManager().callEvent(effectGiveEvent);
                                        if (!effectGiveEvent.isCancelled()){
                                            player.addPotionEffect(potionEffect, true);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}