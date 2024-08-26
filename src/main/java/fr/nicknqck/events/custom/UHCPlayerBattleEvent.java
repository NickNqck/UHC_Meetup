package fr.nicknqck.events.custom;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


@Getter
public class UHCPlayerBattleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final boolean patch;
    private final GamePlayer victim;
    private final GamePlayer damager;
    @Setter
    private double damage;
    private final EntityDamageByEntityEvent originEvent;
    public UHCPlayerBattleEvent(GamePlayer damaged, GamePlayer damager, EntityDamageByEntityEvent event, boolean patch) {
        this.victim = damaged;
        this.damager = damager;
        this.originEvent = event;
        this.patch = patch;
        if (damager.getRole() == null)return;
        for (Power power : damager.getRole().getPowers()) {
            if (power instanceof ItemPower) {
                if (((Player)getOriginEvent().getDamager()).getItemInHand().isSimilar(((ItemPower) power).getItem())) {
                    ((ItemPower) power).call(this);
                }
            }
        }
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
