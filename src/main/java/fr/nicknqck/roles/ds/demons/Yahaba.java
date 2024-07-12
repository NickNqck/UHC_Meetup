package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Yahaba extends DemonsRoles {
	
	private Player lunesup;
	private Player cible;
	private boolean killcible = false;

	public Yahaba(Player player) {
		super(player);
		setForce(20);
		owner.sendMessage(Desc());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            List<Player> Ciblable = new ArrayList<>(gameState.getInGamePlayers());
			Collections.shuffle(Ciblable, Main.RANDOM);
			for (Player p : Ciblable) {
				if (!gameState.hasRoleNull(p)) {
					if (gameState.getPlayerRoles().get(p) instanceof DemonsRoles) {

					} else {
						
					}
				}
			}
			Ciblable.stream().filter(p -> !gameState.hasRoleNull(p)).filter(p -> gameState.getPlayerRoles().get(p).getRoles().getTeam().equals(TeamList.Slayer));
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
            if (lunesup != null) {
                owner.sendMessage("§cVotre lune supérieure est:§r "+lunesup.getName());
            }
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
		return "§cYahaba";
	}
}