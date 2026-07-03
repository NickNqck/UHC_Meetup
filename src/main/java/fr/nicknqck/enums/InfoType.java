package fr.nicknqck.enums;

import lombok.Getter;

@Getter
public enum InfoType {

    TEAM("Équipe"),
    ROLE("Rôle"),
    CHAKRA("Chakra"),
    INV("Inventaire"),
    POSITION("Position"),
    INTELLIGENCE("Intelligence");

    private final String name;

    InfoType(String name) {
        this.name = name;
    }
}