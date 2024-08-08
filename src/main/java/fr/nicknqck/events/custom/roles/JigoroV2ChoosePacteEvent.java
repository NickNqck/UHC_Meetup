package fr.nicknqck.events.custom.roles;

import fr.nicknqck.roles.ds.solos.JigoroV2;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Setter
@Getter
public class JigoroV2ChoosePacteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private JigoroV2.Pacte pacte;
    private boolean cancelled;
    public JigoroV2ChoosePacteEvent(JigoroV2.@NonNull Pacte pacte) {
        this.pacte = pacte;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
