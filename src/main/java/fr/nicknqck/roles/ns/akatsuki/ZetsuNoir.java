package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.Loc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZetsuNoir extends AkatsukiRoles {

	public ZetsuNoir(Player player) {
		super(player);
		setChakraType(Chakras.DOTON);
		owner.sendMessage(Desc());
	}
	@Override
	public Roles getRoles() {
		return Roles.ZetsuNoir;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.ZetsuBlanc, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§c Zetsu Noir",
				AllDesc.objectifteam+" §cAkatsuki",
				"",
				AllDesc.capacite,
				"",
				AllDesc.point+"En retirant totalement votre armure vous deviendrez invisible et vous obtiendrez ansi l'effet "+AllDesc.Speed+"§e 2",
				"",
				AllDesc.point+"Une fois invisible si vous réstez à moins de 20 blocs d'un joueur pendant 3 minutes au cumulés vous obtiendrez son rôle",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez une régénération naturelle à hauteur d'un§c 1/2"+AllDesc.coeur+" toutes les 15 secondes",
				"Lorsque vous tuerez un joueur vous obtiendrez un pourcentage des effets qu'il avait",
				"Vous connaissez Zetsu Blanc et possédez un chat commun avec ce dernier mettez un§c !§f devant vote message pour lui parler",
				"",
				"Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
				"",
				AllDesc.bar
		};
	}

	@Override
	public ItemStack[] getItems() {
	    return new ItemStack[0];
	}
	@Override
	public void resetCooldown() {
		regencooldown = 0;
	}
	private int regencooldown = 0;
	private final Map<UUID, Integer> timePassedNearby = new HashMap<>();
	@Override
	public void Update(GameState gameState) {
		if (regencooldown >= 0) {
			regencooldown--;
			if (regencooldown == 0) {
				regencooldown = 15;
				Heal(owner, 2);
			}
		}
		if (gameState.isApoil(owner)) {
			givePotionEffet(PotionEffectType.INVISIBILITY, 20*2, 1, true);
			givePotionEffet(PotionEffectType.SPEED, 20*2, 2, true);
			for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
				if (gameState.hasRoleNull(p)) {
					return;
				} 
			if (timePassedNearby.containsKey(p.getUniqueId())) {
				int i = timePassedNearby.get(p.getUniqueId());
				timePassedNearby.remove(p.getUniqueId(), i);
				timePassedNearby.put(p.getUniqueId(), i+1);
					if (timePassedNearby.get(p.getUniqueId()) == 60*3) {
					owner.sendMessage("le rôle du joueur "+p.getName()+" est §c"+getPlayerRoles(p));
					}
			} else {
				timePassedNearby.put(p.getUniqueId(), Integer.MAX_VALUE);
			}
			}
		}
		super.Update(gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != null) {
					if (hasPermanentEffect(victim, PotionEffectType.DAMAGE_RESISTANCE)) {
						addBonusResi(10);
						owner.sendMessage("Vous venez de tuer un joueur possédant l'effet "+AllDesc.Resi+ " Vous gagnez donc 10% de cette effet");
					}
					if (hasPermanentEffect(victim, PotionEffectType.INCREASE_DAMAGE)) {
						addBonusforce(10);
						owner.sendMessage("Vous venez de tuer un joueur possédant l'effet "+AllDesc.Force+" Vous gagez donc 10% de cette effet");
					}
					if (hasPermanentEffect(victim, PotionEffectType.SPEED)) {
						addSpeedAtInt(owner, 10);
						owner.sendMessage("Vous venez de tuer un joueur possédant l'effet "+AllDesc.Speed+" Vous gagez donc 10% de cette effet");
					}
					if (hasPermanentEffect(victim, PotionEffectType.FIRE_RESISTANCE)) {
						givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, true);
						owner.sendMessage("Vous venez de tuer un joueur possédant l'effet "+AllDesc.fireResi+" Vous gagez donc cette effet");
					}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player blanc = getPlayerFromRole(Roles.ZetsuBlanc);
				owner.sendMessage(CC.translate("&cZetsu§8Noir§r: "+msg.substring(1)));
				if (blanc != null) {
					blanc.sendMessage(CC.translate("&cZetsu§8Noir§r: "+msg.substring(1)));
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}

	@Override
	public String getName() {
		return "§cZetsu Noir";
	}
}