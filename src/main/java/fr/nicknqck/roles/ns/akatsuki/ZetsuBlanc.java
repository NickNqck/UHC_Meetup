package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;

public class ZetsuBlanc extends NSRoles {

	public ZetsuBlanc(Player player, Roles roles) {
		super(player, roles);
		setChakraType(Chakras.DOTON);
		owner.sendMessage(Desc());
		giveItem(owner, false, getItems());
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.ZetsuNoir, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cZetsu Blanc",
				AllDesc.objectifteam+"§c Akatsuki",
				"",
				AllDesc.items,
				"",
				AllDesc.point+SporeItem().getItemMeta().getDisplayName()+"§f: Ouvre un menu pour déposer vos spores sur un joueur. Une fois vos Spores posés sur un joueur, la première fois que vous tomberez sous la bar des§c 3"+AllDesc.coeur+" vous serez téléportez sur le joueur et vous re-gagnerez l'intégralité de vos point de vie",
				"",
				AllDesc.particularite,
				"",
				"Vous connaissez le §cZetsu §0Noir§r et possédez un Chat commun avec ce dernier vous pouvez y accéder en mettant un §c! §r devant votre message",
				"",
				"Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
				"",
				AllDesc.bar
		};
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				SporeItem()
		};
	}
	private ItemStack SporeItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cSpore").setLore("§rVous permez de déposer vos spores sur un joueur").toItemStack();
	}
	@Override
	public void resetCooldown() {
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerChat(org.bukkit.event.player.PlayerChatEvent e, Player p) {
		if (p.getUniqueId() == owner.getUniqueId()) {
			if (e.getMessage().startsWith("!")) {
				String msg = e.getMessage();
				Player obito = getPlayerFromRole(Roles.ZetsuNoir);
				owner.sendMessage(CC.translate("&cZetsu §rBlanc : "+msg.substring(1)));
				if (obito != null) {
					obito.sendMessage(CC.translate("&cZetsu §rBlanc : "+msg.substring(1)));
				}
			}
		}
	}
	private void openSporeInventory() {
		Inventory inv = Bukkit.createInventory(owner, 54, "§cSpore");
	for (int i = 0; i <= 8; i++) {
		if (i == 4) {
			inv.setItem(i, GUIItems.getSelectBackMenu());
		} else {
			inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
		}
	}
	for (Player p : Loc.getNearbyPlayers(owner, 30)) {
		inv.addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(((short)3)).setName(p.getDisplayName()).toItemStack());
	}
	owner.openInventory(inv);
}
	private boolean Spores = false;
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (inv.getTitle().equalsIgnoreCase("§cSpore")) {
			event.setCancelled(true);
			if (!item.isSimilar(GUIItems.getSelectBackMenu())) {
				if (!item.hasItemMeta())return;
				if (!item.getItemMeta().hasDisplayName())return;
				Player player = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
				if (player == null)return;
				if (Loc.getNearbyPlayers(owner, 30).contains(player)) {
					player.sendMessage("Zetsu Blanc viens de déposez ses Spores sur vous");
					owner.sendMessage("Vous venez de déposer vos spores sur "+player.getName());
					Spores = true;
					sporesUse++;
					new BukkitRunnable() {
						@Override
						public void run() {
							if (Spores) {
								if (owner.getHealth() <= 6.0) {
									owner.setHealth(owner.getHealth() + 14.0);
									owner.sendMessage("Vous venez d'utilisez vos spores");
									owner.teleport(player);
									Spores = false;
								}							
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
				}
			}
		}
	}
	private int sporesUse = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SporeItem())) {
			if (!Spores) {
				if (sporesUse == 0) {
					openSporeInventory();
					return true;
				}
			} else {
				owner.sendMessage("Vous avez déjà utiliser vos Spores");
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "§cZetsu Blanc";
	}
}
