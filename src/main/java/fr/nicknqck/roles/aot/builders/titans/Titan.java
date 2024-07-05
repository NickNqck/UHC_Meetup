package fr.nicknqck.roles.aot.builders.titans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.nicknqck.roles.aot.builders.AotRoles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.StringUtils;

public abstract class Titan {
	public abstract void onInteract(PlayerInteractEvent e, Player player);
	public abstract void onSecond();
	public abstract void onBlockBreak(BlockBreakEvent e, Player player);
	public GameState getState() {
		return GameState.getInstance();
	}
	private boolean isTransformed = false;
	public boolean isTransformedinTitan() {
		return isTransformed;
	}
	public void setTransformedinTitan(boolean t) {
		this.isTransformed = t;
	}
	public abstract void Transfo();
	public AotRoles getPlayerRole(UUID uuid) {
		Player target = Bukkit.getPlayer(uuid);
		if (target == null)return null;
		if (!GameState.getInstance().hasRoleNull(target) && GameState.getInstance().getPlayerRoles().get(target) instanceof AotRoles) {
			return (AotRoles) GameState.getInstance().getPlayerRoles().get(target);
		}
		return null;
	}
	public RoleBase getPlayerRole(Player player) {
		if (!GameState.getInstance().hasRoleNull(player)) {
			return GameState.getInstance().getPlayerRoles().get(player);
		}
		return null;
	}
	public abstract void onAPlayerDie(Player player, Entity killer);
	public void TransfoMessage(Player player, boolean eclair) {
		if (eclair) {
			GameState.getInstance().spawnLightningBolt(player.getWorld(), player.getLocation());
			for (Player p : getState().getInGamePlayers()) {p.playSound(p.getLocation(), "aotmtp.transfo", 8, 1);}
		}
		for (Player p : GameState.getInstance().getInGamePlayers()) {p.sendMessage("\n§6§lUn Titan c'est transformé !");p.sendMessage("");}
	}
	public void TransfoMessage(UUID uuid, boolean eclair) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			TransfoMessage(player, eclair);
		}
	}
	public abstract void resetCooldown();
	public abstract ItemStack[] Items();
	public void sendCooldown(Player player, int cooldown) {player.sendMessage("Cooldown: "+StringUtils.secondsTowardsBeautiful(cooldown));}
	public TitanListener getListener() {return TitanListener.getInstance();}
	public String getItemNameInHand(Player player) {
		if (player.getItemInHand() != null) {
			if (player.getItemInHand().hasItemMeta()) {
				if (player.getItemInHand().getItemMeta().hasDisplayName()) {
					return player.getItemInHand().getItemMeta().getDisplayName()+"§r";
				}else {
					return "";
				}
			}else {
				return "";
			}
		}else {
			return "";
		}
	}
	public String cd(int cooldown) {
		if (cooldown <= 0) {
			return "Utilisable";
		}else{
			return StringUtils.secondsTowardsBeautiful(cooldown);}
	}
	public void sendCustomActionBar(Player player, String msg) {NMSPacket.sendActionBar(player, msg);}
	public void sendActionBarCooldown(Player player, int cooldown) {
		if (cooldown > 0) {
		NMSPacket.sendActionBar(player, "Cooldown: "+cd(cooldown));
		}else {
			NMSPacket.sendActionBar(player, getItemNameInHand(player)+" Utilisable");
		}
	}
	public abstract void onSteal(Player sender, String[] args);
	public abstract void onAotTitan(Player player, String[] args);
	public abstract UUID getOwner();
	public static boolean hasTitan(Player player) {
		int i = 0;
		for (Titans t : Titans.values()) {
			i++;
			if (t.getTitan().getOwner() != null && t.getTitan().getOwner().equals(player.getUniqueId())) {
				return true;
			}
			if (i == Titans.values().length) {
				return false;
			}
		}
		return false;
	}
	public static boolean canStealTitan(Player player) {
		for (Titans t : Titans.values()) {
			if (t.getTitan().getListforSteal().contains(player)) {
				return false;
			}
		}
		return true;
	}
	public static Titans getStealTitans(Player player) {
		if (canStealTitan(player)) {
			for (Titans t : Titans.values()) {
				if (t.getTitan().getListforSteal().contains(player)) {
					return t;
				}
			}
		}
		return null;
	}
	public static List<Titans> getListStealTitans(Player player){
		List<Titans> tr = new ArrayList<>();
		for (Titans t : Titans.values()) {
			if (t.getTitan().getListforSteal().contains(player)) {
				tr.add(t);
			}
		}
		return tr;
	}
	public abstract void onGetDescription(Player player);
	public abstract String getName();
	public abstract void onSubCommand(Player player, String[] args);
	public abstract void onPickup(PlayerPickupItemEvent e, Player player);
	public abstract List<Player> getListforSteal();
	public abstract void PlayerKilled(Player player, Entity damager);
	public abstract void onPlayerAttackAnotherPlayer(Player damager, Player victim, EntityDamageByEntityEvent event);
}