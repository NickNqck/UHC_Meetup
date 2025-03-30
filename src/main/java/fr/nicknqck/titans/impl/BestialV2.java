package fr.nicknqck.titans.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanOwnerChangeEvent;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.BestialForm;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BestialV2 extends TitanBase implements Listener {

    private BestialForm bestialForm = BestialForm.UNKNOW;
    private final List<PotionEffect> potionEffects;

    public BestialV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.potionEffects = new ArrayList<>();
        EventUtils.registerRoleEvent(this);
        this.bestialForm = BestialForm.UNKNOW;
        changeForm();
    }

    @Override
    public @NonNull String getParticularites() {
        return "§8 -§7 Votre transformation à une durée de§c 4 minutes§7.\n"+
                " \n"+
                "§8 -§7 Votre transformation possède plusieurs forme différente, celle que vous avez est définie aléatoirement lorsque vous recevez le titan.\n"+
                " \n"+
                "§8 -§7 Vous avez la forme "+this.bestialForm.getName()+"§7 du titan bestial.\n"+
                " \n"+
                "§8 -§7 "+this.bestialForm.getDescriptions();
    }

    @Override
    public @NonNull String getName() {
        if (this.bestialForm == null) {
            return "Bestial";
        }
        return "Bestial§7 ("+this.bestialForm.getName()+"§7)";
    }

    @Override
    public @NonNull Material getTransformationMaterial() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return this.potionEffects;
    }

    @Override
    public int getTransfoDuration() {
        return 60*4;
    }

    @Override
    public @NonNull PrepareTitanStealEvent.TitanForm getTitanForm() {
        return PrepareTitanStealEvent.TitanForm.BESTIAL;
    }
    @EventHandler
    private void TitanOwnerChangeEvent(@NonNull final TitanOwnerChangeEvent event) {
        if (event.isCancelled())return;
        if (event.getOldUUID().equals(getGamePlayer().getUuid()) || event.getNewGamePlayer().getUuid().equals(getGamePlayer().getUuid())) {
            changeForm();
        }
    }
    @EventHandler
    private void TitanTransformEvent(@NonNull final TitanTransformEvent event) {
        if (!event.getTitan().getGamePlayer().getUuid().equals(getGamePlayer().getUuid())) return;
        if (this.bestialForm == null)return;
        if (event.isTransforming()) {
            if (this.bestialForm.equals(BestialForm.SINGE)) {
                this.getGamePlayer().getRole().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth()+6.0);
                event.getPlayer().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth());
                event.getPlayer().setHealth(event.getPlayer().getHealth()+6);
            } else if (this.bestialForm.equals(BestialForm.CROCODILE)) {
                final Player owner = Bukkit.getPlayer(getGamePlayer().getUuid());
                if (owner != null) {
                    @NonNull final ItemStack boots = owner.getInventory().getBoots();
                    if (boots != null) {
                        @NonNull final ItemMeta meta = boots.getItemMeta();
                        meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
                        boots.setItemMeta(meta);
                    }
                }
            }
        } else {
            if (this.bestialForm.equals(BestialForm.SINGE)){
                this.getGamePlayer().getRole().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth()-6.0);
                event.getPlayer().setMaxHealth(this.getGamePlayer().getRole().getMaxHealth());
            } else if (this.bestialForm.equals(BestialForm.CROCODILE)) {
                final Player owner = Bukkit.getPlayer(getGamePlayer().getUuid());
                if (owner != null) {
                    @NonNull final ItemStack boots = owner.getInventory().getBoots();
                    if (boots != null) {
                        @NonNull final ItemMeta meta = boots.getItemMeta();
                        if (meta.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                            meta.removeEnchant(Enchantment.DEPTH_STRIDER);
                        }
                        boots.setItemMeta(meta);
                    }
                }
            }
        }
    }
    @EventHandler
    private void onFallDamage(@NonNull final EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) &&
                event.getEntity().getUniqueId().equals(getGamePlayer().getUuid()) &&
                this.bestialForm != null &&
                this.bestialForm.equals(BestialForm.OISEAU) &&
                isTransformed()) {
            event.setCancelled(true);
        }
    }
    private void changeForm() {
        final int random = Main.RANDOM.nextInt(Math.max(BestialForm.values().length-1, 1));
        this.potionEffects.clear();
        for (@NonNull final BestialForm bestialForm : BestialForm.values()) {
            if (bestialForm.getRandom() == random) {
                this.bestialForm = bestialForm;
                break;
            }
        }
        if (this.bestialForm.equals(BestialForm.TAUREAU)) {
            getGamePlayer().getRole().addPower(new DashPower(this), true);
        }
        if (this.bestialForm.equals(BestialForm.OISEAU)) {
            getGamePlayer().getRole().addPower(new BattementDailePower(this), true);
            getGamePlayer().getRole().addPower(new ChargePower(this), true);
        }
        if (this.bestialForm.equals(BestialForm.OKAPI)) {
            getGamePlayer().getRole().addPower(new LanguePower(this), true);
        }
        getGamePlayer().sendMessage("§7Votre titan bestial à maintenant la forme \""+this.bestialForm.getName()+"§7\"");
        this.potionEffects.addAll(this.bestialForm.getPotionEffects());
        Bukkit.getPlayer(getGamePlayer().getUuid()).performCommand("/aot info");
    }
    private static class DashPower extends ItemPower {

        private final BestialV2 bestialV2;

        protected DashPower(@NonNull final BestialV2 bestialV2) {
            super("Dash (Titan Bestial)", new Cooldown(60*4), new ItemBuilder(Material.QUARTZ).setName("§cDash").addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes(), bestialV2.getGamePlayer().getRole());
            this.bestialV2 = bestialV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.bestialV2.isTransformed()) {
                    player.sendMessage("§cIl faut être transformé pour utiliser ce pouvoir.");
                    return false;
                }
                @NonNull final Vector direction = player.getEyeLocation().getDirection();
                player.setVelocity(direction.multiply(3));
                PotionUtils.addTempNoFall(player.getUniqueId(), 1);
                return true;
            }
            return false;
        }
    }
    private static class BattementDailePower extends ItemPower {

        private final BestialV2 bestialV2;

        protected BattementDailePower(@NonNull BestialV2 bestialV2) {
            super("Battement d'aile", new Cooldown(60*4), new ItemBuilder(Material.FEATHER).setName("§fBattement d'aile"), bestialV2.getTransformationPower().getRole());
            this.bestialV2 = bestialV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!bestialV2.isTransformed()) {
                    player.sendMessage("§cVous ne pouvez utiliser ce pouvoir qu'en tant que Titan");
                    return false;
                }
                player.setAllowFlight(true);
                player.setFlying(true);
                new FlyRunnable(this.getRole().getGameState(), this);
                return true;
            }
            return false;
        }
        private static class FlyRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final BattementDailePower power;
            private int timeLeft;

            private FlyRunnable(GameState gameState, BattementDailePower power) {
                this.gameState = gameState;
                this.power = power;
                this.timeLeft = 10;
                power.bestialV2.getGamePlayer().getActionBarManager().addToActionBar("oiseau.fly", "§bTemp de vol restant:§c "+this.timeLeft+"s");
                runTaskTimer(power.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame) || !this.power.bestialV2.isTransformed() || !this.power.bestialV2.bestialForm.equals(BestialForm.OISEAU) || !this.power.bestialV2.getGamePlayer().isAlive()) {
                    cancel();
                    return;
                }
                power.bestialV2.getGamePlayer().getActionBarManager().updateActionBar("oiseau.fly", "§bTemp de vol restant:§c "+this.timeLeft+"s");
                if (this.timeLeft <= 0) {
                    power.bestialV2.getGamePlayer().getActionBarManager().removeInActionBar("oiseau.fly");
                    @NonNull final Player player = Bukkit.getPlayer(this.power.bestialV2.getGamePlayer().getUuid());
                    if (player == null)return;
                    if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                        cancel();
                        return;
                    }
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    player.sendMessage("§7Vous arrêter de voler...");
                    cancel();
                    return;
                }
                this.timeLeft--;
            }
        }
    }
    private static class ChargePower extends ItemPower {

        private final BestialV2 bestialV2;

        protected ChargePower(@NonNull BestialV2 bestialV2) {
            super("Charge en piqué", new Cooldown(60*4), new ItemBuilder(Material.QUARTZ).setName("§fCharge en piqué"), bestialV2.getTransformationPower().getRole());
            this.bestialV2 = bestialV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.bestialV2.isTransformed()) {
                    player.sendMessage("§cIl faut être transformé en Titan pour utiliser ce pouvoir !");
                    return false;
                }
                @NonNull Vector direction = player.getEyeLocation().getDirection();
                direction.multiply(1.8);
                player.setVelocity(direction);
                @NonNull Location to = direction.toLocation(player.getWorld());
                for (@NonNull Player target : Loc.getNearbyPlayers(to, 10)) {
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    @NonNull Location location = target.getLocation();
                    float nouveauYaw = (location.getYaw() + 180) % 360;
                    location.setYaw(nouveauYaw);
                    @NonNull Vector dir = target.getLocation().getDirection();
                    dir.multiply(1.8);
                    target.setVelocity(dir);
                    target.setHealth(Math.max(1.0, target.getHealth()-3.0));
                }
                PotionUtils.addTempNoFall(player.getUniqueId(), 1);
                return true;
            }
            return false;
        }
    }
    private static class LanguePower extends ItemPower {

        private final BestialV2 bestialV2;

        protected LanguePower(@NonNull BestialV2 bestialV2) {
            super("Langue", new Cooldown(30*3), new ItemBuilder(Material.NETHER_WARTS).setName("§fLangue"), bestialV2.getTransformationPower().getRole());
            this.bestialV2 = bestialV2;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (!this.bestialV2.isTransformed()) {
                    player.sendMessage("§cIl faut être transformé pour utiliser ce pouvoir !");
                    return false;
                }
                @NonNull final Player target = getRole().getTargetPlayer(player, 30);
                if (target != null) {
                    lookAt(player, target);
                    @NonNull Vector direction = target.getEyeLocation().getDirection();
                    direction.setY(0.5);
                    direction.multiply(1.8);
                    target.setVelocity(direction);
                    return true;
                }
            }
            return false;
        }
        private void lookAt(@NonNull Player player,@NonNull Player target) {
            Location loc = player.getLocation();
            Location targetLoc = target.getLocation().clone().add(0, 1.62, 0); // Viser les yeux du joueur

            double dx = targetLoc.getX() - loc.getX();
            double dy = targetLoc.getY() - loc.getY();
            double dz = targetLoc.getZ() - loc.getZ();

            double distanceXZ = Math.sqrt(dx * dx + dz * dz);
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float pitch = (float) Math.toDegrees(-Math.atan2(dy, distanceXZ));

            loc.setYaw(yaw);
            loc.setPitch(pitch);
            player.teleport(loc);
        }
    }
}