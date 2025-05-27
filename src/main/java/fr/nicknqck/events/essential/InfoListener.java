package fr.nicknqck.events.essential;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.roles.TeamChangeEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.player.PlayerInfo;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfoListener implements Listener {

    private final Map<UUID, Long> lastTeamChange = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(uuid);
        info.incrementJoin();
        Main.getInstance().getInfoManager().save(uuid);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDisconnect(final PlayerQuitEvent event) {
        final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getPlayer().getUniqueId());
        info.incrementQuit();
        Main.getInstance().getInfoManager().save(event.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onShoot(final EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getEntity().getUniqueId());
            info.incrementArrows();
            Main.getInstance().getInfoManager().save(event.getEntity().getUniqueId());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityKill(final EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && !(event.getEntity() instanceof Player)) {
            final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(event.getEntity().getKiller().getUniqueId());
            info.incrementKills();
            Main.getInstance().getInfoManager().save(event.getEntity().getKiller().getUniqueId());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRoleGive(final RoleGiveEvent event) {
        if (event.isEndGive()) {
            for (final GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
                if (gamePlayer.getRole() == null)continue;
                final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(gamePlayer.getUuid());
                info.incrementRolePlayed(gamePlayer.getRole().getName());
                info.addTeamPlayed(gamePlayer.getRole().getOriginTeam());
                Main.getInstance().getInfoManager().save(gamePlayer.getUuid());
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTeamChange(final TeamChangeEvent event) {
        if (event.isCancelled())return;
        if (event.getRole() == null)return;
        if (event.getRole().getPlayer() == null)return;
        if (event.getOldTeam() == null)return;
        final UUID playerId = event.getRole().getPlayer();
        long currentTime = System.currentTimeMillis();
        if (this.lastTeamChange.containsKey(playerId)) {
            long lastTime = this.lastTeamChange.get(playerId);
            if (currentTime - lastTime < 1000) {
                return;
            }
        }
        this.lastTeamChange.put(playerId, currentTime);
        final PlayerInfo info = Main.getInstance().getInfoManager().getPlayerInfo(playerId);
        info.addAmountTeamChange();
        info.addTeamPlayed(event.getNewTeam());
        Main.getInstance().getInfoManager().save(event.getRole().getPlayer());
    }
}