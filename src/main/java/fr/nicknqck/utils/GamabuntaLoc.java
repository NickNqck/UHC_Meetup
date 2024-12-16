package fr.nicknqck.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import fr.nicknqck.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class GamabuntaLoc {
	
	private final ArrayList<Location> locs = new ArrayList<>();
	
	public GamabuntaLoc() {
		loc1.setPitch(90);
		loc2.setPitch(91);
		loc3.setPitch(-91);
		loc4.setPitch(-90);
		JirayaLoc.setPitch(90f);
		JirayaLoc.setYaw(1);
		locs.add(loc1);
		locs.add(loc2);
		locs.add(loc3);
		locs.add(loc4);
		Collections.shuffle(locs);
	}
	
	private final Location JirayaLoc = new Location(Bukkit.getWorld("Gamabunta"), 0.0, 6.0, 0.0);
	private final Location loc1 = new Location(Bukkit.getWorld("Gamabunta"), 25.0, 6.0, -14.0);
	private final Location loc2 = new Location(Bukkit.getWorld("Gamabunta"), 26.0, 6.0, 17.0);
	private final Location loc3 = new Location(Bukkit.getWorld("Gamabunta"), -26.0, 6.0, 17.0);
	private final Location loc4 = new Location(Bukkit.getWorld("Gamabunta"), -26.0, 6.0, -15.0);
	
	public Location getRandomPositionStart() {
        Random ran = Main.RANDOM;
        int random = ran.nextInt(locs.size());
        return locs.get(random);
    }
	public Location getJirayaSpawn() {
		JirayaLoc.setPitch(90f);
		JirayaLoc.setYaw(1);
		return JirayaLoc;
	}
}