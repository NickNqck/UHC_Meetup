package fr.nicknqck.events.custom;

import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public final class GiveRoleDeclenchExternalPluginEvent extends Event {

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
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}