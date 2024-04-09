package fr.nicknqck.scenarios.impl;

import fr.nicknqck.Main;
import fr.nicknqck.scenarios.BasicScenarios;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import fr.nicknqck.GameListener;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class BetaZombie extends BasicScenarios implements Listener{


	private boolean isActive = false;
	private int proba = 0;

	public BetaZombie(){
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}

	@EventHandler
	private void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntityType().equals(EntityType.ZOMBIE) && isActive) {
			if (RandomUtils.getOwnRandomProbability(proba)){
				e.setDroppedExp(e.getDroppedExp()*4);
				GameListener.dropItem(e.getEntity().getLocation(), new ItemBuilder(Material.FEATHER, 1).toItemStack());
			}
		}
	}

	@Override
	public String getName() {
		return "§r§fBetaZombie";
	}

	@Override
	public ItemStack getAffichedItem() {
		return new ItemBuilder(Material.FEATHER).setName(getName()).setLore(getName()+"§r§f est actuellement: "+(isActive ? "§aActivé" : "§cDésactivé"),"§r§fLa probabilité de loot sur un zombie est actuellement de:§b "+proba+"%").toItemStack();
	}

	@Override
	public void onClick(Player player) {
		if (isClickGauche()) {
			isActive = true;
		}
		if (isClickDroit()){
			isActive = false;
		}
		if (isShiftClick() && proba < 100){
			proba++;
		}
		if (isDropClick() && proba > 0){
			proba--;
		}
	}
}