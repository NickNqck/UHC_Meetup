package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.events.Events;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.slayers.pillier.Kyojuro;
import fr.nicknqck.utils.Loc;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Shinjuro extends DemonsSlayersRoles {

	public Shinjuro(UUID player) {
		super(player);
		setCanuseblade(true);
		Lames.FireResistance.getUsers().put(getPlayer(), Integer.MAX_VALUE);
		setMaxHealth(24.0);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		setLameIncassable(owner, true);
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}
	@Override
	public Roles getRoles() {
		return Roles.Shinjuro;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public String[] Desc() {
		return AllDesc.Shinjuro;
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
		giveItem(owner, false, Items.getLamedenichirin());
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				Items.getSake(),
				Items.getSoufleDuFeu()
		};
	}
	private boolean usesoufle = false;
	private int cooldownsake = 0;
	private int souflecooldown = 0;
	private boolean killkyojuro = false;
	private int regencooldown = 10;
	public void setSakeCooldown(int i) {
		cooldownsake = i;
	}
	@Override
	public void resetCooldown() {
		cooldownsake = 0;
		souflecooldown = 0;
	}
	@Override
 	public void Update(GameState gameState) {
		if (Events.Alliance.getEvent().isActivated()) {
			if (gameState.getOwner(Roles.Kyojuro) != null) {
				for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
					if (p.equals(gameState.getOwner(Roles.Kyojuro))) {
						givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
					}
				}
			}
		}
		if (owner.getItemInHand().isSimilar(Items.getSake())) {
			sendActionBarCooldown(owner, cooldownsake);
		}
		if (owner.getItemInHand().isSimilar(Items.getSoufleDuFeu())) {
			sendActionBarCooldown(owner, souflecooldown);
		}
		if (cooldownsake >= 1) cooldownsake--;
		if (souflecooldown >= 1) souflecooldown--;
		if (regencooldown >= 1) {
			   regencooldown--;
		}
		if (usesoufle) {
			owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0, false, false));
			if (killkyojuro) owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, false, false));
		} else {
			owner.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			if (killkyojuro && owner.hasPotionEffect(PotionEffectType.SPEED)) owner.removePotionEffect(PotionEffectType.SPEED);
		}
		super.Update(gameState);
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item != null) {
			if (item.isSimilar(Items.getSake())) {
				if (cooldownsake <= 0) {
						owner.sendMessage("§7Vous venez de boire de l'alcool");
						owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*((60 + 30)), 0, false, false));
						if (killkyojuro) {
							owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*((60 + 30)), 1, false, false), true);
						}
						cooldownsake = 60*5;
				}else {
					sendCooldown(owner, cooldownsake);
				}
			}
			if (item.isSimilar(Items.getSoufleDuFeu())) {
				if (souflecooldown <= 0) {
					if (!usesoufle) {
						owner.sendMessage("Vous venez d'activer le Soufle du Feu");
						usesoufle = true;
                    } else {
						owner.sendMessage("Vous venez de désactiver le Soufle du Feu");
						usesoufle = false;
                    }
                    souflecooldown = 3;
                } else {
					sendCooldown(owner, souflecooldown);
				}
			}
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				if (gameState.getInGamePlayers().contains(victim)) {
					if (gameState.getPlayerRoles().containsKey(victim)) {
						RoleBase role = gameState.getPlayerRoles().get(victim);
						if (role instanceof Kyojuro) {
							killkyojuro = true;
							owner.sendMessage(ChatColor.GRAY+"Vous venez de tuée: "+ victim.getName()+" il possédait le rôle de: "+ChatColor.GOLD+role.getRoles().name()+ChatColor.GRAY+" maintenant en utilisant le Soufle du Feu vous obtiendrez l'effet: "+ChatColor.RED+"Speed 1"+ChatColor.GRAY+" permanent");
						}
					}
				}
			}
		}
		super.PlayerKilled(killer, victim, gameState);
	}
	@Override
	public void ItemUseAgainst(ItemStack item, Player victim, GameState gameState) {
		if (victim == null) return;
		if (item == null) return;
		if (victim == owner) return;
		if (!usesoufle) return;
		int x = 10 * 20;
        victim.setFireTicks(x);
        super.ItemUseAgainst(item, victim, gameState);
	}

	@Override
	public String getName() {
		return "Shinjuro";
	}
	private static class onTick extends BukkitRunnable {
		private final Shinjuro shinjuro;
		private onTick(Shinjuro shinjuro) {
			this.shinjuro = shinjuro;
		}
		@Override
		public void run() {
			if (!shinjuro.getGameState().getServerState().equals(GameState.ServerStates.InGame) || !shinjuro.getGamePlayer().isAlive()) {
				cancel();
				return;
			}
			if (Events.Alliance.getEvent().isActivated()) {
				if (shinjuro.gameState.getOwner(Roles.Kyojuro) != null) {
					shinjuro.sendCustomActionBar(shinjuro.owner, Loc.getDirectionMate(shinjuro.owner, shinjuro.gameState.getOwner(Roles.Kyojuro), true));
				}
			}
			Material m = shinjuro.owner.getPlayer().getLocation().getBlock().getType();
			Location y1 = new Location(shinjuro.owner.getWorld(), shinjuro.owner.getLocation().getX(), shinjuro.owner.getLocation().getY()+1, shinjuro.owner.getLocation().getZ());
			Material a = y1.getBlock().getType();
			if (m == Material.LAVA || m == Material.STATIONARY_LAVA || a == Material.LAVA || a == Material.STATIONARY_LAVA) {
				if (shinjuro.owner.getHealth() != shinjuro.getMaxHealth()) {
					if (shinjuro.regencooldown == 0) {
						double max = shinjuro.getMaxHealth();
						double ahealth = shinjuro.owner.getHealth();
						double dif = max-ahealth;
						if (!(dif <= 1.0)) {
							shinjuro.Heal(shinjuro.owner, 1);
							shinjuro.owner.sendMessage("§7Vous venez de gagné§c 1/2"+AllDesc.coeur+"§7 suite à votre temp passé au chaud");
						} else {
							shinjuro.owner.setHealth(max);
						}
						shinjuro.regencooldown = 10;
					}else {
						shinjuro.sendCustomActionBar(shinjuro.owner, "§7Temp avant§d régénération§7:§l "+shinjuro.regencooldown+"s");
					}
				}
			} else {
				if (shinjuro.regencooldown != 10) shinjuro.regencooldown = 10;
			}
		}
	}
}