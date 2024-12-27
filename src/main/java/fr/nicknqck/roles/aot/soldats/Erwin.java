package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.aot.solo.Eren;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Erwin extends SoldatsRoles {
	public Erwin(UUID player) {
		super(player);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Erwin;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Erwin",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/aot camp <§ljoueur§r§6§>§f: Vous donne le camp d'un joueur, cependant il y à 1 chance sur 3 que la personne sois mise au courant immédiatement qu'elle été observé par vous, une autre chance qu'elle le sache mais 5minutes après et une dernière chance qu'elle ne l'apprenne jamais.\nCette commande n'est utilisable que 2x par partie",
				"",
				AllDesc.bar
		};
	}
	private int cmdUse = 0;
	private int maxcmdUse = 2;

	@Override
	public String getName() {
		return "Erwin";
	}

	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("camp")) {
			if (args.length > 1) {
				if (args[1] != null) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						owner.sendMessage("Il faut indiqué un pseudo correcte");
					}else {
						if (cmdUse < maxcmdUse) {
							if (gameState.hasRoleNull(target.getUniqueId())) {
								owner.sendMessage("La personne visée ne possède pas de rôle veuiller visée quelqu'un d'autre");
							}else {
								if (gameState.getPlayerRoles().get(target).getOriginTeam() != null) {
									if (gameState.getGamePlayer().get(target.getUniqueId()).getRole() instanceof Eren) {
										owner.sendMessage(gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeamColor()+target.getName()+"§r appartient au camp des§a "+TeamList.Soldat.name());
										cmdUse+=1;
										int r = RandomUtils.getRandomInt(0, 2);
										if (r < 1) {
											target.sendMessage(AllDesc.bar);
											target.sendMessage("§7Vous avez été éspionné par§a Erwin");
											target.sendMessage(AllDesc.bar);
										}
									}else {
										owner.sendMessage(gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeamColor()+target.getName()+"§r appartient au camp des "+gameState.getGamePlayer().get(target.getUniqueId()).getRole().getTeamColor()+gameState.getPlayerRoles().get(target).getOriginTeam().name());
										cmdUse+=1;
										int r = RandomUtils.getRandomInt(0, 2);
										if (r < 1) {
											target.sendMessage(AllDesc.bar);
											target.sendMessage("§7Vous avez été éspionné par§a Erwin");
											target.sendMessage(AllDesc.bar);
										}
									}									
								}else {
									owner.sendMessage("Le joueur visée ne possède pas de team est n'est donc pas ciblable par votre commande");
								}
							}
						}else {
							owner.sendMessage("Vous avez atteind le nombre maximum d'utilisation de cette commande");
						}
					}
				}else {
					owner.sendMessage("Il faut précisé un joueur");
				}
			}else {
				owner.sendMessage("Il faut précisé un joueur !");
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		cmdUse = 0;
		maxcmdUse = 2;
	}
}