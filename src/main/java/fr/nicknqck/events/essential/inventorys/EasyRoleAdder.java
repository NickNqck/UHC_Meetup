package fr.nicknqck.events.essential.inventorys;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.RoleBase;

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
        for (Class<? extends RoleBase> aClass : Main.getInstance().getRoleManager().getRolesRegistery().keySet()) {
            if (name.equalsIgnoreCase(cleanString(Main.getInstance().getRoleManager().getRolesRegistery().get(aClass).getName())) || name.equalsIgnoreCase(getCleanSimpleName(aClass))) {
                Main.getInstance().getLogger().info("ClassName: "+aClass.getSimpleName()+", class: "+aClass+", roles: "+name+", cleanName: "+getCleanSimpleName(aClass)+", cleanedString: "+cleanString(Main.getInstance().getRoleManager().getRolesRegistery().get(aClass).getName()));
            }
        }
        //Ancien système de rôle
        for (Roles roles : Roles.values()) {
            if (roles.name().equalsIgnoreCase(name)) {
                GameState.getInstance().addInAvailableRoles(roles, Math.min(GameState.getInstance().getInLobbyPlayers().size(), GameState.getInstance().getAvailableRoles().get(roles)+1));
                GameState.getInstance().updateGameCanLaunch();
                break;
            }
        }
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