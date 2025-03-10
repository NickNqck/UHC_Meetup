package fr.nicknqck.entity.krystalbeast.creator;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface IBeastCreator {

    List<PotionEffect> getPotionEffects();
    Location getRandomLocation();
    List<ItemStack> getLoots();
    int getMaxKrystalDrop();
    List<EntityDamageEvent.DamageCause> getImmunisedDamageCause();

}
