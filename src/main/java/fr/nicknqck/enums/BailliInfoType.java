package fr.nicknqck.enums;

import lombok.Getter;

import javax.annotation.Nullable;

public enum BailliInfoType {

    LOCATION(3);

    @Getter
    private final int reputationCost;

    BailliInfoType(int reputationCost) {
        this.reputationCost = reputationCost;
    }

    @Nullable
    public static BailliInfoType fromString(String str) {
        for (BailliInfoType value : BailliInfoType.values()) {
            if (value.name().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }
}