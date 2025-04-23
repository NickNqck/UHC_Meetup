package fr.nicknqck.events.custom.biju;

import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.player.GamePlayer;

public class BijuRecupItemEvent extends BijuEvent {

    private final GamePlayer gamePlayer;

    public BijuRecupItemEvent(BijuBase biju, GamePlayer gamePlayer) {
        super(biju);
        this.gamePlayer = gamePlayer;
    }
}
