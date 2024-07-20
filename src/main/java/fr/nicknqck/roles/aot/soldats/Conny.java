package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Conny extends SoldatsRoles {

	public Conny(Player player) {
		super(player);
	}
	@Override
	public Roles getRoles() {
		return Roles.Conny;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				"",
				AllDesc.role+"Conny",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"Vous possèdez un sucre qui selon votre choix de protection vous donnera des effets",
				"",
				AllDesc.point+"Si vous proteger Sasha : à l'activation de votre sucre vous obtiendrez "+AllDesc.Speed+" 2",
				AllDesc.point+"Si vous proteger Jean : à l'activation de votre sucre vous obtiendrez "+AllDesc.Speed+" 1 ainsi que "+AllDesc.Resi+" 1",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"/aot proteger ",
				"",
				AllDesc.point+"vous pouvez soit proteger Jean soit Sasha, vous obtiendrez le pseudo du joueur que vous protégerez si le joueur que vous avec protegez viens à mourir vous écoperez de l'effet "+AllDesc.weak+" 1 ansi que uniquement "+AllDesc.Speed+" 1 à l'activation de votre sucre",
				"",
				AllDesc.bar,
		};
	}
	private int cd = 0;
	private boolean Jean = false;
	private boolean Sasha = false;
	private boolean cmd = false;
	private boolean protegerdead = false;

	@Override
	public String getName() {
		return "§aConny";
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
			BetterItem.of(new ItemBuilder(Material.SUGAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§fSucre").setLore("§7"+StringID).toItemStack(), event -> {
				if (cd <= 0) {
					if (Jean) {
						if (!protegerdead) {
						givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 1, true);
						givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 20*(60*3), 1, true);
						cd = 240;
						} else {
							givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 1, true);
						}
					} else {
						if (Sasha) {
							if (!protegerdead) {
							givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 2, true);
							cd = 240;
							}else {
								givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 1, true);
							}
						} else {
							owner.sendMessage("Vous n'avez pas choisi de personne à proteger vous obtenez donc 0 effet");
						}
					}
				} else {
					sendCooldown(owner, cd);
				}
				return true;
			}).setDespawnable(false).setDroppable(false).setMovableOther(false).getItemStack()
		};
	}
	@Override
	public void Update(GameState gameState) {
		if (cd >= 1) {
			cd -= 1;
		}
	}
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("proteger")) {
			if (!cmd) {
			if (args[1].equalsIgnoreCase("Jean")) {
				for (Player p : gameState.getInGamePlayers()) {
					if (getPlayerRoles(p) instanceof Jean) {
				Jean = true;
				cmd = true;
				owner.sendMessage("Vous protegez désormais Jean vous obtenez donc le pseudo du Jean "+p.getName());
					}
				}
				} else {
				if (args[1].equalsIgnoreCase("Sasha")) {
					for (Player p : gameState.getInGamePlayers()) {
						if (getPlayerRoles(p) instanceof Sasha) {
					Sasha = true;
					cmd = true;
					owner.sendMessage("Vous protegez désormais Sasha vous obtenez donc le pseudo du Sasha "+p.getName());
						}
					}
				} else {
					owner.sendMessage("veuillez préciser si vous proteger Sasha ou Jean");
				}
			}
			} else {
				owner.sendMessage("Vous avez déjà fais votre commande");
			}
		} else {
			owner.sendMessage("Veuillez préciser une commande correct");
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player != null) {
			if (killer != null) {
				if (Jean) {
				for (Player p : gameState.getInGamePlayers()) {
					if (getPlayerRoles(p) instanceof Jean) {
						owner.sendMessage("Votre proteger viens de mourir vous obtenez désormais Weakness 1 ansi que speed 1 lors de votre utilisation de votre sucre");
						givePotionEffet(owner, PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1, true);
					}
				}
			} else {
				if (Sasha) {
					for (Player p : gameState.getInGamePlayers()) {
						if (getPlayerRoles(p) instanceof Sasha) {
							owner.sendMessage("Votre proteger viens de mourir vous obtenez désormais Weakness 1 ansi que speed 1 lors de votre utilisation de votre sucre");
							givePotionEffet(owner, PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1, true);
						}
					}
				}
			}
			}
		}
	}
	@Override
	public void resetCooldown() {
		cd = 0;
	}
}
