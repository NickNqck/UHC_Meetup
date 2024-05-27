package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;

@Getter
public class RoleGiveEvent extends CustomEventBase {

    private final GameState gameState;
    private final RoleBase role;
    private final GameState.Roles type;
    private final GamePlayer gamePlayer;
    public RoleGiveEvent(GameState gameState, RoleBase role, GameState.Roles roleType, GamePlayer gamePlayer) {
        super();
        this.gameState = gameState;
        this.role = role;
        this.type = roleType;
        this.gamePlayer = gamePlayer;
    }
}
