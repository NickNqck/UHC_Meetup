package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
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

public class Tsunade extends ShinobiRoles {

	public Tsunade(UUID player) {
		super(player);
		setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.KATON, Chakras.RAITON, Chakras.SUITON));
		setCanBeHokage(true);
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Tsunade;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aTsunade",
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
				"Vous possédez l'identité de§a Sakura",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
			};
	}
	private ItemStack ByakugoItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aByakugo").setLore("§7Vous permet de§c stocker§7 votre§c vie§7 ou d'utiliser la vie§c stocker").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				ByakugoItem()
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

    private int SavedHP = 0;
	private boolean Receve = false;
	@Override
	public void Update(GameState gameState) {
		OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}
	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.INTELLIGENT;
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(ByakugoItem())) {
			if (!Receve) {
				SavedHP += 2;
				Heal(owner, -2.0);
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
	public String getName() {
		return "Tsunade";
	}

    @Override
    public void RoleGiven(GameState gameState) {
        addKnowedRole(Sakura.class);
        Main.getInstance().getKatsuyuManager().addUser(getPlayer());
        super.RoleGiven(gameState);
    }

    private static class onTick extends BukkitRunnable {
		private final Tsunade sakura;
		private onTick(Tsunade sakura) {
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
