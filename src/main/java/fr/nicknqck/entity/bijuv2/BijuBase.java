package fr.nicknqck.entity.bijuv2;

import fr.nicknqck.Border;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.biju.BijuCheckSpawnEvent;
import fr.nicknqck.events.custom.biju.BijuSpawnEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class BijuBase implements IBiju {

    private int minTimeProc = 60;
    private int maxTimeProc = 60*5;
    @Getter
    private BijuPower bijuPower;
    @Setter
    private GamePlayer gamePlayer;
    private Power power = null;

    public BijuBase() {
    }

    @Override
    public int getMinTimeProc() {
        return minTimeProc;
    }

    @Override
    public void setMinTimeProc(int i) {
        this.minTimeProc = i;
    }

    @Override
    public int getMaxTimeProc() {
        return maxTimeProc;
    }

    @Override
    public void setMaxTimeProc(int maxTimeProc) {
        this.maxTimeProc = maxTimeProc;
    }
    public Location getRandomLocation() {
        Location location = null;
        int essaie = 0;
        while (location == null && essaie <= 1000) {
            essaie++;
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    Main.RANDOM.nextInt(Border.getMaxBijuSpawn()),
                    60,
                    Main.RANDOM.nextInt(Border.getMaxBijuSpawn()));
            if (Main.RANDOM.nextInt(100) <= 50) {
                location.setX(-location.getBlockX());
            }
            if (Main.RANDOM.nextInt(100) <= 50) {
                location.setZ(-location.getBlockZ());
            }
            if (location.getBlockZ() > 0) {
                if (location.getBlockZ() <= Border.getMinBijuSpawn()) {
                    location = null;
                    continue;
                }
                if (location.getBlockZ() >= Border.getMaxBijuSpawn()) {
                    location = null;
                    continue;
                }
            } else {
                if (location.getBlockZ() >= -Border.getMinBijuSpawn()) {
                    location = null;
                    continue;
                }
                if (location.getBlockZ() <= -Border.getMaxBijuSpawn()) {
                    location = null;
                    continue;
                }
            }
            if (location.getBlockX() > 0) {
                if (location.getBlockX() <= Border.getMinBijuSpawn()) {
                    location = null;
                    continue;
                }
                if (location.getBlockX() >= Border.getMaxBijuSpawn()) {
                    location = null;
                }
            } else {
                if (location.getBlockX() >= -Border.getMinBijuSpawn()) {
                    location = null;
                    continue;
                }
                if (location.getBlockX() <= -Border.getMaxBijuSpawn()) {
                    location = null;
                }
            }
        }
        if (location == null) {//location par défaut x:100 z:100
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    100,
                    0,
                    100);
        }
        location.setY(location.getWorld().getHighestBlockYAt(location)+1);
        return location;
    }

    @Override
    public GamePlayer getHote() {
        return this.gamePlayer;
    }

    @Override
    public boolean checkCanSpawn() {
        @NonNull final BijuCheckSpawnEvent event = new BijuCheckSpawnEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        spawn();
        @NonNull final BijuSpawnEvent spawnEvent = new BijuSpawnEvent(this);
        Bukkit.getPluginManager().callEvent(spawnEvent);
        return true;
    }
    public @Nullable Power getBijuOriginalPower() {
        return this.power;
    }
    public void changeOriginalPower(@NonNull final Power power) {
        this.power = power;
    }
    @Getter
    public static class BijuPower extends ItemPower {

        private final BijuBase biju;

        public BijuPower(@NonNull BijuBase biju, @NonNull RoleBase role) {
            super(biju.getName(), new Cooldown(60*20),
                    new ItemBuilder(biju.getItemMaterial())
                            .setName(biju.getName())
                            .addEnchant(Enchantment.ARROW_INFINITE, 1)
                            .hideEnchantAttributes(), role, biju.getItemDescription());
            this.biju = biju;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                for (@NonNull final PotionEffect potionEffect : this.biju.getEffectsWhenUse()) {
                    if (!player.hasPotionEffect(potionEffect.getType())) {
                        player.addPotionEffect(potionEffect, true);
                    } else {
                        for (PotionEffect po : player.getActivePotionEffects()) {
                            if (po.getType().equals(potionEffect.getType())) {
                                if (po.getAmplifier() <= potionEffect.getAmplifier()) {
                                    player.addPotionEffect(potionEffect, true);
                                }
                                break;
                            }
                        }
                    }
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.biju.onUse(player, getRole()));
                new BijuRunnable(getRole().getGameState(), getRole().getGamePlayer(), this);
                return true;
            }
            return false;
        }
        private static class BijuRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private final BijuPower bijuPower;
            private int timeRemaining;

            private BijuRunnable(GameState gameState, GamePlayer gamePlayer, BijuPower bijuPower) {
                this.gameState = gameState;
                this.timeRemaining = 60*5;
                this.gamePlayer = gamePlayer;
                this.bijuPower = bijuPower;
                this.gamePlayer.getActionBarManager().addToActionBar("bijurunnable."+this.bijuPower.getBiju().getName(), "§bTemp restant sous l'effet de "+this.bijuPower.getBiju().getName()+"§b: §c5 minutes");
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeRemaining <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("bijurunnable."+this.bijuPower.getBiju().getName());
                    this.bijuPower.getBiju().onEnd(this.gamePlayer.getRole());
                    this.gamePlayer.sendMessage("§7Vous n'êtes plus sous l'effet de "+this.bijuPower.getBiju().getName()+"§7.");
                    cancel();
                    return;
                }
                this.gamePlayer.getActionBarManager().updateActionBar(
                        "bijurunnable."+this.bijuPower.getBiju().getName(),
                        "§bTemp restant sous l'effet de "+this.bijuPower.getBiju().getName()+"§b: §c"+ StringUtils.secondsTowardsBeautiful(this.timeRemaining)
                );
                this.timeRemaining--;
            }
        }
    }
}
