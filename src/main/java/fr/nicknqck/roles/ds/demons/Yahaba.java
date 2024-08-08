package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Yahaba extends DemonInferieurRole {

	private Player cible;
	private boolean killcible = false;

	public Yahaba(UUID player) {
		super(player);
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            List<Player> Ciblable = new ArrayList<>(gameState.getInGamePlayers());
			Collections.shuffle(Ciblable, Main.RANDOM);
			Ciblable.stream().filter(p -> !gameState.hasRoleNull(p)).filter(p -> !(gameState.getPlayerRoles().get(p) instanceof SlayerRoles)).forEach(Ciblable::remove);
			Collections.shuffle(Ciblable, Main.RANDOM);
			this.cible = Ciblable.get(0);
			owner.sendMessage("§7Votre §ccible§7 est "+(cible != null ? cible.getDisplayName() : "inexistante"));
		}, 60L);
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}

	@Override
	public DemonType getRank() {
		return DemonType.Demon;
	}

	@Override
	public Roles getRoles() {
		return Roles.Yahaba;
	}
	@Override
	public String[] Desc() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (cible != null && !killcible) {
            	owner.sendMessage("§cVotre cible est:§r "+cible.getName());
            }
        }, 20);
		return AllDesc.Yahaba;
	} 
	@Override
	public void Update(GameState gameState) {
		if (!killcible) {
         for (Player p:gameState.getInGamePlayers()) {
        	 if (cible != null) {
        		if (p == cible && p.getWorld().equals(owner.getWorld())) {
        			 if (p.getLocation().distance(owner.getLocation())<= 15) {
        				 owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
        			 }
        	    }
        	 }
         }
        }else {
        	givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*3, 1, true);
        }
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setMaxHealth(24.0);
		super.RoleGiven(gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim == cible) {
				killcible = true;
				owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), true);
				owner.sendMessage("Vous venez de tuer votre cible vous obtenez désormais l'effet§c force");
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
	}

	@Override
	public String getName() {
		return "Yahaba";
	}
}