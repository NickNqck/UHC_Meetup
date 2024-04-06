package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Chakra;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.RandomUtils;

public class Katon extends Chakra{

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity	 victim) {
		if (getList().contains(event.getDamager().getUniqueId())) {
			if (RandomUtils.getOwnRandomProbability(2)) {
				victim.setFireTicks(victim.getFireTicks()+100);
			}
		}
	}

	@Override
	public Chakras getChakres() {
		return Chakras.KATON;
	}
	private List<UUID> Katon = new ArrayList<>();

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
