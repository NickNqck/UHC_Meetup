package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.utils.LunarHandler;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public final class ShowGlowingRunnable extends BukkitRunnable {

    private final ItemPower itemPower;
    private Player target = null;
    private boolean glowing = false;

    public ShowGlowingRunnable(ItemPower itemPower) {
        this.itemPower = itemPower;
        runTaskTimerAsynchronously(Main.getInstance(), 100, 1);
    }

    @Override
    public void run() {
        if (!GameState.inGame()) {
            cancel();
            return;
        }

        if (!this.itemPower.isTargetPlayer()) return;

        final Player owner = Bukkit.getPlayer(this.itemPower.getRole().getGamePlayer().getUuid());
        if (owner == null) return;

        boolean isApolloEnabled = Bukkit.getPluginManager().isPluginEnabled("Apollo-Bukkit");
        if (!isApolloEnabled) {
            cancel();
            return;
        }
        // Si le joueur ne tient plus le bon item en main
        if (owner.getItemInHand() == null || !owner.getItemInHand().isSimilar(this.itemPower.getItem())) {
            if (this.glowing) {
                LunarHandler.resetGlow(owner);
                this.glowing = false;
            }
            return;
        }

        // Récupération de la cible avec la logique d'arguments conservée de ton code original
        double rayTraceArg = 1;
        Player playerTarget = RayTrace.getTargetPlayer(owner, this.itemPower.getTargetDistance(), rayTraceArg);
        this.target = playerTarget;

        // Si aucune cible n'est regardée
        if (playerTarget == null) {
            if (this.glowing) {
                LunarHandler.resetGlow(owner);
                this.glowing = false;
            }
            return;
        }

        // Si une cible est trouvée et qu'Apollo est activé, on applique le Glow
        if (!this.glowing) {
            LunarHandler.setGlow(owner, playerTarget.getUniqueId());
        }

        this.glowing = true;
    }

    public Player getTarget() {
        if (!Main.getInstance().isLunarEnabled()) {
            this.target = RayTrace.getTargetPlayer(this.itemPower.getRole().getGamePlayer().getPlayer(), this.itemPower.getTargetDistance(), null);
        }
        return target;
    }
}