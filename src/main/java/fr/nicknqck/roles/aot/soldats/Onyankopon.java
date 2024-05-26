package fr.nicknqck.roles.aot.soldats;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.betteritem.BetterItem;

public class Onyankopon extends RoleBase{

	public Onyankopon(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
		owner.getInventory().addItem(getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Onyankopon",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"Fuite: vous permez de téléportez tous les joueurs dans un rayon de 20 blocs aléatoirement sur la carte, vous possédez 1min de cooldown ainsi que 2 utilisation maximum",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§l/aot fuite§r: fait exactement la même chose que l'item§l Fuite","",
				AllDesc.bar,
		};
	}
	int cd = 0;
	int use = 0;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				BetterItem.of(new ItemBuilder(Material.NETHER_STAR).setName("§lFuite").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack(), (event) -> {
					if (cd <= 0) {
						if (use <= 2) {
							fuite();
						} else {
							owner.sendMessage("Vous avez déjà utilisez votre fuite "+use+" fois");
						}
					} else {
						sendCooldown(owner, cd);
					}
					return true;
				}).setDespawnable(false).setDroppable(false).getItemStack()
		};
	}
	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("fuite")) {
			if (cd <= 0) {
				if (use <= 2) {
					fuite();
				} else {
					owner.sendMessage("Vous avez déjà utilisez votre fuite 2 fois");
				}
			} else {
				sendCooldown(owner, cd);
			}
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (cd > 0) {
			cd -= 1;
		}else if (cd == 0) {
			owner.sendMessage("§lFuite est à nouveau utilisable !");
			cd--;
		}
		super.Update(gameState);
	}
	private void fuite() {
		for (Player p:gameState.getNearbyPlayers(owner, 20)) {
			GameListener.RandomTp(p, gameState, Main.getInstance().gameWorld);
			cd = 60;
		}
		use += 1;
	}
	@Override
	public void resetCooldown() {
		use = 0;
		cd = 0;
	}
}