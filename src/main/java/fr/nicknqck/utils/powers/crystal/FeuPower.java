package fr.nicknqck.utils.powers.crystal;

import fr.nicknqck.enums.CeintureElements;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ElementalPower;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FeuPower extends ElementalPower {

    public FeuPower(@NonNull RoleBase role) {
        super("§cFeu§r", new Cooldown(60*3), role, CeintureElements.FEU, 255, 0, 0);
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        launchBall(player);
        return true;
    }

    @Override
    public void onImpact(@NonNull Location impactLocation, @Nullable Player hitPlayer) {
        final List<Player> touchedPlayers = Loc.getNearbyPlayers(impactLocation, 4.3);
        for (@NonNull final Player touchedPlayer : touchedPlayers) {
            if (touchedPlayer.getUniqueId().equals(getRole().getPlayer())) {
                continue;
            }
            touchedPlayer.setFireTicks(touchedPlayer.getFireTicks() + 180);
            touchedPlayer.sendMessage("§fVous avez été§6 brûlé§f par un§d crystal§f de§c feu§f. ");
        }
        if (hitPlayer != null) {
            hitPlayer.setFireTicks(hitPlayer.getFireTicks() + 180);
            hitPlayer.setHealth(Math.max(1.0, hitPlayer.getHealth()-1.0));
        }
    }

}