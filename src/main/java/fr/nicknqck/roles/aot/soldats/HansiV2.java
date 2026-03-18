package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.aot.powers.LancePowerManager;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class HansiV2 extends SoldatsRoles {

    public HansiV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Hansi";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Hansi;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new GiveSeringueCommand(this));
        LancePowerManager.giveLance(getGamePlayer(), 3);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLines(
                        new String[]{
                                "§7Toute les§c 5 minutes§7, vous recevrez une§c lance explosive§7.",
                                "",
                                "§7Lorsqu'un joueur est toucher par une§c lance explosive§7 il §7subirat des§c dégâts",
                                "§7plus ou moins conséquant en fonction de s'il est§c transformer §7ou§c non§7."
                        }
                )
                .getText();
    }
    private static final class GiveSeringueCommand extends CommandPower {

        public GiveSeringueCommand(@NonNull RoleBase role) {
            super("/aot seringue <joueur>", "seringue", new Cooldown(300), role, CommandType.AOT,
                    "§7Vous permet de donner une§a§l Seringue§7 à un joueur",
                    "§7§o(Si vous vous visez vous même vous perdrez§c 2"+ AllDesc.coeur+"§c permanent§7§o)",
                    "",
                    "§7Les joueurs ayant une§a§l Seringue§7 peuvent récupérer un§c titan§7 si son détenteur",
                    "§7meurt à moins de§c 25 blocs§7 d'eux."
            );
            setMaxUse(3);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas !");
                    return false;
                }
                GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                if (gamePlayer != null) {
                    if (gamePlayer.getRole() != null && gamePlayer.isOnline() && gamePlayer.isAlive()) {
                        if (gamePlayer.getRole() instanceof AotRoles) {
                            final AotRoles role = (AotRoles) gamePlayer.getRole();
                            if (!role.isCanVoleTitan()) {
                                player.sendMessage("§b"+target.getDisplayName()+"§7 recevra bientôt une§a§l Seringue§7.");
                                new SeringueRunnable(role, this.getRole()).runTaskTimerAsynchronously(getPlugin(), 1, 20);
                                return true;
                            }
                        }
                    }
                }
                player.sendMessage("§cImpossible de donner une§a§l Seringue§c à ce joueur !");
                return false;
            }
            return false;
        }
        private static final class SeringueRunnable extends BukkitRunnable {

            private final AotRoles role;
            private final RoleBase hansi;
            private final int maxTimeToGo;
            private int actualTime = 0;

            private SeringueRunnable(AotRoles role, RoleBase hansi) {
                this.role = role;
                this.hansi = hansi;
                this.maxTimeToGo = RandomUtils.getRandomInt(10, 60);
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (this.actualTime >= maxTimeToGo) {
                    this.role.setCanVoleTitan(true);
                    this.role.setStealPriority(1);
                    this.role.getGamePlayer().sendMessage("§aHansi§7 vous a offert une§a§l Seringue§7.");
                    this.hansi.getGamePlayer().sendMessage("§7Une§a§l Seringue§7 a été offerte à§a "+this.role.getGamePlayer().getPlayerName());
                    return;
                }
                this.actualTime++;
            }
        }
    }

}