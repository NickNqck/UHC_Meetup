package fr.nicknqck.roles.ns.chakratype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.ns.Chakra;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.RandomUtils;

public class Doton implements Chakra {

	@Override
	public void onPlayerDamageAnEntity(EntityDamageByEntityEvent event, Entity entity) {}

	@Override
	public Chakras getChakres() {
		return Chakras.DOTON;
	}
	private final List<UUID> Doton = new ArrayList<>();
	@Override
	public List<UUID> getList() {
		return Doton;
	}

	@Override
	public void onSecond(GameState gameState) {
		for (UUID uuid : Doton) {
			if (Bukkit.getPlayer(uuid) != null) {
				if (Bukkit.getPlayer(uuid).getLocation().getY() < 50) {
					Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 0, false, false), true);
				}
			}
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {
		if (event.isCancelled())return;
		if (!Doton.contains(player.getUniqueId()))return;
		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL))return;
		if (RandomUtils.getOwnRandomProbability(3)) {
			player.setNoDamageTicks(15);
			player.sendMessage("§7Vous avez esquivé un coup grâce à votre Chakra.");
			event.setCancelled(true);
		}
	}

	@Override
	public void onPlayerMoove(PlayerMoveEvent e, Player p, Location from, Location to) {}

}
