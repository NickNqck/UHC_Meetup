package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.CC;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.particles.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Kisame extends AkatsukiRoles {

	public Kisame(Player player) {
		super(player);
		setChakraType(Chakras.SUITON);
		owner.sendMessage(Desc());
	}
	@Override
	public Roles getRoles() {
		return Roles.Kisame;
	}
	@Override
	public Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public String[] Desc() {
		KnowRole(owner, Roles.Itachi, 1);
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§cKisame",
				AllDesc.objectifteam+"§cAkatsuki",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§9Résistance 1§f et§e Speed 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+SamehadaItem().getItemMeta().getDisplayName()+"§f: Permet tout les 25 coups infliger à un joueur, de lui infliger §c2"+AllDesc.coeur,
				"",
				AllDesc.point+SuibunItem().getItemMeta().getDisplayName()+"§f: Permet de créer une bulle d'eau attirant tout joueur étant à moins de 20blocs en son centre, également, vos botte obtienne l'enchantement§b Depth Strider 5§f pendant la durée de la bulle (1 minutes)",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité d'§cItachi",
				"A l'annonce des rôles vous obtenez un livre§b Depth Strider 3",
				"",
				"Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
				AllDesc.bar
				};
	}
	@Override
	public void GiveItems() {
		ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
		BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, false); 
		Book.setItemMeta(BookMeta);
		giveItem(owner, false, Book);
		owner.setLevel(owner.getLevel()+6);
		giveItem(owner, false, getItems());
	}
	private ItemStack SamehadaItem() {
		return new ItemBuilder(Material.DIAMOND_SWORD).setName("§cSamehada").setUnbreakable(true).addEnchant(Enchantment.DAMAGE_ALL, 3).setLore("§7Au bout de§c 25§7 coups subit par un joueur lui inflige§c 2"+AllDesc.coeur).toItemStack();
	}
	private ItemStack SuibunItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§bSuibun").setLore("§7Permet de crée une bulle d'eau autours de vous").toItemStack();
	}
	@Override
	public void resetCooldown() {
		SuibunCD = 0;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				SamehadaItem(),
				SuibunItem()
		};
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setResi(20);
	}
	private int SuibunCD = 0;
	private final HashMap<UUID, Integer> nmbCoup = new HashMap<>();
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (SuibunCD >= 0) {
			SuibunCD--;
			if (SuibunCD == 0) {
				owner.sendMessage(SuibunItem().getItemMeta().getDisplayName()+"§7 est de nouveau utilisable !");
			}
		}
	}
	@Override
	public void neoItemUseAgainst(ItemStack item, Player player, GameState gameState, Player damager) {
		if (damager.getUniqueId() == owner.getUniqueId()) {
			if (nmbCoup.containsKey(player.getUniqueId())) {
				if (owner.getItemInHand().isSimilar(SamehadaItem())) {
					int i = nmbCoup.get(player.getUniqueId());
					if (i == 25) {
						nmbCoup.remove(player.getUniqueId(),i);
						sendCustomActionBar(owner, "§6§l"+player.getDisplayName()+"§r§c à subit les effets de §nSamehada§r§c !");
						damage(player, 4.0, 1);
					}else {
						nmbCoup.remove(player.getUniqueId(), i);
						sendCustomActionBar(owner, "§cCoups contre§6 "+player.getDisplayName()+"§c: "+i+"§l/§r§c25");
						i++;
						nmbCoup.put(player.getUniqueId(), i);
					}
				}
			}else {
				nmbCoup.put(player.getUniqueId(), 1);
			}
		}
	}
	private void clearSuibun() {
		if (Centrer == null) {
			return;
		}
		for(Location loc : new MathUtil().sphere(Centrer, 14, false)) {
			if (loc.getBlock().getType().name().contains("WATER")) {
				loc.getBlock().setType(Material.AIR);
			}
		}
		Centrer = null;
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player.getUniqueId() == owner.getUniqueId()) {
			 clearSuibun();
		}
	}
	private Location Centrer = null;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SuibunItem())) {
			if (SuibunCD > 0) {
				sendCooldown(owner, SuibunCD);
				return true;
			}
			if (owner.getInventory().getBoots() != null) {
				owner.getInventory().setBoots(new ItemBuilder(owner.getInventory().getBoots()).removeEnchantment(Enchantment.DEPTH_STRIDER).addEnchant(Enchantment.DEPTH_STRIDER, 5).toItemStack());
			}
			int goodY = owner.getLocation().clone().getWorld().getHighestBlockYAt(owner.getLocation().clone())+10;
			Location Center = new Location(owner.getWorld(), owner.getLocation().getX(), goodY+15, owner.getLocation().getZ());
			Location KisameCenter = new Location(owner.getWorld(), owner.getLocation().getX(), goodY+14, owner.getLocation().getZ());
			this.Centrer = Center;
			for(Location loc : new MathUtil().sphere(Center, 14, false)) {
				if (loc.getBlock().getType() == Material.AIR) {
					loc.getBlock().setType(Material.STATIONARY_WATER, false);
				}
			}
			for (Player p : Loc.getNearbyPlayersExcept(owner, 14)) {
				p.teleport(Center);
			}
			owner.teleport(KisameCenter);
			SuibunCD = 60*8;
			new BukkitRunnable() {
				int s = 0;
				@Override
				public void run() {
					s++;
					if (gameState.getServerState() != ServerStates.InGame) {
						clearSuibun();
						cancel();
						return;
					}
					if (Centrer == null) {
						clearSuibun();
						cancel();
						return;
					}
					if (owner.getGameMode() == GameMode.SPECTATOR) {
						clearSuibun();
						cancel();
						return;
					}
					if (s <= 60) {
						sendCustomActionBar(owner, CC.translate("&bTemp restant: "+gameState.sendIntBar(s, 60, 2)+"§b (§c"+(60-s)+"§b)"));
						if (s == 60) {
							clearSuibun();
							if (owner.getInventory().getBoots() != null) {
								owner.getInventory().setBoots(new ItemBuilder(owner.getInventory().getBoots()).removeEnchantment(Enchantment.DEPTH_STRIDER).addEnchant(Enchantment.DEPTH_STRIDER, 3).toItemStack());
							}
							cancel();
						}
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public String getName() {
		return "§cKisame";
	}
}