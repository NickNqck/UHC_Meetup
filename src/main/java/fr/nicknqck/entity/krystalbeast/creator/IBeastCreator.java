package fr.nicknqck.entity.krystalbeast.creator;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface IBeastCreator {

    List<PotionEffect> getPotionEffects();
    Location getRandomLocation();

}
