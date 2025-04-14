package fr.nicknqck.roles.ns.builders;

import lombok.Getter;

@Getter
public enum EUchiwaType {

    LEGENDAIRE("Légendaire"),
    IMPORTANT("Important"),
    INUTILE("Inutile");

    private final String name;

    EUchiwaType(String name) {
        this.name = name;
    }
}