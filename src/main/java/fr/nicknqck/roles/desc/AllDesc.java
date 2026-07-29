package fr.nicknqck.roles.desc;

import org.bukkit.ChatColor;

public class AllDesc{
	// §c❤§r ❤ rouge
	public final static String coeur = "§c❤§r";
	public static String Coeur(String color) {
		return color+"❤§r";
	}
	public final static String Speed = "§eSpeed§r";
	public final static String Force = "§cForce§r";
	public final static String Resi = "§9Résistance§r";
	public final static String nuit = "§1nuit§r";
	public final static String jour = "§ejour§r";
	public final static String fireResi = "§6Fire Résistance§r";
	public final static String bar = "§8§o§m-----------------------------------§r";
	public final static String regen = "§dRégénération§r";
	public final static String blind = "§1Blindness§r";
	public final static String slow = "§9Slowness§r";
	public final static String weak = "§7Weakness§r";
	public final static String items = "§lItems: ";
	public final static String commande = "§lCommande: ";
	public final static String effet = "§lEffet: ";
	public final static String capacite = "§lCapacité: ";
	public final static String role = "§lRôle§r:§6 ";
	public final static String point = "§8 • §r";
	public final static String objectifteam = "§fVotre objectif est de gagner avec le camp: ";
	public final static String objectifsolo = "§fVotre objectif est de gagner ";
	public final static String particularite = "§lParticularité: ";
	public static final String chakra = "Vous possédez la nature de Chakra: ";
	public final static String tab = "§7     →";


	public static String[] JigoroV2 = new String[] {
			bar,
			"§lRôle: §r§6Jigoro",
			"",
           "§lEffet: ",
            "",
            ChatColor.DARK_GRAY + " • " + Speed + " 1 permanent",
            "",
            "§lItems: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD + ChatColor.BOLD + "Vitesse:§r Permet d'obtenir "+Speed+" 3",
            "",
            ChatColor.BOLD + " Commande: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD + ChatColor.BOLD + "/ds pacte:§r Permet de choisir un pacte parmi 3",
            "",
            ChatColor.GOLD+"Pacte 1§r: Vous devrez gagner seul en tant que rôle entièrement§6 solitaire§r pour ce faire vous obtiendrez "+Force+" 1 permanent§r, de plus vous obtiendrez "+Resi+" 1 le jour si vous tuez§a ZenItsu§r ainsi que "+Resi+" 1 la "+nuit+" si vous tuez§c Kaigaku§r, pour chaque kill de vos disciples vous récupérerez 10% de "+Speed,
            "",
            ChatColor.GOLD+"Pacte 2§r: Vous devrez gagner avec§c Kaigaku§r pour ce faire vous et lui disposez de l'effet "+Resi+" 1 à moins de 50blocs de lui ainsi que lorsque l'un de vous fait un kill vous gagnez tout les deux§c 1/2"+coeur+" permanent, de plus vous disposez d'un chat avec ce dernier via la commande§6 /ds chat, si vous ou§c Kaigaku§f parvenez à tuer§a Zen'Itsu§f vous obtiendrez l'effet§c Force I§f permanent",
            "",
            ChatColor.GOLD+"Pacte 3§r: Vous devrez gagner avec§a ZenItsu§r pour ce faire vous et lui  disposez de l'effet "+Resi+" 1 permanent, également vous possédez tous les deux l'effet§c "+Force+"§c 1§r à moins de 20blocs, pour vous aider à vous retrouver vous deux possédez une flèche pointant vers l'autre, si l'un de vous deux tue§c Kaigaku§r vous obtiendrez§6 "+Speed+"§b 2§r en-dessous de 5"+coeur,
            "",
            bar	
	};

	public static String[] JigoroV2Pacte1 = new String[] {
			bar,
			"§lRôle: §r§6Jigoro",
			"",
           "§lEffet: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.WHITE + "§b "+Speed+" 1§r et§c "+Force+" 1§r permanent",
            "",
            "§lItems: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD + ChatColor.BOLD + "Vitesse:§r Permet d'obtenir "+Speed+" 3",
            "",
            ChatColor.BOLD + "Pacte 1: ",
            "",
            "Vous gagner seul, pour ce faire vous obtiendrez§6 "+Resi+" 1 le jour en tuant§a ZenItsu§r ainsi que§6 "+Resi+" 1 la "+nuit+" si vous tuez§c Kaigaku§r, de plus pour chaque kill de vos disciple effectué vous récupérerez 10% de "+Speed,
            "",
            bar	
	};
	public static String[] JigoroV2Pacte2 = new String[] {
			bar,
			"§lRôle: §r§6Jigoro",
			"",
           "§lEffet: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.WHITE + "§b "+Speed+" 1§r permanent",
            "",
            "§lItems: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD + ChatColor.BOLD + "Vitesse:§r Permet d'obtenir "+Speed+" 3",
            "",
            ChatColor.BOLD + "Pacte 2: ",
            "",
            "Vous gagner en duo avec§c Kaigaku§r, pour ce faire vous et§c lui§r obtenez l'effet§3 "+Resi+" 1§r à moins de 50blocs l'un de l'autre, de plus quand§c Kaigaku§r ou vous faite un kill vous deux obtenez 1 demi"+coeur+" permanent, pour finir vous disposer d'un chat en commun via la commande§6 /ds chat",
            "",
            bar	
	};
	public static String[] JigoroV2Pacte3 = new String[] {
			bar,
			"§lRôle: §r§6Jigoro",
			"",
           "§lEffet: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.WHITE + "§b "+Speed+" 1§r et§3 "+Resi+" 1§r permanent",
            "",
            "§lItems: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD + ChatColor.BOLD + "Vitesse:§r Permet d'obtenir "+Speed+" 3",
            "",
            ChatColor.BOLD + "Pacte 3: ",
            "",
            "Vous gagner en duo avec§a ZenItsu§r, pour ce faire vous et§a lui§r obtenez l'effet§c "+Force+" 1§r à moins de 20blocs l'un de l'autre et l'effet§3 "+Resi+" 1§r permanent, de plus si vous ou§a ZenItsu§r parvenez à tué§c Kaigaku§r vous gagnerez§b "+Speed+" 2§r en dessous de 5"+coeur,
            "",
            bar	
	};
    public static String[] Nakime = new String[] {
            bar,
            role+"Nakime",
            "",
            effet,
            "",
            point+weak+" 1 permanent sauf dans votre cage ou une fois à l'intérieur vous possédez "+Force+" 1, "+Resi+" 1 ainsi que§a NoFall",
            "",
            items,
            "",
            point+"§cCage§f: Vous téléporte vous ainsi que toute les personnes étant à moins de 20blocs de vous dans la§6 cage de Nakime§f, de plus quand un joueur meurt dans cette cage son rôle n'est pas afficher dans le chat au yeux des autres joueur exepté§c Muzan§f et§c vous§f",
            "",
            commande,
            "",
            point+"§7/§cds return§7: Vous permet d'éjécter tout les joueurs étant dans votre§c cage de Nakime§7 (dont vous)",
            "",
            bar
    };
}
