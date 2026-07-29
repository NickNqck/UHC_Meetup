package fr.nicknqck.roles.ns.chakratype;

import java.util.*;

import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Doton implements IChakraV2, Listener {

	private final Map<UUID, Boolean> map;

    public Doton() {
        map = new HashMap<>();
		EventUtils.registerEvents(this);
    }

    @Override
	public @NonNull Map<UUID, Boolean> getMap() {
		return map;
	}

	@Override
	public @NonNull String getArg0() {
		return "Doton";
	}

	@Override
	public @NonNull EChakras getChakraType() {
		return EChakras.DOTON;
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
	private void onDamage(@NonNull final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))return;
		if (isActivate(event.getEntity().getUniqueId())) {
			if (RandomUtils.getOwnRandomProbability(Main.getInstance().getGameConfig().getNarutoConfig().getDotonPercent())) {
				event.setDamage(0.0);
				event.setCancelled(true);
				((Player) event.getEntity()).setNoDamageTicks(20);
				event.getEntity().sendMessage(Main.getInstance().getNAME()+"§7 Vous avez esquiver un coup grace au§6 Doton§7.");
				if (event instanceof EntityDamageByEntityEvent) {
					((EntityDamageByEntityEvent) event).getDamager().sendMessage(Main.getInstance().getNAME()+"§c "+event.getEntity().getName()+"§7 a esquiver un coup grace au§6 Doton§7.");
				}
			}
		}
	}
}
