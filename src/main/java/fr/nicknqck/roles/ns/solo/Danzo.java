package fr.nicknqck.roles.ns.solo;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;

public class Danzo extends RoleBase{

	private int futonCD = 0;
	private int izanagiItemCD = 0;
	private int sceauCD = 0;
	private int coupToScelled = 0;
	private boolean SceauActived = false;
	public Danzo(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		setChakraType(Chakras.FUTON);
		owner.sendMessage(Desc());
	}
	@Override
	public void RoleGiven(GameState gameState) {
		setForce(20);
		giveHealedHeartatInt(2);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§eDanzo",
				AllDesc.objectifsolo+"§e Seul",
				"",
				AllDesc.effet,
				"",
				"Vous possédez les effets§e Speed 1§f et§c Force 1§f permanent",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aFûton§f: Vous propulse en avant",
				"",
				AllDesc.point+IzanagiItem().getItemMeta().getDisplayName()+": En visant un joueur, vous permet de vous téléportez dans les§c 10 blocs§f autours d'un joueur, également, vous gagnerez§e 2 pommes d'or§f, de plus, vous §dvous régénéreriez entièrement§f, cependant, vous perdrez§c 1/2"+AllDesc.coeur+"§c permanent",
				"",
				AllDesc.point+"Sceau: En visant un joueur, vous permet de lui imposer un sceau ayant un effet aléatoire la cible obtiendra sois l'effet§1 Wither 1§f pendant§c 12s§f, sois, l'incapacité de bouger pendant§c 5 secondes§f.",
				"§c⚠ Pour activer le §nSceau§c il faudra infliger 15 coups à la cible ⚠",
				"",
				AllDesc.commande,
				"",
				"§6/ns izanagi§f: Permet (1x par partie) de vous remettre full vie et de vous donnez§e 5 pommes en or§f, en contrepartie, vous perdez§c 1"+AllDesc.coeur+"§f permanent",
				"",
				AllDesc.particularite,
				"",
				"Si vous parvenez à tuer un membre du clan§4§l Uchiwa§f vous obtiendrez l'effet "+AllDesc.Resi+"§9 1 permanent",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (killUchiwa) {
			givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
		}
		if (futonCD >= 0) {
			futonCD--;
			if (futonCD == 0) {
				owner.sendMessage("§aFûton§7 est à nouveau utilisable !");
			}
		}
		if (izanagiItemCD >= 0) {
			izanagiItemCD--;
			if (izanagiItemCD == 0) {
				owner.sendMessage("§cIzanagi§f (§cOffensif§f)§7 est à nouveau utilisable !");
			}
		}
		if (sceauCD >= 0) {
			sceauCD--;
			if (sceauCD == 0) {
				owner.sendMessage("Sceau§7 est à nouveau utilisable !");
			}
		}
	}
	private ItemStack FutonItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aFûton").setLore("§7Vous permet de vous propulsez").toItemStack();
	}
	private ItemStack IzanagiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§cIzanagi§f (§cOffensif§f)").setLore("§7Vous permet de vous téléportez autours de la cible").toItemStack();
	}
	public ItemStack SceauItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("Sceau").setLore("§7Permet de sceller un joueur.").toItemStack();
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer.getUniqueId() == owner.getUniqueId()) {
			if (owner.getMaxHealth() < 24.0) {
				setMaxHealth(owner.getMaxHealth()+1.0);
				owner.setMaxHealth(getMaxHealth());
				owner.sendMessage("§7Tuer un joueur vous a rendu §c1/2"+AllDesc.coeur+"§7 permanent");
			}
			if (isUchiwa(victim)) {
				owner.sendMessage("§7Vous venez de tuer un de ces démons du clan §4§lUchiwa !");
				if (!killUchiwa) {
					killUchiwa = true;
					owner.sendMessage("§7Vous obtenez l'effet §9Résistance 1§7 de manière permanente");
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				FutonItem(),
				IzanagiItem(),
				SceauItem()
		};
	}
	private boolean izanagi = false;
	private boolean killUchiwa = false;
	@Override
	public void onNsCommand(String[] args) {
		if (args[0].equalsIgnoreCase("izanagi")) {
			if (!izanagi) {
				setMaxHealth(getMaxHealth()-2.0);
				owner.setMaxHealth(getMaxHealth());
				owner.setHealth(getMaxHealth());
				giveItem(owner, false, new ItemStack(Material.GOLDEN_APPLE, 5));
				izanagi = true;
			}
		}
	}
	private boolean isUchiwa(Player p){
		return getListPlayerFromRole(Roles.Madara).contains(p) || getListPlayerFromRole(Roles.Obito).contains(p) || getListPlayerFromRole(Roles.Sasuke).contains(p) || getListPlayerFromRole(Roles.Itachi).contains(p);
	}

	private final HashMap<Player, SceauAction> inSceau = new HashMap<>();
	private enum SceauAction {
		Wither(),
		Bouger()
	}

	@Override
	public void resetCooldown() {
		izanagiItemCD = 0;
		sceauCD = 0;
		futonCD = 0;
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId().equals(owner.getUniqueId())){
			if (isUchiwa(victim)){
				event.setDamage(event.getDamage()*1.1);
			}
		}
		if (inSceau.containsKey(victim)) {
			if (inSceau.get(victim) == SceauAction.Bouger && SceauActived) {
				event.setDamage(0);
				event.setCancelled(true);
			}
			if (entity.getUniqueId() == owner.getUniqueId()) {
				if (coupToScelled == 15 && !SceauActived) {
					owner.sendMessage("§7Votre Sceau c'est activé");
					victim.sendMessage("§7Vous subissez les effets d'un sceau inconnu...");
					if (inSceau.get(victim) == SceauAction.Wither) {
						SceauActived = true;
						givePotionEffet(victim, PotionEffectType.WITHER, 20*12, 2, false);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
							owner.sendMessage("§7Votre sceau ne fais plus effet sur "+victim.getDisplayName());
							victim.sendMessage("§7Les effets du sceau se dissipent");
							inSceau.remove(victim, inSceau.get(victim));
							coupToScelled = 0;
							SceauActived = false;
						}, 20*12);
					} else {
						SceauActived = true;
						victim.setAllowFlight(true);
						victim.setFlying(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
							SceauActived = false;
							owner.sendMessage("§7Votre sceau ne fais plus effet sur "+victim.getDisplayName());
							victim.setFlying(false);
							victim.setAllowFlight(false);
							victim.sendMessage("§7Les effets du sceau se dissipent");
							inSceau.remove(victim, inSceau.get(victim));
							coupToScelled = 0;
						}, 20*5);
					}
				} else {
					if (inSceau.size() == 1 && !SceauActived) {
						coupToScelled++;
					}
				}
			}
		}
	}
	@Override
	public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
		if (SceauActived) {
			if (inSceau.containsKey(moover)) {
				if (inSceau.get(moover) == SceauAction.Bouger) {
					moover.teleport(e.getFrom());
					e.setCancelled(true);
				}
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SceauItem())) {
			if (sceauCD <= 0) {
				Player target = getTargetPlayer(owner, 30);
				if (target == null) {
					owner.sendMessage("§cIl faut viser un joueur !");
					return true;
				}
				owner.sendMessage("§7Vous placez un sceau sur §e§l§n"+target.getName());
				sceauCD = 60*5;
				if (RandomUtils.getOwnRandomProbability(50)) {
					inSceau.put(target, SceauAction.Wither);
				} else {
					inSceau.put(target, SceauAction.Bouger);
				}
				owner.sendMessage("§7Dans§c 15 coups§f "+target.getName()+"§7 subirat les effets de votre§c Sceau");
				return true;
			} else {
				sendCooldown(owner, sceauCD);
				return true;
			}
		}
		if (item.isSimilar(IzanagiItem())) {
			if (izanagiItemCD <= 0) {
				Player target = getTargetPlayer(owner, 30);
				if (target == null) {
					owner.sendMessage("§cMerci de viser un joueur !");
					return true;
				}
				Location loc = Loc.getRandomLocationAroundPlayer(target, 10);
                owner.sendMessage("§cIzanagi !");
				owner.teleport(loc);
				izanagiItemCD = 60*3;
				setMaxHealth(getMaxHealth()-1.0);
				owner.setMaxHealth(getMaxHealth());
				owner.setHealth(owner.getMaxHealth());
				giveItem(owner, false, new ItemStack(Material.GOLDEN_APPLE, 2));
				owner.updateInventory();
				return true;
			} else {
				sendCooldown(owner, izanagiItemCD);
				return true;
			}
		}
		if (item.isSimilar(FutonItem())) {
			if (futonCD <= 0) {
				Vector dir = owner.getLocation().getDirection();
				owner.setVelocity(dir.multiply(3));
				futonCD = 90;
				return true;
			} else {
				sendCooldown(owner, futonCD);
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
}