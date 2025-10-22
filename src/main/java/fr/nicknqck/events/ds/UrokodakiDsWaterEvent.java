package fr.nicknqck.events.ds;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class UrokodakiDsWaterEvent extends GameEvent {

    private final Player urokodaki;
    private final RoleBase role;
    private final GamePlayer gameTarget;

    public UrokodakiDsWaterEvent(Player urokodaki, RoleBase role, GamePlayer gameTarget) {
        this.urokodaki = urokodaki;
        this.role = role;
        this.gameTarget = gameTarget;
    }
}
