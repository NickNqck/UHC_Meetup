package fr.nicknqck.managers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.assassin.ChooseAssassinEvent;
import fr.nicknqck.events.custom.assassin.PrepareAssassinEvent;
import fr.nicknqck.events.custom.assassin.ProcAssassinEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.slayers.NezukoV2;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssassinManagerV2 implements Listener {

    private final GameState gameState;
    private final List<GamePlayer> canBeAssassin;
    @Getter
    @Setter
    private int timeBeforeProc = 0;
    private final String prefix = "[Assassin-Manager] ";

    public AssassinManagerV2(GameState gameState) {
        this.gameState = gameState;
        this.canBeAssassin = new ArrayList<>();
        EventUtils.registerEvents(this);
        System.out.println(this.prefix+"has just started");
        updateTime();
    }

    @EventHandler
    private void RoleGiveEvent(@NonNull final RoleGiveEvent event) {
        final PrepareAssassinEvent prepareAssassinEvent = new PrepareAssassinEvent(this.gameState, new ArrayList<>(), false);
        Bukkit.getPluginManager().callEvent(prepareAssassinEvent);
        if (prepareAssassinEvent.isCancelled()) return;
        if (event.getRole() instanceof DemonsRoles && !(event.getRole() instanceof NezukoV2)) {
            this.canBeAssassin.add(event.getGamePlayer());
            System.out.println(this.prefix+"GamePlayer: "+event.getGamePlayer()+", has been added to canBeAssassin List !");
        }
        updateTime();
        if (event.isEndGive()) {
            if (this.canBeAssassin.isEmpty()) {
                EventUtils.unregisterEvents(this);
                System.out.println(this.prefix+"has been cancelled, cause: no players can be the Assassin");
                return;
            }
            final PrepareAssassinEvent endPrepareAssassinEvent = new PrepareAssassinEvent(this.gameState, this.canBeAssassin, true);
            Bukkit.getPluginManager().callEvent(endPrepareAssassinEvent);
        }
    }
    @EventHandler
    private void PrepareAssassinEvent(@NonNull final PrepareAssassinEvent event) {
        if (event.isCancelled())return;
        if (!event.isFinish())return;
        final List<GamePlayer> goodGamePlayers = event.getGamePlayers();
        if (goodGamePlayers.isEmpty())return;
        Collections.shuffle(goodGamePlayers, Main.RANDOM);
        final GamePlayer assassin = goodGamePlayers.get(0);
        updateTime();
        Bukkit.getPluginManager().callEvent(new ChooseAssassinEvent(assassin, this.gameState));
        new SetAssassinRunnable(this.gameState, this.timeBeforeProc, assassin).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
    @EventHandler
    private void ProcAssassinEvent(@NonNull final ProcAssassinEvent event) {
        event.getRole().setSuffixString(event.getRole().getSuffixString()+"§7 (§cAssassin§7)§r");
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onEndGame(@NonNull final EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    private void updateTime() {
        int oldTime = this.timeBeforeProc;
        this.timeBeforeProc = Main.getInstance().getGameConfig().getTimingAssassin();
        System.out.println(this.prefix+"updated time from "+oldTime+" to "+this.timeBeforeProc);
    }
    private static class SetAssassinRunnable extends BukkitRunnable {

        private final GameState gameState;
        private int timeBeforeProc;
        private final GamePlayer futureAssassin;

        private SetAssassinRunnable(GameState gameState, int timeBeforeProc, GamePlayer futureAssassin) {
            this.gameState = gameState;
            this.timeBeforeProc = timeBeforeProc;
            this.futureAssassin = futureAssassin;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.futureAssassin == null) {
                cancel();
                return;
            }
            if (!this.futureAssassin.isAlive()) {
                cancel();
                return;
            }
            if (this.timeBeforeProc <= 0) {
                final DemonsRoles role = (DemonsRoles) futureAssassin.getRole();
                role.setMaxHealth(role.getMaxHealth()+4.0);
                final Player owner = Bukkit.getPlayer(role.getPlayer());
                if (owner != null) {
                    owner.setMaxHealth(role.getMaxHealth());
                    owner.setHealth(Math.min(owner.getHealth()+4.0, owner.getMaxHealth()));
                    owner.resetTitle();
                    owner.sendTitle("§c§lVous êtes l'§4§lAssassin", "§cVous obtenez donc 2❤ supplémentaires !");
                }
                this.futureAssassin.sendMessage("Vous êtes l'assassin vous possédez désormais§c 2❤ supplémentaire de manière permanente, faite attention au rôle de§a Tanjiro§f qui obtiendra un bonus s'il vous tue.");
                Bukkit.getPluginManager().callEvent(new ProcAssassinEvent(this.gameState, this.futureAssassin, role));
                cancel();
                return;
            }
            this.timeBeforeProc--;
        }
    }
}