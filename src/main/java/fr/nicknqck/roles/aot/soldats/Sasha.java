package fr.nicknqck.roles.aot.soldats;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;

public class Sasha extends RoleBase{

	public Sasha(Player player, Roles roles) {
		super(player, roles);
		owner.sendMessage(Desc());
		gameState.GiveRodTridi(owner);
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"Sasha",
				"",
				AllDesc.items,
				"§732 flèche vous sont donnez à l'annonce des rôles",
				AllDesc.point+"§7Lorsque vous tirez sur un joueur avec un arc enchanté vous pourrez infliger des effets à la personne touché en fonction de ou elle à été touchée: ",
				"Tête:§7 Inflige l'effet "+AllDesc.blind+" 1 pendant 7s",
				"Torse:§7 Inflige 1"+AllDesc.coeur+" de dégat supplémentaire",
				"Jambe:§7 Inflige l'effet "+AllDesc.slow+" 1 pendant 7s",
				AllDesc.bar
		};
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[0];
	}
	@Override
	public void GiveItems() {
		owner.getInventory().addItem(new ItemStack(Material.ARROW, 32));//give 32 flèches
	}
	private int cooldown = 0;
	@Override
	public void onProjectileLaunch(Projectile projectile, Player shooter) {
		if (projectile instanceof Arrow) {
			if (shooter == owner) {
				if (shooter.getItemInHand().isSimilar(Items.getbow())){
					Arrow fleche = (Arrow) projectile;
					if (fleche.getShooter() == shooter){
						projectile.setMetadata("SashaArrow", new FixedMetadataValue(Main.getInstance(), shooter.getLocation()));
					}
				}
			}
		}
		super.onProjectileLaunch(projectile, shooter);
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity instanceof Arrow) {
			Arrow arrow = (Arrow) entity;
			if (victim != null) {
				if (arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();
					if (shooter == owner) {
						if (arrow.hasMetadata("SashaArrow")) {
							if (cooldown <= 0) {
			                    Vector arrowLocation = arrow.getLocation().toVector();
			                    Vector targetLocation = victim.getLocation().toVector();
			                    
			                    // Vérifiez la partie du corps touchée en fonction de la hauteur de la cible
			                    double heightDifference = arrowLocation.getY() - targetLocation.getY();
			                    if (heightDifference > 1.5) {
			                        shooter.sendMessage("§7Tu as touché la tête de§a " + victim.getName());
			                        givePotionEffet(victim, PotionEffectType.BLINDNESS, 20*7, 1, true);
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+AllDesc.blind);
			                    } else if (heightDifference < 0.9) {
			                        shooter.sendMessage("§7Tu as touché les jambes de§a " + victim.getName());
			                        givePotionEffet(victim, PotionEffectType.SLOW, 20*7, 1, true);
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+AllDesc.slow);
			                    } else {
			                        shooter.sendMessage("§7Tu as touché le torse de§a " + victim.getName());
			                        Heal(victim, -2);
			                        victim.sendMessage("§7Vous avez été toucher par l'arc de§a Sasha§7 au niveau de votre torse");
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 1"+AllDesc.Coeur("§c"));
			                    }
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void resetCooldown() {
		cooldown = 0;
	}
}