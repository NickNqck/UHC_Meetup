package fr.nicknqck.roles.builder;

import lombok.Getter;
import org.bukkit.entity.Player;
@Getter
public enum TeamList {
	
	Demon("§c", "§cDémon"),
	Slayer("§a", "§aSlayer"),
	Solo("§e", "§eSolo"),
	Jigoro("§6", "§6Jigoro"),
	Mahr("§9", "§9Mahr"),
	Titan("§c", "§cTitan"),
	Soldat("§a", "§aSoldat"),
	Alliance("§6", "§6Kyojuro - Shinjuro"),
	Jubi("§d", "§dJubi"),
	Orochimaru("§5", "§5Orochimaru"),
	Akatsuki("§c", "§cAkatsuki"),
	Sasuke("§e§l", "§e§lSasuke"),
	Zabuza_et_Haku("§b", "§bZabuza et Haku"),
	Shinobi("§a", "§aShinobi"),
	Kabuto("§6§l", "§6§lKabuto"),
	Kumogakure("§6", "§6Kumogakure"),
	Shisui("§e§l", "§e§lShisui");

	private final java.util.List<Player> list;
	private final String Color;
	private final String name;
	TeamList(String color, String name){
		this.Color = color;
		this.name = name;
		this.list = new java.util.ArrayList<>();
	}
	public void addPlayer(Player player) {
		list.add(player);
	}
	public TeamList getTeam(){
		return this;
	}
}