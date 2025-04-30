package fr.nicknqck.entity.bijuv2.impl;

import fr.nicknqck.entity.bijus.HorseInvoker;
import fr.nicknqck.entity.bijuv2.BijuBase;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
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

public class Kokuo extends BijuBase {

    private final Location location;
    private Horse horse;
    private final List<PotionEffect> potionEffects;

    public Kokuo() {
        this.location = getRandomLocation();
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false));
    }

    @Override
    public @NonNull String getName() {
        return "Kokuo";
    }

    @Override
    public @NonNull ItemStack getItemInMenu() {
        return new ItemBuilder(Material.BONE).setName(getName()).toItemStack();
    }

    @Override
    public void spawn() {
        getOriginSpawn().getChunk().load();
        this.horse = HorseInvoker.invokeKokuo(horse, this.getOriginSpawn(), this.getName());
    }

    @Override
    public Entity getEntity() {
        return this.horse;
    }

    @Override
    public @NonNull Material getItemMaterial() {
        return Material.BONE;
    }

    @Override
    public @NonNull String[] getItemDescription() {
        return new String[0];
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
        @NonNull final HorsePower horsePower = new HorsePower(role);
        changeOriginalPower(horsePower);
        role.addPower(horsePower);
    }

    @Override
    public void onEnd(@NonNull RoleBase role) {
        if (getBijuOriginalPower() != null) {
            if (getBijuOriginalPower() instanceof HorsePower) {
                EventUtils.unregisterEvents(((HorsePower)getBijuOriginalPower()));
            }
        }
    }
    private static class HorsePower extends Power implements Listener {

        public HorsePower(@NonNull RoleBase role) {
            super("Kokuo", new Cooldown(30), role);
            EventUtils.registerRoleEvent(this);
            setSendCooldown(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onEntityDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                if (!(event.getEntity() instanceof Player))return;
                if (!checkUse((Player) event.getDamager(), new HashMap<>()))return;
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false));
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*10, 0, false, false));
            }
        }
    }
}