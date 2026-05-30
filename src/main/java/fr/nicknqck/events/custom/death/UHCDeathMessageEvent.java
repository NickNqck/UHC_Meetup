package fr.nicknqck.events.custom.death;

import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UHCDeathMessageEvent extends GameEvent {

    private final List<String> deathMessages;
    private final Player victim;
    private final UUID killerUUID;
    private boolean sendToVictim = true;
    private boolean sendToEveryoneElse = true;
    private boolean sendToKiller = true;

    public UHCDeathMessageEvent(List<String> deathMessages, Player victim, UUID killerUUID) {
        this.deathMessages = deathMessages;
        this.victim = victim;
        this.killerUUID = killerUUID;
    }
}
