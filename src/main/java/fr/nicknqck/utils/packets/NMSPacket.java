package fr.nicknqck.utils.packets;

import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class NMSPacket {
	//le reste dans Main
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @SuppressWarnings("all")
    public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        try {
            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, Integer.valueOf(fadeOut));
                sendPacket(player, titlePacket);
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                sendPacket(player, titlePacket);
            }
            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    public static void clearTitle(Player player) {
        sendTitle(player, 0, 0, 0, "", "");
    }
    
    public static void sendActionBar(Player player, String message) {
        try{
            Object messageICB = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + message + "\"}");
            Constructor<?> actionBarConstructor = getNMSClass("PacketPlayOutChat").getConstructor();
            Object packet = actionBarConstructor.newInstance();

            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, messageICB);

            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, (byte) 2);

            sendPacket(player, packet);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void sendActionBarPregen(Player player, String message) {
        try{
            Object messageICB = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + message + "\"}");
            Constructor<?> actionBarConstructor = getNMSClass("PacketPlayOutChat").getConstructor();
            Object packet = actionBarConstructor.newInstance();

            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, messageICB);

            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, (byte) 2);

            sendPacket(player, packet);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void sendArmorStand(Player target, Player viewer) {
    	try {
    		if (!target.hasPotionEffect(PotionEffectType.INVISIBILITY) && target.isOnline() && viewer.isOnline()) {
    			double y = target.getLocation().getY();
        		double useY = y+0.5;
        		Location loc = new Location(target.getWorld(), target.getLocation().getX(), useY, target.getLocation().getZ());
        		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        		EntityArmorStand stand = new EntityArmorStand(world);
        		double x = (loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
    			double z = (loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
    			loc.setPitch(0);
    			DecimalFormat df = new DecimalFormat("0");
        		stand.setCustomName(df.format(target.getHealth())+ AllDesc.coeur);
        		stand.setCustomNameVisible(true);
        		stand.setLocation(x, useY, z, target.getEyeLocation().getPitch(), target.getEyeLocation().getYaw());
        		stand.setInvisible(true);
        		stand.setSneaking(target.isSneaking());
        		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
        		((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
        		
        		new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (viewer.isOnline()) {
                            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(stand.getId());
                            ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(destroyPacket);
                        }
                    }
                }.runTaskLater(Main.getInstance(), 1);
    		}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
}