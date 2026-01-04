package fr.nicknqck.roles.ns.power;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;

public abstract class SubSusanoPower extends Power {

    public SubSusanoPower(@NonNull String name, Cooldown cooldown, @NonNull RoleBase role, String... descriptions) {
        super(name, cooldown, role, descriptions);
    }

    public abstract void onSusanoEnd();
}