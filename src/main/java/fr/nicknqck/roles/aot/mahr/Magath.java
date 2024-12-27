package fr.nicknqck.roles.aot.mahr;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ArrowTargetUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.UUID;

public class Magath extends MahrRoles {

	public Magath(UUID player) {
		super(player);
		toSearch = null;
	}

	@Override
	public void RoleGiven(GameState gameState) {
		giveHealedHeartatInt(4.0);
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Magath;
	}
	private Player toSearch;
	@Override
	public void Update(GameState gameState) {
		if (toSearch != null) {
			DecimalFormat df = new DecimalFormat("0");
			sendCustomActionBar(owner, toSearch.getName()+ArrowTargetUtils.calculateArrow(owner, toSearch.getLocation())+"("+df.format(owner.getLocation().distance(toSearch.getLocation()))+")");
			GamePlayer gamePlayer = gameState.getGamePlayer().get(toSearch.getUniqueId());
			if (gamePlayer.getRole() instanceof AotRoles && ((AotRoles) gamePlayer.getRole()).isTransformedinTitan && toSearch.getWorld().equals(owner.getWorld())) {
				if (owner.getLocation().distance(toSearch.getLocation()) <= 15){
					setResi(20);
					givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
				}else {
					setResi(0);
				}
			}			
		} else {
			setResi(0);
		}
	}

	@Override
	public String getName() {
		return "Magath";
	}

	@Override
	public String[] Desc() {
		Main.getInstance().getGetterList().getMahrList(owner);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Magath",
				"",
				AllDesc.capacite,
				"",
				AllDesc.point+"Vous possédez 2"+AllDesc.coeur+" supplémentaire, vous possédez également la list des§9 Mahr§r, cependant vous n'apparaissez pas dedans",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/aot search§r: Permet de chercher un de vos allier§9 Mahr§r possédant un Titan, de plus si la personne visée est transformée en Titan et que vous êtes proche (15blocs) de cette dernière vous obtiendrez "+AllDesc.Resi+" §9I",
				"",
				AllDesc.bar
		};	
	}
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("search")) {
			if (args.length == 1) {
				owner.sendMessage("Veuiller indiquer un pseudo !");
			}else {
				if (args[1] != null && args.length == 2) {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						owner.sendMessage("Veuiller écrire un pseudo correcte");
					}else {
						if (target.getUniqueId().equals(getPlayer())) {
							target.sendMessage("§cVous ne pouvez pas vous traquer vous même !");
							return;
						}
						if (!gameState.hasRoleNull(target.getUniqueId())) {
							RoleBase role = gameState.getGamePlayer().get(target.getUniqueId()).getRole();
							if (role instanceof MahrRoles) {
								if (toSearch == null) {
									toSearch = target;
									owner.sendMessage("Commencement de la traque de "+target.getName());
								}else {
									if (toSearch == target) {
										toSearch = null;
										owner.sendMessage("Fin de la traque de "+target.getName());
									} else {
										owner.sendMessage("Fin de la traque de "+toSearch.getName());
										toSearch = target;
										owner.sendMessage("Commencement de la traque de "+target.getName());
									}
								}
							}else {
								owner.sendMessage(target.getName()+" n'est pas traquable");
							}
						}
					}
				}else {
					owner.sendMessage("Veuiller indiquer un pseudo");
				}
			}			
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		toSearch = null;
		setResi(0);
	}
}