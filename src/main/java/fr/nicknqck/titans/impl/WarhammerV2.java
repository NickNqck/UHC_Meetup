package fr.nicknqck.titans.impl;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanTransformEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.titans.TitanBase;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WarhammerV2 extends TitanBase implements Listener {

    private final List<PotionEffect> potionEffects;
    private Location location;

    public WarhammerV2(GamePlayer gamePlayer) {
        super(gamePlayer);
        this.potionEffects = new ArrayList<>();
        this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false));
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public @NonNull String getParticularites() {
        return "";
    }

    @Override
    public @NonNull String getName() {
        return "WarHammer";
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
    public @NonNull PrepareTitanStealEvent.TitanForm getTitanForm() {
        return PrepareTitanStealEvent.TitanForm.WARHAMMER;
    }
    @EventHandler
    private void onTransformation(@NonNull final TitanTransformEvent event) {
        if (event.getTitan().getGamePlayer().getUuid().equals(this.getGamePlayer().getUuid())) {
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
        }
    }
    @EventHandler
    private void onDeath(@NonNull final UHCDeathEvent event) {
        if (event.isCancelled())return;
        if (!event.getPlayer().getUniqueId().equals(this.getGamePlayer().getUuid()))return;
        if (!isTransformed())return;
        @NonNull final Location location = event.getPlayer().getLocation();
        location.setY(location.getBlockY()-10);
        location.getBlock().setType(Material.IRON_BLOCK);
        event.setCancelled(true);
    }
    private static class DeathRunnable extends BukkitRunnable {



        @Override
        public void run() {

        }
    }
}