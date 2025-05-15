package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.DayEvent;
import fr.nicknqck.events.custom.NightEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class KokushiboV2 extends DemonsRoles {

    private int pourcentageLunaire = 0;
    public double orginalMaxHealth;

    public KokushiboV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Kokushibo";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kokushibo;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        getGamePlayer().startChatWith("§cKokushibo: ", "!", MuzanV2.class);
        addKnowedRole(MuzanV2.class);
        addPower(new SouflePower(this), true);
        addPower(new DSLunePower(this));
        addPower(new RegenPower(this));
        new PourcentageLunaireRunnable(this);
        setCanuseblade(true);
        this.orginalMaxHealth = getMaxHealth();
        super.RoleGiven(gameState);
    }
    private static class DSLunePower extends CommandPower implements Listener {

        public DSLunePower(@NonNull KokushiboV2 role) {
            super("/ds lune", "lune", null, role, CommandType.DS, "§7Vous permet de forcer la§c nuit instantanément§7, également vous offre jusqu'au jour§e +10% Speed§7.");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            getRole().getGameState().nightTime = true;
            getRole().getGameState().t = Main.getInstance().getGameConfig().getMaxTimeDay();
            GameListener.SendToEveryone("§cKokushibo§f à forcer la§9 nuit");
            Bukkit.getPluginManager().callEvent(new NightEvent(getRole().getGameState(), Main.getInstance().getGameConfig().getMaxTimeDay()));
            player.sendMessage("§cVous avez forcez la nuit");
            getRole().addSpeedAtInt(player, 10f);
            EventUtils.registerRoleEvent(this);
            return true;
        }
        @EventHandler
        private void onDay(final DayEvent event) {
            for (final UUID uuid : event.getInGamePlayersWithRole()) {
                if (uuid.equals(getRole().getPlayer())) {
                    final Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        getRole().addSpeedAtInt(player, -10f);
                        EventUtils.unregisterEvents(this);
                    }
                    break;
                }
            }
        }
    }
    private static class RegenPower extends Power {

        public RegenPower(@NonNull RoleBase role) {
            super("Pouvoir régénérant", null, role,
                    "§7Vous possédez une§c régénération naturel§7 à hauteur de§c 1/2❤§7 toute les§c 15 secondes");
            new RegenPower.RegenerationRunnable(role.getGameState(), role.getPlayer(), this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class RegenerationRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final UUID uuid;
            private final RegenPower regenPower;
            private int timeLeft;

            private RegenerationRunnable(GameState gameState, UUID uuid, RegenPower regenPower) {
                this.gameState = gameState;
                this.uuid = uuid;
                this.regenPower = regenPower;
                runTaskTimerAsynchronously(regenPower.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft == 0) {
                    final Player owner = Bukkit.getPlayer(this.uuid);
                    if (owner != null) {
                        if (this.regenPower.checkUse(owner, new HashMap<>())){
                            Bukkit.getScheduler().runTask(this.regenPower.getPlugin(), () -> owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0)));
                        }
                    }
                    this.timeLeft = 15;
                    return;
                }
                timeLeft--;
            }
        }
    }
    private static class PourcentageLunaireRunnable extends BukkitRunnable {

        private final KokushiboV2 kokushiboV2;
        private int time = 0;

        private PourcentageLunaireRunnable(KokushiboV2 kokushiboV2) {
            this.kokushiboV2 = kokushiboV2;
            kokushiboV2.getGamePlayer().getActionBarManager().addToActionBar("kokushibo.pourcentage", "§bPourcentage Lunaire:§c "+kokushiboV2.pourcentageLunaire+"%");
            runTaskTimerAsynchronously(Main.getInstance(), 20, 20);
        }

        @Override
        public void run() {
            if (!kokushiboV2.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (kokushiboV2.getGameState().isNightTime()) {
                if (this.time <= 0) {
                    this.time = 4;
                    this.kokushiboV2.pourcentageLunaire = Math.min(this.kokushiboV2.pourcentageLunaire+ Loc.getNearbyGamePlayers(kokushiboV2.getGamePlayer().getLastLocation(), 15).size(), 100);
                    return;
                }
                this.time--;
            }
            kokushiboV2.getGamePlayer().getActionBarManager().updateActionBar("kokushibo.pourcentage", "§bPourcentage Lunaire:§c "+kokushiboV2.pourcentageLunaire+"%");
        }
    }
    private static class SouflePower extends ItemPower {

        private final HorizontalPower horizontalPower;
        private final KokushiboV2 role;
        private final VerticalPower verticalPower;

        public SouflePower(@NonNull KokushiboV2 role) {
            super("Soufle de la lune", new Cooldown(30), new ItemBuilder(Material.NETHER_STAR).setName("§1Soufle de la lune"), role,
                    "§7Vous permet d'utiliser votre \"§bPourcentage Lunaire§7\" pour utiliser les pouvoirs suivant: ",
                    "",
                    "§fClique gauche§7 (Frappe Horizontal): Vous permet de lancer un§c croissant de lune§7 la ou vous regardez qui parcourra§c 10 blocs" ,
                    "§7Il détruira les blocs sur son passage et en infligeant§c 2❤§7 de§c dégâts§7 au joueurs touchés tout en les repoussants fortement en arrière. (1x/5m)",
                    "",
                    "§fClique droite§7 (Frappe Vertical): En visant un joueur, vous permet de lui infligez§c 2❤§7 de§c dégâts§7 et de le repousser légèrement en arrière. (1x/5m)",
                    "",
                    "§7Vous rechargerez automatiquement votre§b Pourcentage Lunaire§7 la§1 nuit§7 à autours de (nombre de joueur autours de vous) toute les§c 4 secondes§7.",
                    "",
                    "§cChaque pouvoir du§1 Soufle de la lune§c vous coûtera§b 50%§c du \"§bPourcentage Lunaire§c\"");
            this.role = role;
            this.horizontalPower = new HorizontalPower(role);
            this.verticalPower = new VerticalPower(role);
            role.addPower(horizontalPower);
            role.addPower(verticalPower);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {
                    if (this.role.pourcentageLunaire < 50) {
                        player.sendMessage("§cIl vous faut au minimum§4 50%§c de§7 \"§bPourcentage Lunaire§7\"§c pour utiliser votre§4 Frappe Horizontal§c.");
                        return false;
                    }
                    return this.horizontalPower.checkUse(player, map);
                } else if (event.getAction().name().contains("RIGHT")) {
                    if (this.role.pourcentageLunaire < 50) {
                        player.sendMessage("§cIl vous faut au minimum§4 50%§c de§7 \"§bPourcentage Lunaire§7\"§c pour utiliser votre§4 Frappe Vertical§c.");
                        return false;
                    }
                    return this.verticalPower.checkUse(player, map);
                }
            }
            return false;
        }
        private static class HorizontalPower extends Power {

            public HorizontalPower(@NonNull RoleBase role) {
                super("Soufle de la lune (Attaque Horizontal)", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                new MoonRunnable(player).start();
                return true;
            }
            private static class MoonRunnable extends BukkitRunnable {

                private final Player player;
                private int count = 0;
                private Location lastLoc;

                private MoonRunnable(Player player) {
                    this.player = player;
                    this.lastLoc = player.getEyeLocation();
                }

                @Override
                public void run() {
                    if (count >= 10 || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    this.lastLoc = displayCrescentMoon(this.lastLoc);
                    count++;
                }
                public void start() {
                    runTaskTimerAsynchronously(Main.getInstance(), 0L, 4L);
                }
                private Location displayCrescentMoon(Location location) {
                    World world = location.getWorld();
                    Vector direction = location.getDirection().clone();
                    // On prend uniquement la composante horizontale
                    direction.setY(0);
                    direction.normalize();

                    // Position 2 blocs devant le joueur dans la direction regardée
                    Location center = location.clone().add(direction.multiply(2));

                    // Paramètres du croissant
                    double outerRadius = 5.0;
                    int points = 50; // nombre de points pour chaque arc

                    // Calcul de l'angle de base à partir de la direction horizontale
                    double baseAngle = Math.atan2(direction.getZ(), direction.getX());
                    // L'arc extérieur s'étend de -45° à +45° autour du regard
                    double startAngle = baseAngle - Math.toRadians(45);
                    double endAngle = baseAngle + Math.toRadians(45);
                    double angleStep = (endAngle - startAngle) / (points - 1);

                    List<Location> outerArc = new ArrayList<>();
                    for (int i = 0; i < points; i++) {
                        double angle = startAngle + (i * angleStep);
                        double x = center.getX() + outerRadius * Math.cos(angle);
                        double z = center.getZ() + outerRadius * Math.sin(angle);
                        outerArc.add(new Location(world, x, center.getY(), z));
                    }
                    List<Location> crescentOutline = new ArrayList<>(outerArc);

                    // Affichage des particules le long du croissant
                    for (Location loc : crescentOutline) {
                        MathUtil.sendParticle(EnumParticle.CLOUD, loc);
                        final Block block = loc.getBlock();
                        if (block != null && !block.getType().equals(Material.AIR)) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> block.setType(Material.AIR));
                        }
                        for (Entity entity : world.getNearbyEntities(loc, 0.5, 0.5, 0.5)) {
                            if (entity instanceof LivingEntity && entity.getUniqueId() != player.getUniqueId()) {
                                LivingEntity target = (LivingEntity) entity;

                                // Inflige les dégâts
                                target.damage(0.0, player);
                                target.setHealth(Math.max(1.0, target.getHealth()-4.0));
                                // Calcul de la direction entre le joueur et la cible
                                Vector pushDirection = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                                // Appliquer une poussée vers l'arrière (environ 30 blocs avec un facteur élevé)
                                pushDirection.multiply(3.5); // Environ 30 blocs si non arrêté

                                // Appliquer le vecteur
                                target.setVelocity(pushDirection);
                                target.sendMessage("§cVous avez été toucher par la§4 Frappe Horizontal§c de§4 Kokushibo§c.");
                            }
                        }
                    }
                    return center;
                }
            }
        }
        private static class VerticalPower extends Power {

            public VerticalPower(@NonNull RoleBase role) {
                super("Soufle de la lune (Frappe Vertical)", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 5, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur à moins de§4 5 blocs§c.");
                    return false;
                }
                // Inflige les dégâts
                target.damage(0.0, player);
                target.setHealth(Math.max(1.0, target.getHealth()-4.0));
                // Calcul de la direction entre le joueur et la cible
                Vector pushDirection = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                pushDirection.multiply(1.5);

                // Appliquer le vecteur
                target.setVelocity(pushDirection);
                target.sendMessage("§cVous avez été toucher par la§4 Frappe Vertical§c de§4 Kokushibo§c.");
                return true;
            }
        }
    }
}