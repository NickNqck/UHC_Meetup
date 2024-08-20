package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
	private TextComponent desc;
	public Yahaba(UUID player) {
		super(player);
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            List<SlayerRoles> roles = new ArrayList<>();
			for (UUID u : gameState.getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
				if (!gameState.hasRoleNull(p)) {
					if (gameState.getPlayerRoles().get(p) instanceof SlayerRoles) {
						roles.add((SlayerRoles) gameState.getPlayerRoles().get(p));
					}
				}
			}
			if (!roles.isEmpty()) {
				Collections.shuffle(roles, Main.RANDOM);
				this.cible = roles.get(0).owner;
				getMessageOnDescription().add("§7Votre§c cible§7 est§c "+cible.getName()+"§7.");
			} else {
				owner.sendMessage("§7Aucune cible n'a été trouver");
			}
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
		return new String[0];
	} 
	@Override
	public void Update(GameState gameState) {
		if (!killcible) {
         for (UUID u : gameState.getInGamePlayers()) {
        	 if (cible != null) {
				 Player p = Bukkit.getPlayer(u);
				 if (p == null)continue;
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
	@Override
	public void RoleGiven(GameState gameState) {
		setMaxHealth(24.0);
		AutomaticDesc desc = new AutomaticDesc(this)
		.addCustomWhenEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 0, 0), "proche de votre cible")
		.addParticularites(
		new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possedez §c12❤§7 permanent")}),
		new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Au début de la partie une§c cible§7 vous est attribué, si vous parvenez à la tuer vous obtiendrez l'effet§c Force I§7 permanent")})
		,new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Au début de la partie une§c Lune Supérieur§7 est désigné, vous obtiendrez donc son §cpseudo")}));
		this.desc = desc.getText();
	}

	@Override
	public TextComponent getComponent() {
		return this.desc;
	}
}