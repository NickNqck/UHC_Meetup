package fr.nicknqck.interfaces;

import fr.nicknqck.enums.EChakras;
import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

public interface IChakraV2 {

    @NonNull
    Map<UUID, Boolean> getMap();
    @NonNull
    String getArg0();
    @NonNull
    EChakras getChakraType();

    boolean isActivate(UUID uuid);
    void setActivateFor(UUID uuid, boolean activate);
}