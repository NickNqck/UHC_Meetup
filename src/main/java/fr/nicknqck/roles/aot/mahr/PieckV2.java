package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.titans.impl.CharetteV2;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PieckV2 extends MahrRoles {

    public PieckV2(UUID player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Pieck§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Pieck;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Vous possédez une régénération naturel à hauteurs de§c 1/2❤ §7toute les§c 30 secondes§7, si vous êtes transformer en titan le §ccooldown§7 de votre§d régénération§7 sera§c divisé§7 par§c deux.")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        @NonNull CharetteV2 charette = new CharetteV2(getGamePlayer());
        setTitan(charette);
        Main.getInstance().getTitanManager().addTitan(getPlayer(), charette);
        new RegenerationRunnable(this, getGameState());
        addKnowedPlayersWithRoles("§7Voici la liste de vos coéquipier§9 Mahr§7: ", BertoltV2.class, LaraV2.class, PorcoV2.class, ReinerV2.class, Magath.class, PieckV2.class);
    }
    private static class RegenerationRunnable extends BukkitRunnable {

        private final MahrRoles role;
        private final GameState gameState;
        private int timeLeft;

        private RegenerationRunnable(MahrRoles role, GameState gameState) {
            this.role = role;
            this.gameState = gameState;
            this.timeLeft = 30;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (timeLeft <= 0) {
                final Player player = Bukkit.getPlayer(this.role.getPlayer());
                if (player != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.setHealth(Math.min(player.getHealth()+1.0, player.getMaxHealth())));
                }
                this.timeLeft = 30;
                return;
            }
            if (Main.getInstance().getTitanManager().hasTitan(role.getPlayer())) {
                if (Main.getInstance().getTitanManager().getTitan(role.getPlayer()).isTransformed()) {
                    this.timeLeft--;
                }
            }
            this.timeLeft--;
        }
    }
}
