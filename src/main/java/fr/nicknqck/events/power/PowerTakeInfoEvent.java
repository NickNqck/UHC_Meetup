package fr.nicknqck.events.power;

import fr.nicknqck.enums.InfoType;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PowerTakeInfoEvent extends Event implements Cancellable {

    @Getter
    private final Power power;
    @Getter
    @Setter
    private GamePlayer gameTarget;
    @Getter
    private final InfoType infoType;
    private boolean cancelled = false;

    public PowerTakeInfoEvent(Power power, GamePlayer gameTarget, InfoType infoType) {
        this.power = power;
        this.gameTarget = gameTarget;
        this.infoType = infoType;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public void sendCancelMessage(@NonNull final GamePlayer gamePlayer) {
        gamePlayer.sendMessage("§cQuelque chose vous empêche d'utiliser votre pouvoir !");
    }
    public void sendCancelMessage(@NonNull final Player player) {
        player.sendMessage("§cQuelque chose vous empêche d'utiliser votre pouvoir !");
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}