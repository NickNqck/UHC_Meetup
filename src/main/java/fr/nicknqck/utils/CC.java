package fr.nicknqck.utils;

import org.bukkit.ChatColor;

public class CC {

    public static String translate(String var1) {
        return ChatColor.translateAlternateColorCodes('&', var1);
    }

    public static String prefix(String var1) {
        return "§7▎ §f" + ChatColor.translateAlternateColorCodes('&', var1);
    }

}
