package fr.nicknqck.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import fr.nicknqck.Main;

public class Manager {

    private final Main naruto;
    private final World narutoWorld;
    private final Location kamuiSpawn;
    private final Location jumpLocation;
    private final Location endJumpLocation;
    private final HashMap<String, UUID> death;
    private final HashMap<UUID, Integer> strength;
    private final HashMap<UUID, Integer> resistance;
    private final HashMap<UUID, Location> deathLocation;
    private boolean isDay;

    public Manager(Main naruto) {
        this.narutoWorld = new WorldCreator("kamui").createWorld();
        this.naruto = naruto;
        this.kamuiSpawn = new Location(narutoWorld, 25108, 14, 25015);
        this.isDay = false;
        this.death = new HashMap<>();
        this.strength = new HashMap<>();
        this.resistance = new HashMap<>();
        this.deathLocation = new HashMap<>();
        this.jumpLocation = new Location(narutoWorld, 25109, 13, 25015);
        this.endJumpLocation = new Location(narutoWorld, 25109, 20, 25015);
    }

    public HashMap<UUID, Location> getDeathLocation() {
        return deathLocation;
    }

    public HashMap<UUID, Integer> getStrength() {
        return strength;
    }

    public HashMap<UUID, Integer> getResistance() {
        return resistance;
    }

    public Location getJumpLocation() {
        return jumpLocation;
    }

    public Location getEndJumpLocation() {
        return endJumpLocation;
    }

    public HashMap<String, UUID> getDeath() {
        return death;
    }

    public Location getKamuiSpawn() {
        return kamuiSpawn;
    }

    public World getNarutoWorld() {
        return narutoWorld;
    }

    public Main getNaruto() {
        return naruto;
    }

    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }
}
