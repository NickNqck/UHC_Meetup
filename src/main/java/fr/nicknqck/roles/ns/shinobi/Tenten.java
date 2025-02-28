package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Tenten extends ShinobiRoles {

	public Tenten(UUID player) {
		super(player);
		setChakraType(getRandomChakras());
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public @NonNull Intelligence getIntelligence() {
		return Intelligence.MOYENNE;
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.TenTen;
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§aTenten",
				AllDesc.objectifteam+"§aShinobi",
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§aKunai§f: Sous forme de§l boule de neige§f cette arme permet d'infliger§c 1"+AllDesc.coeur+"§f à la §ccible§f.§7 (1x/45s)",
				"",
				AllDesc.point+"§aParchemin§f: Vous permet de créer une zone d'une taille de§c 15x15§f pendant§c 20 secondes§f, à l'intérieur tout les joueurs (sauf vous) ce verront apparaitre des§c flèches§f qui tomberont sur eux",
				"",
				AllDesc.particularite,
				"",
				AllDesc.chakra+getChakras().getShowedName(),
				AllDesc.bar
		};
	}
	private ItemStack KunaiItem() {
		return new ItemBuilder(Material.SNOW_BALL).setName("§aKunai").setUnbreakable(true).setLore("§7Une fois lancé permet d'infliger§c 1"+AllDesc.coeur+"§7 de dégat à la personne touché").toItemStack();
	}
	private ItemStack ParcheminItem() {
		return new ItemBuilder(Material.PAPER).setName("§aParchemin").setLore("§7Vous permet de créer une§c zone§7.").toItemStack();
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KunaiItem(),
				ParcheminItem()
		};
	}
	private int cdKunai = 0;
	private int cdParchemin = 0;
	@Override
	public void resetCooldown() {
		cdKunai = 0;
		cdParchemin = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (cdKunai >= 0) {
			cdKunai--;
			if (cdKunai == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau lancer un§a Kunai§7.");
			}
		}
		if (cdParchemin >= 0) {
			cdParchemin--;
			if (cdParchemin == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser vos§a Parchemin§7.");
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(ParcheminItem())) {
			if (cdParchemin > 0) {
				sendCooldown(owner, cdParchemin);
				return true;
			}
			owner.sendMessage("§7Vous activez votre zone de§a parchemin");
			cdParchemin+=45;
			new BukkitRunnable() {
				final Location initLoc = owner.getLocation().clone();
				int i = 20;
				@Override
				public void run() {
					if (i == 0) {
						owner.sendMessage("§7Votre zone de§a Parchemin§7 c'est terminé");
						cancel();
						return;
					}
					for (Player p : Loc.getNearbyPlayers(initLoc, 20)) {
				//		if (p.getUniqueId() != owner.getUniqueId()) {
						Arrow arrow = (Arrow) p.getWorld().spawnEntity(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+5, p.getLocation().getZ()), EntityType.ARROW);
						arrow.setShooter(owner);
						arrow.setFallDistance(25f);
						arrow.setBounce(false);
				//	    }
					}
					i--;
				}
			}.runTaskTimer(Main.getInstance(), 0, 20);
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity instanceof Projectile) {
			Projectile proj = (Projectile) entity;
			if (proj instanceof Snowball) {
				Snowball ball = (Snowball) proj;
				if (ball.getShooter() instanceof Player) {
					Player shooter = (Player) ball.getShooter();
					if (shooter.getUniqueId().equals(owner.getUniqueId())) {
						if (ball.hasMetadata(StringID)) {
							if (cdKunai <= 0) {
								damage(victim, 2.0, 1, owner, true);
								cdKunai = 45;
								owner.sendMessage(victim.getDisplayName()+"§7 à subit les effets de votre§a Kunai");
								victim.sendMessage("§7Vous avez été toucher par le§a Kunai§7 de§a Tenten");
								ball.removeMetadata(StringID, Main.getInstance());
								giveItem(owner, false, KunaiItem());
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onProjectileHit(ProjectileHitEvent event, Player shooter) {
		if (event.getEntity().hasMetadata(StringID) && shooter.getUniqueId().equals(owner.getUniqueId())) {
			giveItem(owner, false, KunaiItem());
		}
	}
	@Override
	public void onProjectileLaunch(ProjectileLaunchEvent event, Player shooter) {
		if (event.getEntity() instanceof Snowball) {
			Snowball snow = (Snowball) event.getEntity();
			if (snow.getShooter().equals(shooter) && shooter == owner) {
				if (cdKunai > 0) {
					sendCooldown(owner, cdKunai);
					giveItem(owner, false, KunaiItem());
					event.setCancelled(true);
				} else {
					snow.setMetadata(StringID, new FixedMetadataValue(Main.getInstance(), owner));
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Tenten";
	}
}