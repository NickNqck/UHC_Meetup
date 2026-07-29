package fr.nicknqck.roles.ns.chakratype;

import java.util.*;

import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Katon implements IChakraV2, Listener {

	private final Map<UUID, Boolean> map;

    public Katon() {
        map = new HashMap<>();
		EventUtils.registerEvents(this);
    }

    @Override
	public @NonNull Map<UUID, Boolean> getMap() {
		return this.map;
	}

	@Override
	public @NonNull String getArg0() {
		return "Katon";
	}

	@Override
	public @NonNull EChakras getChakraType() {
		return EChakras.KATON;
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
	private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
		if (isActivate(event.getDamager().getUniqueId())) {
			if (RandomUtils.getOwnRandomProbability(Main.getInstance().getGameConfig().getNarutoConfig().getKatonPercent())) {
				event.getEntity().setFireTicks(100);
				event.getDamager().sendMessage(Main.getInstance().getNAME()+"§c "+event.getEntity().getName()+"§7 a subit les effets de votre§c Katon§7.");
				event.getEntity().sendMessage(Main.getInstance().getNAME()+"§7Vous avez subit les effets d'un§c Katon§7.");
			}
		}
	}
}
