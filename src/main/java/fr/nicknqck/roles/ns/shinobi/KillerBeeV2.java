package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.roles.ns.SamehadaUseEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillerBeeV2 extends ShinobiRoles implements Listener {

    private HashibiPower hashibiPower;

    public KillerBeeV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Killer Bee";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.KillerBee;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addKnowedRole(KillerBee.class);
        addKnowedRole(RaikageV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        setChakraType(Chakras.RAITON);
        EventUtils.registerRoleEvent(this);
        this.hashibiPower = new HashibiPower(this);
        addPower(this.hashibiPower, true);
        addPower(new TentaculesPower(this), true);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this).addCustomLine("§7Vous êtes immunisé aux effets de§c Samehada§7.").getText();
    }
    @EventHandler
    private void onKisameTap(final SamehadaUseEvent event) {
        if (event.getTarget().getUniqueId().equals(getPlayer())){
            event.setCancelled(true);
        }
    }
    private static class HashibiPower extends ItemPower {

        public HashibiPower(@NonNull RoleBase role) {
            super("Gyuki", new Cooldown(60*15), new ItemBuilder(Material.NETHER_STAR).setName("§aGyuki"), role
            ,"§7Vous donne§b Speed I§7 pendant§c 5 minutes§7 ainsi que§c 2❤ supplémentaires§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false), EffectWhen.NOW);
                player.sendMessage("§7Vous commencez à recevoir le§a chakra§7 de§a Gyuki§7.");
                getRole().setMaxHealth(getRole().getMaxHealth()+4.0);
                player.setMaxHealth(getRole().getMaxHealth());
                player.setHealth(player.getHealth()+4.0);
                new GyukiRunnable(this);
                return true;
            }
            return false;
        }
        private static class GyukiRunnable extends BukkitRunnable {

            private final HashibiPower hashibiPower;
            private int timeLeft;

            private GyukiRunnable(HashibiPower hashibiPower) {
                this.hashibiPower = hashibiPower;
                this.timeLeft = 60*5;
                this.hashibiPower.getRole().getGamePlayer().getActionBarManager().addToActionBar("killerbeev2.gyukitimer", "§bTemp restant (§aGyuki§b): §c"+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame) || this.timeLeft <= 0) {
                    this.hashibiPower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("killerbeev2.gyukitimer");
                    cancel();
                    return;
                }
                this.timeLeft--;
                final Player owner = Bukkit.getPlayer(this.hashibiPower.getRole().getPlayer());
                if (owner == null)return;
                this.hashibiPower.getRole().getGamePlayer().getActionBarManager().updateActionBar("killerbeev2.gyukitimer", "§bTemp restant (§aGyuki§b): §c"+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), true));
            }
        }
    }
    private static class TentaculesPower extends ItemPower implements Listener {

        private final KillerBeeV2 killerBeeV2;

        public TentaculesPower(@NonNull KillerBeeV2 role) {
            super("Tentacules", new Cooldown(15), new ItemBuilder(Material.FISHING_ROD).setName("§aTentacules"), role,
                    "§7Tant que vous êtes sous l'effet de§a Gyuki§7, vous pouvez utiliser cette objet pour vous propulser");
            this.killerBeeV2 = role;
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.containsKey("force")) {
                if (map.get("force") instanceof Boolean) {
                    return (boolean) map.get("force");
                }
            }
            return false;
        }
        @EventHandler
        public void onProjectile(ProjectileLaunchEvent event) {
            if (event.getEntity() == null || !(event.getEntity() instanceof FishHook) || event
                    .getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player)) {
                return;
            }

            FishHook hook = (FishHook)event.getEntity();
            if (hook.isOnGround()) {
                event.setCancelled(true);
            }
            final Player player = (Player) event.getEntity().getShooter();
            if (!player.getUniqueId().equals(getRole().getPlayer()))return;//donc le lanceur est mon KillerBee
            if (this.killerBeeV2.hashibiPower == null)return;
            if (!this.killerBeeV2.hashibiPower.getCooldown().isInCooldown()) {
                player.sendMessage("§cIl faut être sous l'effet de§a Gyuki§c pour utiliser ce pouvoir !");
                event.setCancelled(true);
                return;
            }
            if (!(this.killerBeeV2.hashibiPower.getCooldown().getCooldownRemaining() >= this.killerBeeV2.hashibiPower.getCooldown().getOriginalCooldown()-(60*5))) {
                player.sendMessage("§cIl faut être sous l'effet de§a Gyuki§c pour utiliser ce pouvoir !");
                event.setCancelled(true);
                return;
            }
            final Map<String, Object> map = new HashMap<>();
            map.put("force", true);
            if (checkUse(player, map)){
                hook.setVelocity(hook.getVelocity().multiply(1.5));
                new LaunchFishHook(hook, player).runTaskTimer(Main.getInstance(), 1, 1);
            } else {
                event.setCancelled(true);
            }
        }
        public class LaunchFishHook extends BukkitRunnable {
            private final Player player;
            private final FishHook fishHook;
            public LaunchFishHook(FishHook fishHook, Player player) {
                this.fishHook = fishHook;
                this.player = player;
            }

            public void run() {
                if (this.player == null || !this.player.isOnline() || this.fishHook.isDead()) {
                    cancel();
                    return;
                }
                double velocityX = Math.abs(this.fishHook.getVelocity().getX());
                double velocityY = Math.abs(this.fishHook.getVelocity().getY());
                double velocityZ = Math.abs(this.fishHook.getVelocity().getZ());
                if (velocityX >= 0.1D || velocityY >= 0.1D || velocityZ >= 0.1D) {
                    return;
                }
                cancel();
                Location loc = this.fishHook.getLocation();
                PotionUtils.effetGiveNofall(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> PotionUtils.effetRemoveNofall(player), 20*5);
                (new AttractFishHook(this.fishHook, this.player, loc)).runTaskTimer(Main.getInstance(), 0L, 1L);
            }
        }
        public class AttractFishHook extends BukkitRunnable {
            private final Player player;
            private final FishHook fishHook;
            private final Location loc;
            public AttractFishHook(FishHook fishHook, Player player, Location location) {
                this.fishHook = fishHook;
                this.player = player;
                this.loc = location;
            }
            public void run() {
                if (this.player == null || !this.player.isOnline() || this.player.getWorld() != this.fishHook.getWorld()) {
                    cancel();
                    return;
                }
                if (GameState.getInstance().hasRoleNull(player.getUniqueId())) {
                    cancel();
                    return;
                }
                this.fishHook.setVelocity(new Vector(0, 0, 0));
                double vectorX = this.loc.getX() - this.player.getLocation().getX();
                double vectorY = this.loc.getY() - this.player.getLocation().getY();
                double vectorZ = this.loc.getZ() - this.player.getLocation().getZ();
                Vector v = (new Vector(vectorX, vectorY, vectorZ)).add(new Vector(0, 3, 0)).multiply(0.02D);
                if (this.player.getLocation().distance(this.fishHook.getLocation()) > 10.0D) {
                    v.multiply(0.9D);
                }
                this.player.setVelocity(this.player.getVelocity().add(v));
                if (!this.fishHook.isDead() && this.player.getLocation().distance(this.fishHook.getLocation()) >= 3.0D) {
                    return;
                }
                Vector current = this.player.getVelocity();
                if (!getItem().isSimilar(this.player.getItemInHand())) {
                    current.multiply(0.3D);
                    current.setY(0.5D);
                } else {
                    current.setY(0.75D);
                }
                this.player.setVelocity(current);
                cancel();
                this.player.sendMessage("§7Vos§a tentacules§7 vous propulse !");
            }
        }
    }
}