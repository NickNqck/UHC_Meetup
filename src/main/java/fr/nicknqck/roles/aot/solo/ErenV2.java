package fr.nicknqck.roles.aot.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.titanrouge.Sieg;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.titans.impl.*;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ErenV2 extends AotRoles {

    public ErenV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Eren§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Eren;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Main.getInstance().getTitanManager().addTitan(getPlayer(), new AssaillantV2(getGamePlayer()));
        addPower(new KillBonusPower(this));
        addPower(new FastTimeTravelPower(this));
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class KillBonusPower extends Power implements Listener {

        public KillBonusPower(@NonNull RoleBase role) {
            super("Obteneur d'effet pour Eren", null, role);
            setShowInDesc(false);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onKill(@NonNull final UHCPlayerKillEvent event) {
            if (!event.getKiller().getUniqueId().equals(this.getRole().getPlayer()))return;
            if (event.isCancel())return;
            if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
            @NonNull final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
            if (!(role instanceof AotRoles))return;
            if (role instanceof Sieg) {
                this.getRole().addKnowedPlayersFromTeam(TeamList.Titan);
                event.getKiller().sendMessage("§7En tuant§c Sieg§7, vous avez obtenu la liste des§c titans rouges§7.");
                return;
            }
            if (!Main.getInstance().getTitanManager().hasTitan(role.getPlayer()))return;
            @NonNull final TitanBase titan = Main.getInstance().getTitanManager().getTitan(role.getPlayer());
            if (titan instanceof CharetteV2) {
                this.getRole().addSpeedAtInt(event.getPlayerKiller(), 10);
                event.getKiller().sendMessage("§7En tuant la personne ayant le§c Titan Charette§7 vous avez obtenue§c 10%§7 de§c vitesse supplémentaire");
            }
            if (titan instanceof MachoireV2) {
                this.getRole().addBonusforce(5.0);
                event.getKiller().sendMessage("§7En tuant la personne ayant le§c Titan Machoire§7 vous avez obtenue§c 5%§7 de§c force supplémentaire");
            }
            if (titan instanceof CuirasseV2) {
                this.getRole().addBonusResi(5.0);
                event.getKiller().sendMessage("§7En tuant la personne ayant le§c Titan Cuirasse§7 vous avez obtenue§c 5%§7 de§c résistance supplémentaire");
            }
            if (titan instanceof ColossalV2) {
                this.getRole().addBonusResi(10.0);
                event.getKiller().sendMessage("§7En tuant la personne ayant le§c Titan Colossal§7 vous avez obtenue§c 10%§7 de§c résistance supplémentaire");
            }
        }
    }
    private static class FastTimeTravelPower extends CommandPower {

        private final Map<Integer, Location> timeLocations;

        public FastTimeTravelPower(@NonNull RoleBase role) {
            super("/aot fasttimetravel <Temp entre 1 et 60 secondes>", "fasttimetravel", new Cooldown(60*8), role, CommandType.AOT,
                    "§7Vous permet de vous téléportez la ou vous étiez a un temp donné de maximum§c 60 secondes");
            this.timeLocations = new HashMap<>();
            new ChangeOldPositionRunnable(role.getGameState(), this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            @NonNull final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                try {
                    final int i = Integer.parseInt(args[1]);
                    if (this.timeLocations.containsKey(i)) {
                        player.sendMessage("§7Vous vous téléportez il y a§c "+i+ " secondes");
                        player.teleport(this.timeLocations.get(i));
                        return true;
                    } else {
                        player.sendMessage("§b"+i+"§c n'est pas un temp valide, essayer autre chose.");
                    }
                } catch (NumberFormatException e) {
                    e.fillInStackTrace();
                }
            }
            return false;
        }
        private static class ChangeOldPositionRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final FastTimeTravelPower power;

            private ChangeOldPositionRunnable(GameState gameState, FastTimeTravelPower power) {
                this.gameState = gameState;
                this.power = power;
            }

            @Override
            public void run() {
                if (gameState.getServerState() != GameState.ServerStates.InGame) {
                    cancel();
                    return;
                }
                final GamePlayer gamePlayer = this.power.getRole().getGamePlayer();
                if (gamePlayer.isAlive()) {
                    if (this.power.timeLocations.isEmpty()) {
                        this.power.timeLocations.put(1, gamePlayer.getLastLocation());
                    } else {
                        @NonNull final Map<Integer, Location> nextMap = new HashMap<>();
                        for (int i = Math.min(59, this.power.timeLocations.size()); i > 0; i--) {
                            nextMap.put(i+1, this.power.timeLocations.get(i));
                        }
                        this.power.timeLocations.clear();
                        nextMap.put(1, this.power.getRole().getGamePlayer().getLastLocation());
                        this.power.timeLocations.putAll(nextMap);
                    }
                }
            }
        }
    }
}