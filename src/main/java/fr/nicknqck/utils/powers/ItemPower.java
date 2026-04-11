package fr.nicknqck.utils.powers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.power.CreateActionBarEvent;
import fr.nicknqck.events.custom.power.UpdateActionBarEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
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
public abstract class ItemPower extends Power {

    private final ItemStack item;
    private InteractType interactType;
    @Setter
    private boolean showCdInHand = true;
    private final ShowCdRunnable showCdRunnable;

    public ItemPower(@NonNull String name, Cooldown cooldown, ItemBuilder item,@NonNull RoleBase role, String... description) {
        super(name, cooldown, role, description);
        if (description != null && description.length > 0) {
            item.setLore(description);
        }
        this.item = item.setUnbreakable(true).setDroppable(false).toItemStack();
        if (showCdInHand && cooldown != null && cooldown.getOriginalCooldown() > 0) {
            this.showCdRunnable = new ShowCdRunnable(this);
            this.showCdRunnable.runTaskTimerAsynchronously(getPlugin(), 0, 1);
        } else {
            this.showCdRunnable = null;
        }
        EventUtils.getPowerCantBeDropMap().put(this.item, this);
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
    public void tryUpdateActionBar() {}

    public enum InteractType {
        INTERACT,
        BLOCK_PLACE,
        BLOCK_BREAK,
        ATTACK_ENTITY,
        INTERACT_ENTITY,
        DROP_ITEM
    }

    public static class ShowCdRunnable extends BukkitRunnable {

        private final UUID user;
        private final GameState gameState;
        private final ItemStack item;
        private final GamePlayer gamePlayer;
        @Getter
        @Setter
        private boolean isCustomText = false;
        @Getter
        @Setter
        private String customTexte = "";
        private final ItemPower itemPower;

        public ShowCdRunnable(final ItemPower itemPower) {
            this.user = itemPower.getRole().getPlayer();
            this.gamePlayer = itemPower.getRole().getGamePlayer();
            this.gameState = GameState.getInstance();
            this.item = itemPower.getItem();
            this.itemPower = itemPower;
            System.out.println("Started "+this+" ("+itemPower.getName()+") for "+user);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!this.gamePlayer.isAlive())return;
            if (!this.itemPower.isShowCdInHand())  {
                Main.getInstance().debug("Cancelled "+this+" for "+user+" because ce n'etait pas cense start de base");
                cancel();
                return;
            }
            final Player player = Bukkit.getPlayer(user);
            if (player != null) {
                if (this.itemPower.getCooldown() == null)return;
                final Cooldown cooldown = this.itemPower.getCooldown();
                if (player.getItemInHand().isSimilar(item)) {
                    if (this.gamePlayer.getActionBarManager().containsKey(cooldown.getUniqueId().toString())) {
                        updateActionBar(cooldown);
                    } else {
                        createActionBar(cooldown);
                    }
                } else if (this.gamePlayer.getActionBarManager().containsKey(cooldown.getUniqueId().toString())) {
                    this.gamePlayer.getActionBarManager().removeInActionBar(cooldown.getUniqueId().toString());
                }
            }
        }
        private void updateActionBar(@NonNull final Cooldown cooldown) {
            if (this.isCustomText()) {
                this.itemPower.tryUpdateActionBar();
                final UpdateActionBarEvent updateActionBarEvent = new UpdateActionBarEvent(cooldown.getUniqueId().toString(), this.customTexte, this.itemPower, true);
                Bukkit.getPluginManager().callEvent(updateActionBarEvent);
                if (!updateActionBarEvent.isCancelled()){
                    this.gamePlayer.getActionBarManager().updateActionBar(cooldown.getUniqueId().toString(), updateActionBarEvent.getValue());
                }
            } else {
                final UpdateActionBarEvent updateActionBarEvent = new UpdateActionBarEvent(cooldown.getUniqueId().toString(), cooldown.isInCooldown() ?
                        "§bCooldown: §c"+ StringUtils.secondsTowardsBeautiful(cooldown.getCooldownRemaining()) :
                        item.getItemMeta().getDisplayName()+" est§c utilisable", this.itemPower, false);
                Bukkit.getPluginManager().callEvent(updateActionBarEvent);
                if (!updateActionBarEvent.isCancelled()){
                    this.gamePlayer.getActionBarManager().updateActionBar(cooldown.getUniqueId().toString(), updateActionBarEvent.getValue());
                }
            }
        }
        private void createActionBar(@NonNull final Cooldown cooldown) {
            if (this.isCustomText()) {
                this.itemPower.tryUpdateActionBar();
                final CreateActionBarEvent createActionBarEvent = new CreateActionBarEvent(cooldown.getUniqueId().toString(), this.customTexte, this.itemPower, true);
                Bukkit.getPluginManager().callEvent(createActionBarEvent);
                if (!createActionBarEvent.isCancelled()){
                    this.gamePlayer.getActionBarManager().addToActionBar(cooldown.getUniqueId().toString(), createActionBarEvent.getValue());
                }
            } else {
                final CreateActionBarEvent createActionBarEvent = new CreateActionBarEvent(cooldown.getUniqueId().toString(), cooldown.isInCooldown() ?
                        "§bCooldown: §c"+ StringUtils.secondsTowardsBeautiful(cooldown.getCooldownRemaining()) :
                        item.getItemMeta().getDisplayName()+" est§c utilisable", this.itemPower, true);
                Bukkit.getPluginManager().callEvent(createActionBarEvent);
                if (!createActionBarEvent.isCancelled()){
                    this.gamePlayer.getActionBarManager().addToActionBar(cooldown.getUniqueId().toString(), createActionBarEvent.getValue());
                }
            }
        }
    }
}