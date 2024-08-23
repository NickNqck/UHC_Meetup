package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.roles.ns.shinobi.Fugaku;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class FugakuPowerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    @NonNull
    private final Power power;
    @NonNull
    private final Player cible;
    @NonNull
    private final Fugaku fugaku;
    public FugakuPowerEvent(@NonNull Power power, @NonNull Player clicked, @NonNull Fugaku fugaku) {
        this.power = power;
        this.cible = clicked;
        this.fugaku = fugaku;
    }

    @Getter
    public enum Power {
        AFFAIBLISSEMENT("Affaiblissement"),
        COMBAT("§6§lPlace au combat !"),
        ATTAQUE("§cAttaque");
        private final String name;
        Power(String name) {
            this.name = name;
        }
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
