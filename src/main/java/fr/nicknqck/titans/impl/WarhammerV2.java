package fr.nicknqck.titans.impl;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.events.custom.roles.aot.WarHammerBlockBreakEvent;
import fr.nicknqck.events.custom.time.OnSecond;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.mahr.LaraV2;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.particles.MathUtil;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WarhammerV2 extends TitanBase implements Listener {

    private final List<PotionEffect> potionEffects;
    private Location location;
    private Location blocDeathLocation;
    private Player lastDamager;
    private boolean canRes = true;

    public WarhammerV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation à une durée de§c 3 minutes§7.\n"+
                "§8 -§7 Lorsque vous êtes transformé en titan vous possédez l'effet §cForce I§7 ainsi que §c3❤ supplémentaires§7, de plus, un fil de particule vous liera à la ou vous vous êtes transformé.\n" +
                "§8 -§7 A votre transformation §c10 blocs§7 en dessous de vous, un §fbloc de fer§7 est posé, s'il est cassé vous serez transformer en humain et perdrez §c1❤ permanent§7.\n" +
                "§8 -§7 Tant que vous êtes transformé en titan, à votre mort vous ferez apparaitre un§f bloc de fer§7 sera posé§c 10 blocs§7 en dessous de vous, s'il n'est pas cassé dans les§c 30 secondes§7, §cvous réssusciterez§7.";
    }

    @Override
    public @NonNull String getName() {
        return "§9WarHammer";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.FEATHER;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.potionEffects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*3;
    }

    @Override
    public @NonNull TitanForm getTitanForm() {
        return TitanForm.WARHAMMER;
    }
    @EventHandler
    private void onTransformation(@NonNull final TitanTransformEvent event) {
        if (event.getTitan().getGamePlayer().getUuid().equals(this.getGamePlayer().getUuid())) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                if (event.isTransforming()) {
                    this.getGamePlayer().getRole().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth()+6.0);
                    event.getPlayer().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth());
                    event.getPlayer().setHealth(Math.min(event.getPlayer().getMaxHealth(), event.getPlayer().getHealth()+6.0));
                    @NonNull final Location location = event.getPlayer().getLocation();
                    location.setY(location.getBlockY()-10);
                    location.getBlock().setType(Material.IRON_BLOCK);
                    this.location = location;
                } else {
                    this.getGamePlayer().getRole().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth()-6.0);
                    event.getPlayer().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth());
                    this.location.getBlock().setType(Material.DIRT);
                }
            });
        }
    }
    @EventHandler
    private void onDeath(@NonNull final UHCDeathEvent event) {
        if (event.isCancelled())return;
        if (!event.getPlayer().getUniqueId().equals(this.getGamePlayer().getUuid()))return;
        final RoleBase role = this.getGamePlayer().getRole();
        if (!(role instanceof LaraV2)){
            if (!isTransformed())return;
        }
        if (!this.canRes)return;
        if (role.getMaxHealth() <= 10.0)return;
        @NonNull final Location location = event.getPlayer().getLocation();
        this.getGamePlayer().setDeathLocation(location);
        location.setY(location.getBlockY()-10);
        location.getBlock().setType(Material.IRON_BLOCK);
        event.setCancelled(true);
        final World world = Main.getInstance().getWorldManager().getGameWorld();
        poseCocon(world);
        poseProtectionCocon(world);
        @NonNull final Location tp = new Location(world, 0.5, 2.0, 0.5, 0, 0);
        this.getGamePlayer().setLastLocation(tp);
        final Player owner = Bukkit.getPlayer(this.getGamePlayer().getUuid());
        owner.teleport(tp);
        owner.setGameMode(GameMode.ADVENTURE);
        this.getGamePlayer().setAlive(false);
        this.blocDeathLocation = location;
        new DeathRunnable(GameState.getInstance(), this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }
    @EventHandler
    private void EntityDamageByEntityEvent(@NonNull final EntityDamageByEntityEvent event) {
        if (!event.getEntity().getUniqueId().equals(this.getGamePlayer().getUuid()))return;
        if (!(event.getDamager() instanceof Player))return;
        this.lastDamager = (Player) event.getDamager();
    }
    private void poseCocon(@NonNull final World world) {
        world.getBlockAt(0, 1, 0).setType(Material.IRON_BLOCK);
        world.getBlockAt(0, 4, 0).setType(Material.IRON_BLOCK);

        world.getBlockAt(0, 3, -1).setType(Material.IRON_BLOCK);
        world.getBlockAt(0, 2, -1).setType(Material.IRON_BLOCK);

        world.getBlockAt(-1, 3, 0).setType(Material.IRON_BLOCK);
        world.getBlockAt(-1, 2, 0).setType(Material.IRON_BLOCK);

        world.getBlockAt(0, 3, 1).setType(Material.IRON_BLOCK);
        world.getBlockAt(0, 2, 1).setType(Material.IRON_BLOCK);

        world.getBlockAt(1, 3, 0).setType(Material.IRON_BLOCK);
        world.getBlockAt(1, 2, 0).setType(Material.IRON_BLOCK);
    }
    private void poseProtectionCocon(@NonNull final World world) {
        world.getBlockAt(0, 0, 0).setType(Material.BEDROCK);
        world.getBlockAt(0, 5, 0).setType(Material.BEDROCK);

        world.getBlockAt(0, 4, 1).setType(Material.BEDROCK);
        world.getBlockAt(-1, 4, 0).setType(Material.BEDROCK);
        world.getBlockAt(0, 4, -1).setType(Material.BEDROCK);
        world.getBlockAt(1, 4, 0).setType(Material.BEDROCK);

        world.getBlockAt(1, 2, 1).setType(Material.BEDROCK);
        world.getBlockAt(1, 3, 1).setType(Material.BEDROCK);
        world.getBlockAt(0, 2, 2).setType(Material.BEDROCK);
        world.getBlockAt(0, 3, 2).setType(Material.BEDROCK);
        world.getBlockAt(2, 2, 0).setType(Material.BEDROCK);
        world.getBlockAt(2, 3, 0).setType(Material.BEDROCK);
        world.getBlockAt(1, 2, -1).setType(Material.BEDROCK);
        world.getBlockAt(1, 3, -1).setType(Material.BEDROCK);
        world.getBlockAt(0, 2, -2).setType(Material.BEDROCK);
        world.getBlockAt(0, 3, -2).setType(Material.BEDROCK);
        world.getBlockAt(-1, 2, -1).setType(Material.BEDROCK);
        world.getBlockAt(-1, 3, -1).setType(Material.BEDROCK);
        world.getBlockAt(-2, 2, 0).setType(Material.BEDROCK);
        world.getBlockAt(-2, 3, 0).setType(Material.BEDROCK);
        world.getBlockAt(-1, 2, 1).setType(Material.BEDROCK);
        world.getBlockAt(-1, 3, 1).setType(Material.BEDROCK);

        world.getBlockAt(0, 1, 1).setType(Material.BEDROCK);
        world.getBlockAt(-1, 1, 0).setType(Material.BEDROCK);
        world.getBlockAt(0, 1, -1).setType(Material.BEDROCK);
        world.getBlockAt(1, 1, 0).setType(Material.BEDROCK);
    }
    @EventHandler
    private void onSecond(@NonNull final OnSecond onSecond) {
        if (!onSecond.isInGame())return;
        if (this.location == null)return;
        if (!isTransformed())return;
        final Location location = new Location(
                this.location.getWorld(),
                this.location.getX(),
                this.location.getY()+10,
                this.location.getZ()
        );
        MathUtil.sendParticleLine(
                location,
                this.getGamePlayer().getLastLocation(),
                EnumParticle.CLOUD,
                (int) location.distance(this.getGamePlayer().getLastLocation())
        );
        MathUtil.sendParticleLine(
                location,
                this.location,
                EnumParticle.PORTAL,
                (int) location.distance(this.location)
        );
    }
    @EventHandler
    private void onBlockBreak(@NonNull final BlockBreakEvent event) {
        if (event.getBlock() == null)return;
        if (event.getBlock().getType() != Material.IRON_BLOCK)return;
        if (this.location != null) {
            if (isTransformed()) {
                if (event.getBlock().getLocation().distance(this.location) <= 1.0) {
                    this.getTransformationPower().stopTransformation(Bukkit.getPlayer(this.getGamePlayer().getUuid()));
                    this.getGamePlayer().sendMessage("§b"+event.getPlayer().getDisplayName()+"§c a cassé votre bloc de transformation");
                    this.getGamePlayer().getRole().setMaxHealth(Math.max(2.0, this.getGamePlayer().getRole().getMaxHealth()-2.0));
                    Bukkit.getPluginManager().callEvent(new WarHammerBlockBreakEvent(event.getBlock(), event.getPlayer(), this));
                }
            }
        }
        if (this.blocDeathLocation != null) {
            if (!this.blocDeathLocation.getWorld().equals(event.getBlock().getWorld()))return;
            if (event.getBlock().getLocation().distance(this.blocDeathLocation) <= 2.0) {
                this.blocDeathLocation = null;
            }
        }
    }
    @EventHandler
    private void onDamage(final EntityDamageEvent event) {
        if (event.isCancelled())return;
        if (event.getEntity().getUniqueId().equals(getGamePlayer().getUuid())) {
            if (!event.getEntity().getWorld().getName().equalsIgnoreCase("arena")) {
                event.setDamage(0.0);
            }
        }
    }
    private static class DeathRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final WarhammerV2 warhammer;
        private final Location deathLocation;
        private int timeRemaining;

        private DeathRunnable(GameState gameState, WarhammerV2 warhammer) {
            this.gameState = gameState;
            this.warhammer = warhammer;
            this.deathLocation = this.warhammer.blocDeathLocation;
            this.timeRemaining = 30;
            this.warhammer.getGamePlayer().getActionBarManager().addToActionBar("warhammerv2.timedeath", "§bTemp avant réapparition: §c30 secondes");
        }


        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.warhammer.blocDeathLocation == null) {
                this.warhammer.getGamePlayer().sendMessage("§7Quelqu'un a détruit votre point de réaparition, vous allez donc mourir...");
                this.warhammer.getGamePlayer().getActionBarManager().removeInActionBar("warhammerv2.timedeath");
                Player warhammer = Bukkit.getPlayer(this.warhammer.getGamePlayer().getUuid());
                if (warhammer != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        warhammer.teleport(this.warhammer.getGamePlayer().getDeathLocation());
                        for (ItemStack item : this.warhammer.getGamePlayer().getLastInventoryContent()) {
                            if (item == null)continue;
                            if (item.getType().equals(Material.AIR))continue;
                            warhammer.getInventory().addItem(item);
                        }
                        warhammer.getInventory().setArmorContents(this.warhammer.getGamePlayer().getLastArmorContent());
                        warhammer.damage(9999, this.warhammer.lastDamager);
                    });
                }
                this.warhammer.canRes = false;
                cancel();
                return;
            }
            if (this.timeRemaining <= 0) {
                final Player player = Bukkit.getPlayer(this.warhammer.getGamePlayer().getUuid());
                if (player != null) {
                    this.warhammer.getGamePlayer().getActionBarManager().removeInActionBar("warhammerv2.timedeath");
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.teleport(this.getRandomLocation());
                        player.sendMessage("§7Votre pouvoir de§c War Hammer§7 vous à permis de ressusciter");
                        this.warhammer.getGamePlayer().getRole().setMaxHealth(this.warhammer.getGamePlayer().getRole().getMaxHealth()-2.0);
                        for (ItemStack item : this.warhammer.getGamePlayer().getLastInventoryContent()) {
                            if (item == null)continue;
                            if (item.getType().equals(Material.AIR))continue;
                            player.getInventory().addItem(item);
                        }
                        player.getInventory().setArmorContents(this.warhammer.getGamePlayer().getLastArmorContent());
                    });
                    this.warhammer.getGamePlayer().setAlive(true);
                    cancel();
                    return;
                }
            }
            this.warhammer.getGamePlayer().getActionBarManager().updateActionBar("warhammerv2.timedeath", "§bTemp avant réapparition: §c"+this.timeRemaining+" secondes");
            Location loc = new Location(
                    this.deathLocation.getWorld(),
                    this.deathLocation.getBlockX(),
                    this.deathLocation.getY()+15,
                    this.deathLocation.getBlockZ()
            );
            MathUtil.sendParticleLine(loc, this.warhammer.blocDeathLocation, EnumParticle.PORTAL, (int) this.deathLocation.distance(this.warhammer.blocDeathLocation));
            this.timeRemaining--;
        }
        private Location getRandomLocation() {
            Location location = null;
            int essaie = 0;
            while (location == null && essaie <= 1000) {
                essaie++;
                location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                        Main.RANDOM.nextInt(Border.getMaxBorderSize()),
                        60,
                        Main.RANDOM.nextInt(Border.getMaxBorderSize()));
                if (Main.RANDOM.nextInt(100) <= 50) {
                    location.setX(-location.getBlockX());
                }
                if (Main.RANDOM.nextInt(100) <= 50) {
                    location.setZ(-location.getBlockZ());
                }
                if (location.getBlockX() >= (Border.getMaxBorderSize()-10)) {
                    location = null;
                    continue;
                }
                if (location.getBlockX() <= (-Border.getMaxBorderSize()+10)) {
                    location = null;
                    continue;
                }
                if (location.getBlockZ() >= (Border.getMaxBorderSize()-10)) {
                    location = null;
                    continue;
                }
                if (location.getBlockZ() <= (-Border.getMinBorderSize()+10)) {
                    location = null;
                    continue;
                }
                if (location.getBlockX() <= Border.getMinBorderSize()) {
                    location = null;
                    continue;
                }
                if (location.getBlockZ() <= Border.getMinBorderSize()) {
                    location = null;
                }
            }
            if (location == null) {//location par défaut x:100 z:100
                location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                        100,
                        0,
                        100);
            }
            location.setY(location.getWorld().getHighestBlockYAt(location)+1);
            return location;
        }
    }
}