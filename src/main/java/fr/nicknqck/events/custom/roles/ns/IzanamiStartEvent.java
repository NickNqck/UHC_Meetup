package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.roles.ns.power.IzanamiV2;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
public class IzanamiStartEvent extends Event {

    private final List<IzanamiV2.MissionUser> missionUserList;
    private final List<IzanamiV2.MissionTarget> missionTargetList;


    public IzanamiStartEvent(List<IzanamiV2.MissionUser> missionUserList, List<IzanamiV2.MissionTarget> missionTargetList) {
        super();
        this.missionUserList = missionUserList;
        this.missionTargetList = missionTargetList;
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}