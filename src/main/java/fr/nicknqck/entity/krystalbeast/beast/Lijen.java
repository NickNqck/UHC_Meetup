package fr.nicknqck.entity.krystalbeast.beast;

import fr.nicknqck.entity.krystalbeast.rank.EBeast;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Lijen extends EBeast {

    private Zombie zombie;
    private final Location location;

    public Lijen() {
        this.location = getRandomLocation();
    }

    @Override
    public Entity getBeast() {
        return zombie;
    }

    @Override
    public Location getOriginSpawn() {
        return this.location;
    }

    @Override
    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(Material.ROTTEN_FLESH).setName(getName());
    }

    @Override
    public String getName() {
        return "§aLijen";
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        final List<PotionEffect> list = new ArrayList<>();
        list.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        list.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
        list.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
        return list;
    }

    @Override
    public List<ItemStack> getLoots() {
        return this.getDefaultLoot();
    }

    @Override
    public int getMaxKrystalDrop() {
        return 3;
    }

    @Override
    public List<EntityDamageEvent.DamageCause> getImmunisedDamageCause() {
        final List<EntityDamageEvent.DamageCause> list = new ArrayList<>();
        list.add(EntityDamageEvent.DamageCause.FALL);
        list.add(EntityDamageEvent.DamageCause.FIRE_TICK);
        list.add(EntityDamageEvent.DamageCause.FIRE);
        return list;
    }

    @Override
    public boolean spawn() {
        getOriginSpawn().getChunk().load();
        this.zombie = (Zombie) getOriginSpawn().getWorld().spawnEntity(getOriginSpawn(), EntityType.ZOMBIE);
        zombie.setBaby(false);
        zombie.setVillager(false);
        zombie.setCustomName(getName());
        zombie.setCustomNameVisible(true);
        zombie.setMaxHealth(38.0);
        zombie.setHealth(zombie.getMaxHealth());
        for (final PotionEffect potionEffect : getPotionEffects()) {
            zombie.addPotionEffect(potionEffect);
        }
        zombie.setRemoveWhenFarAway(false);
        EntityLiving nmsEntity = ((CraftLivingEntity) zombie).getHandle();
        ((CraftLivingEntity) nmsEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        return true;
    }
}
