package fr.nicknqck.roles.ns.orochimaru;

import java.util.ArrayList;
import java.util.List;

import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.ns.Intelligence;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;

public class Suigetsu extends NSRoles {

	public Suigetsu(Player player) {
		super(player);
		setChakraType(Chakras.SUITON);
		owner.sendMessage(Desc());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			if (!gameState.attributedRole.contains(Roles.Orochimaru)) {
				onOrochimaruDeath(false);
				owner.sendMessage("§5Orochimaru§7 n'étant pas dans la composition de la partie, vous avez quand même obtenue les bonus dû à sa mort");
			}
		}, 20*10);
	}
	@Override
	public Roles getRoles() {
		return Roles.Suigetsu;
	}

	@Override
	public Intelligence getIntelligence() {
		return Intelligence.PEUINTELLIGENT;
	}

	private boolean orochimaruDeath = false;
	private void onOrochimaruDeath(boolean message) {
		if (message) {
			owner.sendMessage("§5Orochimaru§7 est mort, vous obtenez l'effet§c Force 1§f permanent, §6/ns me§7 pour connaitre vos allier");
		}
		orochimaruDeath = true;
		setForce(20);
	}
	@Override
	public String[] Desc() {
		if (!orochimaruDeath) {
			KnowRole(owner, Roles.Orochimaru, 5);
		} else {
			KnowRole(owner, Roles.Karin, 5);
		}
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§5Suigetsu",
				AllDesc.objectifteam+"§5Orochimaru",
				"",
				AllDesc.items,
				"",
				AllDesc.point+SuikaItem().getItemMeta().getDisplayName()+"§f: Vous permet de vous téléportez à la position visé",
				"",
				AllDesc.particularite,
				"",
				"Vous possédez l'identité d'§5Orochimaru",
				"Dans l'eau vous possédez l'effet§9 Résistance 1§f et§b Respiration 1",
				"A l'annonce des rôles vous obtenez un livre§b Agilitée Aquatique 3",
				"A la mort d'§5Orochimaru§f vous obtenez l'effet§c Force 1§f permanent ainsi que l'identité de§5 Karin§f et de§5 Jugo",
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
			SuikaItem()
		};
	}
	private ItemStack SuikaItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§bSuika").setLore("§7Vous téléporte derrière le bloc visé").toItemStack();
	}

	@Override
	public void resetCooldown() {
		suikaCD = 0;
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
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (getPlayerRoles(player) instanceof Orochimaru) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true);
			boolean KarinAlive = !getListPlayerFromRole(Roles.Karin).isEmpty();
            onOrochimaruDeath(true);
			owner.sendMessage("§5Orochimaru§7 vient de mourir vous obtenez donc "+AllDesc.Force+"§c 1§7 ainsi que le nom du joueur possédant le rôle §5Karin§7 qui est "+(KarinAlive ? "§5"+getPlayerFromRole(Roles.Karin).getDisplayName() : "§cMort"));
		}
	}
	private boolean Invisible = false;
	@Override
	public void Update(GameState gameState) {
		if (orochimaruDeath) {
			givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
		}
		if (owner.getLocation().getBlock().getType().name().contains("WATER")) {
			givePotionEffet(PotionEffectType.WATER_BREATHING, 60, 1, true);
			givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
			setResi(20);
			if (gameState.isApoil(owner)) {
				givePotionEffet(PotionEffectType.INVISIBILITY, 60, 1, true);
				Invisible = true;
			}
		} else {
			setResi(0);
		}
		if (!gameState.isApoil(owner)) {
			Invisible = false;
		}
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (Invisible) {
				event.setCancelled(true);
				return;
			}
		}
		if (entity instanceof Projectile) {
			if (Invisible) {
				if (((Projectile)entity).getShooter() instanceof Player) {
					if (((Player)((Projectile)entity).getShooter()).getUniqueId() == owner.getUniqueId()) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		if (victim.getUniqueId() == owner.getUniqueId()) {
			if (Invisible) {
				event.setCancelled(true);
            } else {
				int r = RandomUtils.getRandomInt(1, 10);
				if (r == 5) {
					event.setCancelled(true);
                }
			}
		}
	}
	private int suikaCD = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(SuikaItem())) {
			if (suikaCD <= 0) {
				Location toTP = null;
				for (Block b : obtenirBlocsEntreDeuxPositions(owner, getTargetLocation(owner, 20))) {
					if (b.getType().name().contains("WATER")) {
                        toTP = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
						break;
					}
				}
				if (toTP == null) {
					owner.sendMessage("§cIl faut visée un endroit ou il y a de l'eau !");
					return true;
				}
				owner.teleport(toTP);
            } else {
				sendCooldown(owner, suikaCD);
            }
            return true;
        }
		return super.ItemUse(item, gameState);
	}
	public List<Block> obtenirBlocsEntreDeuxPositions(Player joueur, Location positionVoulue) {
        Location positionJoueur = joueur.getLocation();

        World monde = positionJoueur.getWorld();
        List<Block> tr = new ArrayList<>();
        int minX = Math.min(positionJoueur.getBlockX(), positionVoulue.getBlockX());
        int minY = Math.min(positionJoueur.getBlockY(), positionVoulue.getBlockY());
        int minZ = Math.min(positionJoueur.getBlockZ(), positionVoulue.getBlockZ());

        int maxX = Math.max(positionJoueur.getBlockX(), positionVoulue.getBlockX());
        int maxY = Math.max(positionJoueur.getBlockY(), positionVoulue.getBlockY());
        int maxZ = Math.max(positionJoueur.getBlockZ(), positionVoulue.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block bloc = monde.getBlockAt(x, y, z);
                    tr.add(bloc);
                }
            }
        }
        return tr;
    }

	@Override
	public String getName() {
		return "§5Suigetsu";
	}

	@Override
	public void OnAPlayerKillAnotherPlayer(Player player, Player damager, GameState gameState) {
		super.OnAPlayerKillAnotherPlayer(player, damager, gameState);
	}
}