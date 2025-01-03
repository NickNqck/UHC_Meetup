package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class DemonInferieurRole extends DemonsRoles implements Listener {
    private DemonsRoles lune;
    public DemonInferieurRole(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            List<DemonsRoles> roles = new ArrayList<>();
            for (UUID u : gameState.getInGamePlayers()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null)continue;
                if (!gameState.hasRoleNull(p)) {
                    RoleBase role = gameState.getPlayerRoles().get(p);
                    if (role instanceof DemonsRoles) {
                        DemonsRoles d = (DemonsRoles) role;
                        if (d.getRank().equals(DemonType.SUPERIEUR) || d.getRank().equals(DemonType.INFERIEUR)) {
                            roles.add((DemonsRoles) role);
                        }
                    }
                }
            }
            if (!roles.isEmpty()) {
                Collections.shuffle(roles, Main.RANDOM);
                DemonsRoles lune = roles.get(0);
                if (lune != null) {
                    getMessageOnDescription().add("§7Votre§c lune§7 est §c"+lune.owner.getName());
                    this.lune = lune;
                }
            } else {
                getMessageOnDescription().add("§7Aucune§c Lunes§7 n'a pus vous êtres assigner.");
            }
        }, 20*5);
        EventUtils.registerEvents(this);
    }

    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onUHCDeath(UHCDeathEvent event) {
        if (!event.getGameState().hasRoleNull(event.getPlayer()) && this.lune != null) {
            if (event.getGameState().getPlayerRoles().get(event.getPlayer()) instanceof DemonsRoles) {
                DemonsRoles role = (DemonsRoles) event.getGameState().getPlayerRoles().get(event.getPlayer());
                if (role.getPlayer().equals(this.lune.getPlayer())) {
                    getMessageOnDescription().remove("§7Votre§c lune§7 est §c"+event.getPlayer().getName());
                }
            }
        }
    }
}