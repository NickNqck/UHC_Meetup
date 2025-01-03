package fr.nicknqck.utils.packets;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

import static fr.nicknqck.utils.packets.NMSPacket.getNMSClass;
import static fr.nicknqck.utils.packets.NMSPacket.sendPacket;

public class TabTitleManager {

    private final Player player;
    private final String header;
    private final String footer;

    public TabTitleManager(final Player player, @Nullable String header, @Nullable String footer) {
        this.player = player;
        if (header == null) {
            header = "";
        }
        header = ChatColor.translateAlternateColorCodes('&', header);
        this.header = header;
        if (footer == null) {
            footer = "";
        }
        footer = ChatColor.translateAlternateColorCodes('&', footer);
        this.footer = footer;
    }
    public static void sendTabTitle(Player player, String header, String footer) {
        if (header == null)
            header = "";
        header = ChatColor.translateAlternateColorCodes('&', header);
        if (footer == null)
            footer = "";
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        try {
            Object tabHeader = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + header + "\"}");
            Object tabFooter = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + footer + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutPlayerListHeaderFooter")).getConstructor();
            Object packet = titleConstructor.newInstance();
            Field aField = packet.getClass().getDeclaredField("a");
            aField.setAccessible(true);
            aField.set(packet, tabHeader);
            Field bField = packet.getClass().getDeclaredField("b");
            bField.setAccessible(true);
            bField.set(packet, tabFooter);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}