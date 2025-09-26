package fr.nicknqck.roles.ns.solo.kumogakure;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.KumogakureRole;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GinkakuV2 extends KumogakureRole {

    public GinkakuV2(UUID player) {
        super(player);
    }

    @Override
    public void onEndKyubi() {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false , false), EffectWhen.NIGHT);
    }

    @Override
    public String getName() {
        return "Ginkaku";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Ginkaku;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addKnowedRole(Kinkaku.class);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false , false), EffectWhen.NIGHT);
        new EffectGiver(getGameState(), this);
        addPower(new KyubiPower(this), true);
        addPower(new GourdePower(this), true);
        addPower(new SabrePower(this), true);
        addPower(new CordeOrPower(this), true);
        setChakraType(getRandomChakras());
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(getGameState().getDeadRoles().contains(Roles.Kinkaku) ? "" : "§7Lorsque vous êtes proche de §6Kinkaku§7 vous avez l'effet §cRésistance I")
                .addCustomLine(getGameState().getDeadRoles().contains(Roles.Kinkaku) ? "" : "§7Lors de la mort de §6Kinkaku§7 vous obtener l'effet§c Résistance I")
                .getText();
    }

    private static class EffectGiver extends BukkitRunnable {

        private final GameState gameState;
        private final GinkakuV2 ginkaku;

        private EffectGiver(@NonNull final GameState gameState,@NonNull final GinkakuV2 ginkaku) {
            this.gameState = gameState;
            this.ginkaku = ginkaku;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.gameState.getDeadRoles().contains(Roles.Kinkaku)) {
                this.ginkaku.getGamePlayer().sendMessage("§6Kinkaku§7 est mort, vous obtenez l'effet§c Résistance I§7 de manière§c permanente");
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.ginkaku.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false), EffectWhen.PERMANENT));
                cancel();
                return;
            }
            @NonNull final Player owner = Bukkit.getPlayer(this.ginkaku.getPlayer());
            if (owner == null)return;
            @NonNull final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(owner.getLocation(), 30));
            if (gamePlayerList.isEmpty())return;
            for (@NonNull final GamePlayer gamePlayer : gamePlayerList) {
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof Kinkaku) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.ginkaku.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
    private static class GourdePower extends ItemPower implements Listener{

        protected GourdePower(@NonNull RoleBase role) {
            super("Gourde", new Cooldown(60*7+10), new ItemBuilder(Material.HOPPER).setName("§6Gourde"), role,
                    "§7En frappant un joueur, pose un §chopper §7à sa position",
                    "§c10 secondes§7 plus tard, si le §chopper §7 n'a pas été cassé celà téléportera le joueur sur le §chopper",
                    "§7Il obtiendra les effets:§2 Poison I§7 et§8 Wither I§7 pendant§c 10s§7.§7 (1x/7m)");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (!player.getWorld().getName().equalsIgnoreCase("arena")) {
                    player.sendMessage("§cCe pouvoir n'est pas utilisable ici.");
                    return false;
                }
                @NonNull final Location loc = event.getVictim().getLastLocation();
                loc.getWorld().getBlockAt(loc).setType(Material.HOPPER);
                new GourdeRunnable(getRole().getGameState(), event.getDamager(), event.getVictim(), loc);
                return true;
            }
            return false;
        }
        @EventHandler
        private void BlockPoseEvent(@NonNull final BlockPlaceEvent event) {
            if (event.getBlock().getType().equals(Material.HOPPER)) {
                event.setCancelled(true);
            }
        }
        private static class GourdeRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final GamePlayer gameVictim;
            private final Location hopperLocation;
            private int timeRemaining;

            private GourdeRunnable(GameState gameState, GamePlayer gamePlayer, GamePlayer gameVictim, Location hopperLocation) {
                this.timeRemaining = 10;
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.gameVictim = gameVictim;
                this.hopperLocation = hopperLocation;
                this.gamePlayer.getActionBarManager().addToActionBar("gourde.runnable", "§bTemp avant téléportation:§c "+ StringUtils.secondsTowardsBeautiful(this.timeRemaining));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.hopperLocation.getBlock().getType().equals(Material.HOPPER)) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("gourde.runnable");
                    cancel();
                    return;
                }
                if (this.timeRemaining <= 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        this.gameVictim.getRole().givePotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false), EffectWhen.NOW);
                        this.gameVictim.getRole().givePotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), EffectWhen.NOW);
                        this.gameVictim.getRole().givePotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*10, 0, false, false), EffectWhen.NOW);
                        @NonNull final Location loc = this.hopperLocation;
                        loc.getBlock().setType(Material.AIR);
                        loc.setY(loc.getBlockY()+1.5);
                        this.gameVictim.teleport(loc);
                        this.gamePlayer.getActionBarManager().removeInActionBar("gourde.runnable");
                    });
                    cancel();
                    return;
                }
                this.timeRemaining--;
                this.gamePlayer.getActionBarManager().updateActionBar("gourde.runnable", "§bTemp avant téléportation:§c "+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
            }
        }
    }
    private static class SabrePower extends ItemPower {

        private String targetName = "";
        private int amountCoups = 0;

        protected SabrePower(@NonNull RoleBase role) {
            super("Sabre des Étoiles", new Cooldown(30), new ItemBuilder(Material.DIAMOND_SWORD).setName("§aSabre des Étoiles").addEnchant(Enchantment.DAMAGE_ALL, 3), role,
                    "§7Infligera un effet de§c saignement§7 (§c1/2❤§7 toute les§c 2 secondes§7 pendant§c 8 secondes§7) au joueur sur qui vous infligerez§c 15 coups§7 d'affilé.§7 (1x/30s)");
            role.getGamePlayer().getActionBarManager().addToActionBar("sabre.coups", "§7Coup avant d'infliger le§c saignement§7:§c "+this.amountCoups+"§7/§615");
            setShowCdInHand(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (!event.isPatch())return false;
                if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return false;
                if (event.getVictim().getPlayerName().equalsIgnoreCase(this.targetName)) {
                    this.amountCoups+=1;
                } else {
                    this.amountCoups = 1;
                    this.targetName = event.getVictim().getPlayerName();
                }
                if (this.amountCoups >= 15) {
                    this.amountCoups = 0;
                    new SaignementRunnable(getRole().getGameState(), event.getVictim());
                    player.sendMessage("§c"+event.getVictim().getPlayerName());
                    return true;
                }
                this.getRole().getGamePlayer().getActionBarManager().updateActionBar("sabre.coups", "§7Coup avant d'infliger le§c saignement§7 (§b"+this.targetName+"§7):§c "+this.amountCoups+"§7/§615");
            }
            return false;
        }
        private static class SaignementRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gameTarget;
            private int timeLeft = 8;
            private int timeLeftDamage = 0;

            private SaignementRunnable(GameState gameState, GamePlayer gameTarget) {
                this.gameState = gameState;
                this.gameTarget = gameTarget;
            }

            @Override
            public void run() {
                if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeftDamage <= 0) {
                    @NonNull final Player player = Bukkit.getPlayer(this.gameTarget.getUuid());
                    if (player != null) {
                        this.gameTarget.getActionBarManager().addToActionBar("saignement.1", "§7Vous subissez un effet de§c saignement§7.");
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> this.gameTarget.getActionBarManager().removeInActionBar("saignement.1"), 20);
                        player.setHealth(Math.max(1.0, player.getHealth()-1.0));
                        player.damage(0);
                        this.timeLeftDamage = 2;
                    }
                }
                if (this.timeLeft <= 0) {
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.timeLeftDamage--;
            }
        }
    }
    private static class CordeOrPower extends ItemPower {

        protected CordeOrPower(@NonNull RoleBase role) {
            super("Corde d'or", new Cooldown(180), new ItemBuilder(Material.NETHER_STAR).setName("§6Corde d'or"), role,
                    "§7En visant un joueur, le repousse en l'air, puis, lorsqu'il atterrit, l'empêche de bouger pendant§c 5s§7.§7 (1x/3m)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target != null) {
                    new PropulserUtils(player, 30).setNoFall(true).applyPropulsion(target);
                    new TargetFallChecker(getRole().getGameState()).starter(target.getUniqueId());
                    return true;
                }
            }
            return false;
        }
        private static class TargetFallChecker implements Listener {

            private UUID gTarget;
            private boolean stun = false;
            private final GameState gameState;

            private TargetFallChecker(GameState gameState){
                this.gameState = gameState;
                Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
            }
            public void starter(UUID uuid){
                gTarget = uuid;
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (stun)return;
                    if (gTarget != null){
                        if (Bukkit.getPlayer(gTarget) != null){
                            if (!gameState.hasRoleNull(gTarget)){
                                gameState.getGamePlayer().get(gTarget).stun(5*20);
                                stun = true;
                                EventUtils.unregisterEvents(this);
                            }
                        }
                        gTarget = null;
                    }
                }, 100);
            }
            @EventHandler
            private void onDamage(EntityDamageEvent event){
                if (gTarget != null && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity().getUniqueId().equals(gTarget)){
                    event.setDamage(0.0);
                    event.getEntity().setFallDistance(0f);
                    if (gameState.getGamePlayer().containsKey(event.getEntity().getUniqueId())) {
                        gameState.getGamePlayer().get(gTarget).stun(5*20, true);
                        EventUtils.unregisterEvents(this);
                        this.stun = true;
                    }
                }
            }
        }
    }
}