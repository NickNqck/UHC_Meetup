package fr.nicknqck.events.custom.roles.ns;

import fr.nicknqck.events.custom.GameEvent;
import fr.nicknqck.roles.ns.power.IzanamiV2;
import lombok.Getter;

import java.util.List;

@Getter
public class IzanamiStartEvent extends GameEvent {

    private final List<IzanamiV2.MissionUser> missionUserList;
    private final List<IzanamiV2.MissionTarget> missionTargetList;


    public IzanamiStartEvent(List<IzanamiV2.MissionUser> missionUserList, List<IzanamiV2.MissionTarget> missionTargetList) {
        super();
        this.missionUserList = missionUserList;
        this.missionTargetList = missionTargetList;
    }
}