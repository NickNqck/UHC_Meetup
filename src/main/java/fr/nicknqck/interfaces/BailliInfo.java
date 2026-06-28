package fr.nicknqck.interfaces;

import fr.nicknqck.enums.BailliInfoType;
import fr.nicknqck.player.GamePlayer;
import lombok.NonNull;

public interface BailliInfo {

    @NonNull
    BailliInfoType getType();
    @NonNull
    GamePlayer getGamePlayer();
    @NonNull
    Object getValue();
    void sendValueInfoTo(@NonNull final GamePlayer gamePlayer);

}
