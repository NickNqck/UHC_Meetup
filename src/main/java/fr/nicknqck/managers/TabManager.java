package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TPS;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static fr.nicknqck.utils.packets.NMSPacket.getNMSClass;
import static fr.nicknqck.utils.packets.NMSPacket.sendPacket;

@Getter
public class TabManager implements Listener {

    private final Map<UUID, Map<UUID, String>> roleTabMap;
    private final Map<UUID, Map<UUID, String>> teamTabMap;
    private final Map<UUID, TabScheduler> schedulerMap;

    public TabManager() {
        EventUtils.registerEvents(this);
        this.roleTabMap = new HashMap<>();
        this.teamTabMap = new HashMap<>();
        this.schedulerMap = new HashMap<>();
        for (@NonNull final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.addToMap(onlinePlayer);
        }
    }
    @EventHandler
    private void onJoin(@NonNull final PlayerJoinEvent event) {
        if (!this.schedulerMap.containsKey(event.getPlayer().getUniqueId())) {
            this.addToMap(event.getPlayer());
        }
    }
    @EventHandler
    private void onEndGame(@NonNull final GameEndEvent event) {
        for (@NonNull final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (this.schedulerMap.containsKey(onlinePlayer.getUniqueId())) {
                for (@NonNull final Player target : Bukkit.getOnlinePlayers()) {
                    this.schedulerMap.get(onlinePlayer.getUniqueId()).setTabName(onlinePlayer, target.getName());
                    this.roleTabMap.get(onlinePlayer.getUniqueId()).clear();
                    this.teamTabMap.get(onlinePlayer.getUniqueId()).clear();
                }
            }
        }
    }
    private void addToMap(@NonNull final Player onlinePlayer) {
        final Map<UUID, String> map = new HashMap<>();
        map.put(onlinePlayer.getUniqueId(), onlinePlayer.getName());
        this.roleTabMap.put(onlinePlayer.getUniqueId(), map);
        this.teamTabMap.put(onlinePlayer.getUniqueId(), map);
        this.schedulerMap.put(onlinePlayer.getUniqueId(), new TabScheduler(this, onlinePlayer.getUniqueId()));
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
            ex.fillInStackTrace();
        }
    }
    private static class TabScheduler extends BukkitRunnable {

        private final TabManager tabManager;
        private final UUID uuid;

        private TabScheduler(TabManager tabManager, UUID uuid) {
            this.tabManager = tabManager;
            this.uuid = uuid;
            runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
        }

        @Override
        public void run() {
            final Player player = Bukkit.getPlayer(this.uuid);
            if (player == null)return;
            final GameState gameState = GameState.getInstance();
            if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                sendTabTitle(
                        player,
                        "\n§6UHC-Meetup\n" ,
                        "\n§bTemp de jeu: "+ StringUtils.secondsTowardsBeautiful(gameState.getInGameTime())+"     TPS: "+ new DecimalFormat("0").format(TPS.getAverageTPS(1)) +
                                "\n\n§bDev: NickNqck");
            } else {
                sendTabTitle(
                        player,
                        "\n§6UHC-Meetup\n",
                        "\n§bDev: NickNqck"
                );
            }
            boolean change = false;
            {
                @NonNull final Map<UUID, String> map = this.tabManager.getRoleTabMap().get(this.uuid);
                if (map.isEmpty())return;
                for (@NonNull final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    if (map.containsKey(onlinePlayers.getUniqueId())) {
                        if (map.get(onlinePlayers.getUniqueId()).equalsIgnoreCase(player.getName()))continue;
                        setTabName(player, map.get(onlinePlayers.getUniqueId())+" "+onlinePlayers.getDisplayName());
                        change = true;
                    }
                }
            }
            {
                @NonNull final Map<UUID, String> map = this.tabManager.getTeamTabMap().get(this.uuid);
                if (map.isEmpty() || change)return;
                for (@NonNull final Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    if (map.containsKey(onlinePlayers.getUniqueId())) {
                        if (map.get(onlinePlayers.getUniqueId()).equalsIgnoreCase(player.getName()))continue;
                        setTabName(player, map.get(onlinePlayers.getUniqueId())+ onlinePlayers.getDisplayName());
                    }
                }
            }
        }
        public void setTabName(@NonNull final Player recever, @NonNull final String toDisplay) {
            EntityPlayer entityPlayer = ((CraftPlayer) recever).getHandle();
            entityPlayer.listName = new net.minecraft.server.v1_8_R3.ChatComponentText(toDisplay);

            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ((CraftPlayer) recever).getHandle());
            ((CraftPlayer) recever).getHandle().playerConnection.sendPacket(packet);
        }
    }
}