package fr.nicknqck.entity.bijuv2;

import fr.nicknqck.Border;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.biju.BijuCheckSpawnEvent;
import fr.nicknqck.events.custom.biju.BijuSpawnEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.powers.Power;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

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
    public @NonNull ItemStack getItemInMenu() {
        return GlobalUtils.setNBT(getTempMenuItem(), "biju.power", "biju");
    }
    @NonNull
    public abstract ItemStack getTempMenuItem();

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
}
