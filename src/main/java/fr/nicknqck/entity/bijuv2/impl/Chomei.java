package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chomei extends BijuBase {

    private Ghast ghast;
    private final Location initSpawn;
    private final List<PotionEffect> potionEffectList;

    public Chomei() {
        this.initSpawn = getRandomLocation();
        this.potionEffectList = new ArrayList<>();
        this.potionEffectList.add(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "§aChomei";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(Material.INK_SACK).setName(getName()).setDurability(2).toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.ghast = (Ghast) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.GHAST);
        ghast.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        ghast.setCustomName(getName());
        ghast.setCustomNameVisible(true);
        ghast.setMaxHealth(50);
        ghast.setHealth(ghast.getMaxHealth());
        ghast.setRemoveWhenFarAway(false);
        final EntityLiving nmsEntity = ((CraftLivingEntity) ghast).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    @Override
    public Entity getEntity() {
        return this.ghast;
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.NETHER_STAR;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[] {
                "§7À son activation vous confère les pouvoirs de "+this.getName()+"§7 pendant §c5 minutes §7:",
                "§8 - §eSpeed II §7pendant§c 5 minutes§7 et un§a fly§7 d'une durée de§c 10 secondes"
        };
    }

    @Override
    public @NonNull Location getOriginSpawn() {
        return this.initSpawn;
    }

    @Override
    public @NonNull List<PotionEffect> getEffectsWhenUse() {
        return this.potionEffectList;
    }

    @Override
    public void onUse(@NonNull Player player, @NonNull RoleBase role) {
        new FlyRunnable(player, role.getGamePlayer());
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {

    }
    private static class FlyRunnable extends BukkitRunnable {

        private final UUID uuid;
        private final GamePlayer gamePlayer;
        private int timeLeft = 10;

        private FlyRunnable(final Player player, final GamePlayer gamePlayer) {
            this.uuid = player.getUniqueId();
            this.gamePlayer = gamePlayer;
            gamePlayer.getActionBarManager().addToActionBar("chomei.fly", "§bTemp de fly restant: §c10 secondes");
            player.setAllowFlight(true);
            player.setFlying(true);
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!gamePlayer.isAlive()) {
                this.gamePlayer.getActionBarManager().removeInActionBar("chomei.fly");
                cancel();
                return;
            }
            if (this.timeLeft <= 0) {
                this.gamePlayer.getActionBarManager().removeInActionBar("chomei.fly");
                final Player player = Bukkit.getPlayer(this.uuid);
                if (player != null){
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    cancel();
                }
                return;
            }
            gamePlayer.getActionBarManager().updateActionBar("chomei.fly", "§bTemp de fly restant: §c"+this.timeLeft+" secondes");
            this.timeLeft--;
        }
    }
}