package fr.nicknqck.roles.ds.builders;

import org.bukkit.entity.Player;

public abstract class DemonsRoles extends DemonsSlayersRoles {
    public DemonsRoles(Player player) {
        super(player);
    }
    public abstract DemonType getRank();
}