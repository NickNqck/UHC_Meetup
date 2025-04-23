package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.WorldUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Saiken extends BijuBase {

    private Slime slime;
    private final Location location;
    private final List<PotionEffect> potionEffects;

    public Saiken() {
        this.location = getRandomLocation();
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false));
        this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "§5Saiken";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(new ItemStack(Material.SLIME_BALL)).addEnchant(Enchantment.DURABILITY, 1).hideEnchantAttributes().setName(getName()).toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.slime = (Slime) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.SLIME);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        slime.setCustomName(getName());
        slime.setCustomNameVisible(true);
        slime.setMaxHealth(200);
        slime.setSize(8);
        slime.setHealth(slime.getMaxHealth());
        slime.setRemoveWhenFarAway(false);
        EntityLiving nmsEntity = ((CraftLivingEntity) slime).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    @Override
    public @NonNull Location getOriginSpawn() {
        return this.location;
    }

    @Override
    public @NonNull List<PotionEffect> getEffectsWhenUse() {
        return this.potionEffects;
    }

    @Override
    public void onUse(@NonNull Player player, @NonNull RoleBase role) {
        @NonNull final SaikenPower saikenPower = new SaikenPower(role);
        this.changeOriginalPower(saikenPower);
        role.addPower(saikenPower);
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {
        if (this.getBijuOriginalPower() != null) {
            if (this.getBijuOriginalPower() instanceof SaikenPower){
                ((SaikenPower) this.getBijuOriginalPower()).end();
            }
        }
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.SLIME_BALL;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[] {
                "§7À son activation vous confère les pouvoirs de §5Saiken pendant §c5 minutes §7:",
                "§8 - §cForce 1 et §eSpeed 1 §7pendant §c5 minutes",
                "§8 - §7En frappant un joueur vous aurez §c5%§7 de chance de provoquer une §6explosion §7qui infligera §c1❤ de dégât§7.",
        };
    }

    @Override
    public Entity getEntity() {
        return this.slime;
    }

    private static class SaikenPower extends Power implements Listener {

        public SaikenPower(@NonNull RoleBase role) {
            super("Saiken", new Cooldown(15), role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void EntityDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(this.getRole().getPlayer()))return;
            if (Main.RANDOM.nextInt(101) <= 5) {
                if (!this.checkUse((Player) event.getDamager(), new HashMap<>()))return;
                WorldUtils.createBeautyExplosion(event.getEntity().getLocation(), 2, false);
                MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, event.getEntity().getLocation());
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1, false, false), true);
                ((Player) event.getEntity()).setHealth(Math.max(1.0, ((Player) event.getEntity()).getHealth()-2.0));
            }
        }

        public void end() {
            EventUtils.unregisterEvents(this);
        }
    }
}