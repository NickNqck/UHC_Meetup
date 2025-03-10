package fr.nicknqck.events.custom.beast;

import fr.nicknqck.entity.krystalbeast.Beast;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

public class BeastDamageEvent extends BeastEvent implements Cancellable {

    private boolean cancelled = false;
    @Getter
    @Setter
    private double damage;
    @Getter
    private final double finalDamage;
    @Getter
    private final EntityDamageEvent.DamageCause damageCause;

    public BeastDamageEvent(@NonNull Beast beast, double damage, double finalDamage, EntityDamageEvent.DamageCause cause) {
        super(beast);
        this.damage = damage;
        this.finalDamage = finalDamage;
        this.damageCause = cause;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
