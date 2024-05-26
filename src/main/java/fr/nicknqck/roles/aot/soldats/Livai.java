package fr.nicknqck.roles.aot.soldats;

import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.betteritem.BetterItem;
import fr.nicknqck.utils.betteritem.BetterItemEvent;

public class Livai extends RoleBase{

	public Livai(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
		setAckerMan(true);
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Livaï",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6§lSucre§r: A son activation vous octroie les effets "+AllDesc.Speed+" 2 ansi que "+AllDesc.Force+" 1",
				"",
				AllDesc.point+"§4ATTENTION§r : ce pouvoir perd en efficacité à chaque utilisation",
				"",
				AllDesc.point+"§lPremière utilisation :§r "+AllDesc.Force+" 1 et "+AllDesc.Speed+" 2 pendant 4 minutes",
				"",
				AllDesc.point+"§lDeuxième utilisation :§r "+AllDesc.Force+" 1 et "+AllDesc.Speed+" 1 pendant 3 minutes",
				"",
				AllDesc.point+"§lTroisième utilisation :§r "+AllDesc.Speed+" 1 pendant 2 minutes",
				"",
				AllDesc.point+"Chaque utilisation suivantes donneront également"+AllDesc.Speed+"1 pendant 2 minutes",
				"",
				AllDesc.point+"Cependant si vous venez à manger 15 pommes en or, vous reculerez d'un stade d'utilisation",
				AllDesc.point+"si vous êtes au deuxième stade et que vous mangez 15 pommes en or vous passerez au stade 1",
				"",
				AllDesc.point+"Si vous effectuez un clique gauche avec votre équipement tridimensionelle sur un joueurr, vous serez téléportez sur celui-ci",
				"",
				AllDesc.bar
		};
	} 
		@Override
		public void GiveItems() {
			owner.getInventory().addItem(Items.getsugar());
		}
		@Override
		public ItemStack[] getItems() {
			return new ItemStack[] {
					Items.getsugar()
			};
		}

	@Override
	public String getName() {
		return "§aLivai";
	}

	private int cdsugar = 0;
		private int nbsugar = 0;
		@Override
		public boolean ItemUse(ItemStack item, GameState gameState) {
			if(item.isSimilar(Items.getsugar())) {
				if (cdsugar <= 0) {
					if (nbsugar <= 0) {
						givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*4), 2, true);
						givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*(60*4), 1, true);
							cdsugar = 60*3;
							nbsugar += 1;
					} else {
						if (nbsugar == 1) {
							givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*(60*3), 1, true);
							givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*3), 1, true);
							cdsugar = 60*3;
							nbsugar+= 1;
						} else {
							if (nbsugar >= 2) {
								givePotionEffet(owner, PotionEffectType.SPEED, 20*(60*2), 1, true);
								cdsugar = 60*3;
							}
						}
					}
				} else {
					sendCooldown(owner, cdsugar);
				}
			}
			return super.ItemUse(item, gameState);
		}
		private int nbgapple = 0;
		@Override
		public void onEat(ItemStack item, GameState gameState) {
			if (item.getType().equals(org.bukkit.Material.GOLDEN_APPLE)) {
				nbgapple += 1;
			}
			super.onEat(item, gameState);
		}
		@Override
		public void Update(GameState gameState) {
			if (nbgapple == 15) {
				owner.sendMessage("Vous avez mangé 15 Pomme en or vous reculez donc d'un stade");
				nbsugar -=1;
				nbgapple = 0;
			}
			if (cdsugar >= 1) {
				cdsugar -= 1;
			}
			if (cdtp >= 1) {
				cdtp -= 1;
			}
			if (cdsugar == 0) {
				owner.sendMessage("Vous pouvez de nouveau utiliser votre sucre");
				cdsugar -=1;
			}
			if (cdtp == 0) {
				owner.sendMessage("Vous pouvez de nouveau vous téléportez sur un joueur");
				cdtp -= 1;
			}
			super.Update(gameState);
		}
		private int cdtp = 0;
		@Override
		public void onEndGame() {
			ItemStack stack = gameState.EquipementTridi();
			Predicate<BetterItemEvent> eventPredicate  = new Predicate<BetterItemEvent>() {
				@Override
				public boolean test(BetterItemEvent t) {
					return false;
				}
			};
			BetterItem item = new BetterItem(stack, eventPredicate);
			BetterItem.registeredItems.remove(item);
		}
		@Override
		public void resetCooldown() {
			cdtp = 0;
			cdsugar = 0;
			nbsugar = 0;
			nbgapple = 0;
		}
}