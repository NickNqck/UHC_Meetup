package fr.nicknqck.events.power;

import fr.nicknqck.interfaces.IElements;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.ElementalPower;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class CrystalSearchForPowerEvent extends Event {

    private final IElements element;
    private final RoleBase role;
    private ElementalPower power;

    public CrystalSearchForPowerEvent(IElements element, RoleBase role) {
        this.element = element;
        this.role = role;
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}