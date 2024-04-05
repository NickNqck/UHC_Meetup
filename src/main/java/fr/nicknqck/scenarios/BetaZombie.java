package fr.nicknqck.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import fr.nicknqck.GameListener;
import fr.nicknqck.utils.ItemBuilder;

public class BetaZombie implements Listener{


	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntityType().equals(EntityType.ZOMBIE)) {
			e.setDroppedExp(e.getDroppedExp()*4);
			GameListener.dropItem(e.getEntity().getLocation(), new ItemBuilder(Material.FEATHER, 1).toItemStack());
		}
	}
}