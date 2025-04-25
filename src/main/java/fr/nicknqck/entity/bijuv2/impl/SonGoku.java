package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.Loc;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SonGoku extends BijuBase {

    private MagmaCube magmaCube;
    private final Location location;
    private final List<PotionEffect> potionEffects;

    public SonGoku() {
        this.location = getRandomLocation();
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false));
        this.potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 0, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "§cSon Gokû";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(Material.MAGMA_CREAM)
                .addEnchant(Enchantment.DURABILITY, 1)
                .hideEnchantAttributes()
                .setName(getName())
                .toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.magmaCube = (MagmaCube) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.MAGMA_CUBE);
        magmaCube.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        magmaCube.setCustomName(getName());
        magmaCube.setCustomNameVisible(true);
        magmaCube.setSize(8);
        magmaCube.setMaxHealth(20);
        magmaCube.setHealth(magmaCube.getMaxHealth());
        magmaCube.setRemoveWhenFarAway(false);
        EntityLiving nmsEntity = ((CraftLivingEntity) magmaCube).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
    }

    @Override
    public Entity getEntity() {
        return this.magmaCube;
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.MAGMA_CREAM;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[] {
                "§7À son activation vous offre les pouvoirs de "+this.getName()+"§7:",
                "§8 -§7 §bSpeed I§7 et§6 Fire Résistance I§7 pendant §c5 minutes",
                "§8 -§7 Quand vous prenez des dégâts de chute, ils seront infligé à la place au joueurs proche de vous"
        };
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
        @NonNull final SonGoku.FallDamage songokuPower = new SonGoku.FallDamage(role);
        this.changeOriginalPower(songokuPower);
        role.addPower(songokuPower);
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {
        if (this.getBijuOriginalPower() != null) {
            if (this.getBijuOriginalPower() instanceof FallDamage){
                ((SonGoku.FallDamage) this.getBijuOriginalPower()).end();
            }
        }
    }
    private static class FallDamage extends Power implements Listener {

        public FallDamage(@NonNull RoleBase role) {
            super("§cSon Gokû", null, role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onFallDamage(@NonNull final EntityDamageEvent event) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                if (!event.getEntity().getUniqueId().equals(this.getRole().getPlayer()))return;
                final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(event.getEntity(), 30));
                if (playerList.isEmpty())return;
                for (final Player player : playerList) {
                    player.damage(event.getDamage());
                }
                event.setCancelled(true);
            }
        }
        public void end() {
            EventUtils.unregisterEvents(this);
        }
    }
}