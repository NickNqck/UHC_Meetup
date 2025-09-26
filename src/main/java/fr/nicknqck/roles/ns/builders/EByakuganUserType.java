package fr.nicknqck.roles.ns.builders;

import lombok.Getter;

@Getter
public enum EByakuganUserType {

    HINATA("Hinata", "§a", "§aHinata"),
    NEJI("Neji", "§a", "§aNeji"),
    KAGUYA("Kaguya", "§e", "§eKaguya"),
    AUTRE("?", "?", "??");

    private final String name;
    private final String color;
    private final String nameAndColor;

    EByakuganUserType(String name, String color, String nameAndColor) {
        this.name = name;
        this.color = color;
        this.nameAndColor = nameAndColor;
    }
}
