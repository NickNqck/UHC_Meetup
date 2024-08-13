package fr.nicknqck.events.custom.roles;

import fr.nicknqck.roles.ds.solos.JigoroV2;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Setter
@Getter
public class JigoroV2ChoosePacteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private JigoroV2.Pacte pacte;
    private boolean cancelled = false;
    private final Player jigoro;
    private String message;
    public JigoroV2ChoosePacteEvent(JigoroV2.@NonNull Pacte pacte, Player jigoro) {
        this.pacte = pacte;
        this.jigoro = jigoro;
        this.message = "§7Le pacte que vous aviez choisis est §cinterdit§7 !";
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
