package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;

public final class EasyRoleAdder {

    public static void addRoles(String name) {
        for (GameState.Roles roles : GameState.Roles.values()) {
            if (roles.getItem().getItemMeta().getDisplayName().equals(name)) {
                GameState.getInstance().addInAvailableRoles(roles, Math.min(GameState.getInstance().getInLobbyPlayers().size(), GameState.getInstance().getAvailableRoles().get(roles)+1));
                GameState.getInstance().updateGameCanLaunch();
                break;
            }
        }
    }
    public static void removeRoles(String name) {
        for (GameState.Roles roles : GameState.Roles.values()) {
            if (roles.getItem().getItemMeta().getDisplayName().equals(name)) {
                if (GameState.getInstance().getAvailableRoles().get(roles) > 0) {
                    GameState.getInstance().addInAvailableRoles(roles, Math.max(GameState.getInstance().getInLobbyPlayers().size()-1, GameState.getInstance().getAvailableRoles().get(roles)-1));
                    GameState.getInstance().updateGameCanLaunch();
                    break;
                }
            }
        }
    }
}