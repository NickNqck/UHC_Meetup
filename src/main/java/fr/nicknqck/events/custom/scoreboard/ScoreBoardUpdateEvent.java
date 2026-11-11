package fr.nicknqck.events.custom.scoreboard;

import fr.nicknqck.scoreboard.ObjectiveSign;
import fr.nicknqck.scoreboard.PersonalScoreboard;
import fr.nicknqck.scoreboard.VObjective;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class ScoreBoardUpdateEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final Map<Integer, String> lines;
    private final ConcurrentLinkedQueue<VObjective.VScore> scores;
    private final PersonalScoreboard scoreboard;

    /**
     * Événement asynchrone : il est appelé depuis l'exécuteur du ScoreboardManager,
     * jamais depuis le thread principal. Les listeners ne doivent donc pas toucher
     * au monde ni aux entités directement (passer par runTask si besoin).
     */
    public ScoreBoardUpdateEvent(final HashMap<Integer, String> lines,final ConcurrentLinkedQueue<VObjective.VScore> scores,final PersonalScoreboard scoreboard) {
        super(true);
        this.lines = lines;
        this.scores = scores;
        this.scoreboard = scoreboard;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
