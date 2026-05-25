package fr.nicknqck.enums;

import lombok.Getter;

@Getter
public enum Intelligence {

    PEUINTELLIGENT("Peu intelligent", 60*20),
    MOYENNE("Moyenne", 60*15),
    CONNUE("Shikamaru connait l'identité des roles qui on cette stat la", 60*15),
    INTELLIGENT("Intelligent", 60*10),
    GENIE("Génie", 60*5);

    private final String name;
    private final int enseignemenTime;

    Intelligence(String name, int enseignemenTime){
        this.name = name;
        this.enseignemenTime = enseignemenTime;
    }


}
