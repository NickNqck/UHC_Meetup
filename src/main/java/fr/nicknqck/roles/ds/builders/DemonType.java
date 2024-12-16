package fr.nicknqck.roles.ds.builders;

import lombok.Getter;

@Getter
public enum DemonType {

    SUPERIEUR("§cLune Supérieur"),
    INFERIEUR("§cLune Inferieur"),
    NEZUKO("§cLune Supérieur"),
    DEMON("§cDémon Inferieur");
    private final String name;
    DemonType(String name){
        this.name = name;
    }
}