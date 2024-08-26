package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public final class Cooldown {
    private final int cooldown;
    private int lastUse;

    public Cooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldownRemaining() {
        return this.lastUse;
    }

    public boolean isInCooldown() {
        return this.lastUse > 0;
    }

    public void use() {
        this.lastUse = cooldown;
        new cdRemover(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    public void addSeconds(int seconds) {
        this.lastUse += seconds;
    }

    public void resetCooldown() {
        this.lastUse = 0;
    }
    private static class cdRemover extends BukkitRunnable {

        private final Cooldown cooldown;

        public cdRemover(Cooldown cooldown) {
            this.cooldown = cooldown;
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.cooldown.lastUse > 0) {
                this.cooldown.lastUse--;
            } else {
                for (GamePlayer gamePlayer : GameState.getInstance().getGamePlayer().values()) {
                    if (gamePlayer.getRole() == null)continue;
                    for (Power power : gamePlayer.getRole().getPowers()) {
                        power.onEndCooldown(cooldown);
                    }
                }
                cancel();
            }
        }
    }
}