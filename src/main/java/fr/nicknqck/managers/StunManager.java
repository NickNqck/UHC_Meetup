package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.config.GameConfig;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class StunManager {

    public static void stun(final GamePlayer gamePlayer, final int tick, final boolean blind, final boolean text, final Location stunLocation) {
        if (Main.getInstance().getGameConfig().getStunType().equals(GameConfig.StunType.TELEPORT)) {
            new TeleportationStunRunnable(gamePlayer, tick, blind, text, stunLocation);
        } else {
            new StuckStunListener(gamePlayer, tick, blind, text);
        }
    }

    private static class TeleportationStunRunnable extends BukkitRunnable {

        private final GamePlayer gamePlayer;
        private int tick;
        private final boolean blind;
        private final boolean text;
        private final Location stunLocation;

        private TeleportationStunRunnable( GamePlayer gamePlayer, int tick, boolean blind, boolean text, final Location stunLocation) {
            this.gamePlayer = gamePlayer;
            this.tick = tick;
            this.blind = blind;
            this.text = text;
            this.stunLocation = stunLocation;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if (tick == 0 || !gamePlayer.isAlive() || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            Player player = Bukkit.getPlayer(gamePlayer.getUuid());
            if (player == null)return;
            if (!player.getWorld().equals(stunLocation.getWorld())) {
                cancel();
                return;
            }
            player.teleport(stunLocation);
            if (blind) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false), true));
            }
            if (text && isGoodNumber(tick)) {
                player.sendTitle("§7Vous êtes immobilisé", "§7Il reste§c "+(tick/20)+"!");
            }
            tick--;
        }
        private boolean isGoodNumber(int number) {
            return number % 20 == 0;
        }
    }
    private static class StuckStunListener implements Listener {

        private final GamePlayer gamePlayer;
        private int tick;
        private final boolean blind;
        private final boolean text;

        private StuckStunListener(GamePlayer gamePlayer, int tick, boolean blind, boolean text) {
            this.gamePlayer = gamePlayer;
            this.tick = tick;
            this.blind = blind;
            this.text = text;
            EventUtils.registerEvents(this);
            new StuckStunRunnable(this);
        }
        @EventHandler
        private void onEndGame(final GameEndEvent event) {
            EventUtils.unregisterEvents(this);
        }
        @EventHandler
        private void onMove(final PlayerMoveEvent event) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            if (to != null && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
                final Location stayStill = from.clone();
                stayStill.setYaw(to.getYaw());
                stayStill.setPitch(to.getPitch());
                event.setTo(stayStill);
            }
        }
        private static class StuckStunRunnable extends BukkitRunnable {

            private final StuckStunListener stuckStunListener;

            private StuckStunRunnable(StuckStunListener stuckStunListener) {
                this.stuckStunListener = stuckStunListener;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (this.stuckStunListener.tick <= 0 || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                Player player = Bukkit.getPlayer(this.stuckStunListener.gamePlayer.getUuid());
                if (player == null)return;
                if (this.stuckStunListener.blind) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false), true));
                }
                if (this.stuckStunListener.text && isGoodNumber(this.stuckStunListener.tick)) {
                    player.sendTitle("§7Vous êtes immobilisé", "§7Il reste§c "+(this.stuckStunListener.tick/20)+"!");
                }
                this.stuckStunListener.tick--;
            }
            private boolean isGoodNumber(int number) {
                return number % 20 == 0;
            }
        }
    }

}