package fr.nicknqck.events.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.events.IEvent;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Event implements IEvent {

    private int percent = 0;
    private boolean enable = false;
    private int minTimeProc = 60;
    private int maxTimeProc = 60*5;

    public abstract boolean isActivated();

    public String[] getLore() {
        return new String[] {
                "",
                "§fPourcentage de chance:§c "+percent+"%",
                "",
                "§fTemp minimal de déclanchement: §c"+ StringUtils.secondsTowardsBeautiful(minTimeProc),
                "§fTemp maximal de déclanchement: §c"+ StringUtils.secondsTowardsBeautiful(maxTimeProc),
                "",
                (isEnable() ? "§aActivé" : "§cDésactivé")
        };
    }
    public boolean onGameStart(final GameState gameState) {
        return false;
    }

}