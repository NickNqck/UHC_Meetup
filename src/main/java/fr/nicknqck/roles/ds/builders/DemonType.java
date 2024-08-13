package fr.nicknqck.roles.ds.builders;

import lombok.Getter;

@Getter
public enum DemonType {

    LuneSuperieur("§cLune Supérieur"),
    LuneInferieur("§cLune Inferieur"),
    Demon("§cDémon Inferieur");
    private final String name;
    DemonType(String name){
        this.name = name;
    }
}