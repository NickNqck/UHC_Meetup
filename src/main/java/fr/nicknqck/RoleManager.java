package fr.nicknqck;

import fr.nicknqck.roles.builder.Role;
import fr.nicknqck.roles.ds.slayers.Hotaru;
import fr.nicknqck.roles.ds.slayers.Inosuke;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RoleManager {
    private final Map<Class<? extends Role>, Role> rolesRegistery;

    public RoleManager() {
        this.rolesRegistery = new HashMap<>();
    }

    public void registerRoles() throws Exception {
        registerRole(Inosuke.class);
        registerRole(Hotaru.class);
    }

    private void registerRole(Class<? extends Role> roleClass) throws Exception {
        final Role role = roleClass.getConstructor(UUID.class).newInstance(UUID.randomUUID());
        this.rolesRegistery.put(roleClass, role);
    }
}
