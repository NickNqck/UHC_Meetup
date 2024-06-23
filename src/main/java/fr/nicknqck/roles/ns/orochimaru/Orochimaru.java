package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.HubListener;
import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orochimaru extends OrochimaruRoles {

	public Orochimaru(Player player) {
		super(player);
		setChakraType(getRandomChakras());
		chakrasVoled.add(getChakras());
		owner.sendMessage(Desc());
	}
	@Override
	public Roles getRoles() {
		return Roles.Orochimaru;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setResi(20);
	}
	private final List<Chakras> chakrasVoled = new ArrayList<>();
	@Override
	public String[] Desc() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Chakras chakras : chakrasVoled) {
			i++;
			if (i + 1 != chakrasVoled.size()+1) {
				sb.append(chakras.getShowedName()).append("§f,");
			}else {
				sb.append(chakras.getShowedName()).append("§f.");
			}
		}
		List<Player> mates = new ArrayList<>();
		for (Player p : getIGPlayers()) {
			if (!gameState.hasRoleNull(p)) {
				if (getTeam(p) != null && p.getUniqueId() != owner.getUniqueId()) {
					if (getTeam(p) == TeamList.Orochimaru || getPlayerRoles(p) instanceof Sasuke) {
						mates.add(p);
					}
				}
			}
		}
		if (!mates.isEmpty()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				owner.sendMessage("Voici la liste de vos coéquipier: ");
				mates.forEach(p -> owner.sendMessage("§7 - §5"+p.getName()));}, 1);
		}
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Orochimaru",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+AllDesc.Resi+"§9 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+KusanagiItem().getItemMeta().getDisplayName()+"§f: Épée en diamant tranchant 4",
				"",
				AllDesc.point+EdoTenseiItem().getItemMeta().getDisplayName()+"§f: Permet de réssusciter un joueur que vous avez précedemment tuer et de le faire rejoindre votre camp en échange de§c 3"+AllDesc.coeur+" permanent",
				"",
				AllDesc.particularite,
				"",
				"En tuant un joueur vous gagnez§e 4"+AllDesc.Coeur("§e")+"§e d'absorbtion",
				"En tuant un joueur vous aurez 25% de chance de gagner sa nature de Chakra",
				"En mangeant une§e pomme d'or§f vous obtenez§e 3"+AllDesc.Coeur("§e")+"§e d'absorbtion§f au lieu de§e 2"+AllDesc.Coeur("§e"),
				"Vous possédez une nature de Chakra aléatoire",
				"",
				"Vos natures de Chakras: "+ sb,
				AllDesc.bar
		};
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
	}
	private ItemStack EdoTenseiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei").setLore("§7Permet de réssusciter jusqu'à deux personnes que vous avez tuer").toItemStack();
	}
	private ItemStack KusanagiItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§5Kusanagi").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setLore("§7L'épée légendaire en possession du§5 Orochimaru").toItemStack();
	}
	private final HashMap<Player, RoleBase> edoTensei = new HashMap<>();
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (edoTensei.containsKey(player)) {
			edoTensei.remove(player, getPlayerRoles(player));
		}
		if (killer.getUniqueId() == owner.getUniqueId()) {
			((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+4.0f);
			if (player.getLocation().getWorld().equals(Main.getInstance().gameWorld)) {
				killLoc.put(player, player.getLocation());
			} else {
				Location rLoc = new Location(Main.getInstance().gameWorld, 0.0, 75, 0.0, player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
				killLoc.put(player, rLoc);
			}
			if (getPlayerRoles(player).getChakras() != null) {
				if (RandomUtils.getOwnRandomProbability(25)) {
					if (!chakrasVoled.contains(getPlayerRoles(player).getChakras())) {
						chakrasVoled.add(getPlayerRoles(player).getChakras());
						getPlayerRoles(player).getChakras().getChakra().getList().add(owner.getUniqueId());
						owner.sendMessage("§7Vous maitrisez maintenant la nature de Chakra: "+getPlayerRoles(player).getChakras().getShowedName());
					}
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KusanagiItem(),
				EdoTenseiItem()
		};
	}
	@Override
	public void onEat(ItemStack item, GameState gameState) {
		if (item.getType() == Material.GOLDEN_APPLE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				owner.removePotionEffect(PotionEffectType.ABSORPTION);
				((CraftPlayer) owner).getHandle().setAbsorptionHearts(0);
				((CraftPlayer) owner).getHandle().setAbsorptionHearts(((CraftPlayer) owner).getHandle().getAbsorptionHearts()+6.0f);
			}, 1);
		}
	}
	private final HashMap<Player, Location> killLoc = new HashMap<>();
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(EdoTenseiItem())) {
			if (killLoc.isEmpty()) {
				owner.sendMessage("§7Il faut avoir tué au moins§n 1 joueurs§7 pour utiliser cette technique");
				return true;
			}
			if (!edoTensei.isEmpty()) {
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
					if (killLoc.get(p).getWorld().equals(owner.getWorld())) {
						if (owner.getLocation().distance(killLoc.get(p)) <= 50) {
							inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(p.getUniqueId())).setName(p.getName()).setLore("§7Cliquez ici pour réssusciter ce joueur !").toItemStack());
						}
					}
				}
			}
			owner.openInventory(inv);
			owner.updateInventory();
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
						getPlayerRoles(clicked).setTeam(getPlayerRoles(clicker).getOriginTeam());
						HubListener.getInstance().giveStartInventory(clicked);
						gameState.RevivePlayer(clicked);
						setMaxHealth(getMaxHealth()-4.0);
						clicked.teleport(clicker);
						giveItem(clicked, false, getPlayerRoles(clicked).getItems());
						killLoc.remove(clicked);
						clicked.resetTitle();
						clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+TeamList.Orochimaru);
						
					}
				}
			}
		}
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.GENIE;
	}

	@Override
	public void resetCooldown() {
	}

	@Override
	public String getName() {
		return "§5Orochimaru";
	}
}