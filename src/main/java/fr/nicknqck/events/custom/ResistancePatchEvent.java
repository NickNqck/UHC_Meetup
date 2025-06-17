package fr.nicknqck.events.custom;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class ResistancePatchEvent extends GameEvent implements Cancellable {

    private final double resistancePercent;
    private final boolean isEffect;
    private final Player victim;
    private final Player damager;
    private final boolean negateResistance;
    private boolean cancel = false;

    public ResistancePatchEvent(double resistancePercent, boolean isEffect, Player victim, Player damager, boolean negateResistance) {
        this.resistancePercent = resistancePercent;
        this.isEffect = isEffect;
        this.victim = victim;
        this.damager = damager;
        this.negateResistance = negateResistance;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}