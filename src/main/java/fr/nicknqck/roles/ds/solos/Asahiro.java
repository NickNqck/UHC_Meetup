package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.RoleCustomLore;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.util.Color;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Asahiro extends DemonsSlayersRoles implements RoleCustomLore, Listener {

    private boolean killLune = false;
    private boolean killPilier = false;

    public Asahiro(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Asahiro";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Asahiro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public String[] getCustomLore(String amount, String gDesign) {
        return new String[] {
                "§7Ce rôle ne viens pas de l'univers§c DemonSlayer§7 mais entièrement",
                "§7par§b NickNqck§7, il est simplement inspirer de son§c univers§7,",
                "",
                "§7Lore:",
                "",
                "§7Blessé au coeur de la nuit, §fAsahiro§7 ne dut sa survie",
                "§7qu’à la §bpatience§7 et au §fsabre hérité§7 de son grand-père.",
                "",
                "§7Face au démon, il ne chercha ni la §cforce§7 ni la §6gloire§7.",
                "§7Il esquiva, §btemporisa§7, et §fattendit§7.",
                "",
                "§7Lorsque l’§e§laube§7 se leva, le démon chargea une dernière fois.",
                "§fAsahiro§7 trancha alors, §bau moment exact§7 où la lumière l’atteignit.",
                "",
                "§7De ce combat naquit un style unique :",
                "§b§lle Souffle du Nuage§7,",
                "§7un art destiné à §fsurvivre§7 jusqu’au dernier instant.",
                "",
                gDesign
        };
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60*60*60, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
        addPower(new SwordPower(this), true);
        addPower(new BrumeSuspendue(this), true);
        addPower(new PatiencePower(this));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Lorsque vous êtes en dessous de §c5❤§7 vous recevez§c 10%§7 de§c dégâts en moins§7.")
                .addCustomLine(this.killLune ? "" : "§7Si vous parvenez à§c vaincre§7 une§c Lune Supérieur§7 ou§c Muzan§7, vous gagnerez§c Force I§7 la§c nuit§7.")
                .addCustomLine(this.killPilier ? "" : "§7Si vous parvenez à§c vaincre§7 un§a Pilier§7, vous gagnerez§e Speed I§7 le§c jour§7.")
                .getText();
    }
    @EventHandler
    private void onDamage(@NonNull final EntityDamageEvent event) {
        if (!event.getEntity().getUniqueId().equals(getPlayer()))return;
        if (!(event.getEntity() instanceof Player))return;
        if (((Player) event.getEntity()).getHealth() <= (((Player) event.getEntity()).getMaxHealth()/2)) {
            event.setDamage(event.getDamage()*0.9);
        }
    }
    @EventHandler
    private void onKill(@NonNull final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() != null) {
            if (event.getGamePlayerKiller().getRole() == null)return;
            if (event.getGamePlayerKiller().getRole() instanceof DemonsRoles && !this.killLune) {
                DemonsRoles role = (DemonsRoles) event.getGamePlayerKiller().getRole();
                if (role.getRank().equals(DemonType.SUPERIEUR) || role.getRank().equals(DemonType.NEZUKO) || role instanceof MuzanV2) {
                    this.killLune = true;
                    givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
                    event.getKiller().sendMessage(Main.getInstance().getNAME()+"§7 En tuant une§c Lune Supérieur§7, vous avez acquis du§c sang de démon§7, ce qui vous permet de vous§c renforcer la nuit§7.");
                }
            }
            if (event.getGamePlayerKiller().getRole() instanceof PilierRoles && !killPilier) {
                this.killPilier = true;
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.DAY);
                event.getKiller().sendMessage(Main.getInstance().getNAME()+"§7 En tuant un§a Pilier§7, vous avez acquis de l'§cexperience§7, ce qui vous à permit d'être§c plus rapide§7 le§c jour§7.");
            }
        }
    }
    @EventHandler
    private void onEndGiveRole(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        boolean pil = false;
        boolean lun = false;
        for (GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
            if (gamePlayer.getRole() == null)continue;
            if (!gamePlayer.isAlive())continue;
            if (!gamePlayer.isOnline())continue;
            if (gamePlayer.getRole() instanceof PilierRoles) {
                pil = true;
            }
            if (gamePlayer.getRole() instanceof DemonsRoles) {
                DemonType demonType = ((DemonsRoles) gamePlayer.getRole()).getRank();
                if (demonType.equals(DemonType.SUPERIEUR) ||demonType.equals(DemonType.NEZUKO) || gamePlayer.getRole() instanceof MuzanV2) {
                    lun = true;
                }
            }
        }
        if (!pil) {
            this.killPilier = true;
            givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.DAY);
            getGamePlayer().sendMessage(Main.getInstance().getNAME()+"§7 En tuant un§a Pilier§7, vous avez acquis de l'§cexperience§7, ce qui vous à permit d'être§c plus rapide§7 le§c jour§7.");
        }
        if (!lun) {
            this.killLune = true;
            givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
            getGamePlayer().sendMessage(Main.getInstance().getNAME()+"§7 En tuant une§c Lune Supérieur§7, vous avez acquis du§c sang de démon§7, ce qui vous permet de vous§c renforcer la nuit§7.");
        }
    }
    private static final class SwordPower extends ItemPower {

        public SwordPower(@NonNull final RoleBase role) {
            super("§aÉpée de grand père§r", null, new ItemBuilder(Material.IRON_SWORD)
                            .setName("§aÉpée de grand père")
                            .addEnchant(Enchantment.DAMAGE_ALL, Main.getInstance().getGameConfig().getStuffConfig().getSharpness()+1), role,
                    "§7L'Épée mystérieuse de§a grand père§7, il n'a jamais expliquer comment il l'avait eu...");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return false;
        }
    }
    private static final class BrumeSuspendue extends ItemPower implements Listener{

        private final ZoneRunnable zoneRunnable;

        public BrumeSuspendue(@NonNull RoleBase role) {
            super("§bBrume suspendue§r", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§bBrume suspendue"), role,
                    "§7Crée une§c zone§7 de§c 7 blocs§7 vous suivant pendant§c 12 secondes§7.",
                    "",
                    "§7Les joueurs à l'intérieur de votre§c zone§7 auront l'effet§c slowness§7.",
                    "§7Lorsque votre§c zone§7 est§a activer§7 vous aurez§c 20%§7 de§c chance§7",
                    "§7d'§cesquiver§7 les coups qui vous sont porter."
            );
            this.zoneRunnable = new ZoneRunnable(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.zoneRunnable.start();
                player.sendMessage(Main.getInstance().getNAME()+"§7 Vous avez§a activer§7 votre§b Brume suspendue§7.");
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer()) && event.getEntity() instanceof Player) {
                if (this.zoneRunnable.running && this.zoneRunnable.timeLeft > 0) {
                    if (RandomUtils.getOwnRandomProbability(20.0)) {
                        event.setCancelled(true);
                        ((Player) event.getEntity()).setNoDamageTicks(15);
                    }
                }
            }
        }
        private static final class ZoneRunnable extends BukkitRunnable {

            private final BrumeSuspendue brumeSuspendue;
            private boolean running = false;
            private int timeLeft = 12;

            private ZoneRunnable(BrumeSuspendue brumeSuspendue) {
                this.brumeSuspendue = brumeSuspendue;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    this.running = false;
                    cancel();
                    return;
                }
                this.brumeSuspendue.getRole().getGamePlayer().getActionBarManager().updateActionBar("asahiro.brume", "§bBrume suspendue:§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                final Player player = Bukkit.getPlayer(this.brumeSuspendue.getRole().getPlayer());
                if (player == null) {
                    return;
                }
                MathUtil.sendCircleParticle(EnumParticle.EXPLOSION_NORMAL, player.getLocation(), 7.0, 48);
                final List<Player> list = new ArrayList<>(Loc.getNearbyPlayers(player, 7));
                for (@NonNull Player target : list) {
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (gamePlayer.getRole() == null || !gamePlayer.isAlive() || !gamePlayer.isOnline())continue;
                    gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0, false, false), EffectWhen.NOW);
                }
                if (this.timeLeft <= 0) {
                    this.running = false;
                    this.brumeSuspendue.getRole().getGamePlayer().getActionBarManager().removeInActionBar("asahiro.brume");
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
            public synchronized void start() {
                if (running)return;
                this.timeLeft = 12;
                this.running = true;
                runTaskTimer(Main.getInstance(), 0, 20);
            }
        }
    }
    private static final class PatiencePower extends Power implements Listener{

        private final PointsRunnable pointsRunnable;

        public PatiencePower(@NonNull RoleBase role) {
            super("§fPatience§r", null, role,
                    "§7Au bout de§c 4 minutes§7 passer proche d'un joueur vous verrez",
                    "§7des particules autours de lui qui seront coloré en fonction de son§a camp§7:",
                    "",
                    "§8 -§a Vert: Slayer",
                    "",
                    "§8 -§c Rouge: Démon",
                    "",
                    "§8 -§9 Bleu: Les autres camps",
                    "",
                    "§7Vous aurez§c Force 0,5§7 contre les joueurs avec qui vous serez rester§c 4 minutes§7.");
            this.pointsRunnable = new PointsRunnable(role);
            this.pointsRunnable.runTaskTimerAsynchronously(getPlugin(), 100, 20);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (this.pointsRunnable.map.getOrDefault(event.getEntity().getUniqueId(), 0) >= this.pointsRunnable.maxTime) {
                event.setDamage(event.getDamage()*(1+((double) Main.getInstance().getGameConfig().getForcePercent() /100)/2));
            }
        }
        private static final class PointsRunnable extends BukkitRunnable {

            private final RoleBase asahiro;
            private final Map<UUID, Integer> map = new HashMap<>();
            private final int maxTime = 60*4;

            private PointsRunnable(RoleBase asahiro) {
                this.asahiro = asahiro;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final Player player = Bukkit.getPlayer(this.asahiro.getPlayer());
                if (player == null)return;
                final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(player.getLocation(), 30));
                for (@NonNull final GamePlayer gamePlayer : gamePlayerList) {
                    if (gamePlayer.getRole() == null)continue;
                    if (!gamePlayer.isAlive())continue;
                    if (!gamePlayer.isOnline())continue;
                    if (gamePlayer.getUuid().equals(asahiro.getPlayer()))continue;
                    if (map.containsKey(gamePlayer.getUuid())) {
                        int points = map.get(gamePlayer.getUuid());
                        map.put(gamePlayer.getUuid(), points+1);
                        if (points == this.maxTime) {
                            this.asahiro.getGamePlayer().sendMessage("§7Vous comprenez enfin comment fonctionne§a "+gamePlayer.getPlayerName()+"§7.");
                        }
                    } else {
                        map.put(gamePlayer.getUuid(), 1);
                    }
                }
                for (@NonNull final Player target : Loc.getNearbyPlayersExcept(player, 40)) {
                    if (!map.containsKey(target.getUniqueId()))continue;
                    if (map.get(target.getUniqueId()) < this.maxTime)continue;
                    GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.isOnline())continue;
                    if (!gamePlayer.isAlive())continue;
                    if (gamePlayer.getRole() == null)continue;
                    final TeamList team = gamePlayer.getRole().getTeam();
                    final Location center = target.getEyeLocation();
                    center.setY(center.getY()+0.5);
                    if (team.equals(TeamList.Slayer)) {
                        for (Location location : MathUtil.getCircle(center, 1.0)) {
                            new ParticleDustColored(Color.GREEN).setSpeed(0.0).displayForPlayers(location, player);
                        }
                    } else if (team.equals(TeamList.Demon)) {
                        for (Location location : MathUtil.getCircle(center, 1.0)) {
                            new ParticleDustColored(Color.RED).setSpeed(0.0).displayForPlayers(location, player);
                        }
                    } else {
                        for (Location location : MathUtil.getCircle(center, 1.0)) {
                            new ParticleDustColored(Color.BLUE).setSpeed(0.0).displayForPlayers(location, player);
                        }
                    }
                }
                for (UUID uuid : map.keySet()) {
                    Player target = Bukkit.getPlayer(uuid);
                    if (target == null) continue;

                    int seconds = map.get(uuid);

                    int percent = Math.min(100, (seconds * 100) / (this.maxTime));

                    updatePointsNametag(player, target, percent);
                }

            }
            private void updatePointsNametag(Player viewer, Player target, int percent) {
                if (viewer == null || target == null) return;
                if (!viewer.isOnline() || !target.isOnline()) return;
                if (viewer.equals(target)) return;

                Scoreboard board = viewer.getScoreboard();
                if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard()) {
                    board = Main.getInstance().getScoreboardManager().getColorScoreboard().get(viewer.getUniqueId());
                    viewer.setScoreboard(board);
                }

                String teamName = target.getName();
                Team team = board.getTeam(teamName);

                if (team == null) {
                    team = board.registerNewTeam(teamName);
                    team.addEntry(target.getName());
                }
                if (percent < 25){
                    team.setSuffix("§c " + percent + "%");
                } else if (percent < 75) {
                    team.setSuffix("§6 " + percent + "%");
                } else if (percent < 95) {
                    team.setSuffix("§a " + percent + "%");
                } else {
                    team.setSuffix("§2 " + percent + "%");
                }
                if (percent == 100) {
                    team.setSuffix("§2 ✔");
                }
            }

        }
    }
}