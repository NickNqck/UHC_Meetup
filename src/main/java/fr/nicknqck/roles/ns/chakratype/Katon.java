package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.IChakra;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.EChakras;
import fr.nicknqck.utils.RandomUtils;

public class Katon implements IChakra {

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity	 victim) {
		if (getList().contains(event.getDamager().getUniqueId())) {
			if (RandomUtils.getOwnRandomProbability(2)) {
				victim.setFireTicks(victim.getFireTicks()+100);
			}
		}
	}

	@Override
	public EChakras getChakres() {
		return EChakras.KATON;
	}
	private final List<UUID> Katon = new ArrayList<>();

	@Override
	public List<UUID> getList() {
		return Katon;
	}

	@Override
	public void onSecond(GameState gameState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to) {
		// TODO Auto-generated method stub
		
	}

}
