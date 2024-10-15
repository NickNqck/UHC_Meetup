package fr.nicknqck.utils.powers;

import fr.nicknqck.roles.builder.RoleBase;
import lombok.NonNull;

public abstract class CommandPower extends Power{

    public CommandPower(@NonNull String name, Cooldown cooldown, @NonNull RoleBase role, String... descriptions) {
        super(name, cooldown, role, descriptions);
    }

}