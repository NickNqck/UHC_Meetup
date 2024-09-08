package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;

import java.util.Arrays;

public abstract class ConfiguratorRole implements IConfiguratorRole {

    public void addRoles(String name) {
        GameState.Roles role = Arrays.stream(GameState.Roles.values()).filter(r -> r.getItem().getItemMeta().getDisplayName().equals(name)).findAny().get();
        GameState.getInstance().addInAvailableRoles(role, Math.min(GameState.getInstance().getInLobbyPlayers().size(), GameState.getInstance().getAvailableRoles().get(role)+1));
    }
    public void removeRoles(String name) {
        GameState.Roles role = Arrays.stream(GameState.Roles.values()).filter(r -> r.getItem().getItemMeta().getDisplayName().equals(name)).findAny().get();
        GameState.getInstance().addInAvailableRoles(role, Math.max(0, GameState.getInstance().getAvailableRoles().get(role)-1));
    }

}