package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DemonInferieurRole extends DemonsRoles {
    public DemonInferieurRole(Player player) {
        super(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            List<DemonsRoles> roles = new ArrayList<>();
            for (Player p : gameState.getInGamePlayers()) {
                if (!gameState.hasRoleNull(p)) {
                    RoleBase role = gameState.getPlayerRoles().get(p);
                    if (role instanceof DemonsRoles) {
                        if (((DemonsRoles) role).getRank().name().contains("Lune")) {
                            roles.add((DemonsRoles) role);
                        }
                    }
                }
            }
            if (!roles.isEmpty()) {
                Collections.shuffle(roles, Main.RANDOM);
                DemonsRoles lune = roles.get(0);
                if (lune != null) {
                    getKnowedRoles().add(lune);
                }
            } else {
                getMessageOnDescription().add("§7Aucune§c Lunes§7 n'a pus vous êtres assigner.");
            }
        }, 20*5);
    }
}
