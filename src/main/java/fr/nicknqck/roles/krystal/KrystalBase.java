package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.scoreboard.ScoreBoardUpdateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

@Setter
@Getter
public abstract class KrystalBase extends RoleBase {

    private int krystalAmount;
    private final ScoreBoardEditManager scoreBoardEditManager;
    private final KrystalManager krystalManager;

    public KrystalBase(UUID player) {
        super(player);
        this.krystalAmount = 0;
        this.scoreBoardEditManager = new ScoreBoardEditManager(this);
        this.krystalManager = new KrystalManager(this);
    }

    private static class ScoreBoardEditManager implements Listener {

        private final KrystalBase role;

        private ScoreBoardEditManager(KrystalBase role) {
            this.role = role;
            if (role.getGameState().getServerState().equals(GameState.ServerStates.InGame)){
                EventUtils.registerRoleEvent(this);
            }
        }
        @EventHandler
        private void ScoreboardUpdateEvent(ScoreBoardUpdateEvent event) {
            if (!event.getScoreboard().getUuid().equals(this.role.getPlayer()))return;
            if (!this.role.getGameState().getServerState().equals(GameState.ServerStates.InGame))return;
            event.getScoreboard().getObjectiveSign().setLine(13, "");
            event.getScoreboard().getObjectiveSign().setLine(14, "§fCrystaux:§c "+this.role.getKrystalAmount());
        }
    }
    private static class KrystalManager implements Listener {

        private final KrystalBase role;

        public KrystalManager(KrystalBase krystalBase) {
            this.role = krystalBase;
            if (this.role.getGameState().getServerState().equals(GameState.ServerStates.InGame)){
                EventUtils.registerRoleEvent(this);
            }
        }
        @EventHandler
        private void onKill(final UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() == null)return;
            if (event.getGamePlayerKiller().getRole() == null)return;
            if (event.getGamePlayerKiller().getUuid().equals(role.getPlayer())) {
                if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
                final GamePlayer gameVictim = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
                if (gameVictim.getRole() instanceof KrystalBase) {
                    int toAdd = ((KrystalBase) gameVictim.getRole()).getKrystalAmount()/3;
                    if (toAdd <= 0) {
                        event.getGamePlayerKiller().sendMessage("§c"+event.getVictim().getDisplayName()+"§b n'avait aucun§c krystal§b sur lui, vous n'avez donc rien récupérer.");
                        return;
                    }
                    this.role.krystalAmount += toAdd;
                    event.getGamePlayerKiller().sendMessage("§c"+event.getVictim().getDisplayName()+"§b avait quelque krystaux sur lui, vous n'avez pus en récupérer que§c "+toAdd);
                    ((KrystalBase) gameVictim.getRole()).setKrystalAmount(((KrystalBase) gameVictim.getRole()).getKrystalAmount()-toAdd);
                }
            }
        }
        @EventHandler
        private void onBlockBreak(final BlockBreakEvent event) {
            if (!event.getPlayer().getUniqueId().equals(role.getPlayer()))return;
            if (event.getBlock().getType().equals(Material.EMERALD_ORE)) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getPlayer().sendMessage("§bQuel chance, vous avez trouvé§c 1 Krystal");
                this.role.krystalAmount+=1;
                return;
            }
            if (event.getBlock().getType().equals(Material.DIAMOND_ORE)) {
                int random = Main.RANDOM.nextInt(101);
                if (random <= 7) {
                    event.getPlayer().sendMessage("§bQuel chance, vous avez trouvé§c 1 Krystal");
                    this.role.krystalAmount+=1;
                }
            }
        }

    }
}