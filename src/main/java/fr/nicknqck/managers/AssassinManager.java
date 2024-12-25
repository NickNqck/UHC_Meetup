package fr.nicknqck.managers;

import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class AssassinManager implements Listener {

    private boolean enable = false;
    private boolean activated = false;
    private final List<GamePlayer> canBeAssassin;

    public AssassinManager() {
        this.canBeAssassin = new ArrayList<>();
    }

    @EventHandler
    private void onGiveRole(RoleGiveEvent event) {
        if (!this.enable)return;
        if (event.isEndGive()) {
            if (!this.canBeAssassin.isEmpty()) {
                this.activated = true;
                System.out.println("[UHC] Assassin system is now enable, and it will use a list of "+canBeAssassin.stream());
                return;
            }
            System.out.println("[UHC] Assassin system can't be enable, because the value of demon_size is 0");
        }
        if (event.getRole() != null) {
            if (event.getRole() instanceof DemonsRoles) {
                canBeAssassin.add(event.getGamePlayer());
            }
        }
    }
}
