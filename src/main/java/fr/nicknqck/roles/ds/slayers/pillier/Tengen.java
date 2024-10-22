package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.UUID;

public class Tengen extends PillierRoles {

	public Tengen(UUID player) {
		super(player);
		this.setCanuseblade(true);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.FOUDRE;
	}

	@Override
	public Roles getRoles() {
		return Roles.Tengen;
	}
	@Override
	public String[] Desc() {
		return AllDesc.Tengen;
	}
	private int tonnerrecooldown = 0;
	private int glascooldown = 0;
	@Override
	public void resetCooldown() {
		explosioncooldown = 0;
		glascooldown = 0;
		tonnerrecooldown = 0;
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(Items.getLamedenichirin());
		giveItem(owner, false, getItems());
	}
	private ItemStack Explosif() {
		return new ItemBuilder(Material.SNOW_BALL)
				.setName("§fExplosif")
				.setLore("§7Crée une explosion à l'endroit ou l'§fExplosif§7 arrive","§7"+StringID)
				.addEnchant(Enchantment.ARROW_DAMAGE, 1)
				.hideAllAttributes()
				.toItemStack();
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getSoufleduSonTonnerre())) {
			if (tonnerrecooldown <= 0) {
					Player target = getTargetPlayer(owner, 25);
					
					if (target != null) {
						Location loc = target.getLocation();
						loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
						loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
						loc.setPitch(0);
						System.out.println(loc);
						owner.teleport(loc);
						owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*10, 0, false, false));
						tonnerrecooldown = 120;
					} else {
						owner.sendMessage("Veuiller Séléctionner une cible");
					}
					
				}  else {
					sendCooldown(owner, tonnerrecooldown);
					}
				}
		if (item.isSimilar(Items.getSoufleduSonGlasMorteldAvici())) {
			if (!isPowerEnabled()) {
				owner.sendMessage(ChatColor.RED+"Votre pouvoir est désactivé.");
				return false;
			}
			if (glascooldown <= 0) {
				Player target = getTargetPlayer(owner, 30);
				//Player target = owner;
				if (target != null) {
					MathUtil.sendParticle(EnumParticle.EXPLOSION_HUGE, target.getLocation());
					Loc.inverserDirectionJoueur(target);
					org.bukkit.util.Vector direction = target.getLocation().getDirection();
					direction.setY(1);
					direction.setX(new Random().nextDouble()*RandomUtils.getRandomInt(1, 5));
					direction.setZ(new Random().nextDouble()*RandomUtils.getRandomInt(1, 5));
					target.setVelocity(direction.multiply(2));
					glascooldown = 20*4;
					Heal(target, -4);
					owner.sendMessage("§7Vous avez touché§l "+target.getName()+"§7 avec votre explosion");
					target.sendMessage("§aTengen§7 vous à infligé 2"+AllDesc.coeur+"§7 suite à une explosion visée");
				}else {
					owner.sendMessage("§cVeuiller visée un joueur !");
				}
			} else {
				sendCooldown(owner, glascooldown);
			}
		}
		return super.ItemUse(item, gameState);
		}	
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			victim.getInventory().remove(Items.getSoufleduSonTonnerre());
			victim.getInventory().remove(Items.getSoufleduSonGlasMorteldAvici());
			victim.getInventory().remove(Explosif());
		}
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getSoufleduSonTonnerre())) {
			sendActionBarCooldown(owner, tonnerrecooldown);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleduSonGlasMorteldAvici())) {
			sendActionBarCooldown(owner, glascooldown);
		}
		givePotionEffet(owner, PotionEffectType.SPEED, 100, 1, true);
		if (glascooldown >= 1) {glascooldown--;}
		if (tonnerrecooldown >= 1) {tonnerrecooldown--;}
		if (explosioncooldown == 0) {
			owner.sendMessage(this.Explosif().getItemMeta().getDisplayName()+"§7 est à nouveau utilisable !");
			explosioncooldown-=5;
		}else if (explosioncooldown >= 0) {
			explosioncooldown--;
		}
		if (owner.getItemInHand().isSimilar(Explosif())) {
			sendActionBarCooldown(owner, explosioncooldown);
		}
	}
	private int explosioncooldown = 0;
	@Override
	public void onProjectileHit(ProjectileHitEvent e, Player shooter) {
		Projectile entity = e.getEntity();
		if (entity instanceof Snowball) {
			Snowball snow = (Snowball) entity;
			if (snow.getShooter().equals(shooter) && shooter == owner) {
				if (explosioncooldown <= 0) {
					giveItem(owner, false, Explosif());
					explosioncooldown = 60;
					MathUtil.sendParticle(EnumParticle.EXPLOSION_LARGE, snow.getLocation());
					for (Player p : Loc.getNearbyPlayersExcept(snow, 5, owner)) {
						p.sendMessage("§aTengen§7 vous a infligé 1"+AllDesc.coeur+"§7 d'explosion !");
						owner.sendMessage("§7Votre explosion à touché§l "+p.getName());
						Heal(p, -2);
						MathUtil.sendParticle(EnumParticle.EXPLOSION_NORMAL, p.getLocation());
						explosioncooldown+=10;
					}
				}
			}
		}
	}
	@Override
	public void onProjectileLaunch(ProjectileLaunchEvent event, Player shooter) {
		if (event.getEntity() instanceof Snowball) {
			Snowball snow = (Snowball) event.getEntity();
			if (snow.getShooter().equals(shooter) && shooter == owner) {
				if (explosioncooldown > 0) {
					sendCooldown(owner, explosioncooldown);
					giveItem(owner, false, Explosif());
					event.setCancelled(true);
				}
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
			Items.getSoufleduSonTonnerre(),
			Items.getSoufleduSonGlasMorteldAvici(),
			Explosif()
		};
	}

	@Override
	public String getName() {
		return "Tengen";
	}
}