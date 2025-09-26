package fr.nicknqck.roles.ds.demons.lune;

import java.util.*;

import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.roles.ds.slayers.pillier.MuichiroV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nicknqck.GameState;
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
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Demon;
	}

    @Override
	public String getName() {
		return "Gyokko";
	}

    @Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (victim.getUniqueId() == getPlayer()) {
			owner.getInventory().remove(Items.getGyokkoPlastron());
			owner.getInventory().remove(Items.getGyokkoBoots());
		}
		if (killer.getUniqueId() == getPlayer()) {
			if (victim.getUniqueId() != killer.getUniqueId()){
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						final RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof MuichiroV2) {
                            giveItem(killer, false, Items.getGyokkoBoots());
							givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.DAY);
							killer.sendMessage("§7Vous venez de tuez§6 "+role.getRoles() +"§7vous obtenez donc§c force 1 le§e jour§7, ainsi que des bottes en diamant enchantée avec:§6 Depht Strider 2");
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
		addPower(new BulleItemPower(this), true);
		addKnowedRole(MuzanV2.class);
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
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
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
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
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
	private static class BulleItemPower extends ItemPower {

		protected BulleItemPower(@NonNull RoleBase role) {
			super("Bulle d'eau", new Cooldown(60*7), new ItemBuilder(Material.NETHER_STAR).setName("§bBulle d'eau"), role);
		}

		@Override
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				final Player target = RayTrace.getTargetPlayer(player, 20.0, null);
				if (target == null) {
					player.sendMessage("§cIl faut viser un joueur !");
					return false;
				}
				final Location loc = player.getLocation();
				final Map<Block, Material> iron = new HashMap<>();
				final Map<Block, Material> water = new HashMap<>();
				getRole().givePotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20*65, 1, false, false), EffectWhen.NOW);
				for (final Block block : getBlocks(loc, 5, true)) {
					iron.put(block, block.getType());
					block.setType(Material.IRON_BLOCK);
				}
				for (final Block waters : getBlocks(loc, 4, false)) {
					water.put(waters, waters.getType());
					waters.setType(Material.WATER);
				}
				loc.add(2.0, 0, 0);
				target.teleport(loc);
				loc.add(-4.0, 0, 0);
				player.teleport(loc);
				new setOldBlocksRunnable(getRole().getGameState(), 60, iron);
				new setOldBlocksRunnable(getRole().getGameState(), 59, water);
				return true;
			}
			return false;
		}
		private List<Block> getBlocks(Location center, int radius, boolean hollow) {
			List<Location> locs = circle(center, radius, hollow);
			List<Block> blocks = new ArrayList<>();

			for (Location loc : locs) {
				blocks.add(loc.getBlock());
			}

			return blocks;
		}
		private List<Location> circle(final Location loc,final int radius,final boolean hollow) {
			List<Location> circleblocks = new ArrayList<>();
			int cx = loc.getBlockX();
			int cy = loc.getBlockY();
			int cz = loc.getBlockZ();

			for (int x = cx - radius; x <= cx + radius; x++) {
				for (int z = cz - radius; z <= cz + radius; z++) {
					for (int y = (cy - radius); y < (cy
							+ radius); y++) {
						double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
								+ ((cy - y) * (cy - y));

						if (dist < radius * radius
								&& !(hollow && dist < (radius - 1) * (radius - 1))) {
							Location l = new Location(loc.getWorld(), x, y,
									z);
							circleblocks.add(l);
						}
					}
				}
			}

			return circleblocks;
		}
		private static class setOldBlocksRunnable extends BukkitRunnable {

			private final GameState gameState;
			private final Map<Block, Material> toRemove;
			private int timeRemaining;

            private setOldBlocksRunnable(@NonNull final GameState gameState, final int timeToGo, @NonNull final Map<Block, Material> toRemove) {
                this.gameState = gameState;
				this.toRemove = toRemove;
				this.timeRemaining = timeToGo;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					remakeTerrain();
					cancel();
					return;
				}
				if (timeRemaining <= 0) {
					remakeTerrain();
					cancel();
					return;
				}
				timeRemaining--;
			}
			private void remakeTerrain() {
				Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.toRemove.keySet().forEach(block -> block.setType(this.toRemove.get(block))));
			}
		}
	}
}