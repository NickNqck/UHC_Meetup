package fr.nicknqck.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KamuiUtils {
	
	public static enum Users{
		obito(null),
		kakashi(null),
		cibleObito(null),
		cibleKakashi(null);
		private Location loc;
		Users(Location Loca) {
			this.loc = Loca;
			
		}
		public void SetLoc(Location Locat) {
			this.loc = Locat;
		}
		public Location GetLoc() {
			return loc;
		}
	}
	private static HashMap<Users, Player> Cible = new HashMap<>();
	
	public static void start(Location Loc,Users user,Player p, boolean tpInKamui) {
		for (Users L : Users.values()) {
			if (user == L) {
				user.SetLoc(Loc);
				Cible.put(user, p);
				System.out.println("puting "+p.getName()+" at "+L.name());
			}
		}
		if (tpInKamui) {
			teleportInKamui(p);
		}
	}
	private static void teleportInKamui(Player target) {
		System.out.println(target.getName()+" is teleported to Kamui");
		target.teleport(new Location(Bukkit.getWorld("Kamui"), 0, 19, 0, -90, 0));
		
	}
	public static void end(Player p) {
		System.out.println("calling "+p.getName());
		for (Player pl : Cible.values()) {
			System.out.println(p.getName()+" is count in Cible");
			if (pl.getUniqueId() == p.getUniqueId()) {
				for(Users u : Cible.keySet()) {
					System.out.println(p.getName()+" is "+u.name()+" using");
					if (Cible.get(u).getUniqueId() == pl.getUniqueId()) {
						System.out.println(u.name()+" targeting "+pl.getName());
						if (u.GetLoc() != null) {
							System.out.println(u.name()+" location isn't null");
							p.teleport(u.GetLoc());
							u.SetLoc(null);
							break;
						}
					}
				}
			}
		}
	}
	public static void resetUtils() {
		for (Users l : Users.values()) {
			if (l != null) {
				if (l.GetLoc() != null) {
					l.SetLoc(null);
				}
			}
		}
		if (Cible != null) {
			Cible.clear();
		}
	}
}
