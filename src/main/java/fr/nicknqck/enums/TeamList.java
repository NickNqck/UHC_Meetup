package fr.nicknqck.enums;

import lombok.Getter;
import org.bukkit.entity.Player;
@Getter
public enum TeamList {
	
	Demon("§c", "§cDémon", "ds", false),
	Slayer("§a", "§aSlayer", "ds", false),
	Solo("§e", "§eSolo", "all", true),
	Jigoro("§6", "§6Jigoro", "ds", true),
	Mahr("§9", "§9Mahr", "aot", false),
	Titan("§c", "§cTitan", "aot", false),
	Soldat("§a", "§aSoldat", "aot", false),
	Alliance("§6", "§6Kyojuro - Shinjuro", "ds", true),
	Jubi("§d", "§dJubi", "ns", true),
	Orochimaru("§5", "§5Orochimaru", "ns", false),
	Akatsuki("§c", "§cAkatsuki", "ns", false),
	Sasuke("§e§l", "§e§lSasuke", "ns", true),
	Zabuza_et_Haku("§b", "§bZabuza et Haku", "ns", true),
	Shinobi("§a", "§aShinobi", "ns", false),
	Kabuto("§6§l", "§6§lKabuto", "ns", true),
	Kumogakure("§6", "§6Kumogakure", "ns", true),
	Shisui("§e§l", "§e§lShisui", "ns", true);

	private final java.util.List<Player> list;
	private final String Color;
	private final String name;
    private final String mdj;
    private final boolean solo;

	TeamList(String color, String name, String mdj, boolean solo){
		this.Color = color;
		this.name = name;
        this.mdj = mdj;
        this.solo = solo;
        this.list = new java.util.ArrayList<>();
	}
	public void addPlayer(Player player) {
		list.add(player);
	}
	public TeamList getTeam(){
		return this;
	}
}