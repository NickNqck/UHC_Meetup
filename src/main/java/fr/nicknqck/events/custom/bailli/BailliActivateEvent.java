package fr.nicknqck.events.custom.bailli;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.managers.crystaluhc.BailliManager;

public class BailliActivateEvent extends GameEvent {

    private final BailliManager bailliManager;

    public BailliActivateEvent(BailliManager bailliManager) {
        this.bailliManager = bailliManager;
    }
}
