package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.Chakra;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Chakras;

public class Futon implements Chakra {

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity entity) {
		
	}

	@Override
	public Chakras getChakres() {
		return Chakras.FUTON;
	}

	@Override
	public List<UUID> getList() {
		return Futon;
	}
	private List<UUID> Futon = new ArrayList<>();
	@Override
	public void onSecond(GameState gameState) {
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {
		if (Futon.contains(player.getUniqueId())) {
			if (event.getCause() == DamageCause.FALL) {
				event.setDamage(0.0);
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to) {
		// TODO Auto-generated method stub
		
	}

}
