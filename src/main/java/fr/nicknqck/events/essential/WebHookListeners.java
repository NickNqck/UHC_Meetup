package fr.nicknqck.events.essential;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.GameStartEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.utils.discord.WebhookBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class WebHookListeners implements Listener {

    private final GameState gameState;

    public WebHookListeners(GameState gameState) {
        this.gameState = gameState;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Main.getInstance().updateWebHookConfig();
        if (goReturn() || !isOnJoin()) {
            return;
        }
        if (isHookEnable()) {
            if (!Objects.equals(getUrl(), "")) {
                String name = getOriginName();
                if (name.equalsIgnoreCase("<joueur>")) {
                    name = event.getPlayer().getName();
                }
                sendWebHook(name, event.getPlayer().getName()+" a rejoint la partie");
            }
        }
    }
    @EventHandler
    private void onDisconnect(PlayerQuitEvent event) {
        Main.getInstance().updateWebHookConfig();
        if (goReturn() || !isOnDisc()) {
            return;
        }
        if (isHookEnable()) {
            if (!Objects.equals(getUrl(), "")) {
                String name = getOriginName();
                if (name.equalsIgnoreCase("<joueur>")) {
                    name = event.getPlayer().getName();
                }
                sendWebHook(name, event.getPlayer().getName()+" a quitter la partie");
            }
        }
    }
    @EventHandler
    private void onUHCBattle(UHCPlayerBattleEvent event) {
        if (goReturn() || !isBattle() || !event.isAsynchronous() || !event.isPatch()) {
            return;
        }
        if (isHookEnable()) {
            if (!Objects.equals(getUrl(), "")) {
                String name = getOriginName();
                if (name.equalsIgnoreCase("<joueur>")) {
                    name = event.getVictim().getPlayerName();
                }
                sendWebHook(name, event.getVictim().getPlayerName()+" ("+event.getVictim().getRole().getName()+") se fait frapper par "+event.getDamager().getPlayerName()+" ("+event.getDamager().getRole().getName()+"), les dégats sont de "+event.getDamage());
            }
        }
    }
    @EventHandler
    private void onStartGame(GameStartEvent event) {
        if (goReturn() || !isStart()) {
            return;
        }
        if (isHookEnable()) {
            if (!Objects.equals(getUrl(), "")) {
                String name = getDefaulltName();
                if (name.isEmpty()) {
                    name = "Nom par défaut";
                }
                if (name.equalsIgnoreCase("Nom par défaut")){
                    name = "La game se lance";
                }
                String playerNames = "";
                for (UUID uuid : event.getInGamePlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)continue;
                    playerNames += player.getName()+", ";
                }
                String igRoles = "";
                for (Roles roles : event.getIgRoles()) {
                    igRoles += roles.getItem().getItemMeta().getDisplayName()+", ";
                }
                sendWebHook(name,
                        "Voici la liste des joueurs en jeu:",
                        "",
                        playerNames,
                        "",
                        "Et voici les roles qui sont la composition de la partie",
                        "",
                        igRoles,
                        "",
                        "Have Fun !"
                        );
            }
        }
    }
    private void sendWebHook(String HookName, String content) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                WebhookBuilder webhookBuilder = WebhookBuilder.newBuilder(getUrl());
                webhookBuilder.username(HookName);
                webhookBuilder.content(content);
                webhookBuilder.buildAndExecute();
            } catch (IOException io) {
                io.fillInStackTrace();
                Main.getInstance().getLogger().info("Erreur, "+io);
            }
        });
    }
    private void sendWebHook(String name, String... contents) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
           try {
               WebhookBuilder webhookBuilder = WebhookBuilder.newBuilder(getUrl());
               webhookBuilder.username(name);
               webhookBuilder.content(contents);
               webhookBuilder.buildAndExecute();
           } catch (IOException e) {
               Main.getInstance().getLogger().info("Erreur, "+e);
               throw new RuntimeException(e);
           }
        });
    }
    private boolean goReturn() {
        if (gameState.getServerState().equals(GameState.ServerStates.InLobby)) {
            return !isLobbyEnable();
        } else {
            return !isInGameEnable();
        }
    }
    private boolean isHookEnable() {
        return Main.getInstance().getWebhookConfig().getBoolean("hook", false);
    }
    private String getUrl() {
        return Main.getInstance().getWebhookConfig().getString("url", "");
    }
    private String getOriginName() {
        return Main.getInstance().getWebhookConfig().getString("name", "Nom par defaut");
    }
    private boolean isLobbyEnable() {
        return Main.getInstance().getWebhookConfig().getBoolean("lobby", false);
    }
    private boolean isInGameEnable() {
        return Main.getInstance().getWebhookConfig().getBoolean("game", false);
    }
    private boolean isOnJoin() {
        return Main.getInstance().getWebhookConfig().getBoolean("join", false);
    }
    private boolean isOnDisc() {
        return Main.getInstance().getWebhookConfig().getBoolean("disc", false);
    }
    private boolean isBattle() {
        return Main.getInstance().getWebhookConfig().getBoolean("battleInfo", false);
    }
    private boolean isStart() {
        return Main.getInstance().getWebhookConfig().getBoolean("start", false);
    }
    private String getDefaulltName() {
        return Main.getInstance().getWebhookConfig().getString("defaultname", "");
    }
}