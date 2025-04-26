package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Matatabi extends BijuBase {

    private final List<PotionEffect> potionEffects;
    private final Location initLoc;
    private Blaze matatabi;

    public Matatabi() {
        this.potionEffects = new ArrayList<>();
        this.initLoc = getRandomLocation();
        this.potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 0, false, false));
        this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "§6Matatabi";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(Material.BLAZE_ROD).setName(getName())
                .addEnchant(Enchantment.ARROW_INFINITE, 1)
                .hideEnchantAttributes()
                .toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.matatabi = (Blaze) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.BLAZE);
        matatabi.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        matatabi.setCustomName(getName());
        matatabi.setCustomNameVisible(true);
        matatabi.setMaxHealth(100);
        matatabi.setHealth(matatabi.getMaxHealth());
        matatabi.setRemoveWhenFarAway(false);
        EntityLiving nmsEntity = ((CraftLivingEntity) matatabi).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    @Override
    public Entity getEntity() {
        return this.matatabi;
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[] {
                "§7À son activation vous confère les pouvoirs de "+this.getName()+"§7 pendant §c5 minutes §7:",
                "§8 - §cForce 1§7 et §6Fire Résistance 1 §7pendant §c5 minutes",
                "§8 - §7En frappant un joueur vous le mettrez en§6 feu§7."
        };
    }

    @Override
    public @NonNull Location getOriginSpawn() {
        return this.initLoc;
    }

    @Override
    public @NonNull List<PotionEffect> getEffectsWhenUse() {
        return this.potionEffects;
    }

    @Override
    public void onUse(@NonNull Player player, @NonNull RoleBase role) {
        FirePower power = new FirePower(role);
        this.changeOriginalPower(power);
        role.addPower(power);
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {
        if (this.getBijuOriginalPower() != null) {
            if (this.getBijuOriginalPower() instanceof FirePower) {
                ((FirePower) this.getBijuOriginalPower()).end();
            }
        }
    }
    private static class FirePower extends Power implements Listener {

        public FirePower(@NonNull RoleBase role) {
            super("Matatabi", null, role);
            EventUtils.registerRoleEvent(this);
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onAttack(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                if (!checkUse((Player) event.getDamager(), new HashMap<>()))return;
                event.getEntity().setFireTicks(100);
            }
        }
        private void end() {
            EventUtils.unregisterEvents(this);
        }
    }
}