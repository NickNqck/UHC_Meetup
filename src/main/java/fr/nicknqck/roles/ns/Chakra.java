package fr.nicknqck.roles.ns;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nicknqck.GameState;

public abstract class Chakra {
	public abstract void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity entity);
	public abstract Chakras getChakres();
	public abstract List<UUID> getList();
	public abstract void onSecond(GameState gameState);
	public abstract void onEntityDamage(EntityDamageEvent event, Player player);
	public abstract void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to);
}