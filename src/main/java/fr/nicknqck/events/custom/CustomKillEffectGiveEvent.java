package fr.nicknqck.events.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

@Getter
public class CustomKillEffectGiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final GamePlayer killer;
    private final GamePlayer victim;
    private final RoleBase vRole;
    private final PotionEffect potionEffect;
    private final GameState gameState;

    public CustomKillEffectGiveEvent(GamePlayer killer, GamePlayer victim, RoleBase role, PotionEffect potionEffect, GameState gameState) {
        this.killer = killer;
        this.victim = victim;
        this.vRole = role;
        this.potionEffect = potionEffect;
        this.gameState = gameState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
