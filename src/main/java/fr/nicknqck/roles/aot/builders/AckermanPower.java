package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.aot.soldats.LivaiV2;
import fr.nicknqck.roles.aot.soldats.Mikasa;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

public class AckermanPower extends Power {

    @Getter
    private final Ackerman ackerman;
    @Getter
    private final AckermanTimer ackermanTimer;

    public AckermanPower(@NonNull RoleBase role, @NonNull final Ackerman ackerman) {
        super("§aAckerman§r", null, role,
                "§7Au début de la partie, parmi les autres membres du camps§a Soldat§7,",
                "§7un joueur est désigner comme étant votre§a maitre§7, toute les§c 5 minutes§7",
                "§7passer proche de lui vous gagnerez§c 1/2❤ permanent§7.",
                "",
                role instanceof LivaiV2 ? "§7Votre§a maitre§7 à§a 10%§7 de chance en plus d'être§a Erwin§7." :
                        role instanceof Mikasa ? "§7Votre§a maitre§7 à§a 10%§7 de chance en plus d'être§a Armin§7." :
                                "§7Tout les joueurs ont la même probabilité d'être votre§a maitre§7.");
        this.ackerman = ackerman;
        this.ackermanTimer = new AckermanTimer(this.ackerman);
        this.ackermanTimer.startNewTimer();
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        return true;
    }
}
