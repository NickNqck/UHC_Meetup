package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class AckermanTimer extends BukkitRunnable {

    private final Ackerman ackerman;
    private int amountHalfHeartGive = 0;
    private int timeNear = 0;

    public AckermanTimer(Ackerman ackerman) {
        this.ackerman = ackerman;
    }

    @Override
    public void run() {
        if (!GameState.inGame()) {
            cancel();
            return;
        }
        if (this.ackerman.getMaster() == null)return;
        final GamePlayer gameOwner = this.ackerman.getGamePlayer();
        if (!gameOwner.check())return;
        final GamePlayer gameTarget = this.ackerman.getMaster().getGamePlayer();
        if (!gameTarget.check())return;
        final Location ownerLoc = gameOwner.getLastLocation();
        final Location masterLoc = gameTarget.getLastLocation();
        if (!ownerLoc.getWorld().equals(masterLoc.getWorld()))return;
        if (ownerLoc.distance(masterLoc) <= 30) {
            if (this.timeNear == -1) {
                if (ownerLoc.distance(masterLoc) <= 5.0) {
                    this.ackerman.setKnowMaster(true);
                    this.ackerman.getGamePlayer().sendMessage("§7Vous connaissez maintenant votre§a maitre§7 (§6/aot me§7). (§oLui ne le sais pas§7)");
                    this.timeNear = 1;
                }
            } else {
                if (this.timeNear == 60*5) {
                    this.amountHalfHeartGive++;
                    if (this.amountHalfHeartGive <= 4) {
                        this.ackerman.getGamePlayer().sendMessage("§7Vous avez§a gagner§7§c 1/2❤ permanent§7.");
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.ackerman.getGamePlayer().getRole().setMaxHealth(this.ackerman.getGamePlayer().getRole().getMaxHealth()+1.0));
                    }
                }
                this.timeNear++;
            }
        }
    }

    public synchronized void startNewTimer() {
        amountHalfHeartGive = 0;
        this.timeNear = -1;
        runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
}
