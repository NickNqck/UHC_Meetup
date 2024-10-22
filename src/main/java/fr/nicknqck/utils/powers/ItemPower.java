package fr.nicknqck.utils.powers;

import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public abstract class ItemPower extends Power{

    private final ItemStack item;
    private InteractType interactType;

    protected ItemPower(String name, Cooldown cooldown, ItemBuilder item, RoleBase role, String... description) {
        super(name, cooldown, role, description);
        if (description != null && description.length > 0) {
            item.setLore(description);
        }
        this.item = item.setUnbreakable(true).setDroppable(false).toItemStack();
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

}