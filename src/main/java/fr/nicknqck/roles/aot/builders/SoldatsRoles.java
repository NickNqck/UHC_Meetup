package fr.nicknqck.roles.aot.builders;

import fr.nicknqck.roles.builder.TeamList;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class SoldatsRoles extends AotRoles{
    private boolean ackerman = false;
    public boolean isAckerMan() {return ackerman;}
    public void setAckerMan(boolean b) {ackerman = b;}
    public SoldatsRoles(UUID player) {
        super(player);
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Soldat;
    }
}
