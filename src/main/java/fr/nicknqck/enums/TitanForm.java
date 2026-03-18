package fr.nicknqck.enums;

import lombok.Getter;

@Getter
public enum TitanForm {

    ASSAILLANT(false),
    CUIRASSE(true),
    CHARETTE(true),
    BESTIAL(false),
    COLOSSAL(true),
    MACHOIRE(true),
    WARHAMMER(true);

    private final boolean mahr;

    TitanForm(boolean mahr) {
        this.mahr = mahr;
    }
}