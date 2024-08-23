package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Demon_Simple extends DemonInferieurRole implements Listener {
	private double force;
	public Demon_Simple(UUID player) {
		super(player);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.DEMON;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public Roles getRoles() {
		return Roles.Demon;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Demon_Simple;
	}

	@Override
	public String getName() {
		return "Demon";
	}

	@Override
	public void Update(GameState gameState) {
		if (gameState.nightTime) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
		} else {
			givePotionEffet(PotionEffectType.WEAKNESS, 60, 1, true);
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				force+=3;
				owner.sendMessage("Vous venez de tuer: "+victim.getName()+" vous obtenez donc +§c 3% de Force§f ce qui vous à fait monter jusqu'a: "+ force+"%");
			}
		}
	}

	@Override
	public void resetCooldown() {
		
	}
	@EventHandler
	private void onEndGame(EndGameEvent event) {
		HandlerList.unregisterAll(this);
	}
	@EventHandler
	private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager().getUniqueId().equals(getPlayer())) {
			double rValue = (force/100) +1;
			event.setDamage(event.getDamage() *rValue);
		}
	}
}