package fr.nicknqck.roles.ns.shinobi;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;

public class Minato extends RoleBase{

	public Minato(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		giveItem(owner, false, getItems());
		setChakraType(Chakras.KATON);
		owner.sendMessage(Desc());
		canBeHokage = true;
	}
	
	private Location Kunai = null;

	@Override
    public String[] Desc() {
        KnowRole(owner, Roles.Naruto, 5);
        return new String[] {
                AllDesc.bar,
                AllDesc.role+"§aMinato",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+AllDesc.Speed+" §e1§fpermanent.",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§bRasengan §f: En frappant un joueur avec l'item §aen main§f, vous créez une §cexplosion §flui infligeant §c2"+AllDesc.coeur,". §7(1x/3min)",
                "",
                AllDesc.point+"§6Kyûbi §f: Vous obtenez l'effet "+AllDesc.Speed+" §eII §f, pendant §c3 minutes§f. §7(1x/10min)",
                "",
                AllDesc.point+"§eHiraishin Kunai §f: En tirant une §aflèche §favec cet arc, vous pouvez vous §etéléporter §fà l'emplacement actuel de cette dernière, grâce à la technique §aHiraishin no Jutsu.",
                "",
                AllDesc.point+"§aHiraishin no Jutsu §f: Effectue une action en fonction du clique: ",
                "§7     →§f Clique droit: Vous permet de vous téléportez à la position de votre§a flèche§7 (1x/30s)",
                "§7     →§f Clique gauche: En frappant un joueur, le téléporte à la position de votre§a flèche§7 (1x/2m)",
                "",
                AllDesc.particularite,
                "",
                "Vous connaissez l'§eidentité §fde §aNaruto§f.",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KyubiItem(),
				RasenganItem(),
				HiraishinKunaiItem(),
				HiraishinNoJutsuItem()
		};
	}

	@Override
	public void resetCooldown() {
		cdHiraishin = 0;
		cdKyubi = 0;
		cdRasengan = 0;
		cdHiraishinOther = 0;
	}
	private ItemStack KyubiItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§6Kyûbi").setLore("§7Vous obtenez "+AllDesc.Speed+" 2 pendant 3min").toItemStack();
	}
	private ItemStack RasenganItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aRasengan").setLore("§7Vous permez de rassembler votre Chakra en un point").toItemStack();
	}
	private ItemStack HiraishinKunaiItem() {
		return new ItemBuilder(Material.BOW).addEnchant(Enchantment.KNOCKBACK, 1).hideAllAttributes().setName("§eHiraishin Kunai").setLore("§7Vous tirez une flèche à laquelle vous pourrez vous téléportez").setUnbreakable(true).toItemStack();
	}
	private ItemStack HiraishinNoJutsuItem() {
		return new ItemBuilder(Material.NETHER_STAR).setName("§aHiraishin no Jutsu").setLore("§7vous téléporte sur la dernière flèche tirez avec votre Hiraishin Kunai").toItemStack();
	}
	private int cdRasengan = 0;
	private int cdKyubi = 0;
	private int cdHiraishin = 0;
	private int cdHiraishinOther = 0;
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(RasenganItem())) {
			if (cdRasengan <= 0) {
				owner.sendMessage("§7Il faut frapper un joueur pour créer une explosion.");
				return true;
			} else {
				sendCooldown(owner, cdRasengan);
				return true;
			}
		}
		if (item.isSimilar(KyubiItem())) {
			if (cdKyubi <= 0) {
			givePotionEffet(PotionEffectType.SPEED, 20*180, 2, true);
			owner.sendMessage("Vous venez d'utiliser §6Kyûbi");
			cdKyubi = 60*10;
			return true;
			} else {
				sendCooldown(owner, cdKyubi);
				return true;
			}
		}
		if (item.isSimilar(HiraishinNoJutsuItem())) {
			if (cdHiraishin <= 0) {
				if (Kunai != null) {
				owner.teleport(Kunai);
				owner.sendMessage("Vous venez d'être téléportez sur votre Kunai");
				cdHiraishin = 30;
				Kunai = null;
				return true;
				} else {
					owner.sendMessage("Vous n'avez pas tirez de Kunai");
					return true;
				}
			} else {
				sendCooldown(owner, cdHiraishin);
				return true;
			}
		}
		return super.ItemUse(item, gameState);
	}
	
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (owner.getItemInHand().isSimilar(RasenganItem())) {
				if (cdRasengan <= 0) {
					MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, victim.getLocation());
					Heal(victim, -4);
					victim.damage(0.0);
					Location loc = victim.getLocation();
					loc.add(new Vector(0.0, 1.8, 0.0));
					victim.teleport(loc);
					cdRasengan = 60*3;
				} else {
					sendCooldown(owner, cdRasengan);
				}
			}
		}
		if (entity.getUniqueId() == owner.getUniqueId()) {
			if (owner.getItemInHand().isSimilar(HiraishinNoJutsuItem())) {
				if (cdHiraishinOther <= 0) {
					if (Kunai != null) {
					victim.teleport(Kunai);
					owner.sendMessage("Vous venez de téléportez "+victim.getName()+" à votre Kunai");
					cdHiraishinOther = 120;
					Kunai = null;
					}
				} else {
					sendCooldown(owner, cdHiraishinOther);
				}
			}
		}
		if (entity instanceof Arrow) {
			Arrow arrow = (Arrow) entity;
			if (victim != null) {
				if (arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();
					if (shooter == owner) {
						if (arrow.hasMetadata("KunaiArrow")) {
							Kunai = victim.getLocation();
							arrow.removeMetadata("KunaiArrow", Main.getInstance());
						}
					}
				}
			}
		}
	}
	
	@Override
	public void Update(GameState gameState) {
		givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
		if (cdRasengan >= 0) {
			cdRasengan--;
			if (cdRasengan == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§a Rasengan");
			}
		}
		if (cdKyubi >= 0) {
			cdKyubi--;
			if (cdKyubi == 0) {
				owner.sendMessage("§7Vous pouvez a nouveau utiliser§6 Kyûbi");
			}
		}
		if (cdHiraishin >= 0) {
			cdHiraishin--;
			if (cdHiraishin == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre §aHiraishin no Jutsu");
			}
		}
		if (cdHiraishinOther >= 0) {
			cdHiraishinOther--;
			if (cdHiraishinOther == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau téléporter un autre joueur avec votre§a Hiraishin no Jutsu");
			}
		}
		super.Update(gameState);
	}
	@Override
	public void onProjectileLaunch(Projectile projectile, Player shooter) {
		if (shooter == owner) {
			if (owner.getItemInHand().isSimilar(HiraishinKunaiItem())) {
				if (projectile instanceof Arrow) {
					Kunai = projectile.getLocation();
					projectile.setMetadata("KunaiArrow", new FixedMetadataValue(Main.getInstance(), shooter.getLocation()));
					new BukkitRunnable() {
					Arrow fleche = (Arrow)projectile;
						@Override
						public void run() {
							if (!fleche.hasMetadata("KunaiArrow")) {
								cancel();
								return;
							}
							if (Kunai != null && fleche.getLocation() != Kunai) {
								Kunai = projectile.getLocation();
							} else {
								cancel();
							}
						}
					}.runTaskTimer(Main.getInstance(), 0, 1);
				}
			}
		}
	}
}