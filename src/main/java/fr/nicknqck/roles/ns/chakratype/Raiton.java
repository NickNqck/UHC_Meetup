package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.Chakra;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.RandomUtils;
import net.minecraft.server.v1_8_R3.DamageSource;

public class Raiton implements Chakra {

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity victim) {
			if (Raiton.contains(event.getDamager().getUniqueId())) {
				if (RandomUtils.getOwnRandomProbability(2)) {
					GameState.getInstance().spawnLightningBolt(victim.getWorld(), victim.getLocation());
					((CraftEntity) victim).getHandle().damageEntity(DamageSource.MAGIC, 1);
				}
			}
	}

	@Override
	public Chakras getChakres() {
		return Chakras.RAITON;
	}
	private List<UUID> Raiton = new ArrayList<>();
	@Override
	public List<UUID> getList() {
		return Raiton;
	}

	@Override
	public void onSecond(GameState gameState) {}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {}

	@Override
	public void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to) {}
}