package fr.nicknqck.events.custom.roles.ds;

import fr.nicknqck.events.custom.GameEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

@Setter
@Getter
public class JigoroV2ChoosePacteEvent extends GameEvent {

    private boolean cancelled = false;
    private final Player jigoro;
    private String message;
    private Pacte pacte;
    public JigoroV2ChoosePacteEvent(@NonNull Pacte pacte, Player jigoro) {
        this.pacte = pacte;
        this.jigoro = jigoro;
        this.message = "§7Le pacte que vous aviez choisis est §cinterdit§7 !";
    }

    public enum Pacte {
        SOLO,
        KAIGAKU,
        ZENITSU,
        NON_CHOISIS
    }
}
