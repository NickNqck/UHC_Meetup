package fr.nicknqck.roles.ns.chakratype;

import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Raiton implements IChakraV2, Listener {

	private final Map<UUID, Boolean> map;


    public Raiton() {
        map = new HashMap<>();
		EventUtils.registerEvents(this);
    }

	@Override
	public @NonNull Map<UUID, Boolean> getMap() {
		return map;
	}

	@Override
	public @NonNull String getArg0() {
		return "Raiton";
	}

	@Override
	public @NonNull EChakras getChakraType() {
		return EChakras.RAITON;
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
		if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))return;
		if (isActivate(event.getDamager().getUniqueId())) {
			if (RandomUtils.getOwnRandomProbability(Main.getInstance().getGameConfig().getNarutoConfig().getRaitonPercent())) {
				final GamePlayer gamePlayer = GamePlayer.of(event.getEntity().getUniqueId());
				if (gamePlayer != null) {
					gamePlayer.stun(10);
					((Player) event.getEntity()).setHealth(Math.max(1.0, ((Player) event.getEntity()).getHealth()-1.0));
					event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
					event.getEntity().sendMessage(Main.getInstance().getNAME()+"§7 Vous avez été toucher par du§e Raiton§7.");
					event.getDamager().sendMessage(Main.getInstance().getNAME()+"§7 Vous avez toucher§c "+event.getEntity().getName()+"§7 avec votre§e Raiton§7.");
				}
			}
		}
	}
}