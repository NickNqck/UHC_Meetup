package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

@Getter
@Setter
public abstract class DemonsSlayersRoles extends RoleBase {

    private Lames lames;
    public DemonsSlayersRoles(Player player) {
        super(player);
    }
    public Player getRightClicked(double maxDistance, int radius) {
        Player player = owner;
        Vector lineOfSight = player.getEyeLocation().getDirection().normalize();
        for (double i = 0; i < maxDistance; ++i) {
            Location add = player.getEyeLocation().add(lineOfSight.clone().multiply(i));
            Block block = add.getBlock();
            if (!block.getType().isSolid()) {
                Collection<Entity> nearbyEntities = add.getWorld().getNearbyEntities(add, radius, radius, radius);
                if (nearbyEntities.isEmpty()) {
                    continue;
                }

                Entity next = nearbyEntities.iterator().next();
                if (next instanceof Player) {
                    Player nextPlayer = (Player) next;
                    if (nextPlayer.getUniqueId().equals(player.getUniqueId()) || nextPlayer.getGameMode() == GameMode.SPECTATOR) continue;
                    return nextPlayer;
                }
                continue;
            }

            return null;
        }
        return null;
    }
}
