package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class YahabaV2 extends DemonInferieurRole {

    public YahabaV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Yahaba";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Yahaba;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        addPower(new ManipulationPower(this), true);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class ManipulationPower extends ItemPower {

        public ManipulationPower(@NonNull RoleBase role) {
            super("Manipulation", new Cooldown(120), new ItemBuilder(Material.COMPASS).setName("§cManipulation"), role,
                    "§7En visant un joueur, vous permet de manipuler ses mouvements pendant§c 10 secondes§7.",
                    "",
                    "§7La personne visée suivra votre§c crosshair§7 en la forcent à être à§c 15 blocs§7 de vous.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 15, Objects::nonNull);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                new ManipulationRunnable(target, getRole().getGamePlayer());
                return true;
            }
            return false;
        }

        private static class ManipulationRunnable extends BukkitRunnable {

            private final UUID uuid;
            private final String name;
            private final GamePlayer gamePlayer;
            private int timeLeft = 10*20;

            private ManipulationRunnable(final Player target, GamePlayer gamePlayer) {
                this.uuid = target.getUniqueId();
                this.name = target.getName();
                this.gamePlayer = gamePlayer;
                gamePlayer.getActionBarManager().addToActionBar("yahaba.manipulation", "§cManipulation§7 (§6"+this.name+"§7)§c:§6 10s");
                runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
            }

            @Override
            public void run() {
                if (!this.gamePlayer.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("yahaba.manipulation");
                    cancel();
                    return;
                }
                this.timeLeft--;
                final Player target = Bukkit.getPlayer(this.uuid);
                final Player player = Bukkit.getPlayer(this.gamePlayer.getUuid());
                gamePlayer.getActionBarManager().updateActionBar("yahaba.manipulation", "§cManipulation§7 (§6"+this.name+"§7)§c:§6 "+(this.timeLeft/20)+"s");
                if (target != null && player != null) {
                    RayTrace ray = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
                    Vector intersection = positionOfIntersection(ray);

                    if (intersection != null) {
                        Location teleportLocation = intersection.toLocation(player.getWorld()).clone();
                        teleportLocation.setY(teleportLocation.getY() + 0.5); // Ajuste pour que le joueur ne soit pas dans le sol
                        teleportLocation.setPitch(target.getLocation().getPitch()); // Conserve son orientation verticale
                        teleportLocation.setYaw(target.getLocation().getYaw());     // Conserve son orientation horizontale
                        target.teleport(teleportLocation);
                    }
                }
            }
            public Vector positionOfIntersection(RayTrace rayTrace) {
                return rayTrace.getPostion(15);
            }
        }
    }
}