package fr.nicknqck.roles.ns.chakratype;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuitonV2 implements IChakraV2, Listener {

    private final Map<UUID, Boolean> map;

    public SuitonV2() {
        map = new HashMap<>();
        EventUtils.registerEvents(this);
    }

    @Override
    public @NonNull Map<UUID, Boolean> getMap() {
        return this.map;
    }

    @Override
    public @NonNull String getArg0() {
        return "suiton";
    }

    @Override
    public @NonNull EChakras getChakraType() {
        return EChakras.SUITON;
    }

    @Override
    public boolean isActivate(UUID uuid) {
        return this.map.containsKey(uuid) && this.map.get(uuid);
    }

    @Override
    public void setActivateFor(UUID uuid, boolean activate) {
        if (!this.map.containsKey(uuid)) {
            this.map.put(uuid, activate);
            return;
        }
        this.map.replace(uuid, activate);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDropItem(PlayerDropItemEvent event) {
        final ItemStack item = event.getItemDrop().getItemStack();

        if (item.containsEnchantment(Enchantment.DEPTH_STRIDER)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVous ne pouvez pas jeter un objet possédant l'enchantement§7 Agilité Aquatique§c.");
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void onMove(@NonNull final PlayerMoveEvent event) {
        if (!GameState.inGame())return;
        if (!map.containsKey(event.getPlayer().getUniqueId())) {return;}
        if (!map.get(event.getPlayer().getUniqueId())) {
            if (hasDepthStrider(event.getPlayer().getInventory().getBoots())) {
                event.getPlayer().getInventory().getBoots().removeEnchantment(Enchantment.DEPTH_STRIDER);
            }
        } else {
            if (event.getPlayer().getInventory().getBoots() == null)return;
            if (!hasDepthStrider(event.getPlayer().getInventory().getBoots())) {
                event.getPlayer().getInventory().getBoots().addEnchantment(Enchantment.DEPTH_STRIDER, 1);
            }
        }
    }
    private boolean hasDepthStrider(@Nullable ItemStack item) {
        return item != null && item.containsEnchantment(Enchantment.DEPTH_STRIDER);
    }
}
