package fr.nicknqck.roles.builder;

import lombok.Getter;
import org.bukkit.entity.Player;
@Getter
public enum TeamList {
	
	Demon("§c", "§cDémon", false),
	Slayer("§a", "§aSlayer", false),
	Solo("§e", "§eSolo", true),
	Jigoro("§6", "§6Jigoro", true),
	Mahr("§9", "§9Mahr", false),
	Titan("§c", "§cTitan", false),
	Soldat("§a", "§aSoldat", false),
	Alliance("§6", "§6Kyojuro - Shinjuro", true),
	Jubi("§d", "§dJubi", true),
	Orochimaru("§5", "§5Orochimaru", false),
	Akatsuki("§c", "§cAkatsuki", false),
	Sasuke("§e§l", "§e§lSasuke", true),
	Zabuza_et_Haku("§b", "§bZabuza et Haku", true),
	Shinobi("§a", "§aShinobi", false),
	Kabuto("§6§l", "§6§lKabuto", true),
	Kumogakure("§6", "§6Kumogakure", true),
	Shisui("§e§l", "§e§lShisui", true);

	private final java.util.List<Player> list;
	private final String Color;
	private final String name;
    private final boolean solo;

	TeamList(String color, String name, boolean solo){
		this.Color = color;
		this.name = name;
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