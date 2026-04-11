package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.interfaces.IRole;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.IUncompatibleRole;
import fr.nicknqck.roles.builder.RoleBase;

public final class EasyRoleAdder {

    public static void addRoles(String name) {
        Main.getInstance().getLogger().info("Name = "+name);
        for (Class<? extends RoleBase> aClass : Main.getInstance().getRoleManager().getRolesRegistery().keySet()) {
            final IRole iRole = Main.getInstance().getRoleManager().getRolesRegistery().get(aClass);
            if (iRole == null) {
                Main.getInstance().debug(aClass+" is null, maybe isn't a IRole ?");
                continue;
            }
            if (name.equalsIgnoreCase(cleanString(Main.getInstance().getRoleManager().getRolesRegistery().get(aClass).getName())) ||
                    name.equalsIgnoreCase(getCleanSimpleName(aClass)) ||
                    iRole.getRoles().getItem().getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
                Main.getInstance().debug("ClassName: "+aClass.getSimpleName()+", class: "+aClass+", roles: "+name+", cleanName: "+getCleanSimpleName(aClass)+", cleanedString: "+cleanString(Main.getInstance().getRoleManager().getRolesRegistery().get(aClass).getName()));
                if (iRole instanceof IUncompatibleRole) {
                    for (final IRoles<?> uncampatibleClass : ((IUncompatibleRole) iRole).getUncompatibleList()) {
                        if (GameState.getInstance().getAvailableRoles().containsKey(uncampatibleClass) && GameState.getInstance().getAvailableRoles().get(uncampatibleClass) > 0) {
                            Main.getInstance().sendMessageToHosts("§cImpossible d'ajouter le rôle§b "+name+"§c, l'un des rôles incompatible est déjà dans la composition de la partie !");
                            return;
                        }
                    }
                }
                GameState.getInstance().addInAvailableRoles(iRole.getRoles(), GameState.getInstance().getAvailableRoles().getOrDefault(iRole.getRoles(), 0)+1);
                break;
            }
        }
       /*Ancien système de rôle
        for (Roles roles : Roles.values()) {
            if (roles.name().equalsIgnoreCase(cleanString(name))) {
                GameState.getInstance().addInAvailableRoles(roles, Math.min(GameState.getInstance().getInLobbyPlayers().size(), GameState.getInstance().getAvailableRoles().get(roles)+1));
                break;
            }
        }*/
        GameState.getInstance().updateGameCanLaunch();
    }
    public static String getCleanSimpleName(Class<?> clazz) {
        String name = clazz.getSimpleName();

        // Supprime un suffixe du type V + chiffre (ex: V1, V2, V9)
        if (name.length() >= 2) {
            char v = name.charAt(name.length() - 2);
            char d = name.charAt(name.length() - 1);

            if (v == 'V' && Character.isDigit(d)) {
                return name.substring(0, name.length() - 2);
            }
        }

        return name;
    }
    public static String cleanString(String input) {
        if (input == null) return null;

        // Supprime § + caractère suivant
        String result = input.replaceAll("§.", "");

        // Supprime espaces et parenthèses
        result = result.replace(" ", "")
                .replace("(", "")
                .replace(")", "");

        return result;
    }

    public static void removeRoles(String name) {
        for (Roles roles : Roles.values()) {
            if (roles.getItem().getItemMeta().getDisplayName().contains(name)) {
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