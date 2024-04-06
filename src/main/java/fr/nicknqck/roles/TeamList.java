package fr.nicknqck.roles;

import org.bukkit.entity.Player;

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
	private java.util.List<Player> list;
	private String color;
	TeamList(String color){
		this.color = color;
		this.list = new java.util.ArrayList<>();
	}
	public String getColor() {
		return color;
	}
	public void addPlayer(Player player) {
		list.add(player);
	}
	public static TeamList getPlayerTeam(Player player) {
		return java.util.Arrays.stream(TeamList.values()).filter(teamList -> teamList.list.contains(player)).findAny().get();
	}
	public void removePlayer(Player player) {
		list.remove(player);
	}
	public java.util.List<Player> getPlayers(){
		return list;
	}
}