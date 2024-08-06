package fr.nicknqck.events.custom;

import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


@Getter
public class UHCPlayerBattleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Setter
    private boolean patch;
    private final GamePlayer victim;
    private final GamePlayer damager;
    @Setter
    private double damage;
    private final EntityDamageByEntityEvent originEvent;
    public UHCPlayerBattleEvent(GamePlayer damaged, GamePlayer damager, EntityDamageByEntityEvent event) {
        this.victim = damaged;
        this.damager = damager;
        this.originEvent = event;
    }
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
