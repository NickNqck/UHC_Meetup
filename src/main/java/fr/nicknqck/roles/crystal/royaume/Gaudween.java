package fr.nicknqck.roles.crystal.royaume;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.CrystalFaction;
import fr.nicknqck.enums.CrystalRoles;
import fr.nicknqck.enums.CrystalTeam;
import fr.nicknqck.interfaces.IGotReputation;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.crystal.CrystalBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//ça se prononce Godwin
public class Gaudween extends RoyaumeBase {

    public Gaudween(UUID player) {
        super(player);
    }

    @Override
    public void onRoleGive(@NonNull GameState gameState) {
        addPower(new PrechePower(this));
    }

    @Override
    public @NonNull CrystalFaction getCrystalFaction() {
        return CrystalFaction.CLERGER;
    }

    @Override
    public String getName() {
        return "Gaudween";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return CrystalRoles.Gaudween;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static final class PrechePower extends CommandPower {

        public PrechePower(@NonNull RoleBase role) {
            super("/cr preche <joueur>", "preche", new Cooldown(60*10), role, CommandType.CRYSTAL,
                    "§7Augmente la§a réputation§7 du joueur§c cibler§7.",
                    "",
                    "§7S'il a plus de§a réputation§7 que vous, alors, il ne gagnera rien,",
                    "§7Si ce n'est pas le cas alors, vous obtiendrez pendant§c 8 minutes§7 la§a Théologie§7.",
                    "",
                    "§aThéologie§7:",
                    "",
                    "§7Tant que vous êtes sous cette effet, des que vous êtes à moins de§c 4❤§7",
                    "§7votre§a vie§7 augmente jusqu'à§c 8❤§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null && !target.getUniqueId().equals(player.getUniqueId())) {
                        if (gamePlayer.check()) {
                            if (gamePlayer.getRole() instanceof IGotReputation) {
                                if (getRole() instanceof IGotReputation) {
                                    if (((IGotReputation) gamePlayer.getRole()).getReputation() <= ((IGotReputation) getRole()).getReputation()) {
                                        ((IGotReputation) gamePlayer.getRole()).setReputation(((IGotReputation) gamePlayer.getRole()).getReputation()+1);
                                    }
                                } else {
                                    ((IGotReputation) gamePlayer.getRole()).setReputation(((IGotReputation) gamePlayer.getRole()).getReputation()+1);
                                }
                                player.sendMessage("§7Vous avez§a augmenté§7 la§c réputation§7 de§c "+gamePlayer.getPlayerName());
                                new PrecheRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 0 ,20);
                                return true;
                            }
                        }
                    }
                    player.sendMessage("§cCe genre de personne ne peuvent pas être sauvé...");
                    return false;
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas ou n'est pas connecté(e) !");
                }
            } else {
                player.sendMessage("§cComment j'ais pu oublier que la commande est§b "+getName());
            }
            return false;
        }

        @Override
        public List<String> getCompletor(String[] args) {
            final List<String> list = new ArrayList<>();
            if (args.length == 2) {
                return StringUtils.getPlayerNameList(getRole().getGamePlayer().getLastLocation().getWorld().getPlayers(), args[1], getRole().getPlayer());
            }
            return list;
        }

        private static final class PrecheRunnable extends BukkitRunnable {

            @NonNull
            private final PrechePower power;
            @Nullable
            private final GamePlayer gamePlayer;
            private int timeLeft = 60*8;

            private PrecheRunnable(@NonNull PrechePower power,@Nullable GamePlayer gamePlayer) {
                this.power = power;
                this.gamePlayer = gamePlayer;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (this.gamePlayer != null) {
                    if (!gamePlayer.check()) {
                        power.getRole().getGamePlayer().sendMessage("§7Vous n'êtes plus sous l'effet de votre§a Théologie");
                        cancel();
                        return;
                    }
                }
                Bukkit.getScheduler().runTask(this.power.getPlugin(), () -> {
                    final Player owner = Bukkit.getPlayer(this.power.getRole().getPlayer());
                    if (owner != null) {
                        if (owner.getHealth() <= 8.0) {
                            owner.setHealth(16.0);
                            owner.sendMessage("§aIncroyable, votre Théologie vous à évitez bien des problèmes !");
                            this.timeLeft = 0;
                        }
                    }
                });
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("godwin.preche", "§bTemps de§a Théologie§b restant:§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                if (this.timeLeft <= 0) {
                    power.getRole().getGamePlayer().sendMessage("§7Vous n'êtes plus sous l'effet de votre§a Théologie");
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("godwin.preche");
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
}