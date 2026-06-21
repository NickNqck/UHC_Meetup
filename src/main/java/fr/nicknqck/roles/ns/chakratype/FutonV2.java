package fr.nicknqck.roles.ns.chakratype;

import fr.nicknqck.enums.EChakras;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FutonV2 implements IChakraV2, Listener {

    private final Map<UUID, Boolean> map;

    public FutonV2() {
        map = new HashMap<>();
        EventUtils.registerEvents(this);
    }

    @Override
    public @NonNull Map<UUID, Boolean> getMap() {
        return this.map;
    }

    @Override
    public @NonNull String getArg0() {
        return "futon";
    }

    @Override
    public @NonNull EChakras getChakraType() {
        return EChakras.FUTON;
    }

    @Override
    public boolean isActivate(UUID uuid) {
        return map.containsKey(uuid) && map.get(uuid);
    }

    @Override
    public void setActivateFor(UUID uuid, boolean activate) {
        if (!map.containsKey(uuid)) {
            map.put(uuid, activate);
            return;
        }
        map.replace(uuid, activate);

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(@NonNull final EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        if (this.map.containsKey(event.getEntity().getUniqueId())) {
            if (this.map.get(event.getEntity().getUniqueId())) {
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
    }
}