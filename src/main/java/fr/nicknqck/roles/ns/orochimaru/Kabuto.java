package fr.nicknqck.roles.ns.orochimaru;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.ItemBuilder;

public class Kabuto extends RoleBase{

	public Kabuto(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		setChakraType(Chakras.SUITON);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la partie vous avez tout de même reçus les bonus dû à sa mort !");
			}
		}, 20*5);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public ItemStack[] getItems() {
		if (mortOrochimaru) {
			return new ItemStack[] {
					EdoTenseiItem(),
					NinjutsuMedicalItem()
			};
		}
		return new ItemStack[] {
				NinjutsuMedicalItem()
		};
	}
	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Orochimaru, 5);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Kabuto",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§eSpeed 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aNinjutsu Médical§f: Effectue une action en fonction du clique:",
				"§7     →§f Clique droit: En visant un joueur, celà permet de le soigner de§c 2"+AllDesc.coeur,
				"§7     →§f Clique gauche: Vous soigne de§c 2"+AllDesc.coeur,
				"§4!§cLe cooldown des deux clique est partagé§4!",
				"",
				AllDesc.particularite,
				"",
				"A la mort de§5 Orochimaru§f vous obtenez l'item§5 Edo Tensei§f qui permet de tué une personne que vous avez précédemment tué",
				"",
				AllDesc.bar
		};
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (ninjutsuCD >= 0) {
			ninjutsuCD--;
			if (ninjutsuCD == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau soigner quelqu'un");
			}
		}
	}
	@Override
	public void resetCooldown() {
		ninjutsuCD = 0;
		edoTensei.clear();
	}
	private ItemStack EdoTenseiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei").setLore("§7Hérité de§5 Orochimaru§7 cette technique permet de réssusciter 1 personne que vous avez tuer").toItemStack();
	}
	private ItemStack NinjutsuMedicalItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aNinjutsu Médical").setLore("§7Permet de vous soignez vous ou un autre joueur").toItemStack();
	}
	private int ninjutsuCD = 0;
	private HashMap<Player, RoleBase> edoTensei = new HashMap<>();
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (edoTensei.containsKey(player)) {
			edoTensei.remove(player, getPlayerRoles(player));
		}
		if (getPlayerRoles(player).type == Roles.Orochimaru) {
			if (!mortOrochimaru) {
				onOrochimaruDeath(true);
			}
		}
		if (killer.getUniqueId() == owner.getUniqueId() && mortOrochimaru) {
			if (player.getLocation().getWorld().equals(Main.getInstance().gameWorld)) {
				killLoc.put(player, player.getLocation());
			} else {
				Location rLoc = new Location(Main.getInstance().gameWorld, 0.0, 75, 0.0, player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
				killLoc.put(player, rLoc);
			}
			((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+2.0f);
		}
	}
	private void onOrochimaruDeath(boolean msg) {
		owner.getInventory().removeItem(getItems());
		mortOrochimaru = true;
		giveItem(owner, true, getItems());
		if (msg) {
			owner.sendMessage("§7C'est terrible !§5 Orochimaru§7 est§c mort§7, en son homage vous récupérez sa plus puissante technique, l'§5Edo Tensei§7.");
		}
	}
	private HashMap<Player, Location> killLoc = new HashMap<>();
	private boolean mortOrochimaru = false;
	@Override
	public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
		if (event.getItem() != null) {
			if (player.getUniqueId() == owner.getUniqueId()) {
				if (event.getItem().isSimilar(NinjutsuMedicalItem())) {
					event.setCancelled(true);
					if (ninjutsuCD > 0) {
						sendCooldown(owner, ninjutsuCD);
						return;
					}
					if (event.getAction().name().contains("LEFT")) {
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
						player.sendMessage("§dSoins !");
						Heal(owner, 4);
					} else {
						Player target = getTargetPlayer(owner, 30);
						if (target == null) {
							owner.sendMessage("§cIl faut viser un joueur !");
							return;
						}
						Heal(target, 4);
						player.sendMessage("§dSoins !");
						target.sendMessage("§5Kabuto§f vous à§d soigner§f !");
						if (!mortOrochimaru) {
							ninjutsuCD = 60*2;
						}else {
							ninjutsuCD = 60;
						}
					}
				}
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (mortOrochimaru) {
			if (item.isSimilar(EdoTenseiItem())) {
				if (killLoc.size() < 1) {
					owner.sendMessage("§7Il faut avoir tué au moins§n 1 joueurs§7 pour utiliser cette technique");
					return true;
				}
				if (edoTensei.size() != 0) {
					owner.sendMessage("§7Vous avez déjà§5 Edo Tensei");
					return true;
				}
				Inventory inv = Bukkit.createInventory(owner, 54, "§5Edo Tensei");
				for (int i = 0; i <= 8; i++) {
					if (i == 4) {
						inv.setItem(i, GUIItems.getSelectBackMenu());
					} else {
						inv.setItem(i, GUIItems.getOrangeStainedGlassPane());
					}
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (killLoc.containsKey(p)) {
						if (owner.getLocation().distance(killLoc.get(p)) <= 50) {
							inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getUniqueId())).setName(p.getName()).setLore("§7Cliquez ici pour réssusciter ce joueur !").toItemStack());
						}
					}
				}
				owner.openInventory(inv);
				owner.updateInventory();
			}
		}
		return super.ItemUse(item, gameState);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onAllPlayerInventoryClick(InventoryClickEvent event, ItemStack item, Inventory inv, Player clicker) {
		if (clicker.getUniqueId() != owner.getUniqueId())return;
		if (inv.getTitle().equals("§5Edo Tensei")) {
			if (item == null) {
				return;
			}
			if (item.getType() == Material.AIR) {
				return;
			}
			if (item.hasItemMeta()) {
				if (item.getItemMeta().hasDisplayName()) {
					Player clicked = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
					event.setCancelled(true);
					if (clicked != null) {
						edoTensei.put(clicked, getPlayerRoles(clicked));
						clicker.closeInventory();
						clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
						clicker.sendMessage("§5Edo Tensei !");
						getPlayerRoles(clicked).setTeam(getPlayerRoles(clicker).getTeam());
						HubListener.getInstance().giveStartInventory(clicked);
						gameState.RevivePlayer(clicked);
						setMaxHealth(getMaxHealth()-4.0);
						clicked.teleport(clicker);
						giveItem(clicked, false, getPlayerRoles(clicked).getItems());
						killLoc.remove(clicked);
						clicked.resetTitle();
						clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+getTeam(clicked).name());
						
					}
				}
			}
		}
	}
}