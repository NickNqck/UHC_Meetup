package fr.nicknqck.managers;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.solo.Danzo;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HokageManager implements Listener {

    private final GameState gameState;
    @Getter
    @Setter
    private boolean enable = true;
    @Getter
    private GamePlayer hokage;

    public HokageManager(GameState gameState) {
        this.gameState = gameState;
        EventUtils.registerEvents(this);
    }

    @EventHandler
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (isEnable()) {
            if (!event.isEndGive())return;
            new HokageRunnable(this.gameState, this, event.getGameState().getTimeProcHokage())
                    .runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerKill(@NonNull final UHCPlayerKillEvent event) {
        if (this.hokage == null)return;
        if (event.isCancel())return;
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getRole() == null)return;
        if (!this.hokage.getUuid().equals(event.getVictim().getUniqueId()))return;
        if (event.getGamePlayerKiller().getRole() instanceof Danzo) {
            ((Danzo) event.getGamePlayerKiller().getRole()).setKillHokage(true);
            event.getPlayerKiller().sendMessage("§7Lors de la prochaine élection de l'§cHokage§7 vous serez obligatoirement élu");
        }
        this.hokage.getRole().addBonusResi(-10);
        this.hokage.getRole().addBonusforce(-10);
        this.hokage.getRole().removePower(NSBoost.class);
        this.hokage.getRole().removePower(NSInfo.class);
        this.hokage = null;
        new HokageRunnable(event.getGameState(), this, event.getGameState().getTimeProcHokage())
                .runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
    private static class HokageRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final HokageManager hokageManager;
        private int timeRemaining;

        private HokageRunnable(GameState gameState, HokageManager hokageManager, int timeRemaining) {
            this.gameState = gameState;
            this.hokageManager = hokageManager;
            this.timeRemaining = timeRemaining;
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.timeRemaining <= 0) {
                GameListener.SendToEveryone(AllDesc.bar);
                final GamePlayer gamePlayer = searchHokage();
                if (gamePlayer != null) {
                    GameListener.SendToEveryone("§bLe conseil viens d'élire un nouveau§e Hokage§b, le village est");
                    GameListener.SendToEveryoneWithHoverMessage("§b maintenant sous le commandement de ",
                            "§e"+gamePlayer.getPlayerName(),
                            "§7La personne désigné comme étant l'§cHokage§7 obtient§c 10%§7 de§c Force§7 et de§9 Résistance", "§e.");
                    this.hokageManager.hokage = gamePlayer;
                    for (final GamePlayer danzo : this.gameState.getGamePlayer().values()) {
                        if (danzo.getRole() == null)continue;
                        if (danzo.getRole() instanceof Danzo) {
                            danzo.sendMessage("§7Voici le rôle de l'Hokage: §a"+gamePlayer.getRole().getName()+"§7 (§cAttention vous êtes le seul joueur à avoir cette information§7)");
                        }
                    }
                    gamePlayer.getRole().addBonusforce(10.0);
                    gamePlayer.getRole().addBonusResi(10.0);
                    gamePlayer.getRole().addPower(new NSBoost(gamePlayer.getRole()));
                    gamePlayer.getRole().addPower(new NSInfo(gamePlayer.getRole()));
                    GameListener.SendToEveryone(AllDesc.bar);
                    gamePlayer.sendMessage("§7Vous êtes devenue le nouvel§e Hokake§7, vous avez maintenant accès à la commande§e /ns boost <joueur>§7 qui donnera au joueurs visé§c +5§7 de§c Force§7 et de§9 Résistance",
                            "§7Vous avez maintenant également accès à la commande§6 /ns infos§7, ces deux commandes sont utilisable §c1x/partie");
                } else {
                    GameListener.SendToEveryone("§bLe conseil n'a trouver personne pour devenir le nouveau§e Hokage§b, le village est attristé par cette nouvelle.");
                    GameListener.SendToEveryone(AllDesc.bar);
                }
                cancel();
                return;
            }
            this.timeRemaining--;
        }
        private GamePlayer searchHokage() {
            GamePlayer toReturn = null;
            Danzo danzo = null;
            for (final GamePlayer gamePlayer : this.gameState.getGamePlayer().values()) {
                if (!gamePlayer.isAlive())continue;
                if (gamePlayer.getRole() == null)continue;
                if (!gamePlayer.isOnline())continue;
                if (gamePlayer.getRole() instanceof Danzo) {
                    if (((Danzo) gamePlayer.getRole()).isKillHokage()) {
                        danzo = (Danzo) gamePlayer.getRole();
                        break;
                    }
                }
            }
            if (danzo != null) {
                return danzo.getGamePlayer();
            }
            int essai = 0;
            while (essai < 3 && toReturn == null) {
                essai++;
                final List<GamePlayer> gamePlayerList = new ArrayList<>();
                final List<GamePlayer> toDrop = new ArrayList<>(this.gameState.getGamePlayer().values());
                Collections.shuffle(toDrop, Main.RANDOM);
                for (final GamePlayer gamePlayer : toDrop) {
                    if (!gamePlayer.isAlive())continue;
                    if (gamePlayer.getRole() == null)continue;
                    if (!gamePlayer.isOnline())continue;
                    if (gamePlayer.getRole() instanceof NSRoles) {
                        if (((NSRoles) gamePlayer.getRole()).isCanBeHokage()) {
                            gamePlayerList.add(gamePlayer);
                        }
                    }
                }
                if (!gamePlayerList.isEmpty()) {
                    Collections.shuffle(gamePlayerList, Main.RANDOM);
                    toReturn = gamePlayerList.get(0);
                }
            }
            return toReturn;
        }
    }
    private static class NSBoost extends CommandPower {

        public NSBoost(@NonNull RoleBase role) {
            super("/ns boost <joueur>", "boost", null, role, CommandType.NS);
            setShowInDesc(false);
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        final GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                        double boost = 5.0;
                        gamePlayer.getRole().addBonusforce(boost);
                        gamePlayer.getRole().addBonusResi(boost);
                        target.sendMessage("§7Vous avez reçus le boost de l'§eHokage");
                        player.sendMessage("§7Vous avez offert votre boost à§c "+target.getDisplayName());
                        return true;
                    } else {
                        player.sendMessage("§7La personne visé(e) n'a pas de rôle, impossible de la boost");
                        return false;
                    }
                } else {
                    player.sendMessage("§c"+args[1]+"§b n'est pas connecté(e)");
                    return false;
                }
            } else {
                player.sendMessage("§cLa commande est§e /ns boost <joueur>");
                return false;
            }
        }
    }
    private static class NSInfo extends CommandPower {

        public NSInfo(@NonNull RoleBase role) {
            super("/ns infos", "infos", null, role, CommandType.NS);
            setShowInDesc(false);
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final List<GamePlayer> gamePlayerList = new ArrayList<>();
            getRole().getGameState().getGamePlayer().values().stream()
                    .filter(GamePlayer::isAlive)
                    .filter(gamePlayer -> gamePlayer.getRole() != null)
                    .filter(gamePlayer -> !player.getUniqueId().equals(gamePlayer.getUuid()))
                    .forEach(gamePlayerList::add);
            if (gamePlayerList.size() < 4) {
                player.sendMessage("§cImpossible d'obtenir des informations, pas assez de personne sont en vie");
                return false;
            }
            GamePlayer shinobi = null;
            GamePlayer mechant = null;
            GamePlayer random = null;
            GamePlayer random2 = null;
            int essai = 0;
            while (shinobi == null && mechant == null && random == null && random2 == null && essai < 10) {
                Collections.shuffle(gamePlayerList, Main.RANDOM);
                for (final GamePlayer gamePlayer : gamePlayerList) {
                    if (gamePlayer.getRole().getOriginTeam().equals(TeamList.Shinobi) && shinobi == null) {
                        shinobi = gamePlayer;
                        continue;
                    }
                    if (!gamePlayer.getRole().getOriginTeam().equals(TeamList.Shinobi) && mechant == null) {
                        mechant = gamePlayer;
                        continue;
                    }
                    if (random2 == null) {
                        random2 = gamePlayer;
                        continue;
                    }
                    if (random == null) {
                        random = gamePlayer;
                    }
                }
                essai++;
            }
            if (shinobi != null && mechant != null && random != null && random2 != null) {
                final List<GamePlayer> gamePlayers = new ArrayList<>();
                gamePlayers.add(shinobi);
                gamePlayers.add(mechant);
                gamePlayers.add(random2);
                gamePlayers.add(random);
                Collections.shuffle(gamePlayers, Main.RANDOM);
                player.sendMessage(new String[]{
                        "§7Voici les informations que vos informateurs on pu trouver: ",
                        "",
                        "§8 -§c "+gamePlayers.get(0).getPlayerName(),
                        "§8 -§c "+gamePlayers.get(1).getPlayerName(),
                        "§8 -§c "+gamePlayers.get(2).getPlayerName(),
                        "§8 -§c "+gamePlayers.get(3).getPlayerName(),
                        "",
                        "§7Dans la liste de joueurs ci-dessus il y a (pas dans cette ordre là) un§a Shinobi§7, une personne n'étant pas un§a Shinobi§7 ainsi que§c 2 joueurs§7 ayant un camp§c aléatoire§7."
                });
            } else {
                player.sendMessage("§cVsos informateurs n'ont rien pus trouver de concluant sur les joueurs encore en vie");
            }
            return true;
        }
    }
}