package fr.nicknqck.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import fr.nicknqck.scoreboard.Reflection;

public class TPS {
	private TPS() {}

    public static double getTPS() {
        return getAverageTPS(1);
    }

    public static double getAverageTPS(int time) {
        double[] recentTps;
        if (canGetWithPaper()) {
            recentTps = getPaperRecentTps();
        } else {
            recentTps = getNMSRecentTps();
        }
        double raw;
        double tps;
        switch (time) {
            case 1 :
                raw = recentTps[0];
                tps = Math.min(Math.round(raw * 100.0) / 100.0, 20.0);
                return tps;
            case 5 :
                raw = recentTps[1];
                tps = Math.min(Math.round(raw * 100.0) / 100.0, 20.0);
                return tps;
            case 15 :
                raw = recentTps[2];
                tps = Math.min(Math.round(raw * 100.0) / 100.0, 20.0);
                return tps;
            default :
                throw new IllegalArgumentException("Unsupported tps measure time " + time);
        }
    }

    private static final Class<?> spigotServerClass = Reflection.getClass("org.bukkit.Server$Spigot");
    private static final Method getSpigotMethod = Reflection.makeMethod(Bukkit.class, "spigot");
    private static final Method getTPSMethod = spigotServerClass != null ? Reflection.makeMethod(spigotServerClass, "getTPS") : null;
    private static double[] getPaperRecentTps() {
        if (!canGetWithPaper()) throw new UnsupportedOperationException("Can't get TPS from Paper");
        Object server = Reflection.callMethod(getServerMethod, null); // Call static MinecraftServer.getServer()
        double[] recent = Reflection.getField(recentTpsField, server);
        return recent;
    }
    
    private static boolean canGetWithPaper() {
        return getSpigotMethod != null && getTPSMethod != null;
    }
    
    private static final Class<?> minecraftServerClass = Reflection.getNMSClass("MinecraftServer");
    private static final Method getServerMethod = minecraftServerClass != null ? Reflection.makeMethod(minecraftServerClass, "getServer") : null;
    private static final Field recentTpsField = minecraftServerClass != null ? Reflection.makeField(minecraftServerClass, "recentTps") : null;
    private static double[] getNMSRecentTps() {
        if (getServerMethod == null || recentTpsField == null) throw new UnsupportedOperationException("Can't get TPS from NMS");
        Object server = Reflection.callMethod(getServerMethod, null); // Call static MinecraftServer.getServer()
        double[] recent = Reflection.getField(recentTpsField, server);
        return recent;
    }
}
