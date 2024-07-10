package fr.nicknqck.roles.ds.builders;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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
    private boolean lameincassable = false;
    private boolean canuseblade = false;
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
    public void setLameIncassable(Player target, boolean a) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (!gameState.hasRoleNull(target)) {
                if (getPlayerRoles(target) instanceof DemonsSlayersRoles) {
                    DemonsSlayersRoles role = (DemonsSlayersRoles) getPlayerRoles(target);
                    role.setLameincassable(a);
                    if (a) {
                        sendMessageAfterXseconde(target, "Votre lame est devenue incassable", 1);
                    } else {
                        sendMessageAfterXseconde(target, "Votre lame n'est plus incassable", 1);
                    }
                }
            } else {
                target.sendMessage("On dirait qu'on à essayer de donner une lame incassable cependant au moment ou on vous l'a donné vous n'aviez pas de rôle");
            }

        }, 20);
    }
}
