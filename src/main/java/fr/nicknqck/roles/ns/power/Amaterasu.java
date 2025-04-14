package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class Amaterasu extends ItemPower {

    public Amaterasu(@NonNull RoleBase role) {
        super("Amaterasu", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§cAmaterasu"), role);
    }

    @Override
    public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
        if (getInteractType().equals(InteractType.INTERACT)) {
            final Player target = RayTrace.getTargetPlayer(player, 30, null);
            if (target == null) {
                player.sendMessage("§cIl faut viser un joueur !");
                return false;
            }
            @NonNull final GameState gameState = getRole().getGameState();
            if (!gameState.getGamePlayer().containsKey(target.getUniqueId())) {
                player.sendMessage("§cImpossible de lancer l'§4Amaterasu§c contre §4"+target.getDisplayName());
                return false;
            }
            player.sendMessage("§7Vous avez lancé l'§cAmaterasu§7 contre§c "+target.getDisplayName());
            new AmaterasuRunnable(gameState, gameState.getGamePlayer().get(target.getUniqueId()));
            return true;
        }
        return false;
    }
    private static class AmaterasuRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final GamePlayer gamePlayer;
        private int timeLeft = 10;

        private AmaterasuRunnable(GameState gameState, GamePlayer gamePlayer) {
            this.gameState = gameState;
            this.gamePlayer = gamePlayer;
            this.gamePlayer.getActionBarManager().addToActionBar("power.amaterasu", "§7Vous êtes sous l'effet d'§cAmaterasu");
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.timeLeft <= 0) {
                this.gamePlayer.getActionBarManager().removeInActionBar("power.amaterasu");
                cancel();
                return;
            }
            final Player player = Bukkit.getPlayer(this.gamePlayer.getUuid());
            if (player == null)return;
            double damage = 1.75;
            if (this.gamePlayer.getRole() != null) {
                if (this.gamePlayer.getRole().getBonusResi() > 0) {
                    double resi = 1 - (this.gamePlayer.getRole().getBonusResi() / 100);
                    damage = damage*resi;
                }
            }
            if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                damage = damage*0.8;
            }
            player.damage(damage);
            this.timeLeft--;
        }
    }
}
