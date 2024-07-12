package fr.nicknqck.roles.builder;

import lombok.Getter;

@Getter
public enum EffectWhen {
    DAY("Jour"),
    NIGHT("Nuit"),
    PERMANENT("Permanent"),
    AT_KILL("En tuant un joueur");
    private final String name;
    EffectWhen(String name) {
        this.name = name;
    }
}
