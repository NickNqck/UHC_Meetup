package fr.nicknqck.roles.ds.demons.lune;

import java.util.*;

import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.Muzan;
import fr.nicknqck.roles.ds.slayers.pillier.MuichiroV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.BulleGyokko;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;

import static fr.nicknqck.Main.RANDOM;

public class Gyokko extends DemonsRoles {

	public Gyokko(UUID player) {
		super(player);
		this.setResi(20);
	}
	@Override
	public @NonNull Roles getRoles() {
		return Roles.Gyokko;
	}

	@Override
	public @NonNull DemonType getRank() {
		return DemonType.SUPERIEUR;
	}

	@Override
	public String[] Desc() {
		return AllDesc.Gyokko;
	}
	@Override
	public TeamList getOriginTeam() {
		return TeamList.Demon;
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getFormeDemoniaque(),
				BulleGyokko.getBulleGyokko()
		};
	}

	@Override
	public String getName() {
		return "Gyokko";
	}

	private int formecooldown = 0;
	private boolean formedemoniaque = false;
	private boolean killmuichiro = false;
	public int bullecooldown = 0;
	@Override
	public void resetCooldown() {
		bullecooldown = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(Items.getFormeDemoniaque())) {
			sendCustomActionBar(owner, "Temp avant perte de "+AllDesc.coeur+": "+StringUtils.secondsTowardsBeautiful(formecooldown));
		}
		if (owner.getItemInHand().isSimilar(BulleGyokko.getBulleGyokko())) {
			sendActionBarCooldown(owner, bullecooldown);
		}
		if (bullecooldown > 0)bullecooldown-=1;
		if (formedemoniaque) {
			if (formecooldown == 0) {
				formecooldown = 60;
				setMaxHealth(getMaxHealth() - 2.0);
				owner.sendMessage("Vous venez de perdre 1"+AllDesc.coeur+" permanent suite à votre Forme Démoniaque, ce qui vous fait donc tomber à: "+ChatColor.GOLD+ (getMaxHealth() / 2) +" Coeur");
			}
			if (formecooldown >= 1) {
				formecooldown--;
			}
		}
		if (killmuichiro) {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			}
		}
	if (owner.getMaxHealth() == 2.0 && formedemoniaque) {
		owner.sendMessage("Désactivation de votre Forme Démoniaque suite à votre faible santé");
		formedemoniaque = false;
	}
		
		super.Update(gameState);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(Items.getFormeDemoniaque())) {
			if (!formedemoniaque) {
				if (formecooldown == 0) {
					formecooldown = 60;
				}
				formedemoniaque = true;
				owner.sendMessage("Vous venez d'activer votre Forme Démoniaque");
				owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
				owner.getInventory().setChestplate(Items.getGyokkoPlastron());
			} else {
				formedemoniaque = false;
				owner.sendMessage(ChatColor.WHITE+"Vous venez de désactivé votre Forme Démoniaque");
				owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				owner.getInventory().setChestplate(Items.getdiamondchestplate());
			}				
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim == owner) {
			owner.getInventory().remove(Items.getGyokkoPlastron());
			owner.getInventory().remove(Items.getGyokkoBoots());
		}
		if (killer == owner) {
			if (victim != owner){
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof MuichiroV2) {
							killmuichiro = true;
							giveItem(owner, false, Items.getGyokkoBoots());
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuez "+ChatColor.GOLD+ role.getRoles() +ChatColor.GRAY+"vous obtenez donc "+ChatColor.RED+"force 1 le jour"+ChatColor.GRAY+", ainsi que des bottes en diamant enchantée avec: "+ChatColor.GOLD+"Depht Strider 2");
						}
					}
				}
			}
		}
		
		super.PlayerKilled(killer, victim, gameState);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.NIGHT);
		addPower(new PotItemPower(this), true);
		addKnowedRole(Muzan.class);
	}

	private static class PotItemPower extends ItemPower {

		private final List<Location> locations;

		protected PotItemPower(@NonNull RoleBase role) {
			super("Pot de téléportation", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cPot de téléportation"), role,
					"§7Vous permet de poser jusqu'à§c 3 pots§7 (§cShift§7 +§c Clique§7) et vous permet de vous y téléportez à l'un d'entre eux de manière§c aléatoire");
			this.locations = new ArrayList<>();
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (!getInteractType().equals(InteractType.INTERACT))return false;
			if (!player.getWorld().getName().equals("arena")) {
				player.sendMessage("§cImpossible d'utiliser votre téléportation, vous n'êtes pas dans le bon monde");
				return false;
			}
			if (player.isSneaking()) {
				if (locations.size() == 3) {
					teleportRandomLocations(player);
					return true;
				} else {
					if (locations.size() < 3) {
						if (!registerLocation(player.getLocation())) {
							player.sendMessage("§cVous n'avez pas pus poser de pot ici, peut-être que l'un de vos pots est trop proche ?");
							return false;
						}
					}
					player.sendMessage("§cVous avez bien enregistré cette position pour plus tard§7 (§b"+this.locations.size()+"/3§7)");
					return false;
                }
			} else {
				if (locations.isEmpty()) {
					player.sendMessage("§cIl faut d'abord poser un pot");
					return false;
				} else {
					teleportRandomLocations(player);
					return true;
				}
			}
        }
		private boolean registerLocation(final Location location) {
            if (!this.locations.isEmpty()) {
                for (final Location loc : this.locations) {
                    if (loc.distance(location) <= 10) {
                        return false;
                    }
                }
            }
            this.locations.add(location);
            return true;
        }
		private void teleportRandomLocations(final Player player) {
			final List<Location> locs = new LinkedList<>(this.locations);
			Collections.shuffle(locs, RANDOM);
			final Location finalLoc = locs.get(0);
			player.teleport(finalLoc);
			player.sendMessage("§cVous vous êtes téléporter avec succès.");
		}
	}
}