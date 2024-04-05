package fr.nicknqck.utils.betteritem;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BetterItemEvent {

    private PlayerInteractEvent event;
    private Player player;
    private ItemStack itemStack;

    public BetterItemEvent(PlayerInteractEvent event) {
        this.event = event;
        this.player = event.getPlayer();
        this.itemStack = event.getItem();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public PlayerInteractEvent getEvent() {
        return event;
    }

    public boolean isLeftClick() {
        return event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
    }

    public boolean isRightClick() {
        return event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    public void setCancelled(boolean b){
        this.event.setCancelled(b);
    }

    public boolean isCancelled(){
        return this.event.isCancelled();
    }

    public Player getRightClicked(double maxDistance){
        return getRightClicked(maxDistance, 1);
    }

    public Player getRightClicked(double maxDistance, int radius) {

        Vector lineOfSight = player.getEyeLocation().getDirection().normalize();
        for (double i = 0; i < maxDistance; ++i) {
            Location add = player.getEyeLocation().add(lineOfSight.clone().multiply(i));
            Block block = add.getBlock();
            if (!block.getType().isSolid()) {
                Collection<Entity> nearbyEntities = add.getWorld().getNearbyEntities(add, radius, radius, radius);
                if (nearbyEntities.size() == 0) {
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

    public Player getLeftClicked(double maxDistance){
        return getRightClicked(maxDistance, 1);
    }

    public Player getLeftClicked(double maxDistance, int radius) {

        Vector lineOfSight = player.getEyeLocation().getDirection().normalize();
        for (double i = 0; i < maxDistance; ++i) {
            Location add = player.getEyeLocation().add(lineOfSight.clone().multiply(i));
            Block block = add.getBlock();
            if (!block.getType().isSolid()) {
                Collection<Entity> nearbyEntities = add.getWorld().getNearbyEntities(add, radius, radius, radius);
                if (nearbyEntities.size() == 0) {
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
