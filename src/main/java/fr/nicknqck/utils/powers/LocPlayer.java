package fr.nicknqck.utils.powers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Location;

import fr.nicknqck.Main;
import org.bukkit.World;

public class LocPlayer {

    private final ArrayList<Location> locs = new ArrayList<>();

    public LocPlayer(World world) {
        locs.add(new Location(world, 14,158,770));
        locs.add(new Location(world, 56,130,850));
        locs.add(new Location(world, 60,157,809));
        locs.add(new Location(world, 38,187,820));
        locs.add(new Location(world, 38,182,813));
        locs.add(new Location(world, 17,187,809));
        locs.add(new Location(world, 13,155,809));
        locs.add(new Location(world, 12,175,787));
        locs.add(new Location(world, 25,157,830));
        locs.add(new Location(world, 25,178,857));
        locs.add(new Location(world, 46,190,856));
        locs.add(new Location(world, 80,167,811));
        locs.add(new Location(world, 54,168,806));
        locs.add(new Location(world, 52,148,783));
        locs.add(new Location(world, 67,148,793));
        locs.add(new Location(world, 40,157,770));
        locs.add(new Location(world, 30,141,777));
        locs.add(new Location(world, 20,136,788));
        locs.add(new Location(world, 5,177,842));
        locs.add(new Location(world, 8,157,830));
        locs.add(new Location(world, 62,165,848));
        locs.add(new Location(world, 81,145,820));
        locs.add(new Location(world, 14,129,810));
        locs.add(new Location(world, 75,155,782));
        locs.add(new Location(world, 47,190,858));
        Collections.shuffle(locs, Main.RANDOM);
    }

    public Location getRandomPositionStart() {
        Random ran = new Random();
        int random = ran.nextInt(locs.size());
        return locs.get(random);
    }

    public Location getRandomPositionRespawn() {
        ArrayList<Location> locs = new ArrayList<>(this.locs);
        int random = Main.RANDOM.nextInt(locs.size());
        return locs.get(random);
    }
}
