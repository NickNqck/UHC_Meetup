package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.roles.ds.builders.SlayerRoles;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class PillierRoles extends SlayerRoles {
    public PillierRoles(UUID player) {
        super(player);
    }
}