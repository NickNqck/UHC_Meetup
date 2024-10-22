package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;

public final class Cooldown {
    @Getter
    private final int originalCooldown;
    @Setter
    private int actualCooldown;

    public Cooldown(int cooldown) {
        this.originalCooldown = cooldown;
    }

    public int getCooldownRemaining() {
        return this.actualCooldown;
    }

    public boolean isInCooldown() {
        return this.actualCooldown > 0;
    }

    public void use() {
        this.actualCooldown = originalCooldown;
        new cdRemover(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    public void addSeconds(int seconds) {
        this.actualCooldown += seconds;
    }

    public void resetCooldown() {
        this.actualCooldown = 0;
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
            if (this.cooldown.actualCooldown > 0) {
                this.cooldown.actualCooldown--;
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