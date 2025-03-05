package fr.nicknqck.entity.krystalbeast.creator;

import fr.nicknqck.Border;
import fr.nicknqck.Main;
import fr.nicknqck.entity.krystalbeast.Beast;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Location;

public abstract class BeastCreator extends Beast implements IBeastCreator {

    @Override
    public Location getRandomLocation() {
        Location location = null;
        int essaie = 0;
        while (location == null && essaie <= 1000) {
            essaie++;
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    Main.RANDOM.nextInt(RandomUtils.getRandomDeviationValue(Main.RANDOM.nextInt(), Border.getMinBorderSize(), Border.getMaxBorderSize())),
                    60,
                    Main.RANDOM.nextInt(RandomUtils.getRandomDeviationValue(Main.RANDOM.nextInt(), Border.getMinBorderSize(), Border.getMaxBorderSize())));
            if (location.getBlockX() >= (Border.getMaxBorderSize()-10)) {
                location = null;
                continue;
            }
            if (location.getBlockX() <= (-Border.getMaxBorderSize()+10)) {
                location = null;
                continue;
            }
            if (location.getBlockZ() >= (Border.getMaxBorderSize()-10)) {
                location = null;
                continue;
            }
            if (location.getBlockZ() <= (-Border.getMinBorderSize()+10)) {
                location = null;
            }
        }
        if (location == null) {
            location = new Location(Main.getInstance().getWorldManager().getGameWorld(),
                    100,
                    0,
                    100);
        }
        location.setY(location.getWorld().getHighestBlockYAt(location)+1);
        return location;
    }

}