package fr.nicknqck.events.essential;

import fr.nicknqck.GameState;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InteractEntityEvents implements Listener {
    @EventHandler
    private void onInteract(PlayerInteractEntityEvent event) {
        if (!event.isCancelled()) {
            GameState gameState = GameState.getInstance();
            Player player = event.getPlayer();
            if (!gameState.hasRoleNull(player.getUniqueId())) {
                for (Power power : gameState.getGamePlayer().get(player.getUniqueId()).getRole().getPowers()) {
                    if (power instanceof ItemPower) {
                        if (event.getPlayer().getItemInHand().isSimilar(((ItemPower) power).getItem())) {
                            ((ItemPower) power).call(event);
                        }
                    }
                }
            }
        }
    }
}
