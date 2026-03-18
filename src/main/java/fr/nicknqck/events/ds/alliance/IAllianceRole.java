package fr.nicknqck.events.ds.alliance;

import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;

public interface IAllianceRole {

    boolean isInAlliance();
    void setInAlliance(boolean b);
    EAllianceRole knowHas();
    DemonsSlayersRoles getRole();

}
