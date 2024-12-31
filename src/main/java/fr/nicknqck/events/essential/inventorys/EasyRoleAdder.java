package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;

public final class EasyRoleAdder {

    public static void addRoles(String name) {
       /* Nouveau système de rôle
        for (Class<? extends RoleBase> classs : Main.getInstance().getRoleManager().getRolesRegistery().keySet()) {
            if (name.equalsIgnoreCase(classs.getName()) || name.equalsIgnoreCase(classs.toString()) || name.equalsIgnoreCase(Main.getInstance().getRoleManager().getRolesRegistery().get(classs).getName())) {
                Main.getInstance().getRoleManager().addRole(classs);
                System.out.println("Added role: "+classs);
                GameState.getInstance().updateGameCanLaunch();
                break;
            }
        }*/
        //Ancien système de rôle
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
                    GameState.getInstance().addInAvailableRoles(roles,
                            Math.max(0, GameState.getInstance().getAvailableRoles().get(roles)-1));
                    GameState.getInstance().updateGameCanLaunch();
                    break;
                }
            }
        }
    }
}