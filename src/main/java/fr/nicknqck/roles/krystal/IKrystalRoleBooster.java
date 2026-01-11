package fr.nicknqck.roles.krystal;

import lombok.NonNull;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public interface IKrystalRoleBooster {

    @NonNull
    List<Bonus> getBonus();

}