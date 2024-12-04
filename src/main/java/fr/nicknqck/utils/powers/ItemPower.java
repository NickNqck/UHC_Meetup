package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@Getter
public abstract class ItemPower extends Power{

    private final ItemStack item;
    private InteractType interactType;
    @Setter
    private boolean showCdInHand = true;

    protected ItemPower(@NonNull String name, Cooldown cooldown, ItemBuilder item,@NonNull RoleBase role, String... description) {
        super(name, cooldown, role, description);
        if (description != null && description.length > 0) {
            item.setLore(description);
        }
        this.item = item.setUnbreakable(true).setDroppable(false).toItemStack();
        if (showCdInHand && cooldown != null && cooldown.getOriginalCooldown() > 0) {
            new ShowCdRunnable(role, cooldown, this.item).runTaskTimerAsynchronously(getPlugin(), 0, 2);
        }
    }
    public void call(Object event) {
        if (event instanceof PlayerInteractEvent) {
            this.interactType = InteractType.INTERACT;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse(((PlayerInteractEvent) event).getPlayer(), args);
            return;
        }
        if (event instanceof PlayerDropItemEvent) {
            this.interactType = InteractType.DROP_ITEM;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse(((PlayerDropItemEvent) event).getPlayer(), args);
            return;
        }
        if (event instanceof UHCPlayerBattleEvent) {
            this.interactType = InteractType.ATTACK_ENTITY;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse((Player) ((UHCPlayerBattleEvent) event).getOriginEvent().getDamager(), args);
            return;
        }
        if (event instanceof PlayerInteractEntityEvent) {
            this.interactType = InteractType.INTERACT_ENTITY;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse(((PlayerInteractEntityEvent) event).getPlayer(), args);
            return;
        }
        if (event instanceof BlockPlaceEvent) {
            this.interactType = InteractType.BLOCK_PLACE;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse(((BlockPlaceEvent) event).getPlayer(), args);
            return;
        }
        if (event instanceof BlockBreakEvent) {
            this.interactType = InteractType.BLOCK_BREAK;
            HashMap<String, Object> args = new HashMap<String, Object>() {{
                put("event", event);
            }};
            this.checkUse(((BlockBreakEvent) event).getPlayer(), args);
        }
    }


    public enum InteractType {
        INTERACT,
        BLOCK_PLACE,
        BLOCK_BREAK,
        ATTACK_ENTITY,
        INTERACT_ENTITY,
        DROP_ITEM
    }

    private static class ShowCdRunnable extends BukkitRunnable {

        private final UUID user;
        private final Cooldown cooldown;
        private final GameState gameState;
        private final ItemStack item;
        private final GamePlayer gamePlayer;

        private ShowCdRunnable(RoleBase role, Cooldown cooldown, ItemStack item) {
            this.user = role.getPlayer();
            this.gamePlayer = role.getGamePlayer();
            this.cooldown = cooldown;
            this.gameState = GameState.getInstance();
            this.item = item;
            System.out.println("Started "+this+" for "+user);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!this.gamePlayer.isAlive())return;
            Player player = Bukkit.getPlayer(user);
            if (player != null) {
                if (player.getItemInHand().isSimilar(item)) {
                    if (this.gamePlayer.getActionBarManager().containsKey(this.cooldown.getUniqueId().toString())) {
                        this.gamePlayer.getActionBarManager().updateActionBar(this.cooldown.getUniqueId().toString(), this.cooldown.isInCooldown() ?
                                "§bCooldown: §c"+ StringUtils.secondsTowardsBeautiful(cooldown.getCooldownRemaining()) :
                                item.getItemMeta().getDisplayName()+" est§c utilisable");
                    } else {
                        this.gamePlayer.getActionBarManager().addToActionBar(this.cooldown.getUniqueId().toString(), this.cooldown.isInCooldown() ?
                                "§bCooldown: §c"+ StringUtils.secondsTowardsBeautiful(cooldown.getCooldownRemaining()) :
                                item.getItemMeta().getDisplayName()+" est§c utilisable");
                    }
                } else if (this.gamePlayer.getActionBarManager().containsKey(this.cooldown.getUniqueId().toString())) {
                    this.gamePlayer.getActionBarManager().removeInActionBar(this.cooldown.getUniqueId().toString());
                }
            }
        }
    }
}