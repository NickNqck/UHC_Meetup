package fr.nicknqck.events.custom;

import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

public class EffectGiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    @Getter
    private final Player player;
    @Getter
    private final RoleBase role;
    @Getter
    private final PotionEffect potionEffect;
    @Getter
    private final EffectWhen effectWhen;

    public EffectGiveEvent(Player player, RoleBase roleBase, PotionEffect potionEffect, EffectWhen effectWhen) {
        this.player = player;
        this.role = roleBase;
        this.potionEffect = potionEffect;
        this.effectWhen = effectWhen;
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
    public void setCancelled(final boolean b) {
        this.cancelled = b;
    }
}
