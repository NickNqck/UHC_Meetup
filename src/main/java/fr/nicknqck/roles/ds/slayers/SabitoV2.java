package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SabitoV2 extends SlayerRoles {

    public SabitoV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Sabito";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Sabito;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NIGHT);
        addPower(new SouffleItem(this), true);
        super.RoleGiven(gameState);
    }
    private static class SouffleItem extends ItemPower implements Listener {

        private boolean tomiokaDeath = false;
        private final Set<UUID> dashing = new HashSet<>();
        private final Cooldown droitCooldown = new Cooldown(60*5);
        private final Cooldown gaucheCooldown = new Cooldown(60*5);

        public SouffleItem(@NonNull RoleBase role) {
            super("Souffle de l'Eau", new Cooldown(3),
                    new ItemBuilder(Material.NETHER_STAR).setName("§bSouffle de l'Eau"), role,
                    "§7Utilise§c deux pouvoirs différent§7 en fonction du clique effectué:",
                    "",
                    "§fClique droit§7: Vous donne§b Speed I§7 pendant§c 2 minutes§7 (Si§a Tomioka§7 est§c mort§7 vous obtiendrez§b Speed II§7 à la place). (1x/5m)",
                    "",
                    "§fClique gauche§7: En échange d'un§b sceau d'eau§7, effectue un§c dash§7 en avant, si un joueur est touché par celui-ci il",
                    "§7recevra§c 10 secondes§7 de§c Slowness II§7 et perdra§c 1,5❤§7 de§c dégâts§7. (1x/5m)");
            EventUtils.registerRoleEvent(this);
            getShowCdRunnable().setCustomText(true);
            setSendCooldown(false);
            setShowCdInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    if (droitCooldown.isInCooldown()) {
                        player.sendMessage("§cVous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(droitCooldown.getCooldownRemaining()));
                        return false;
                    }
                    player.sendMessage("§7Vous recevez les effets de votre§e Souffle de l'Eau§7.");
                    new SouffleRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                    droitCooldown.use();
                } else {
                    event.setCancelled(true);
                    if (gaucheCooldown.isInCooldown()) {
                        player.sendMessage("§cVous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(gaucheCooldown.getCooldownRemaining()));
                        return false;
                    }
                    if (!consumeWaterBucket(player)) {
                        player.sendMessage("§cVous devez avoir un §9seau d'eau§c pour utiliser ce pouvoir.");
                        return false;
                    }
                    startDash(player);
                    gaucheCooldown.use();
                }
                return true;
            }
            return false;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onDeath(final UHCDeathEvent event) {
            if (event.getRole() == null) return;
            if (event.getRole() instanceof TomiokaV2) {
                this.tomiokaDeath = true;
                if (getRole().getGamePlayer().isAlive()) {
                    getRole().getGamePlayer().sendMessage("§aTomioka§7 est§c mort§7, celà vous a permis d'augmenter les capacités de votre§b Souffle de l'Eau§7.");
                }
            }
        }

        private boolean consumeWaterBucket(Player player) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack content = player.getInventory().getItem(i);
                if (content != null && content.getType() == Material.WATER_BUCKET) {
                    content.setType(Material.BUCKET); // Devient un seau vide
                    player.getInventory().setItem(i, content);
                    player.updateInventory();
                    return true;
                }
            }
            return false;
        }

        private void startDash(Player player) {
            if (dashing.contains(player.getUniqueId())) return;
            dashing.add(player.getUniqueId());

            Vector direction = player.getLocation().getDirection().normalize().multiply(1.2);
            Set<UUID> touchedPlayers = new HashSet<>();
            Map<Location, Material> changedBlocks = new HashMap<>();

            Location startLoc = player.getLocation().clone(); // point de départ
            double maxDistance = 20.0; // distance cible

            new BukkitRunnable() {
                double distanceTravelled = 0;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cleanup();
                        cancel();
                        return;
                    }

                    // Avancer le joueur
                    player.setVelocity(direction);

                    // Particules eau
                    player.getWorld().spigot().playEffect(player.getLocation(), Effect.WATERDRIP, 0, 0,
                            0.3F, 0.3F, 0.3F, 0.1F, 20, 30);

                    // Eau temporaire sous le joueur
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            if (Math.abs(x) + Math.abs(z) != 1) continue;
                            Location loc = player.getLocation().clone().add(x, -1, z);
                            if (!changedBlocks.containsKey(loc)) {
                                changedBlocks.put(loc, loc.getBlock().getType());
                                loc.getBlock().setType(Material.WATER);
                            }
                        }
                    }

                    // Collision avec joueurs
                    for (Player target : player.getWorld().getPlayers()) {
                        if (target.equals(player)) continue;
                        if (touchedPlayers.contains(target.getUniqueId())) continue;

                        if (target.getLocation().distance(player.getLocation()) < 2) {
                            touchedPlayers.add(target.getUniqueId());
                            target.setHealth(Math.max(target.getHealth()-3.0, 1.0));
                            target.damage(0.0, player);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 1, false, false), true);
                            target.sendMessage("§7Vous avez été touché par le§b dash aquatique§7 de§a Sabito§7 !");
                        }
                    }

                    // Incrémenter distance
                    distanceTravelled = startLoc.distance(player.getLocation());

                    // Fin du dash une fois 20 blocs atteints
                    if (distanceTravelled >= maxDistance) {
                        cleanup();
                        cancel();

                        // Log lisible
                        String formatted = String.format("%.1f", distanceTravelled);
                        Bukkit.getLogger().info("[SabitoDash] " + player.getName()
                                + " a parcouru environ " + formatted + " blocs avec son dash.");
                    }
                }

                private void cleanup() {
                    dashing.remove(player.getUniqueId());

                    // Restauration des blocs d'eau
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        for (Map.Entry<Location, Material> entry : changedBlocks.entrySet()) {
                            entry.getKey().getBlock().setType(entry.getValue());
                        }
                    }, 5);
                }
            }.runTaskTimer(getPlugin(), 0, 2);
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(
                    (
                            droitCooldown.isInCooldown() ?
                            "§bCooldown (§fClique droit§b):§c "+StringUtils.secondsTowardsBeautiful(droitCooldown.getCooldownRemaining())
                            :
                            "§fClique droit est§c utilisable"
                    )
                    + "§7 | " +
                            (
                                    gaucheCooldown.isInCooldown() ?
                                            "§bCooldown (§fClique gauche§b):§c "+StringUtils.secondsTowardsBeautiful(gaucheCooldown.getCooldownRemaining())
                                            :
                                            "§fClique gauche est§c utilisable"
                            )
            );
        }

        private static class SouffleRunnable extends BukkitRunnable {
            private final SouffleItem souffleItem;
            private int timeLeft = 120;

            private SouffleRunnable(SouffleItem souffleItem) {
                this.souffleItem = souffleItem;
            }

            @Override
            public void run() {
                if (!souffleItem.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.souffleItem.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sabito.souffle");
                    this.souffleItem.getRole().getGamePlayer().sendMessage("§7Les effets du§b Souffle de l'Eau§7 s'estompe...");
                    cancel();
                    return;
                }
                this.souffleItem.getRole().getGamePlayer().getActionBarManager().updateActionBar(
                        "sabito.souffle",
                        "§bTemps restant (Souffle de l'Eau):§c " + StringUtils.secondsTowardsBeautiful(timeLeft)
                );
                this.timeLeft--;
                Bukkit.getScheduler().runTask(this.souffleItem.getPlugin(), () -> {
                    if (this.souffleItem.tomiokaDeath) {
                        this.souffleItem.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), EffectWhen.NOW);
                    } else {
                        this.souffleItem.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NOW);
                    }
                });
            }
        }
    }

}