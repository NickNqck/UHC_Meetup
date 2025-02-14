package fr.nicknqck.roles.ds.demons.lune;

import java.util.*;

import fr.nicknqck.Main;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.BulleGyokko;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import org.bukkit.scheduler.BukkitRunnable;

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
				BulleGyokko.getBulleGyokko()
		};
	}

	@Override
	public String getName() {
		return "Gyokko";
	}

	private boolean killmuichiro = false;
	public int bullecooldown = 0;
	@Override
	public void resetCooldown() {
		bullecooldown = 0;
	}
	@Override
	public void Update(GameState gameState) {
		if (owner.getItemInHand().isSimilar(BulleGyokko.getBulleGyokko())) {
			sendActionBarCooldown(owner, bullecooldown);
		}
		if (bullecooldown > 0)bullecooldown-=1;
		if (killmuichiro) {
			if (!gameState.nightTime) {
				owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), true);
			}
		}
		super.Update(gameState);
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
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
		addPower(new FormeDemoniaquePower(this), true);
		addKnowedRole(Muzan.class);
	}

	private static class PotItemPower extends ItemPower {

		private final List<Location> locations;

		protected PotItemPower(@NonNull RoleBase role) {
			super("Pot de téléportation", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§cPot de téléportation"), role,
					"§7Vous permet de poser jusqu'à§c 3 pots§7 (§cShift§7 +§c Clique§7)",
					"§7Vous pouvez vous y téléportez à l'un d'entre eux de manière§c aléatoire§7 via un§c Clique droit");
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
	private static class FormeDemoniaquePower extends ItemPower {

		private boolean activated = false;
		private int timeLeft = 60;

		protected FormeDemoniaquePower(@NonNull RoleBase role) {
			super("Forme Démoniaque", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§cForme Démoniaque"), role);
			new FormeRunnable(this, getRole());
			setShowCdInDesc(false);
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (this.activated) {
				getRole().getGamePlayer().getActionBarManager().removeInActionBar("gyokko.forme");
				this.activated = false;
				player.sendMessage("Désactivation de votre§c Forme Démoniaque");
				if (player.getInventory().getChestplate() != null) {
					final ItemStack item = player.getInventory().getChestplate();
					final ItemMeta meta = item.getItemMeta();
					if (meta.hasEnchant(Enchantment.THORNS)) {
						meta.removeEnchant(Enchantment.THORNS);
					}
					item.setItemMeta(meta);
				}
				
			} else {
				if (player.getMaxHealth() <= 8.0) {
					player.sendMessage("§cVous n'avez pas asser de vie pour utiliser ce pouvoir");
					return false;
				}
				getRole().getGamePlayer().getActionBarManager().addToActionBar("gyokko.forme", "§bTemp avant perte de§c coeur§b: §c"+timeLeft+"s");
				this.activated = true;
				player.sendMessage("Activation de votre§c Forme Démoniaque");
				if (player.getInventory().getChestplate() != null) {
					final ItemStack item = player.getInventory().getChestplate();
					final ItemMeta meta = item.getItemMeta();
					meta.addEnchant(Enchantment.THORNS, 3, true);
					item.setItemMeta(meta);
				}
			}
			return true;
		}
		private static class FormeRunnable extends BukkitRunnable {

			private final FormeDemoniaquePower power;
			private final RoleBase role;

            private FormeRunnable(final FormeDemoniaquePower power, final RoleBase role) {
				this.power = power;
                this.role = role;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
			public void run() {
				if (!role.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				if (this.power.timeLeft <= 0) {
					this.role.setMaxHealth(this.role.getMaxHealth()-2.0);
					final Player owner = Bukkit.getPlayer(role.getPlayer());
					if (owner != null) {
						owner.setMaxHealth(this.role.getMaxHealth());
					}
					this.power.timeLeft = 60;
				}
				if (this.role.getMaxHealth() <= 8.0) {
					cancel();
					role.getGamePlayer().getActionBarManager().removeInActionBar("gyokko.forme");
					role.getGamePlayer().sendMessage("§cVous n'avez plus asser de vie pour utiliser votre forme démoniaque...");
					return;
				}
				if (this.power.activated) {
					this.power.timeLeft--;
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.role.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.NOW));
				}
				role.getGamePlayer().getActionBarManager().updateActionBar("gyokko.forme", "§bTemp avant perte de§c coeur§b: §c"+this.power.timeLeft+"s");
			}
		}
	}
}