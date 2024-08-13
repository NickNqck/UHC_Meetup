package fr.nicknqck.utils.powers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Location;

import fr.nicknqck.Main;

public class LocPlayer {
    private final ArrayList<Location> locs = new ArrayList<>();

    public LocPlayer() {
        locs.add(loc1);
        locs.add(loc2);
        locs.add(loc3);
        locs.add(loc4);
        locs.add(loc5);
        locs.add(loc6);
        locs.add(loc7);
        locs.add(loc8);
        locs.add(loc9);
        locs.add(loc10);
        locs.add(loc11);
        locs.add(loc12);
        locs.add(loc13);
        locs.add(loc14);
        locs.add(loc15);
        locs.add(loc16);
        locs.add(loc17);
        locs.add(loc18);
        locs.add(loc19);
        locs.add(loc20);
        locs.add(loc21);
        locs.add(loc22);
        locs.add(loc23);
        locs.add(loc24);
        locs.add(loc25);
        Collections.shuffle(locs);
    }

    public static Location loc1 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 14,158,770);
    public static Location loc2 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 56,130,850);
    public static Location loc3 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 60,157,809);
    public static Location loc4 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 38,187,820);
    public static Location loc5 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 38,182,813);
    public static Location loc6 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 17,187,809);
    public static Location loc7 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 12,175,787);
    public static Location loc8 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 13,155,809);
    public static Location loc9 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 25,157,830);
    public static Location loc10 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 25,178,857);
    public static Location loc11 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 46,190,856);
    public static Location loc12 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 80,167,811);
    public static Location loc13 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 54,168,806);
    public static Location loc14 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 52,148,783);
    public static Location loc15 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 67,148,793);
    public static Location loc16 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 40,157,770);
    public static Location loc17 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 30,141,777);
    public static Location loc18 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 20,136,788);
    public static Location loc19 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 5,177,842);
    public static Location loc20 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 8,157,830);
    public static Location loc21 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 62,165,848);
    public static Location loc22 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 81,145,820);
    public static Location loc23 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 14,129,810);
    public static Location loc24 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 75,155,782);
    public static Location loc25 = new Location(Main.getInstance().getWorldManager().getGameWorld(), 47,190,858);

    public Location getRandomPositionStart() {
        Random ran = new Random();
        int random = ran.nextInt(locs.size());
        return locs.get(random);
    }

    public Location getRandomPositionRespawn() {
        ArrayList<Location> locs = new ArrayList<>();
        locs.add(loc1);
        locs.add(loc2);
        locs.add(loc3);
        locs.add(loc4);
        locs.add(loc5);
        locs.add(loc6);
        locs.add(loc7);
        locs.add(loc8);
        locs.add(loc9);
        locs.add(loc10);
        locs.add(loc11);
        locs.add(loc12);
        locs.add(loc13);
        locs.add(loc14);
        locs.add(loc15);
        locs.add(loc16);
        locs.add(loc17);
        locs.add(loc18);
        locs.add(loc19);
        locs.add(loc20);
        locs.add(loc21);
        locs.add(loc22);
        locs.add(loc23);
        locs.add(loc24);
        locs.add(loc25);
        Random ran = new Random();
        int random = ran.nextInt(locs.size());
        return locs.get(random);
    }
}
