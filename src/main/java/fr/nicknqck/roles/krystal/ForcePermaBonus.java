package fr.nicknqck.roles.krystal;

import fr.nicknqck.roles.builder.EffectWhen;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ForcePermaBonus extends Bonus{

    protected ForcePermaBonus(int amountToHaveBonus, @NonNull KrystalBase role) {
        super("§cForce permanente§r", role, amountToHaveBonus,
                "§7Tant que vous avez§d "+amountToHaveBonus+" krystaux§7, vous aurez l'effet§c Force I§7.");
    }

    @Override
    public boolean onActivate(@NonNull Player player) {
        getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        return true;
    }

    @Override
    public boolean onDisable(@NonNull Player player) {
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        return true;
    }
}
