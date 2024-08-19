package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tsunade extends ShinobiRoles {

	public Tsunade(UUID player) {
		super(player);
		setChakraType(getRandomChakrasBetween(Chakras.DOTON, Chakras.KATON, Chakras.RAITON, Chakras.SUITON));
		setCanBeHokage(true);
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	@Override
	public Roles getRoles() {
		return Roles.Tsunade;
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Sakura, 15);
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
				AllDesc.point+KatsuyuItem().getItemMeta().getDisplayName()+"§f: Vous permet de §asoigner§f tout les joueurs précédemment choisis grâce a ce qui est stocké dans le§a Byakugo§f, via la commande§6 /ns katsuyu§f ",
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
	private ItemStack KatsuyuItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aKatsuyu").setLore("§7Vous permet de§d soigner§7 les personnes choisis (§6/ns katsuyu§7)").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				ByakugoItem(),
				KatsuyuItem()
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public void resetCooldown() {
		
	}
	private int SavedHP = 0;
	private boolean Receve = false;
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("katsuyu")) {
			owner.openInventory(KatsuyuInventory());
			owner.updateInventory();
		}
	}
	private final List<UUID> inKatsuyu = new ArrayList<>();
	private Inventory KatsuyuInventory() {
		Inventory inv = Bukkit.createInventory(owner, 54, "§aKatsuyu");
		for (UUID u : gameState.getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			if (!gameState.hasRoleNull(p)) {
				if (inKatsuyu.contains(p.getUniqueId())) {
					inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getUniqueId())).setName("§a"+p.getName()).toItemStack());
				} else {
					inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getUniqueId())).setName("§c"+p.getName()).toItemStack());
				}
			}
		}
		return inv;
	}
	@Override
	public void onInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (inv.getTitle().equals("§aKatsuyu") || inv.getName().equals("§aKatsuyu")) {
			if (item != null && item.getType() == Material.SKULL_ITEM) {
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (item.getItemMeta().getDisplayName().contains(p.getName())) {
							if (inKatsuyu.contains(p.getUniqueId())) {
								inKatsuyu.remove(p.getUniqueId());
                            } else {
								inKatsuyu.add(p.getUniqueId());
                            }
                            owner.openInventory(KatsuyuInventory());
                            owner.updateInventory();
                            event.setCancelled(true);
                            return;
                        }
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
		if (item.isSimilar(KatsuyuItem())) {
			if (inKatsuyu.isEmpty()) {
				owner.sendMessage("§7Il faut d'abord choisir qui vous voulez soigner");
				return true;
			}
			if (!a) {
				if (!u) {
					owner.sendMessage("§7Vous partagez maintenant votre§c vie");
					u = true;
					if (SavedHP > 0) {
						new BukkitRunnable() {
							@Override
							public void run() {
								if (a) {
									u = false;
									a = false;
									cancel();
								}
								if (inKatsuyu.isEmpty()) {
									a = true;
								}
								for (UUID t : inKatsuyu) {
									if (SavedHP > 0) {
										assert (Bukkit.getPlayer(t) != null);
										Player target = Bukkit.getPlayer(t);
										if (target.getHealth() <= (target.getMaxHealth()-1)) {
											target.setHealth(target.getHealth()+1);
											SavedHP--;
											sendCustomActionBar(owner, "§cHP§f:§c "+SavedHP);
										}
									} else {
										u = false;
										owner.sendMessage("§7Vous n'avez plus asser de§c HP§7 stocker pour pouvoir§a soigner");
										cancel();
									}
								}
							}
						}.runTaskTimer(Main.getInstance(), 0, 1);
                    } else {
						owner.sendMessage("§7Vous n'avez pas asser remplis votre§a Byakugo§7 pour faire ceci");
                    }
                    return true;
                } else {
					a = true;
					owner.sendMessage("§7Vous ne partagez plus votre§c vie");
				}
			} else {
                owner.sendMessage("§7Vous ne partagez plus votre§c vie");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					owner.sendMessage("§7Vous pouvez a nouveau partager votre§c vie");
					a = false;
				}, 20);
			}
		}
		return super.ItemUse(item, gameState);
	}
	private boolean a = false;
	private boolean u = false;
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
