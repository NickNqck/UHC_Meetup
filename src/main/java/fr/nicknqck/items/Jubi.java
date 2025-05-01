package fr.nicknqck.items;

import fr.nicknqck.GameListener;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class Jubi {

    @Getter
    private static UUID uuidCrafter = null;

    public Jubi(final Player jubiCrafter) {
        uuidCrafter = jubiCrafter.getUniqueId();
    }
    private static class JubiPower extends ItemPower {

        public JubiPower(@NonNull RoleBase role) {
            super("Jubi", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§dJubi"), role);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*300, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*300, 1, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*300, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*300, 3, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*300, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false), EffectWhen.NOW);
                getRole().giveHealedHeartatInt(5.0);
                GameListener.SendToEveryone("");
                GameListener.SendToEveryone("§c§lLe récéptacle de§d§l Jûbi§c§l invoque sa puissance !");
                GameListener.SendToEveryone("");
                return true;
            }
            return false;
        }
    }
}