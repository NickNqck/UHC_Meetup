package fr.nicknqck.roles.ns;

import lombok.Getter;

@Getter
public enum Intelligence {

    GENIE("Génié"),
    INTELLIGENT("Intelligent"),
    MOYENNE("Moyenne"),
    PEUINTELLIGENT("Peu intelligent"),
    CONNUE("Shikamaru connait l'identité des roles qui on cette stat la");
    private final String name;
    Intelligence(String name){
        this.name = name;
    }


}
