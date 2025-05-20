package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class SusamaruV2 extends DemonsRoles {

    public SusamaruV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Susamaru";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Susamaru;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new BallonPower(this), true);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class BallonPower extends ItemPower implements Listener {

        private final Cooldown cdExplosif;
        private final Cooldown cdTransporteur;
        private boolean explosifPower = true;
        private boolean flying = false;
        private Arrow explosionArrow;

        public BallonPower(@NonNull RoleBase role) {
            super("Ballon", new Cooldown(1), new ItemBuilder(Material.BOW).setName("§cBallon").addEnchant(Enchantment.ARROW_DAMAGE, 6), role,
                    "§7En tirant une flèche modifie l'effet selon la flèche choisis: ",
                    "",
                    "§cBallon Explosif§7: A l'§cimpact§7, crée une explosion infligeant§c 2❤§7 de§c dégâts§7. (1x/15s)",
                    "",
                    "§cBallon Transporteur§7: Vous fait suivre la flèche que vous avez lancer jusqu'à ce qu'elle touche quelqu'un ou le sol. (1x/60s)",
                    "",
                    "§7Vous pouvez changer le modèle de votre§c Ballon§7 en faisant un§c clique gauche§7 avec votre§c arc§7.");
            EventUtils.registerRoleEvent(this);
            this.cdExplosif = new Cooldown(15);
            this.cdTransporteur = new Cooldown(65);
            this.getShowCdRunnable().setCustomText(true);
            setSendCooldown(false);
            setShowCdInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (this.cdExplosif.isInCooldown() || this.cdTransporteur.isInCooldown()) {
                    player.sendMessage("§cImpossible de changer de pouvoir ");
                    return false;
                }
                if (event.getAction().name().contains("LEFT")) {
                    player.sendMessage("§7Vous avez changer le pouvoir de votre§c Ballon");
                    this.explosifPower = !this.explosifPower;
                    return true;
                }
            }
            return false;
        }
        @EventHandler
        private void onShoot(final EntityShootBowEvent event) {
            if (event.isCancelled())return;
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
                if (event.getBow().isSimilar(getItem())) {
                    if (this.explosifPower) {
                        if (!this.cdExplosif.isInCooldown()) {
                            event.getEntity().sendMessage("§7Vous avez lancer votre§c Ballon Explosif");
                            event.getProjectile().setMetadata("susamaruv2.actualpower", new FixedMetadataValue(Main.getInstance(), event.getEntity().getName()));
                            event.getProjectile().setCustomName("§cBallon");
                            event.getProjectile().setCustomNameVisible(true);
                            this.explosionArrow = (Arrow) event.getProjectile();
                        }
                    } else {
                        if (!cdTransporteur.isInCooldown()) {
                            event.getEntity().sendMessage("§7Vous avez lancer votre§c Ballon Transporteur");
                            event.getProjectile().setCustomName("§cBallon");
                            event.getProjectile().setCustomNameVisible(true);
                            new TransporteurRunnable((Arrow) event.getProjectile(), (Player) event.getEntity(), this).runTaskTimer(Main.getInstance(), 0, 1);
                            this.cdTransporteur.use();
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onProjectileHit(final ProjectileHitEvent event) {
            if (event.getEntity() instanceof Arrow &&
                    event.getEntity().hasMetadata("susamaruv2.actualpower") &&
                    event.getEntity().getShooter() instanceof Player &&
                    ((Player) event.getEntity().getShooter()).getUniqueId().equals(getRole().getPlayer()) &&
                    this.explosifPower &&
                    !this.cdExplosif.isInCooldown()) {
                event.getEntity().setCustomNameVisible(false);
                event.getEntity().removeMetadata("susamaruv2.actualpower", Main.getInstance());
                final Location impactLocation = event.getEntity().getLocation();
                World world = impactLocation.getWorld();
                world.createExplosion(impactLocation, 4.0f, false);
                double radius = 5.0; // Rayon de la zone d'explosion
                for (Player player : world.getPlayers()) {
                    if (player.getLocation().distance(impactLocation) <= radius) {
                        player.sendMessage("Vous avez été touché par l'§6Éxplosion§r de§c Susamaru");
                        if (player.getHealth() > 4.0) {
                            player.setHealth(player.getHealth()-4.0);
                        } else {
                            player.setHealth(1.0);
                        }
                    }
                }
                this.cdExplosif.use();
                this.explosionArrow = null;
            }
        }
        @EventHandler
        private void onEntityDamage(final EntityDamageEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && event.getCause().name().contains("FALL") && this.flying) {
                event.setCancelled(true);
            }
            if (event.getCause().name().contains("EXPLOSION") && this.explosionArrow != null) {
                event.setCancelled(true);
            }
        }
        @Override
        public void tryUpdateActionBar() {
            this.getShowCdRunnable().setCustomTexte(
                    (this.explosifPower ?
                            this.cdExplosif.isInCooldown() ? "§fCooldown (§cExplosif§f): §c"+ StringUtils.secondsTowardsBeautiful(this.cdExplosif.getCooldownRemaining()) : "§fVotre§c Ballon Explosif§f est§c utilisable§f !"
                            :
                            this.cdTransporteur.isInCooldown() ? "§fCooldown (§cTransporteur§f): §c"+StringUtils.secondsTowardsBeautiful(this.cdTransporteur.getCooldownRemaining()) : "§fVotre§c Ballon Transporteur§f est§c utilisable§f !"
                    )
            );
        }

        private static class TransporteurRunnable extends BukkitRunnable {

            private final Arrow arrow;
            private final Player shooter;
            private final BallonPower ballonPower;

            private TransporteurRunnable(Arrow arrow, Player shooter, BallonPower ballonPower) {
                this.arrow = arrow;
                this.shooter = shooter;
                this.ballonPower = ballonPower;
                this.ballonPower.flying = true;
            }

            @Override
            public void run() {
                if (!arrow.isValid() || arrow.isOnGround()) {
                    this.cancel();
                    this.ballonPower.flying = false;
                    shooter.setAllowFlight(false);
                    shooter.setFlying(false);
                    Location arrowLocation = arrow.getLocation();
                    shooter.teleport(arrowLocation);
                    Vector arrowVelocity = arrow.getVelocity().multiply(1.5);
                    arrow.setVelocity(arrowVelocity);
                    arrow.setVelocity(arrowVelocity.multiply(1 / 1.5));
                } else {
                    Vector playerDirection = arrow.getLocation().toVector().subtract(shooter.getLocation().toVector());
                    playerDirection.setY(0).normalize();

                    double distance = 1.5;
                    Vector teleportLocation = arrow.getLocation().toVector().add(playerDirection.multiply(-distance));
                    shooter.teleport(teleportLocation.toLocation(shooter.getWorld()));
                }
            }
        }
    }
}