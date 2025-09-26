package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
import org.bukkit.util.Vector;

import java.util.UUID;

public class Sasha extends SoldatsRoles {

	private TextComponent desc;
	private final ItemStack arcItem = new ItemBuilder(Material.BOW).setUnbreakable(false).addEnchant(Enchantment.ARROW_DAMAGE, 3).setName("§aArc de Chasseur").setDroppable(false).toItemStack();
	public Sasha(UUID player) {
		super(player);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Sasha;
	}

    @Override
	public void RoleGiven(GameState gameState) {
		AutomaticDesc desc = new AutomaticDesc(this);
		desc.setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Lorsque vous tirez une flèche sur un§c joueur§7 avec votre§a Arc de Chasseur§7 vous lui infligerez des effets en fonction de la zone toucher:\n\n"
		+AllDesc.tab+"§aTête§7: Inflige l'effet§9 Blindness I§7 pendant§c 7 secondes§7.\n\n"
		+AllDesc.tab+"§aTorse§7: Augmente les dégats de§a +§c1"+AllDesc.coeur+"\n\n"
		+AllDesc.tab+"§aJambe§7: Inflige l'effet§l Slowness I§r§7 pendant§c 7 secondes§7.")}), "§aArc de Chasseur", 60));
		this.desc = desc.getText();
	}

	@Override
	public TextComponent getComponent() {
		return this.desc;
	}

	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				arcItem
		};
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
		owner.getInventory().addItem(new ItemStack(Material.ARROW, 32));//give 32 flèches
	}
	private int cooldown = 0;
	@Override
	public void onProjectileLaunch(Projectile projectile, Player shooter) {
		if (projectile instanceof Arrow) {
			if (shooter == owner) {
				if (shooter.getItemInHand().isSimilar(arcItem)){
					Arrow fleche = (Arrow) projectile;
					if (fleche.getShooter() == shooter){
						if (cooldown > 0) {
							sendCooldown(owner, cooldown);
							return;
						}
						projectile.setMetadata("SashaArrow", new FixedMetadataValue(Main.getInstance(), shooter.getLocation()));
					}
				}
			}
		}
		super.onProjectileLaunch(projectile, shooter);
	}

	@Override
	public String getName() {
		return "Sasha";
	}

	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		if (entity instanceof Arrow) {
			Arrow arrow = (Arrow) entity;
			if (victim != null) {
				if (arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();
					if (shooter.getUniqueId().equals(getPlayer())) {
						if (arrow.hasMetadata("SashaArrow")) {
							if (cooldown <= 0) {
			                    Vector arrowLocation = arrow.getLocation().toVector();
			                    Vector targetLocation = victim.getLocation().toVector();

			                    double heightDifference = arrowLocation.getY() - targetLocation.getY();
			                    if (heightDifference > 1.5) {
			                        shooter.sendMessage("§7Tu as touché la tête de§a " + victim.getName());
			                        OLDgivePotionEffet(victim, PotionEffectType.BLINDNESS, 20*7, 1, true);
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+AllDesc.blind);
			                    } else if (heightDifference < 0.9) {
			                        shooter.sendMessage("§7Tu as touché les jambes de§a " + victim.getName());
			                        OLDgivePotionEffet(victim, PotionEffectType.SLOW, 20*7, 1, true);
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 7secondes§7 de "+AllDesc.slow);
			                    } else {
			                        shooter.sendMessage("§7Tu as touché le torse de§a " + victim.getName());
			                        Heal(victim, -2);
			                        victim.sendMessage("§7Vous avez été toucher par l'arc de§a Sasha§7 au niveau de votre torse");
			                        victim.sendMessage("§aSasha§7 vous à infliger§l 1"+AllDesc.Coeur("§c"));
			                    }
								cooldown = 60;
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

	@Override
	public void Update(GameState gameState) {
		if (cooldown >= 0) {
			cooldown--;
			if (cooldown == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Arc de Chasseur");
			}
		}
	}
}