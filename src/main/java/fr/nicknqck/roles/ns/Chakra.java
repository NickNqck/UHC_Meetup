package fr.nicknqck.roles.ns;

import fr.nicknqck.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.UUID;

public interface Chakra {

    void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity entity);
    Chakras getChakres();
    List<UUID> getList();
    void onSecond(GameState gameState);
    void onEntityDamage(EntityDamageEvent event, Player player);
    void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to);

}
