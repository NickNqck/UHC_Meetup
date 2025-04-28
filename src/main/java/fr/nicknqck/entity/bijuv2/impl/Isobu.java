package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.Main;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Isobu extends BijuBase {

    private final List<PotionEffect> potionEffectList;
    private final Location randomLoc;
    private Guardian isobu;

    public Isobu() {
        this.potionEffectList = new ArrayList<>();
        this.randomLoc = getRandomLocation();
        this.potionEffectList.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "§eIsobu";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(Material.INK_SACK).setName(getName())
                .addEnchant(Enchantment.ARROW_INFINITE, 1).hideEnchantAttributes().toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.isobu = (Guardian) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.GUARDIAN);
        isobu.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        isobu.setCustomName(getName());
        isobu.setCustomNameVisible(true);
        isobu.setMaxHealth(100);
        isobu.setElder(true);
        isobu.setHealth(isobu.getMaxHealth());
        isobu.setRemoveWhenFarAway(false);
        EntityLiving nmsEntity = ((CraftLivingEntity) isobu).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    @Override
    public Entity getEntity() {
        return this.isobu;
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.INK_SACK;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[] {
                "§7À son activation vous confère les pouvoirs de "+this.getName()+"§7 pendant §c5 minutes §7:",
                "§8 - §9Résistance 1 §7 et §c2 coeurs supplémentaire§7 pendant §c5 minutes",
                "§8 - §7En prenant un coup vous avez§c 10%§7 de§c chance§7 d'§cesquiver le coup."
        };
    }

    @Override
    public @NonNull Location getOriginSpawn() {
        return this.randomLoc;
    }

    @Override
    public @NonNull List<PotionEffect> getEffectsWhenUse() {
        return this.potionEffectList;
    }

    @Override
    public void onUse(@NonNull Player player, @NonNull RoleBase role) {
        role.setMaxHealth(role.getMaxHealth()+4.0);
        player.setMaxHealth(role.getMaxHealth());
        player.setHealth(player.getHealth()+4.0);
        EsquivePower esquivePower = new EsquivePower(role);
        role.addPower(esquivePower);
        changeOriginalPower(esquivePower);
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {
        role.setMaxHealth(role.getMaxHealth()-4.0);
        final Player player = Bukkit.getPlayer(role.getPlayer());
        if (player != null) {
            player.setMaxHealth(role.getMaxHealth());   
        }
        if (getBijuOriginalPower() != null) {
            if (getBijuOriginalPower() instanceof EsquivePower) {
                ((EsquivePower) getBijuOriginalPower()).end();
            }
        }
    }
    private static class EsquivePower extends Power implements Listener {

        public EsquivePower(@NonNull RoleBase role) {
            super("Isobu", null, role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                if (!(event.getEntity() instanceof Player))return;
                if (Main.RANDOM.nextInt(100) <= 10) {
                    if (!checkUse((Player) event.getEntity(), new HashMap<>()))return;
                    ((Player)event.getEntity()).setNoDamageTicks(10);
                }
            }
        }
        private void end() {
            EventUtils.unregisterEvents(this);
        }
    }
}
