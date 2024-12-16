package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.MahrRoles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.aot.builders.TitansRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Soldat extends SoldatsRoles {
	public enum kit {
		Garnison,
		Brigade,
		Bataillon,
	}
	public kit form = null;
	public Soldat(UUID player) {
		super(player);
	}
	@Override
	public Roles getRoles() {
		return Roles.Soldat;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Soldat",
				"",
				AllDesc.point+"A l'annonce des roles vous obtenez aléatoirement un kit, entre la §lGarnison§r, §lBrigade spéciale§r et §l Bataillon d'exploration",
				"",
				AllDesc.bar
				
		};
	}

	@Override
	public String getName() {
		return "Soldat";
	}

	private final String[] Garnison = new String[] {
            AllDesc.bar,
            "§lkit :§r Garnison",
            "",
            AllDesc.point+"Vous possèdez une bouteille d'alcool, en la buvant vous obtenez un pouvoir parmi ces derniers",
            "",
            AllDesc.point+AllDesc.Force+" 1 durant §l1 minute",
            AllDesc.point+AllDesc.Resi+" 1 durant §l1minute",
            AllDesc.point+"§2Nausée §rdurant §l1minute",
            "",
            AllDesc.point+"Votre bouteille d'alcool possède 3 minutes de cooldown"
    };
    private final String[] Brigade = new String[] {
    		AllDesc.bar,
            "§lkit :§r Brigade Spécial",
            "",
            AllDesc.point+"Vous possèdez une carabine, Si cette dernière touche un joueur vous lui infligerez 2 "+AllDesc.coeur,
            "Pour utiliser votre carabine vous devez être à moins de 15 blocs de ce dernier",
            "",
            AllDesc.bar,
    };
    private final String[] Bataillon = new String[] {
    		AllDesc.bar,
            "§lkit :§r Bataillon d'exploration",
            "",
            AllDesc.point+"Lorsque vous tuerez un titan transformé vous obtiendrez 7% d'un effet entre "+AllDesc.Resi+","+AllDesc.Force+"ou "+AllDesc.Speed,
            "",
            AllDesc.point+"Vous n'êtes pas affecter par le cri du titan Bestiale",
            "",
            AllDesc.bar,
    };
@Override
public void RoleGiven(GameState gameState) {
	int rint;
	do {
		rint = RandomUtils.getRandomInt(0, 2);
		System.out.println(rint);
	} while(rint < 0 || rint > 2);
	if (rint == 0) {
	if (form == kit.Garnison) {
		owner.sendMessage(Garnison);
		owner.getInventory().addItem(Items.getalcool());
	}
	}
	if (rint == 1) {
	if (form == kit.Brigade) {
		owner.sendMessage(Brigade);
		owner.getInventory().addItem(Items.getcarabine());
	}
	}
	if (rint == 2) {
	if (form == kit.Bataillon) {
		owner.sendMessage(Bataillon);
	}
	}
	super.RoleGiven(gameState);
}
int cdalcool = 0;
int cdcarabine = 0;
@Override
public boolean ItemUse(ItemStack item, GameState gameState) {
	if (item.isSimilar(Items.getalcool())) {
	if (cdalcool <= 0) {
	owner.sendMessage("Vous venez d'utiliser votre bouteille d'alcool");
	int rint = RandomUtils.getRandomInt(0, 2);
	if (rint == 0) {
		givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*(60), 0, true);
		cdalcool = 60*3;
	} else {
		if (rint == 1) {
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*(60), 0, true);
			cdalcool = 60*3;
		} else {
			if (rint == 2) {
				givePotionEffet(owner, PotionEffectType.CONFUSION, 20*(20), 3, true);
				cdalcool = 60*3;
				
			}
		}
	}
	}
	}
	if (item.isSimilar(Items.getcarabine())) {
		if (cdcarabine <= 0) {
			double min = 15;
			Player target = null;
			for (UUID u : gameState.getInGamePlayers()) {
				Player p = Bukkit.getPlayer(u);
				if (p == null)continue;
				if (owner.canSee(p) && p != owner) {
					double dist = Math.abs(p.getLocation().distance(owner.getLocation()));
					if (dist < min) {
						target = p;
						min = dist;
					}
				}
			}
			if (target != null) {
				if (target.getHealth() > 4) {
				if (owner.canSee(target)) {
					target.sendMessage(ChatColor.WHITE+"Vous venez d'être toucher par la carabine d'un soldat");
				target.setHealth(target.getHealth()- 4.0);
				}
				} else {
					owner.sendMessage("ce joueur possède moins de 2 coeurs");
				}
			} else {
				owner.sendMessage("Veuiller viser un joueur");
			}
		} else {
			sendCooldown(owner, cdcarabine);
		}
	}
	
	return super.ItemUse(item, gameState);
}
@Override
public void Update(GameState gameState) {
	if (cdalcool >= 0) {
		cdalcool -= 1;
	}
	if (cdcarabine >= 0) {
		cdcarabine -= 1;
	}
	super.Update(gameState);
}
@Override
public void resetCooldown() {
	cdalcool = 0;
	cdcarabine = 0;
}
@Override
public void PlayerKilled(Player killer, Player victim, GameState gameState) {
	if (killer == owner) {
		if (victim != null) {
			GamePlayer gamePlayer = gameState.getGamePlayer().get(victim.getUniqueId());
			if (gamePlayer.getRole() instanceof TitansRoles || gamePlayer.getRole() instanceof MahrRoles) {
				int rint = RandomUtils.getRandomInt(0, 2);
				if (rint == 0) {
					addBonusResi(7);
				    owner.sendMessage("Vous venez de tuez un titan et avez obtenue 7% de "+AllDesc.Resi);
				}
				if (rint == 1) {
					addBonusforce(7);
					owner.sendMessage("Vous venez de tuez un titan et avez obtenue 7% de "+AllDesc.Force);
				}
				if (rint == 2) {
					addSpeedAtInt(owner, 7);
					owner.sendMessage("Vous venez de tuez un titan et avez obtenue 7% de "+AllDesc.Speed);
				}
			}
		}
	}
	super.PlayerKilled(killer, victim, gameState);
}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
}