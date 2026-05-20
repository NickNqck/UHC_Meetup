package fr.nicknqck.enums;

import lombok.Getter;

public enum StunType {

    TELEPORT("Téléportation", "§a"),
    STUCK("Anti-Déplacement", "§c");

    @Getter
    private final String name;
    @Getter
    private final String color;

    StunType(String name, String color) {
        this.name = name;
        this.color = color;
    }

}
