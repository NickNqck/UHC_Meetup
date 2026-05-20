package fr.nicknqck.utils.powers.crystal;

import fr.nicknqck.enums.CeintureElements;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ElementalPower;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EauPower extends ElementalPower {

    public EauPower(@NonNull RoleBase role) {
        super("§9Eau§r", new Cooldown(60*3), role, CeintureElements.EAU, 0, 0, 255);
    }

    @Override
    public void onImpact(@NonNull Location impactLocation, @Nullable Player hitPlayer) {
        final List<Player> list = new ArrayList<>(Loc.getNearbyPlayers(impactLocation, 4.5));
        for (Player target : list) {
            if (target.getUniqueId().equals(getRole().getPlayer()))continue;
            MathUtil.sendParticleTo(target, EnumParticle.WATER_SPLASH, target.getLocation());
            final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
            if (gamePlayer != null) {
                if (gamePlayer.check()) {
                    gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, false), EffectWhen.NOW);
                    continue;
                }
            }
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, false));
        }
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        launchBall(player);
        return true;
    }
}
