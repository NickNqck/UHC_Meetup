package fr.nicknqck.events.custom.time;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;

@Getter
public class OnSecond extends GameEvent {

    private final GameState gameState;

    public OnSecond(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isInGame() {
        return this.gameState.getServerState().equals(GameState.ServerStates.InGame);
    }

}