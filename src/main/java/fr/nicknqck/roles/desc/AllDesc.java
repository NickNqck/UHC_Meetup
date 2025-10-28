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
	public final static String bar = ChatColor.DARK_GRAY+"§o§m-----------------------------------§r";
	public final static String regen = "§dRégénération§r";
	public final static String blind = "§1Blindness§r";
	public final static String slow = "§9Slowness§r";
	public final static String weak = "§7Weakness§r";
	public final static String items = ChatColor.BOLD+"Items: ";
	public final static String commande = ChatColor.BOLD + "Commande: ";
	public final static String effet = ChatColor.BOLD+"Effet: ";
	public final static String capacite = ChatColor.BOLD+"Capacité: ";
	public final static String role = "§lRôle§r:§6 ";
	public final static String point = "§8 • §r";
	public final static String objectifteam = "§fVotre objectif est de gagner avec le camp: ";
	public final static String objectifsolo = "§fVotre objectif est de gagner ";
	public final static String particularite = ChatColor.BOLD + "Particularité: ";
	public final static String nausee = "§2Nausée";
	static final String t =  "§fSi vous parvenez à tuez un joueur possédant le rôle de§6 Muzan§r vous obtiendrez "+Force+" 1 permanent, de plus votre§6 Dance du Dieu du Feu§r ne vous coutera plus de "+coeur+" permanent";
	public static final String chakra = "Vous possédez la nature de Chakra: ";
	public final static String tab = "§7     →";

	public static String[] Daki = new String[] {
			bar,
			"§lRôle: §r§cDaki",
			"",
			ChatColor.BOLD+"Capacité: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"1 fois par partie vous pouvez réssucité"),
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Force+" 1 la nuit, "+Resi+" 1 à moins de 30 blocs de Gyutaro, Weakness 1 à moins de 15 blocs des rôles : Tengen, Tanjiro, Inosuke, ZenItsu, Nezuko"),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Obi: "+ChatColor.WHITE+" En activant cette objet tout les joueurs étant à moins de 30 blocs (sauf les rôles: Tanjiro, Inosuke, ZenItsu, Tengen, Gyutaro et les démons) ne pourront plus bouger pendant 8 secondes"),
			"",
			(ChatColor.DARK_GRAY+" • "+ChatColor.GOLD+ChatColor.BOLD+"Troisième Oeil: "+ChatColor.WHITE+" Vous donne "+Speed+" 1 pendant 1 minutes"),
			"",
			(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
			"",
			(ChatColor.DARK_GRAY+" • " + ChatColor.WHITE+"A la mort de: "+ChatColor.GOLD+"Gyutaro"+ChatColor.WHITE+" peux importe qui le tue vous obtenez "+Resi+" 1 permanent, mais vous perdez aussi votre troisième oeil"),
			"",
			(ChatColor.DARK_GRAY+" • "+   ChatColor.WHITE+"A la mort de: "+ChatColor.GOLD+"Tanjiro OU Inosuke OU ZenItsu"+ChatColor.WHITE+" peux importe qui le/les tues vous perdez votre effet weakness proche des rôles: Tanjiro, Inosuke, ZenItsu, Tengen, Nezuko"),
			"",
			bar
	};
	public static String[] Doma = new String[] {
			bar,
			"§lRôle: §r§cDoma",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Force+" 1 la nuit"),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Epouventaille de Glace: "+ChatColor.WHITE+"Épée en diamant tranchant 3"),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Pouvoir Sanginaire, Zone de Glace: "+ChatColor.WHITE+" En activant cette objet pendant 25 secondes tout les joueurs étant à moins de 30 blocs de vous obtiendront slowness 1 et les joueurs étant à moins de 5 blocs auront slowness 3"),
			"",
			(ChatColor.DARK_GRAY+" • "+ChatColor.GOLD+ChatColor.BOLD+"Pouvoir Sanginaire, Statut de Glace: "+ChatColor.WHITE+" En activant cette objet pendant 5 minutes en tapant un joueur avec votre Epouventaille de Glace vous aurez 1 chance sur 4 d'obtenir "+Resi+" 1 pendant 10s"),
			"",
			bar
	};
	public static String[] Gyokko = new String[] {
			bar,
			"§lRôle: §r§cGyokko",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Force+" 1 la "+nuit),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Pouvoir Sanginaire: "+ChatColor.WHITE+"Vous téléportera dans un rayon de 15 blocs au tour de vous (à la même couche que la ou vous êtes)"),
			"",
			(ChatColor.DARK_GRAY+" • "+ChatColor.GOLD+ChatColor.BOLD+"Forme Démoniaque: "+ChatColor.WHITE+"Quand vous activez votre Forme Démonique vous lancez un compte à rebours qui vous fera perdre 1"+coeur+" permanent toute les minutes, mais il vous donnera également des aventages très pratique comme: un plastron Thorns 3, "+Resi+" 1 Permanent"),
			"",
			ChatColor.DARK_GRAY+" • "+ChatColor.GOLD+ChatColor.BOLD+"Bulle: "+ChatColor.WHITE+"Crée une bulle d'eau autours de vous de rayon 8",
			"",
			(ChatColor.BOLD+"Amélioration: "),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez à tuez un joueur possédant le rôle de "+ChatColor.GOLD+"Muichiro"+ChatColor.WHITE+" vous obtiendrez force 1 le jour ainsi que des bottes depht strider 2"),
			"",
			bar
	};
	public static String[] Muzan = new String[] {
			bar,
			"§lRôle: §r§cMuzan",
			"",
			ChatColor.BOLD+"Capacité: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Vous possédez la régénération naturel à hauteur de 1 demi"+coeur+" toute les 10 secondes"),
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Resi+" 1 permanent, "+Force+" 1 et "+Speed+" 1 la "+nuit),
			"",
			(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez à tuer un joueur possédant le rôle de§a Nezuko "+ChatColor.WHITE+"vous obtiendrez "+Force+" 1 le "+jour+" et "+Resi+" 2 la "+nuit),
			"",
			(ChatColor.BOLD+"Commande: "),
			"",
			ChatColor.DARK_GRAY+" • "+"§6§l/ds give§r Vous permet en spécifiant un joueur de lui donner le pouvoir de l'§cinfection§r, il pourra alors via un item séléctionné un joueur pour le rallier au camp des§c démons§r ("+ChatColor.DARK_RED+"ATTENTION§r: ce pouvoir ne permet d'infecter que les rôles§f Slayers§r)",
			"",
			particularite,
			"",
			ChatColor.DARK_GRAY + " • Lorsque vous envoyez un message dans le§c chat§f commençant par un \"§c!§f\", vous permet de parler avec§c Kokushibo",
			"",
			bar
	};
	public static String[] Tanjiro = new String[] {
			bar,
			"§lRôle: §r§aTanjiro",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Speed+" 1 le "+jour),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Dance du Dieu du Feu: "+ChatColor.WHITE+"Vous donne "+Resi+" 1 et "+fireResi+" 1 pendant 3 minutes, pendant la première minutes lorsque vous tapez un joueur vous le mettrez en feu, 3 minutes après activation vous perdrez 2"+coeur+" permanent"),
			"",
			(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
			"",
			ChatColor.DARK_GRAY+" • " +t,
			"",
			ChatColor.BOLD + " Commande: ",
            "",
            ChatColor.DARK_GRAY + " • " + ChatColor.GOLD+"/ds sentir"+ChatColor.RESET+ " Permet de savoir combien il y à de démon autours de vous",
            "",
			bar
	};
	public static String[] Jigoro = new String[] {
			bar,
			"§lRôle: §r§6Jigoro",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Speed+" 1 permanent, "+Resi+" 1 permanent"),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Zone De Foudre:§f Crée une zone circulaire de 5 blocs de rayon, délimitée par des particules, dans laquelle vous recevrez l'effet "+regen+" 1 pendant 15s. Toutes les 4 secondes, un éclair infligeant 2"+coeur+" apparaîtra sur les joueurs présent dans la zone.",
			"",
			(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez à tué un joueur possédant le rôle de§a Zen'Itsu§f vous obtiendrez "+Force+" 1 le "+jour+ChatColor.WHITE+" et aussi l'accès au"+ChatColor.GOLD+" Premier Mouvement du Souffle de la Foudre."),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez a tué un joueur possédant le rôle de§c Kaigaku§f vous obtiendrez " +Force+" 1 la "+nuit+ChatColor.WHITE+" mais également l'accès au:"+ChatColor.GOLD+" Quatrième Mouvement du Souffle de la Foudre."),
			"",
			bar
	};
	public static String[] Yoriichi = new String[] {
			bar,
			"§lRôle: §r§6Yoriichi",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +Speed+" 1 et "+Force+" 1 permanents, "+Resi+" 1 le "+jour),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Souffle du Soleil:§f En activant cet objet vous activerez un passif qui est que quand vous tapez un joueur il perdra automatiquement son absorption"),
			"",
			(ChatColor.WHITE+""+ChatColor.BOLD+"Amélioration: "),
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"Si vous parvenez à tuer un joueur possédant le rôle de "+ChatColor.GOLD+"Kokushibo"+ChatColor.WHITE+" vous obtiendrez "+Resi+" 1 la "+nuit),
			"",
			bar
	};
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
	public static String[] HantenguV2 = new String[] {
			bar,
			"§lRôle: §r§cHantenguV2",
			"",
			ChatColor.BOLD+"Effet: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.WHITE+"(Si vous êtes Hantengu)§b "+Speed+" 2§r, Invisibilité 1 et§a No Fall§r en enlevant son armure, "+weak+" 1 permanent"),
			"",
			ChatColor.BOLD+"Items: ",
			"",
			(ChatColor.DARK_GRAY+" • " +ChatColor.GOLD+ChatColor.BOLD+"Materialisation des émotions: "+ChatColor.WHITE+"Ouvre un menu vous offrant la possibilité de choisir entre plusieurs clone de vous via un arbre de compétence, la première fois vous aurez le choix entre§c Karaku§r et§c Sekido§r puis entre§6Urogi§r et§6 Urami§r (si vous avez choisis§c Karaku§r) sinon entre§6 Aizetsu§r et§6 Urami§r (si vous avez choisis§c Sekido§r), puis à la toute fin vous aurez quoi qu'il arrive accès à§6 Zohakuten"),
			"",
			ChatColor.RED+"Karaku§r: Vous donne "+Speed+" 1 permanent ainsi que l'item§6 Vent§r qui vous permettra de tp les joueurs autours de vous§6 50blocs§r en hauteur",
			"",
			ChatColor.RED+"Sekido§r: Vous donne "+Force+" 1 permanent ainsi que l'item§6 Kakkhara§r qui via un clique droit créera un§e éclair§r sur tout les joueurs non-démon étant à moins de§6 25blocs§r qui leur infligera 2"+coeur+" ainsi que "+blind+" 1 et§9 "+slow+" 4§r pendant§l 4s",
			"",
			ChatColor.GOLD+"Urogi§r: Vous donne "+Speed+" 1 et "+Force+" 1 permanent ainsi que l'item§6 Urogi§r qui vous permettra de gagner l'effet§2 Jump Boost 4§r ainsi que 2 Double Jump qui vous propulseront d'environ 20blocs (Cooldown double jump: 30s), vous recevrez également l'item§6 Cri Sonique§r la personne visée obtiendra "+slow+" 1,§a Nausée 1§r et "+weak+" 1 pendant§6 30s",
			"",
			ChatColor.GOLD+"Urami§r: Vous donne "+Resi+" 2 ainsi que "+weak+" 1 permanent, via ce clone si vous mourrez avec vous réssuciter en temp qu'Hantengu mais avec 3"+coeur+" permanent en moins ",
			"",
			ChatColor.GOLD+"Aizetsu§r: Vous donne "+Force+" 1 permanent ainsi que 2"+coeur+" supplémentaire, vous obtiendrez via ce clone une épée nommé§6 Yari§r qui vous permettra via un clique droit de ne plus prendre de dégat pendant§6 5s",
			"",
			ChatColor.GOLD+"§oZohakuten§r: Vous donne "+Force+" 1 et "+Resi+" 1§r permanent ainsi qu'une "+regen+" naturelle de 0,5"+coeur+" toute les§6 15s§r de plus vous obtiendrez différent item en fonction des clones que vous avec choisis précédemment",
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
