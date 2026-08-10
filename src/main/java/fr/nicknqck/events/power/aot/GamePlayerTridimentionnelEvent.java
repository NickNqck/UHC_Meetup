package fr.nicknqck.events.power.aot;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;

@Getter
public final class GamePlayerTridimentionnelEvent extends GameEvent {

    private final GamePlayer gamePlayer;
    private final RoleBase role;

    public GamePlayerTridimentionnelEvent(GamePlayer gamePlayer, RoleBase role) {
        this.gamePlayer = gamePlayer;
        this.role = role;
    }
}