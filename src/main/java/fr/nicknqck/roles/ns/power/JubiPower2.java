package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameListener;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JubiPower2 extends ItemPower {

    public JubiPower2(@NonNull RoleBase role) {
        super("§dJubi§r", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§dJubi"), role);
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
            @NonNull final List<UUID> jubis = new ArrayList<>();
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                final GamePlayer gamePlayer = GamePlayer.of(onlinePlayer.getUniqueId());
                if (gamePlayer == null)continue;
                if (!gamePlayer.check())continue;
                if (gamePlayer.getRole().getTeam().equals(this.getRole().getTeam())) {
                    jubis.add(onlinePlayer.getUniqueId());
                }
            }
            for (@NonNull final UUID jubi : jubis) {
                Main.getInstance().getCustomTabManager().setPrefixForAll(jubi, "§dJubi §r");
            }
            return true;
        }
        return false;
    }
}