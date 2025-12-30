package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.scoreboard.ScoreBoardUpdateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

@Setter
@Getter
public abstract class KrystalBase extends RoleBase {

    private int krystalAmount;
    private final ScoreBoardEditManager scoreBoardEditManager;
    private final KrystalManager krystalManager;

    public KrystalBase(UUID player) {
        super(player);
        this.krystalAmount = 0;
        this.scoreBoardEditManager = new ScoreBoardEditManager(this);
        krystalManager = Main.getInstance().getKrystalManager();
    }

    private static class ScoreBoardEditManager implements Listener {

        private final KrystalBase role;

        private ScoreBoardEditManager(KrystalBase role) {
            this.role = role;
            if (role.getGameState().getServerState().equals(GameState.ServerStates.InGame)){
                EventUtils.registerRoleEvent(this);
            }
        }
        @EventHandler
        private void ScoreboardUpdateEvent(ScoreBoardUpdateEvent event) {
            if (!event.getScoreboard().getUuid().equals(this.role.getPlayer()))return;
            if (!this.role.getGameState().getServerState().equals(GameState.ServerStates.InGame))return;
            event.getScoreboard().getObjectiveSign().setLine(13, "");
            event.getScoreboard().getObjectiveSign().setLine(14, "§fCrystaux:§c "+this.role.getKrystalAmount());
        }
    }
}