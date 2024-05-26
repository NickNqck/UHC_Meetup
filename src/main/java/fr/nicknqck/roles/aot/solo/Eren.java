package fr.nicknqck.roles.aot.solo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.aot.titans.Titans;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Eren extends RoleBase {
	private boolean killBestial = false;
	private boolean killPieck = false;
	private boolean killPorco = false;
	private boolean killBertolt = false;

	public Eren(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		canShift = true;
		gameState.GiveRodTridi(owner);
		Titans.Assaillant.getTitan().getListener().setAssaillant(owner.getUniqueId());
	}
	
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(getItems());
	}
	
	@Override
	public String[] Desc() {
		if (killBestial) {
			gameState.sendTitansList(owner);
		}
		KnowRole(owner, Roles.Gabi, 20);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Eren",
				"",
				"§7Vous possédez le titan "+Titans.Assaillant.getTitan().getName()+"§7,§6 /aot titan",
				"",
				AllDesc.capacite,
				"",
				AllDesc.point+"Si vous tuez l'un des rôles suivant Vous volerez des capacités du rôle tuez",
				"",
				AllDesc.point+"§cTitan Bestial :§r Vous obtiendrez tous les pseudos du camp des §cTitan§r.",
				AllDesc.point+"§9Pieck :§r Votre effet de "+AllDesc.Speed+" 1 pendant votre transformation passera à "+AllDesc.Speed+" 2",
				AllDesc.point+"§9Lara§r / §9Reiner :§r Si vous parvenez à tuez une de c'est deux personnes Vous obtiendrez 2 "+AllDesc.coeur+" Suplémentaire lors de votre transfomation / si vous parvenez à tuz les deux vous obtiendrez 4 "+AllDesc.coeur+" lors de votre transformation",
				AllDesc.point+"§9Porco :§r Vous obtiendrez la sa slime ball qui vous permetera de faire un bond en avant quand vous serez transformé",
				AllDesc.point+"§9Bertolt :§r Vous obtiendrez son cercle de feu utilisable uniquement quand vous serez transfomé",
				AllDesc.bar
		};
	}

	@Override
	public String getName() {
		return "§eEren";
	}

	private int cdbertolt = -1;
	private int cdPorco = -1;
	private List<ItemStack> itemToRecup = new ArrayList<>();
	
	ItemStack Cercle() {
		return BetterItem.of((new ItemBuilder(Material.MAGMA_CREAM)).setName("§c§lCercle de Feu").setLore("§7"+StringID).toItemStack(), (event) -> {
			if (isTransformedinTitan) {
				if (cdbertolt <= 0) {
					cdbertolt = 100+15;
					owner.sendMessage("Activation du§c§l Cercle de Feu");
					return true;
				}else {
					sendCooldown(owner, cdbertolt);
				}
			}else {
				owner.sendMessage("Il faut être Transformé en Titan");
			}
			return true;
		}).setDroppable(false).setMovableOther(false).setPosable(false).getItemStack();
	}
	ItemStack Saut() {
		return BetterItem.of(new ItemBuilder(Material.SLIME_BALL).setName("§lSaut").setLore("§7» Saut du Titan Machoire ","§7"+StringID).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack(), (event) -> {
			if (cdPorco <= 0) {
				if (isTransformedinTitan) {
					owner.sendMessage("§7C'est l'heure du Saut !");
					org.bukkit.util.Vector direction = owner.getLocation().getDirection();
		            direction.setY(0.8);
		            owner.setVelocity(direction.multiply(1.3));
		            cdPorco = 20;
				}else {
					owner.sendMessage("Il faut être transformé en Titan");
				}
			}else {
				sendCooldown(owner, cdPorco);
			}
			return true;
		}).setDespawnable(false).setDroppable(false).setPosable(false).setMovableOther(false).getItemStack();
	}
	private ItemStack[] items() {
		ItemStack one = null;
		ItemStack two = null;
		for (ItemStack item : Titans.Assaillant.getTitan().Items()) {
			if (one == null) {
				one = item;
			}else {
				two = item;
			}//je fais sa prck je sais que ce titan n'a que 2 items
		}
		if (killBertolt && killPorco) {
			return new ItemStack[] {
					one,
					two,
					Cercle(),
					Saut()
			};
		} else if (killBertolt && !killPorco) {
			return new ItemStack[] {
					one,
					two,
					Cercle()
			};
		}else if (!killBertolt && killPorco) {
			return new ItemStack[] {
					one,
					two,
					Saut()
			};
		}		
			return new ItemStack[] {
					one,
					two
			};
	}
	@Override
	public ItemStack[] getItems() {
		return items();
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
				if (item.getType().equals(Material.SLIME_BALL)) {
					if (cdPorco <= 0) {
						if (isTransformedinTitan) {
							owner.sendMessage("§7C'est l'heure du Saut !");
							org.bukkit.util.Vector direction = owner.getLocation().getDirection();
				            direction.setY(0.8);
				            owner.setVelocity(direction.multiply(1.3));
				            cdPorco = 20;
						}else {
							owner.sendMessage("Il faut être transformé en Titan");
						}
					}else {
						sendCooldown(owner, cdPorco);
					}
					return true;
				}else if (item.getType().equals(Material.MAGMA_CREAM)) {
					if (isTransformedinTitan) {
						if (cdbertolt <= 0) {
							cdbertolt = 100+15;
							owner.sendMessage("Activation du§c§l Cercle de Feu");
							return true;
						}else {
							sendCooldown(owner, cdbertolt);
						}
					}else {
						owner.sendMessage("Il faut être Transformé en Titan");
					}
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (item != null) {
			if (item.getType() != Material.AIR) {
				if (itemToRecup.contains(item)) {
					if (countEmptySlots(owner) > 0) {
						owner.sendMessage("§7Ajout de§f "+item.getItemMeta().getDisplayName());
						owner.getInventory().addItem(item);
						itemToRecup.remove(item);
					}
				}
			}
		}
		super.FormChoosen(item, gameState);
	}
	@Override	
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != null) {
				if (getPlayerRoles(victim).type == Roles.Bertolt) {
					killBertolt = true;
					giveItem(owner, false, Cercle());
					owner.sendMessage("§7Vous venez de tuez le joueur possédant le§9 Titan Colossal§7, vous obtenez donc l'item "+Cercle().getItemMeta().getDisplayName());
					cdbertolt = 0;
				}
				if (getPlayerRoles(victim).type == Roles.TitanBestial) {
					killBestial = true;
					owner.sendMessage("Vous venez de tuez le titan Bestial vous obtnez donc la liste des Titans");
				}
				if (getPlayerRoles(victim).type == Roles.Pieck) {
					killPieck = true;
					owner.sendMessage("Vous de tuez le joueur possédant le rôle Pieck vous obtiendrez donc "+AllDesc.Speed+" 2 durant votre transformation");
				}
				if (getPlayerRoles(victim).type == Roles.Lara) {
					owner.sendMessage("Vous venez de tuez le joueur possédant le rôle de§9 Lara§f vous obtenez donc 2"+AllDesc.coeur+" supplémentaire");
					giveHealedHeartatInt(owner, 2);
				}
				if (getPlayerRoles(victim).type == Roles.Porco) {
					killPorco = true;
					giveItem(owner, false, Saut());
					owner.sendMessage("Vous venez de tuez le joueur possédant le rôle de§9 Porco§f vous obtenez donc son§l Saut");
					cdPorco = 0;
				}
				if (getPlayerRoles(victim).type == Roles.Reiner) {
					giveHealedHeartatInt(owner, 2);
					owner.sendMessage("Vous venez de tuez le joueur possédant le§9 Titan Cuirasse§f vous obtenez donc 2"+AllDesc.coeur+" supplémentaire");
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (cdPorco >= 1) {
			cdPorco -= 1;
		} else if (cdPorco == 0) {
			owner.sendMessage("§lSaut§f est à nouveau utilisable !");
			cdPorco--;
		}
		if (cdbertolt >= 1) {
			cdbertolt -= 1;
			if (isTransformedinTitan) {
				if (cdbertolt >= 60) {
					MathUtil.sendCircleParticle(EnumParticle.FLAME, owner.getLocation(), 8, 24);
					for (Player p : gameState.getInGamePlayers()) {
						if (p.getLocation().distance(owner.getLocation()) <= 8) {
							p.setFireTicks(60);
						}
					}
					givePotionEffet(owner, PotionEffectType.FIRE_RESISTANCE, 60, 2, true);
				}else {
					if (cdbertolt == 60) {
						owner.sendMessage("Désactivation du§c§l Cercle de Feu");
					}
				}
			}	
		}else if (cdbertolt ==0) {
			owner.sendMessage("§c§lCercle de feu§f est à nouveau utilisable !");
			cdbertolt--;
		}
		if (isTransformedinTitan) {
			givePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
			if (killPieck) {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 2, true);
			} else {
				givePotionEffet(owner, PotionEffectType.SPEED, 60, 1, true);
			}
		}
		super.Update(gameState);
	}

	@Override
	public void resetCooldown() {
		cdbertolt = 0;
		cdPorco = 0;
	}
	
}