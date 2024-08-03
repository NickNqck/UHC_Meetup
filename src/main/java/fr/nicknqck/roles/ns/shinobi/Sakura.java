package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Sakura extends ShinobiRoles {

	public Sakura(Player player) {
		super(player);
		setChakraType(getRandomChakras());
		setCanBeHokage(true);
	}
	@Override
	public Roles getRoles() {
		return Roles.Sakura;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aSakura",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§cForce I§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+ByakugoItem().getItemMeta().getDisplayName()+"§f: Vous permet de sois§c stocker votre vie§f sois§c récupérer votre vie§f stocker dans votre item, pour changer de mode d'utilisation, il faudra faire clique gauche",
				"",
				AllDesc.particularite,
				"",
				AllDesc.chakra+getChakras(),
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				ByakugoItem()
		};
	}
	@Override
	public void resetCooldown() {
		
	}
	private ItemStack ByakugoItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dByakugo").setLore("§7Vous permet de stocker des "+AllDesc.coeur).toItemStack();
	}
	private int SavedHP = 0;
	private boolean Receve = false;
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}
	@Override
	public void onTick() {
		if (owner.getItemInHand().isSimilar(ByakugoItem())) {
			sendCustomActionBar(owner, "§cHP§f:§c "+SavedHP);
		}
	}
	
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(ByakugoItem())) {
			if (!Receve) {
				SavedHP += 1;
				Heal(owner, -1.0);
				owner.damage(0.0);
			} else {
				if (SavedHP > 0) {
					if ((owner.getHealth() + 1.0) <= getMaxHealth()) {
						SavedHP -=1;
						Heal(owner, 1.0);
					}
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
		if (player.getUniqueId() == owner.getUniqueId()) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem().isSimilar(ByakugoItem())) {
					if (Receve) {
						Receve = false;
						owner.sendMessage("§7Vous pouvez à nouveau charger votre§d Byakugo");
					} else {
						Receve = true;
						owner.sendMessage("§7Vous pouvez à nouveau utiliser la§c vie§7 contenue dans votre§d Byakugo");
					}
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public String getName() {
		return "§aSakura";
	}
}