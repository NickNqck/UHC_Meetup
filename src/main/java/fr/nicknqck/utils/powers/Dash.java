package fr.nicknqck.utils.powers;

import fr.nicknqck.utils.Loc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dash {
	private final Player target;
	private int distance;
	private boolean degat;
	private double degats;
	private final List<UUID> dashedPlayers;
	private boolean isBreak;
	public Dash(Player target) {
		this.target = target;
		dashedPlayers = new ArrayList<>();
	}
	public Dash start(int DegatDistance) {
		new BukkitRunnable() {
			double dst =0;
			@Override
			public void run() {
				if (dst < distance) {
					teleportOneBlockForward(target);
					dst+=1.0;
					if (degat) {
						for (Player p : Loc.getNearbyPlayersExcept(target, DegatDistance)) {
							if (p.getUniqueId() != target.getUniqueId()) {
								if (!dashedPlayers.contains(p.getUniqueId())) {
									if (p.getHealth() - degats < 0) {
										p.setHealth(1.0);
									} else {
										p.setHealth(p.getHealth()-degats);
									}
									p.sendMessage("§7Vous avez subit§c "+degats/2+AllDesc.coeur+"§7 dû à un Dash");
									dashedPlayers.add(p.getUniqueId());
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		return this;
	}
	public Dash start(int Saut, double poussee, double timeSeparationJump) {
		int timeuse = (int) (timeSeparationJump*20);
		new BukkitRunnable() {
			int S = 0;
			@Override
			public void run() {
				Vector direction = target.getLocation().getDirection();
				target.setVelocity(direction.multiply(poussee));
				S++;
				if (degat) {
					for (Player p : Loc.getNearbyPlayersExcept(target, distance)) {
						if (!dashedPlayers.contains(p.getUniqueId())) {
							if (p.getHealth() > degats) {
								p.setHealth(p.getHealth()-degats);
							}else {
								p.setHealth(0.5);
							}
							p.damage(0.0);
							dashedPlayers.add(p.getUniqueId());
						}
					}
				}
				if (S == Saut) {
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, timeuse);
		return this;
	}
	public Dash playSound(String Sound) {
		target.playSound(target.getLocation(), Sound, 8, 1);
		System.out.println("player sound "+Sound+" for "+target.getName());
		return this;
	}
	public Dash setDegatBoolean(boolean b) {
		this.degat = b;
		return this;
	}
	public Dash setDegatDouble(double d) {
		this.degats = d;
		return this;
	}
	public Dash setDistance(int distance) {
		this.distance = distance;
		return this;
	}
	public Dash setBreak(boolean b) {
		this.isBreak =b;
		return this;
	}
	private void teleportOneBlockForward(Player player) {
        Location playerLocation = player.getLocation();
        Vector playerDirection = playerLocation.getDirection();
        playerDirection.normalize();

        Location newLocation = playerLocation.add(playerDirection);
        newLocation.setYaw(playerLocation.getYaw());
        newLocation.setPitch(playerLocation.getPitch());
        Block block = newLocation.getBlock();
		if (isBreak) {
			for (Block b : getSurroundingBlocks(player)) {
				b.setType(Material.AIR);
			}
		}
        if (block.getType() == Material.AIR || block.getType().name().contains("WATER")) {
        	player.teleport(newLocation);
        }
    }
	public List<Block> getSurroundingBlocks(Player player) {
		List<Block> blocks = new ArrayList<>();
		Location playerLocation = player.getLocation();

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					Block block = playerLocation.clone().add(x, y, z).getBlock();
					blocks.add(block);
				}
			}
		}

		return blocks;
	}

}