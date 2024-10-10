package fr.nicknqck.roles.ns.builders;

import lombok.Getter;

import java.util.UUID;

public abstract class UchiwaRoles extends NSRoles{

    public UchiwaRoles(UUID player) {
        super(player);
    }

    @Getter
    public enum UchiwaType {
        LEGENDAIRE("Légendaire"),
        IMPORTANT("Important"),
        INUTILE("Inutile");
        private final String name;

        UchiwaType(String name) {
            this.name = name;
        }
    }

    public abstract UchiwaType getUchiwaType();

}
