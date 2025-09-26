package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Sakura extends ShinobiRoles {

	public Sakura(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
		setCanBeHokage(true);
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	@Override
	public @NonNull Roles getRoles() {
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

    private ItemStack ByakugoItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§dByakugo").setLore("§7Vous permet de stocker des "+AllDesc.coeur).toItemStack();
	}
	private int SavedHP = 0;
	private boolean Receve = false;
	@Override
	public void Update(GameState gameState) {
		OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}

	public void onTick() {

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
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public String getName() {
		return "Sakura";
	}
	private static class onTick extends BukkitRunnable {
		private final Sakura sakura;
		private onTick(Sakura sakura) {
			this.sakura = sakura;
		}
		@Override
		public void run() {
			if (sakura.getGameState().getServerState() != GameState.ServerStates.InGame) {
				cancel();
				return;
			}
			Player owner = Bukkit.getPlayer(sakura.getPlayer());
			if (owner != null) {
				if (owner.getItemInHand().isSimilar(sakura.ByakugoItem())) {
					sakura.sendCustomActionBar(owner, "§cHP§f:§c "+sakura.SavedHP);
				}
			}
		}
	}
}