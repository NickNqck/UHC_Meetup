package fr.nicknqck.roles.aot.builders;

import com.avaje.ebean.validation.NotNull;
import fr.nicknqck.player.GamePlayer;

import javax.annotation.Nullable;

public interface Ackerman {

    @NotNull
    String getName();
    @Nullable
    SoldatsRoles getMaster();
    GamePlayer getGamePlayer();
    boolean knowHisMaster();
    void setKnowMaster(boolean b);
    AckermanPower getAckermanPower();

}