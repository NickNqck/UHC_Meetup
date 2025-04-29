package fr.nicknqck.utils.packets;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private void onEndGame(@NonNull final EndGameEvent event) {
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
            boolean change = false;
            {
                @NonNull final Map<UUID, String> map = this.tabManager.getRoleTabMap().get(this.uuid);
                if (map.isEmpty())return;
                final Player player = Bukkit.getPlayer(this.uuid);
                if (player == null)return;
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
                final Player player = Bukkit.getPlayer(this.uuid);
                if (player == null)return;
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