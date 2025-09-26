package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DakiV2 extends DemonsRoles {
    public DakiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Daki§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Daki;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new ObisItems(this), true);
        super.RoleGiven(gameState);
    }

    private static class ObisItems extends ItemPower {

        public ObisItems(@NonNull RoleBase role) {
            super("Obis", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§cObis"), role,
                    "§7");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous démarrez votre visée avec des§c Obis§7.");
                new ObisRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 1);
                return true;
            }
            return false;
        }
        private synchronized void onStop(@NonNull final List<GamePlayer> toStuns) {
            if (toStuns.isEmpty()) {
                getRole().getGamePlayer().sendMessage("§7Vous avez réussi à viser personne avec vos§c Obis§7, le cooldown a été réduit.");
                return;
            }
            for (GamePlayer gamePlayer : toStuns) {
                gamePlayer.stun(20*6);
                MathUtil.sendParticleLine(getRole().getGamePlayer().getLastLocation(), gamePlayer.getLastLocation(), EnumParticle.FLAME, ((int) getRole().getGamePlayer().getLastLocation().distance(gamePlayer.getLastLocation()))+1);
                getRole().getGamePlayer().sendMessage("§c"+gamePlayer.getPlayerName()+"§7 a été§c stun§7 par vos§c Obis§7.");
            }
        }
        private static class ObisRunnable extends BukkitRunnable {

            private final ObisItems obisItems;
            private final List<GamePlayer> toStuns = new ArrayList<>();
            private int ticks = 0;

            private ObisRunnable(ObisItems obisItems) {
                this.obisItems = obisItems;
            }

            @Override
            public void run() {
                if (!obisItems.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (ticks > 100) {
                    cancel();
                    return;
                }
                ticks++;
                this.obisItems.getRole().getGamePlayer().getActionBarManager().updateActionBar("daki.a" ,"§bTimeLeft =§c "+ StringUtils.secondsTowardsBeautiful(ticks));
                final Player owner = Bukkit.getPlayer(this.obisItems.getRole().getPlayer());
                if (owner == null) return;
                if (owner.getItemInHand() == null || !owner.getItemInHand().isSimilar(this.obisItems.getItem())) {
                    this.obisItems.onStop(this.toStuns);
                    cancel();
                    return;
                }
                final Player target = RayTrace.getTargetPlayer(owner, 30, null);
                if (target == null)return;
                final GamePlayer gameTarget = this.obisItems.getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                if (gameTarget == null)return;
                if (gameTarget.getRole() != null) {
                    if (gameTarget.getRole() instanceof GyutaroV2 || gameTarget.getRole() instanceof MuzanV2 || gameTarget.getRole() instanceof DakiV2) {
                        return;
                    }
                }
                if (!this.toStuns.contains(gameTarget)) {
                    this.toStuns.add(gameTarget);
                    owner.sendMessage("§c"+target.getDisplayName()+"§7 sera pris pour cible par vos§c Obis§7.");
                    target.sendMessage("§7Vous vous sentez observez...");
                }
            }
        }
    }

}