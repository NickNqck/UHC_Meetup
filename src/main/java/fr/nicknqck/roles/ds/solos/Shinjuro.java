package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.ds.alliance.EAllianceRole;
import fr.nicknqck.events.ds.alliance.IAllianceRole;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.enums.Lames;
import fr.nicknqck.enums.Soufle;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.particles.TornadoWaveEffect;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Shinjuro extends DemonsSlayersRoles implements IAllianceRole{

    private boolean aliance;

    public Shinjuro(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FLAMME;
    }

    @Override
    public String getName() {
        return "Shinjuro";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Shinjuro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        setCanuseblade(true);
        Lames.FireResistance.getUsers().put(getPlayer(), Integer.MAX_VALUE);
        addPower(new UniversDeFlammePower(this), true);
        addPower(new FlammePower(this));
        addPower(new RegenerationPower(this));
        setLameincassable(true);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public boolean isInAlliance() {
        return aliance;
    }

    @Override
    public void setInAlliance(boolean b) {
        this.aliance = b;
    }

    @Override
    public EAllianceRole knowHas() {
        return EAllianceRole.SHINJURO;
    }

    @Override
    public DemonsSlayersRoles getRole() {
        return this;
    }

    private static final class UniversDeFlammePower extends ItemPower {

        public UniversDeFlammePower(@NonNull RoleBase role) {
            super("§6Univers de Flamme§r", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§6Univers de Flamme"), role,
                    "§7Vous permet de faire un saut en avant, à l'arriver,",
                    "§7un immense§c cercle de feu§7 se dessinera n'importe quel joueur",
                    "§7toucher sera§c enflammer§7 pour une durée de§c 12 secondes§7 (ils§c ne§7 pourront§c pas s'éteindre§7).",
                    "",
                    "§7En utilisant cette objet avec le§c clique gauche§7 vous ne ferez pas de saut en avant."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {
                    TornadoWaveEffect.launch(getPlugin(), player);
                } else {
                    final List<Location> list = getForwardArcLocations(player.getEyeLocation(), 30, 20, 10);
                    final Location last = list.get(list.size()-1);
                    new BukkitRunnable() {

                        final List<Location> copyList = new ArrayList<>(list);

                        @Override
                        public void run() {
                            if (!GameState.inGame()) {
                                cancel();
                                return;
                            }
                            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                            if (owner == null) {
                                cancel();
                                return;
                            }
                            for (@NonNull final Location loc : copyList) {
                                MathUtil.sendParticle(EnumParticle.FLAME, loc);
                            }
                            if (list.isEmpty()) {
                                cancel();
                                return;
                            }
                            final Location loc = list.get(0);
                            owner.teleport(loc);
                            owner.setNoDamageTicks(5);
                            list.remove(loc);
                            if (loc.equals(last)) {
                                TornadoWaveEffect.launch(getPlugin(), owner);
                            }
                        }
                    }.runTaskTimer(getPlugin(), 0, 1);
                }
                PotionUtils.addTempNoFall(player.getUniqueId(), 1);
                return true;
            }
            return false;
        }
        public List<Location> getForwardArcLocations(Location playerLoc, double distance, int points, double height) {
            List<Location> locations = new ArrayList<>();

            // 1. Le point de départ est la position exacte (avec la direction du regard)
            Location start = playerLoc.clone();

            // 2. On calcule le point d'arrivée (end) droit devant le joueur
            // getDirection() récupère le vecteur vers lequel la caméra pointe.
            Vector direction = start.getDirection().normalize().multiply(distance);
            Location end = start.clone().add(direction);

            // 3. On trace l'arc de cercle (la parabole) entre start et end
            for (int i = 0; i < points; i++) {
                // t représente le pourcentage d'avancement (de 0.0 à 1.0)
                double t = (double) i / (points - 1);

                // Avancement en ligne droite sur X et Z vers le point d'arrivée
                double x = start.getX() + (end.getX() - start.getX()) * t;
                double z = start.getZ() + (end.getZ() - start.getZ()) * t;

                // Création de la "bosse" sur l'axe Y (l'arc)
                double arcHeight = height * 4 * t * (1 - t);

                // Y final = ligne droite en Y + la hauteur de l'arc
                double y = start.getY() + (end.getY() - start.getY()) * t + arcHeight;
                final Location loc =  new Location(start.getWorld(), x, y, z);
                if (!loc.getBlock().getType().equals(Material.AIR)) {
                    break;
                }
                loc.setPitch(playerLoc.getPitch());
                loc.setYaw(playerLoc.getYaw());
                locations.add(loc);
            }

            return locations;
        }
    }
    private static final class FlammePower extends CommandPower implements Listener {

        private boolean activate = false;

        public FlammePower(@NonNull RoleBase role) {
            super("§6/ds flamme", "flamme", new Cooldown(5), role, CommandType.DS,
                    "§7Vous permet d'§6enflammer§7 les joueurs que vous frappez et ceux sur lesquelles vous tirez,",
                    "§7En frappant un joueur vous aurez§c 35%§7 de§c chance§7 de recevoir§c 5 secondes§7 de l'effet§c Force I§7.",
                    "",
                    "§7Si vous êtes touché par§c l'évènement aléatoire§7: \"§fAlliance§a père-fils§7\",",
                    "§7vous aurez§c 100%§7 de§c chance§7 de recevoir§c 5 secondes§7 de§c force§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getCooldown().isInCooldown() && getCooldown().getCooldownRemaining() >= 60*7) {
                return false;
            }
            if (!activate) {
                EventUtils.registerEvents(this);
                player.sendMessage(Main.getInstance().getNAME()+"§7 Vous§a activez§7 votre§6 /ds flamme");
                activate = true;
            } else {
                EventUtils.unregisterEvents(this);
                player.sendMessage(Main.getInstance().getNAME()+"§7 Vous§c désactiver§7 votre§6 /ds flamme§7.");
                this.activate = false;
            }
            return true;
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager().getWorld().hasMetadata("noeffects")) return;
            if (event.getDamager() instanceof Player) {
                if (event.getDamager().getUniqueId().equals(getRole().getPlayer())){
                    event.getEntity().setFireTicks(event.getEntity().getFireTicks()+160);
                    boolean bool = Main.RANDOM.nextInt(100) <= 35;
                    if (getRole() instanceof IAllianceRole) {
                        if (((IAllianceRole) getRole()).isInAlliance()) {
                            bool = true;
                        }
                    }
                    if (bool) {
                        ((Player) event.getDamager()).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), true);
                    }
                }
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).getUniqueId().equals(getRole().getPlayer())) {
                    Player shooter = (Player) projectile.getShooter();
                    event.getEntity().setFireTicks(event.getEntity().getFireTicks()+160);
                    boolean bool = Main.RANDOM.nextInt(100) <= 35;
                    if (getRole() instanceof IAllianceRole) {
                        if (((IAllianceRole) getRole()).isInAlliance()) {
                            bool = true;
                        }
                    }
                    if (bool) {
                        shooter.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), true);
                    }
                }
            }
        }
        @EventHandler
        private void onEndGame(GameEndEvent event) {
            EventUtils.unregisterEvents(this);
        }
    }
    private static final class RegenerationPower extends Power {

        public RegenerationPower(@NonNull RoleBase role) {
            super("Régénération", null, role,
                    "§7Vous vous§d régénérez§7 de§c 1/2"+ AllDesc.coeur+"§7 toute les§c 10 secondes§7,",
                    "§7en étant en§c feu§7 vous avez l'effet§9 Résistance I§7."
            );
            new RegenerationPower.RegenRunnable(role.getGameState(), role.getGamePlayer());
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class RegenRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private int regenTimeLeft;

            private RegenRunnable(GameState gameState, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.regenTimeLeft = 10;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
                if (owner == null) {
                    this.regenTimeLeft = 10;
                    return;
                }

                if (owner.getFireTicks() > 0) {//donc s'il est en feu
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NOW));
                }

                final Location location = owner.getLocation();
                final Location eye = owner.getEyeLocation();
                if (location.getBlock().getType().name().contains("LAVA") || location.getBlock().getType().name().contains("FIRE")
                        || eye.getBlock().getType().name().contains("LAVA") || eye.getBlock().getType().name().contains("FIRE")) {
                    this.regenTimeLeft--;
                    if (!this.gamePlayer.getActionBarManager().containsKey("shinjuro.healtime")) {
                        gamePlayer.getActionBarManager().addToActionBar("shinjuro.healtime", "§bTemp avant§d régénération§b:§c "+this.regenTimeLeft+"s");
                    } else {
                        this.gamePlayer.getActionBarManager().updateActionBar("shinjuro.healtime", "§bTemp avant§d régénération§b:§c "+this.regenTimeLeft+"s");
                    }
                } else {
                    this.gamePlayer.getActionBarManager().removeInActionBar("shinjuro.healtime");
                }
                if (this.regenTimeLeft <= 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0)));
                    this.regenTimeLeft = 10;
                }
            }
        }
    }
}