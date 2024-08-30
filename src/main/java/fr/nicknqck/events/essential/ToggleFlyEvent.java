package fr.nicknqck.events.essential;

import fr.nicknqck.events.custom.doublejump.JumpEndEvent;
import fr.nicknqck.events.custom.doublejump.JumpStartEvent;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ToggleFlyEvent implements Listener {

    ToggleFlyEvent() {
        EventUtils.registerEvents(this);
    }
    @EventHandler
    private void FlyEvent(PlayerToggleFlightEvent event) {
        if (event.isCancelled())return;
        Player player = event.getPlayer();
        JumpStartEvent jumpEvent = new JumpStartEvent(player);
        Bukkit.getPluginManager().callEvent(jumpEvent);

        if (jumpEvent.isCancelled() || jumpEvent.isEnableDoubleJump()){
            return;
        }

        event.setCancelled(true);

        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().multiply(jumpEvent.getVelocity()).setY(jumpEvent.getVelocityHight()));
        JumpEndEvent endEvent = new JumpEndEvent(player, jumpEvent.getVelocity(), jumpEvent.getVelocityHight());
        Bukkit.getPluginManager().callEvent(endEvent);
    }

}