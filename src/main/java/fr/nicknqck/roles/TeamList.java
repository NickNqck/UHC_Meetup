package fr.nicknqck.roles;

import lombok.Getter;
import org.bukkit.entity.Player;
@Getter
public enum TeamList {
	
	Demon("§c"),
	Slayer("§a"),
	Solo("§e"),
	Jigoro("§6"),
	Mahr("§9"),
	Titan("§c"),
	Soldat("§a"),
	Alliance("§6"),
	Jubi("§d"),
	Orochimaru("§5"),
	Akatsuki("§c"),
	Sasuke("§e§l"),
	Zabuza_et_Haku("§b"),
	Shinobi("§a"),
	Kumogakure("§6");
	private final java.util.List<Player> list;
	private final String Color;
	TeamList(String color){
		this.Color = color;
		this.list = new java.util.ArrayList<>();
	}
	public void addPlayer(Player player) {
		list.add(player);
	}

	public java.util.List<Player> getPlayers(){
		return list;
	}
}