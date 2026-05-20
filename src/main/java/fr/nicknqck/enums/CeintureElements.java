package fr.nicknqck.enums;

import fr.nicknqck.interfaces.IElements;

public enum CeintureElements implements IElements {

    FEU("§cFeu", 1,
            "§7Lance le§d crystal§7 tout droit, à l'atterrissage, tout les joueurs (sauf l'utilisateur)",
            "§7étant à moins de§c 4,3 blocs§6 brûlerons§7 pendant§c 9 secondes§7,",
            " si au lieu de toucher le sol le§d crystal§7 touche un joueur, alors, le temps de§6 brûlure§7",
            "§7pour lui uniquement sera§c doublé§7."),
    EAU("§bEau", 12,
            "§7Lance le§d crystal§7 tout droit, à l'atterrissage, tout les joueurs (sauf l'utilisateur)",
            "§7étant à moins de§c 4,5 blocs§7 subiront§c 10 secondes§7 de§9 Blindness I§7."
    ),
    VENT("§aVent", 10,
            "§7Lance le§d crystal§7 tout droit, à l'atterrissage, tout les joueurs (sauf l'utilisateur)",
            "§7étant à moins de§c 5 blocs§7 subiront pendant§c 10 secondes§7 un effet de§c Slowness 1§7,",
            "§7si au lieu de toucher le sol le§d crystal§7 touche un joueur, alors, le temps sera§c doublé7."
    );

    private final String name;
    private final int dyeColor;
    private final String[] description;

    CeintureElements(String name, int dyeColor, String... description) {
        this.name = name;
        this.dyeColor = dyeColor;
        this.description = description;
    }

    @Override
    public String[] getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getDyeColor() {
        return this.dyeColor;
    }
}