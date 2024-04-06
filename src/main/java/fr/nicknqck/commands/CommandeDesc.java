package fr.nicknqck.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.desc.AllDesc;
import net.md_5.bungee.api.ChatColor;

public class CommandeDesc implements CommandExecutor{
GameState gameState;
	public CommandeDesc(GameState gameState) {
		this.gameState = gameState;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] arg) {		
		String d = ChatColor.GOLD+"[Desc-Manager] "+ChatColor.RESET;
		if (arg.length == 0) {
			sender.sendMessage(d+"Il faut faire la commande /desc <role> (exemple: /desc Akaza)");
			return true;
		}
		if (arg.length == 1) {
			if (sender instanceof Player) {
				if (arg[0].equalsIgnoreCase("Akaza")) {				
					sender.sendMessage(AllDesc.Akaza);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Gyomei")) {					
					sender.sendMessage(AllDesc.Gyomei);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kanae")) {				
					sender.sendMessage(AllDesc.Kanae);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kyogai")) {					
					sender.sendMessage(AllDesc.Kyogai);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Rui")) {				
					sender.sendMessage(AllDesc.Rui);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Daki")) {				
					sender.sendMessage(AllDesc.Daki);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Gyutaro")) {			
					sender.sendMessage(AllDesc.Gyutaro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Demon_Simple") || arg[0].equalsIgnoreCase("ds")||arg[0].equalsIgnoreCase("DemonSimple")) {					
					sender.sendMessage(AllDesc.Demon_Simple);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Doma")) {					
					sender.sendMessage(AllDesc.Doma);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Gyoko") || arg[0].equalsIgnoreCase("Gyokko")) {					
					sender.sendMessage(AllDesc.Gyokko);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Hantengu")) {					
					sender.sendMessage(AllDesc.Hantengu);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kaigaku")||arg[0].equalsIgnoreCase("R3dline_")) {					
					sender.sendMessage(AllDesc.Kaigaku);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kokushibo")) {					
					sender.sendMessage(AllDesc.Kokushibo);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Muzan")) {					
					sender.sendMessage(AllDesc.Muzan);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Inosuke")) {					
					sender.sendMessage(AllDesc.Inosuke);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kanao")) {					
					sender.sendMessage(AllDesc.Kanao);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kyojuro") || arg[0].equalsIgnoreCase("Rengoku")) {					
					sender.sendMessage(AllDesc.Kyojuro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Makomo")) {					
					sender.sendMessage(AllDesc.Makomo);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Muichiro")) {					
					sender.sendMessage(AllDesc.Muichiro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Nezuko")) {					
					sender.sendMessage(AllDesc.Nezuko);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Obanai")) {					
					sender.sendMessage(AllDesc.Obanai);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Pourfendeur") || arg[0].equalsIgnoreCase("sv") || arg[0].equalsIgnoreCase("roledemerde")||arg[0].equalsIgnoreCase("bestrole")) {					
					sender.sendMessage(AllDesc.Pourfendeur);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Sabito")) {					
					sender.sendMessage(AllDesc.Sabito);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Sanemi")) {				
					sender.sendMessage(AllDesc.Sanemi);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Shinobu")) {					
					sender.sendMessage(AllDesc.Shinobu);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Tanjiro")) {					
					sender.sendMessage(AllDesc.Tanjiro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Tengen")) {
					sender.sendMessage(AllDesc.Tengen);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Tomioka")) {
					sender.sendMessage(AllDesc.Tomioka);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Urokodaki")) {
					sender.sendMessage(AllDesc.Urokodaki);
					return true;
				}
				if (arg[0].equalsIgnoreCase("ZenItsu") || arg[0].equalsIgnoreCase("Zen'Itsu")||arg[0].equalsIgnoreCase("Escrow")) {
					sender.sendMessage(AllDesc.ZenItsu);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Jigoro")||arg[0].equalsIgnoreCase("NickNqck")) {					
					sender.sendMessage(AllDesc.Jigoro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Shinjuro")) {					
					sender.sendMessage(AllDesc.Shinjuro);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Yoriichi")) {
					sender.sendMessage(AllDesc.Yoriichi);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Enmu")) {
					sender.sendMessage(AllDesc.Enmu);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Mitsuri")) {
					sender.sendMessage(AllDesc.Mitsuri);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Kagaya")) {
					sender.sendMessage(AllDesc.Kagaya);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Susamaru")) {
					sender.sendMessage(AllDesc.Susamaru);
					return true;
				}
				if (arg[0].equalsIgnoreCase("me")) {
					for (String desc : gameState.getPlayerRoles().get(sender).Desc()) {
						sender.sendMessage(desc);
					}
					return true;
				}
				if (arg[0].equalsIgnoreCase("DemonMain")) {
					sender.sendMessage(AllDesc.DemonMain);
					return true;
				}
				if (arg[0].equalsIgnoreCase("JigoroV2")) {
					sender.sendMessage(AllDesc.JigoroV2);
					return true;
				}
				if (arg[0].equalsIgnoreCase("Hantenguv2")) {
					sender.sendMessage(AllDesc.HantenguV2);
					return true;
				}
				if (arg[0].equalsIgnoreCase("DemonSimplev2")) {
					sender.sendMessage(AllDesc.Demon_SimpleV2);
				}
				if (arg[0].equalsIgnoreCase("yahaba")) {
					sender.sendMessage(AllDesc.Yahaba);
				}
				if (arg[0].equalsIgnoreCase("hotaru")) {
					sender.sendMessage(AllDesc.Hotaru);
				}
				if (arg[0].equalsIgnoreCase("Kumo"))sender.sendMessage(AllDesc.Kumo);
				return true;
				
			} else {
				System.out.println("Il faut etre connecter en temp que joueur pour faire la commande");
				return true;
			}
		}
		return false;
	}

}
