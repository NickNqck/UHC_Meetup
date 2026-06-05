package fr.nicknqck.events.custom.bailli;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.managers.crystaluhc.BailliManager;

public class BailliDesactivateEvent extends GameEvent {

    private final BailliManager bailliManager;

    public BailliDesactivateEvent(BailliManager bailliManager) {
        this.bailliManager = bailliManager;
    }
}
