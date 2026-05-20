package fr.nicknqck.events.power;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.interfaces.IElements;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.ElementalPower;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrystalSearchForPowerEvent extends GameEvent {

    private final IElements element;
    private final RoleBase role;
    private ElementalPower power;

    public CrystalSearchForPowerEvent(IElements element, RoleBase role) {
        this.element = element;
        this.role = role;
    }
}