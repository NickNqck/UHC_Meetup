package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.aot.solo.ErenV2;
import fr.nicknqck.roles.aot.solo.GabiV2;
import fr.nicknqck.roles.aot.titanrouge.Jelena;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hansi extends SoldatsRoles {

	public Hansi(UUID player) {
		super(player);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Hansi;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Hansi","",
				AllDesc.commande,"",
				AllDesc.point+"§6/aot torture <§ljoueur§6>§f: Vous permet de \"torturer\" le joueur cibler ce qui fera des choses différente en fonction de son rôle et de son camp",
				"Si la personne visée est du camp§a Soldat§f ou est§c Jelena§f la personne touchée perdra 2"+AllDesc.coeur+" permanent, cependant vous serez informé qu'elle est du camp§a Soldat",
				"Si la personne visée est§6 Eren§f ou§6 Gabi§f la personne touchée gagnera 1"+AllDesc.coeur+" permanent et vous serez informé qu'elle est du camp§a Soldat",
				"Si la personne visée n'est pas du camp§a Soldat§f et n'a pas été mentionné précédemment la personne visée perdra 1"+AllDesc.coeur+" permanent et vous vous en gagnerez 1"+AllDesc.coeur+" permanent, également vous serez informé qu'elle n'est pas du camp§a Soldat",
				"",
				AllDesc.point+"§6/aot give <§ljoueur§6>§f: Vous permet de donné une seringue au joueur visé, ce qui lui permettra (s'il est proche d'un Titan lors de sa mort) de pouvoir récupérer un Titan mort proche de lui",
				"",
				AllDesc.bar
		};
	}
	private int actualtorture = 0;
	private int actualseringue = 0;
	private final List<Player> tortured = new ArrayList<>();

	@Override
	public String getName() {
		return "Hansi";
	}

	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("torture")) {
				if (args.length == 2) {
					if (args[1] != null) {
						Player target = Bukkit.getPlayer(args[1]);
						if (!gameState.hasRoleNull(target.getUniqueId())) {
							if (actualtorture < 3) {
								if (tortured.contains(target)) {
									owner.sendMessage("§7Ce joueur à déjà été torturé par vos soins");
									return;
								}
								GamePlayer gamePlayer = gameState.getGamePlayer().get(target.getUniqueId());
								if (gamePlayer.getRole() instanceof ErenV2) {
									target.sendMessage("§7Vous avez censé avoir perdu 2"+AllDesc.coeur+"§7 permanent suite à la torture de§a Hansi§7 mais à la place vous avez gagner 1"+AllDesc.coeur+" permanent, elle à appris que vous êtes dans le camp des §a Soldats");
									owner.sendMessage("§7Vous avez torturer§f "+target.getName()+"§7 il a perdu 2"+AllDesc.coeur+"§7 permanent, cependant vous avez appris qu'il est du camp§a Soldat");
									giveHeartatInt(target, 1);
								}else {
									if (gamePlayer.getRole() instanceof GabiV2) {
										target.sendMessage("§7Vous avez censé avoir perdu 2"+AllDesc.coeur+"§7 permanent suite à la torture de§a Hansi§7 mais à la place vous avez gagner 1"+AllDesc.coeur+" permanent, elle à appris que vous êtes dans le camp des §a Soldats");
										owner.sendMessage("§7Vous avez torturer§f "+target.getName()+"§7 il a perdu 2"+AllDesc.coeur+"§7 permanent, cependant vous avez appris qu'il est du camp§a Soldat");
										giveHeartatInt(target, 1);
									}else {
										if (gamePlayer.getRole() instanceof Jelena) {
											target.sendMessage("§7Vous avez perdu 2"+AllDesc.coeur+"§7 permanent suite à la torture de§a Hansi§7 elle à donc compris que vous étiez dans le camp§a Soldat");
											owner.sendMessage("§7Vous avez torturer§f "+target.getName()+"§7 il a perdu 2"+AllDesc.coeur+"§7 permanent, cependant vous avez appris qu'il est du camp§a Soldat");
											giveHeartatInt(target, -2);
										}else {
											if (gamePlayer.getRole().getOriginTeam() == TeamList.Soldat) {
												target.sendMessage("§7Vous avez perdu 2"+AllDesc.coeur+"§7 permanent suite à la torture de§a Hansi§7 elle à donc compris que vous étiez dans le camp§a Soldat");
												owner.sendMessage("§7Vous avez torturer§f "+target.getName()+"§7 il a perdu 2"+AllDesc.coeur+"§7 permanent, cependant vous avez appris qu'il est du camp§a Soldat");
												giveHeartatInt(target, -2);
											}else {
												if (gamePlayer.getRole().getOriginTeam() != TeamList.Soldat) {
													tortured.add(target);
													target.sendMessage("§7Vous avez perdu 1"+AllDesc.coeur+"§7 permanent suite à la torture de§a Hansi§7 elle à donc compris que vous n'étiez pas dans le camp§a Soldat");
													owner.sendMessage("§7Vous avez gagner 1"+AllDesc.coeur+"§7 permanent suite à la torture sur§f "+target.getName()+"§7, vous avez donc compris qu'il§l n'est pas dans votre camp");
													giveHealedHeartatInt(owner, 1);
													gamePlayer.getRole().setMaxHealth(target.getMaxHealth()-2);
													target.setMaxHealth(gamePlayer.getRole().getMaxHealth());
												}
											}
										}
									}
								}
								actualtorture+=1;
								owner.sendMessage("Il ne vous reste que§6 "+(3-actualtorture)+"§f de /aot torture");
							}else {
								owner.sendMessage("Vous avez déjà asser torturé comme sa nan ?");
							}
						}else {
							owner.sendMessage("La personne visée ne possède pas de rôle");
						}
					}
				}else {
					owner.sendMessage("Veuiller indiquer un joueur");
				}
		}
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length == 2 && args[1] != null) {
				Player target = Bukkit.getPlayer(args[1]);
				if (!gameState.hasRoleNull(target.getUniqueId())) {
					if (actualseringue < 3) {
						GamePlayer gamePlayer = gameState.getGamePlayer().get(target.getUniqueId());
						if (gamePlayer.getRole() instanceof AotRoles){
							if (gamePlayer.getRole() instanceof SoldatsRoles && ((SoldatsRoles) gamePlayer.getRole()).isAckerMan())return;
							((AotRoles) gamePlayer.getRole()).setCanVoleTitan(true);
							target.sendMessage("§7Vous avez reçus une seringue");
						}
						actualseringue++;
						owner.sendMessage("§7Vous venez de donner une seringue à§a "+target.getName());
					} else {
						owner.sendMessage("§7Vous n'avez plus de seringue à disposition");
					}
				}
			}
		}
	}

    @Override
	public void resetCooldown() {
		actualtorture = 0;
		tortured.clear();
	}
}