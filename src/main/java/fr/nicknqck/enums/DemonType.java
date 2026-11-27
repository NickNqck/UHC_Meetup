package fr.nicknqck.enums;

import lombok.Getter;

@Getter
public enum DemonType {

    SUPERIEUR("§cLune Supérieure"),
    INFERIEUR("§cLune Inférieure"),
    NEZUKO("§cLune Supérieure"),
    DEMON("§cDémon Inférieur");
    private final String name;
    DemonType(String name){
        this.name = name;
    }
}