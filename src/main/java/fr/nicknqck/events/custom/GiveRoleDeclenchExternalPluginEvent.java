package fr.nicknqck.events.custom;

import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.util.UUID;

public final class GiveRoleDeclenchExternalPluginEvent extends GameEvent{

    @Getter
    @Setter
    @Nullable
    private RoleBase roleBase;
    @Getter
    private final IRoles<?> roleType;
    @Getter
    private final UUID playerUUID;

    public GiveRoleDeclenchExternalPluginEvent(IRoles<?> roleType, UUID playerUUID) {
        this.roleType = roleType;
        this.playerUUID = playerUUID;
    }
}