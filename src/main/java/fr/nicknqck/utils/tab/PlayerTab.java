package fr.nicknqck.utils.tab;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Vue personnalisée du tab list pour un joueur donné.
 * Chaque joueur possède son propre {@link Scoreboard} pour les prefix/suffix/color,
 * et une Map d'entrées permettant de persister les joueurs déconnectés.
 */
public class PlayerTab {

    @Getter private final UUID viewerUUID;
    private final Scoreboard scoreboard;

    /** Entrées du tab, indexées par UUID cible */
    @Getter private final Map<UUID, TabEntry> entries = new LinkedHashMap<>();

    /** Compteur pour générer des noms d'équipe uniques (max 16 chars) */
    private final Map<UUID, String> teamNames = new HashMap<>();
    private int teamCounter = 0;

    public PlayerTab(UUID viewerUUID) {
        this.viewerUUID = viewerUUID;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    // ── Application du scoreboard ─────────────────────────────────────────────

    /**
     * Applique le scoreboard personnalisé au viewer.
     * À appeler après chaque modification.
     */
    public void apply() {
        final Player viewer = Bukkit.getPlayer(viewerUUID);
        if (viewer == null) return;
        viewer.setScoreboard(scoreboard);
    }

    // ── Gestion des entrées ───────────────────────────────────────────────────

    /**
     * Ajoute ou met à jour une entrée dans ce tab.
     * Crée/met à jour l'équipe scoreboard associée.
     */
    public void upsertEntry(TabEntry entry) {
        entries.put(entry.getUuid(), entry);
        refreshTeam(entry);
    }

    /**
     * Supprime l'entrée du tab et son équipe scoreboard.
     * Si {@code keepWhenOffline} est activé, l'entrée reste mais est marquée offline.
     */
    public void markOffline(UUID targetUUID) {
        final TabEntry entry = entries.get(targetUUID);
        if (entry == null) return;

        if (entry.isKeepWhenOffline()) {
            entry.setOnline(false);
            refreshTeam(entry);
            // Re-injecter l'entrée fantôme dans le tab via NMS
            sendKeepOfflinePacket(entry);
        } else {
            removeEntry(targetUUID);
        }
    }

    /**
     * Supprime totalement l'entrée et l'équipe du scoreboard.
     */
    public void removeEntry(UUID targetUUID) {
        final TabEntry entry = entries.remove(targetUUID);
        if (entry == null) return;

        final String teamName = teamNames.remove(targetUUID);
        if (teamName != null) {
            final Team team = scoreboard.getTeam(teamName);
            if (team != null) team.unregister();
        }

        // Retirer du tab via NMS si offline
        if (!entry.isOnline()) {
            sendRemovePacket(entry);
        }
    }

    // ── Raccourcis de modification ────────────────────────────────────────────

    public void setPrefix(UUID targetUUID, String prefix) {
        final TabEntry entry = entries.get(targetUUID);
        if (entry == null) return;
        entry.setPrefix(prefix);
        refreshTeam(entry);
        apply();
    }

    public void setSuffix(UUID targetUUID, String suffix) {
        final TabEntry entry = entries.get(targetUUID);
        if (entry == null) return;
        entry.setSuffix(suffix);
        refreshTeam(entry);
        apply();
    }

    public void setColor(UUID targetUUID, ChatColor color) {
        final TabEntry entry = entries.get(targetUUID);
        if (entry == null) return;
        entry.setColor(color);
        refreshTeam(entry);
        apply();
    }

    public void setKeepWhenOffline(UUID targetUUID, boolean keep) {
        final TabEntry entry = entries.get(targetUUID);
        if (entry == null) return;
        entry.setKeepWhenOffline(keep);
    }

    // ── Logique interne ───────────────────────────────────────────────────────

    /**
     * Crée ou met à jour l'équipe scoreboard pour une entrée.
     * Le préfixe est limité à 16 chars (contrainte Bukkit 1.8.8).
     */
    private void refreshTeam(TabEntry entry) {
        final String teamName = teamNames.computeIfAbsent(
                entry.getUuid(), k -> "tab_" + (teamCounter++)
        );

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        final String prefix = entry.getColor() + truncate(entry.getPrefix(), 14);
        final String suffix = truncate(entry.getSuffix(), 16);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        if (!team.hasEntry(entry.getPlayerName())) {
            team.addEntry(entry.getPlayerName());
        }
    }

    /**
     * Envoie un packet NMS pour maintenir un joueur déconnecté dans le tab.
     * Utilise le {@link com.mojang.authlib.GameProfile} stocké dans l'entrée.
     */
    private void sendKeepOfflinePacket(TabEntry entry) {
        final Player viewer = Bukkit.getPlayer(viewerUUID);
        if (viewer == null || entry.getGameProfile() == null) return;

        // ADD_PLAYER avec le GameProfile stocké = entrée fantôme dans le tab
        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER
        );

        try {
            // On contourne la restriction d'accès au champ "b"
            Field bField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            bField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<PacketPlayOutPlayerInfo.PlayerInfoData> list =
                    (List<PacketPlayOutPlayerInfo.PlayerInfoData>) bField.get(packet);

            list.add(packet.new PlayerInfoData(
                    entry.getGameProfile(),
                    1, // latency fictive
                    WorldSettings.EnumGamemode.SURVIVAL,
                    null // displayName null = utilise le GameProfile name
            ));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Envoie un packet NMS pour retirer une entrée fantôme du tab.
     */
    private void sendRemovePacket(TabEntry entry) {
        final Player viewer = Bukkit.getPlayer(viewerUUID);
        if (viewer == null || entry.getGameProfile() == null) return;

        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER
        );

        try {
            // 1. On cible spécifiquement le champ privé "b" (la liste des données)
            Field bField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");

            // 2. On fait sauter la protection 'private'
            bField.setAccessible(true);

            // 3. On récupère l'instance de la liste contenue dans le paquet
            @SuppressWarnings("unchecked")
            List<PacketPlayOutPlayerInfo.PlayerInfoData> list =
                    (List<PacketPlayOutPlayerInfo.PlayerInfoData>) bField.get(packet);

            // 4. On ajoute proprement ton entrée
            list.add(packet.new PlayerInfoData(
                    entry.getGameProfile(),
                    1,
                    WorldSettings.EnumGamemode.SURVIVAL,
                    null
            ));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // La réflexion peut lever ces exceptions si le champ n'existe pas ou reste inaccessible
            e.printStackTrace();
        }

        // 5. On envoie le paquet final
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }
}