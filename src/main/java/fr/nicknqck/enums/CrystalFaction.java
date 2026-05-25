package fr.nicknqck.enums;

import lombok.Getter;

public enum CrystalFaction {

    ROYAL("Royal"),
    NOBLE("Noble"),
    CLERGER("Clerger"),
    PEUPLE("Peuple"),;

    @Getter
    private final String name;

    CrystalFaction(String name) {
        this.name = name;
    }
}