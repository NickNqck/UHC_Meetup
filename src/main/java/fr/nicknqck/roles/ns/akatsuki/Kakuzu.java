package fr.nicknqck.roles.ns.akatsuki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;

public class Kakuzu extends NSRoles {

	public Kakuzu(Player player, Roles roles) {
		super(player, roles);
		setChakraType(getRandomChakras());
		owner.sendMessage(Desc());
		ChakrasOwned.put(getChakras(), true);
		setForce(20);
		giveItem(owner, false, getItems());
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Hidan, 20);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cKakuzu",
				AllDesc.objectifteam+"§cAkatsuki",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§cForce 1§f permanent",
				"",
				"§lItem:",
				"",
				AllDesc.point+"§cCorps Rapiécé§§f: Empêche tout joueur n'étant pas dans votre camp de bouger pendant§c 5s§f.§7 (1x/3m)",
				"",
				AllDesc.commande,
				"",
				AllDesc.point+"§6/ns change§f: Vous permet de choisir une nature de Chakra parmis celles que vous possédez",
				"",
				AllDesc.particularite,
				"",
				"Votre nature de chakra est aléatoire",
				"Tant que vous avez une nature de chakra vous ne mourrez pas, mais a la place vous perdez la nature de chakra utilisé",
				"Quand vous tuez un joueur vous récuperez sa nature de chakra si vous ne la possédiez pas déja",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private int cdCorpsRapiece = 0;
	private int changeCD = 0;
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				CorpRapieceItem()
		};
	}
	private ItemStack CorpRapieceItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cCorps Rapiécé").setLore("§7Empêche les joueurs proche de bouger").toItemStack();	
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (!gameState.hasRoleNull(victim)) {
			if (getPlayerRoles(victim).getChakras() != null && victim != owner && killer == owner && !ChakrasOwned.containsKey(getPlayerRoles(victim).getChakras())) {
				ChakrasOwned.put(getPlayerRoles(victim).getChakras(), true);
				owner.sendMessage("§7Vous maitrisez maintenant le "+getPlayerRoles(victim).getChakras().getShowedName());
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(CorpRapieceItem())) {
			if (cdCorpsRapiece > 0) {
				sendCooldown(owner, cdCorpsRapiece);
				return true;
			}
				cdCorpsRapiece=	60*3+5;
				HashMap<Player, Location> rap = new HashMap<>();
				for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
					if (!gameState.hasRoleNull(p)) {
						if (getTeam(p) != getTeam()) {
							rap.put(p, p.getLocation());
							owner.sendMessage("§7Vos§c Corps Rapiécé§7 on touché§c "+p.getDisplayName());
						}
					}
				}
				new BukkitRunnable() {
					int tick = 100;
					@Override
					public void run() {
						for (Player p : rap.keySet()) {
							p.teleport(rap.get(p));
						}
						if (!getIGPlayers().contains(owner)) {
							cancel();
							return;
						}
						if (tick == 0) {
							owner.sendMessage("§7Votre§c Corps Rapiécé§7 ne fais plus effet");
							rap.clear();
							cancel();
						}
						tick--;
					}
				}.runTaskTimer(Main.getInstance(), 0, 1);
				return true;
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (cdCorpsRapiece >= 0) {
			cdCorpsRapiece--;
			if (cdCorpsRapiece == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c Corps Rapiécé");
			}
		}
		if (changeCD >= 0) {
			changeCD--;
			if (changeCD == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau changer de nature de Chakras");
			}
		}
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
	}
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("change")) {
			if (changeCD <= 0) {
				Inventory inv = Bukkit.createInventory(owner, 27, "Nature de Chakra");
				int i = 9;
				for (Chakras ch : Chakras.values()) {
					if (ChakrasOwned.containsKey(ch)) {
						if (ChakrasOwned.get(ch)) {
							if (getChakras() == ch) {
								inv.setItem(i, new ItemBuilder(Material.INK_SACK).setDurability(ch.getColorCode()).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName(ch.getShowedName()).toItemStack());
							} else {
								inv.setItem(i, new ItemBuilder(Material.INK_SACK).setDurability(ch.getColorCode()).setName(ch.getShowedName()).toItemStack());
							}
							i+=2;
						}
					}
				}
				owner.openInventory(inv);
			} else {
				sendCooldown(owner, changeCD);
			}
		}
	}
	@Override
	public void FormChoosen(ItemStack item, GameState gameState) {
		if (item != null) {
			if (item.hasItemMeta()) {
				for (Chakras ch : Chakras.values()) {
					if (ChakrasOwned.containsKey(ch)) {
						if (getChakras() != ch) {
							getChakras().getChakra().getList().remove(owner.getUniqueId());
							setChakraType(ch);
							owner.sendMessage("§7Vous pouvez maintenant utilisé le "+ch.getShowedName());
							ch.getChakra().getList().add(owner.getUniqueId());
							owner.closeInventory();
							owner.updateInventory();
							return;
						}
					}
				}
			}
		}
	}
	@Override
	public boolean onPreDie(Entity damager, GameState gameState) {
		List<Chakras> chakras = new ArrayList<>();
		for (Chakras ch : Chakras.values()) {
			if (ch.getChakra().getList().contains(owner.getUniqueId())) {
				chakras.add(ch);
			}
		}
		if (chakras.size() > 1) {
			for (Chakras ch : chakras) {
				ch.getChakra().getList().remove(owner.getUniqueId());
				owner.sendMessage("§7Vous ne maitrisez plus le "+ch.getShowedName());
				owner.teleport(owner.getEyeLocation().clone());
				owner.setHealth(owner.getMaxHealth());
				return true;
			}
		}
		return false;
	}
	@Override
	public void resetCooldown() {
		changeCD = 0;
	}
	private final HashMap<Chakras, Boolean> ChakrasOwned = new HashMap<>();

	@Override
	public String getName() {
		return "§cKakuzu";
	}
}