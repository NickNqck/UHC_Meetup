package fr.nicknqck.events.custom.time;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;

@Getter
public class SecondPassEvent extends GameEvent {

    private final GameState gameState;

    public SecondPassEvent(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isInGame() {
        return this.gameState.getServerState().equals(GameState.ServerStates.InGame);
    }

}