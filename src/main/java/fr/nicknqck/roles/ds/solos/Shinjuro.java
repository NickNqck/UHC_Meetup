package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.KyojuroV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class Shinjuro extends DemonsSlayersRoles {

	public boolean alliance = false;
	private boolean killkyojuro = false;
	private int regencooldown = 10;
	public Shinjuro(UUID player) {
		super(player);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.FLAMME;
	}

	@Override
	public void RoleGiven(GameState gameState) {
		setCanuseblade(true);
		Lames.FireResistance.getUsers().put(getPlayer(), Integer.MAX_VALUE);
		setMaxHealth(24.0);
		givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
		setLameIncassable(owner, true);
		addPower(new SakePower(this), true);
		addPower(new SouflePower(this), true);
		new onTick(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Shinjuro;
	}

	@Override
	public TeamList getOriginTeam() {
		return TeamList.Solo;
	}

	@Override
	public void resetCooldown() {}

	@Override
	public TextComponent getComponent() {
		return new AutomaticDesc(this)
				.addEffects(getEffects())
				.setItems(
						new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
								new TextComponent("§7Vous offre l'effet§b Speed I§7 pendant§c 1m30s§7\n\n§7Si vous avez tuer§a Kyojuro§7 vous aurez l'effet§b Speed II§7 à la place")
						}), "§6Sake", 60*5),
						new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
								new TextComponent("§7Vous permet d'§aactiver§7/§cdésactiver§7 votre effet de§6 Fire Résistance I")
						}), "§6Soufle du feu", 5))
				.addParticularites(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
								new TextComponent("§7Quand vous êtes sous l'effet de votre§6 Soufle du feu§7 vos coups mette en§c feu§7.")
						}),
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
								new TextComponent("§7Vous vous§d régénérez§7 de§c 1/2"+AllDesc.coeur+"§7 toute les§c 10 secondes")
						}))
				.getText();
	}

	@Override
	public String[] Desc() {
		return new String[0];
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
		};
	}
	@Override
 	public void Update(GameState gameState) {
		if (this.alliance) {
			if (gameState.getOwner(Roles.Kyojuro) != null) {
				for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
					if (p.equals(gameState.getOwner(Roles.Kyojuro))) {
						OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
					}
				}
			}
		}
		if (regencooldown >= 1) {
			   regencooldown--;
		}
		super.Update(gameState);
	}

	@Override
	public void PlayerKilled(Player killer, Player victim, GameState gameState) {
		if (killer == owner) {
			if (victim != owner) {
				if (gameState.getInGamePlayers().contains(victim.getUniqueId())) {
					if (!gameState.hasRoleNull(victim.getUniqueId())) {
						RoleBase role = gameState.getGamePlayer().get(victim.getUniqueId()).getRole();
						if (role instanceof KyojuroV2 && !killkyojuro) {
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
	public String getName() {
		return "Shinjuro";
	}

	public void procAlliance() {
		this.alliance = true;
		Power power = null;
		for (final Power p : this.getPowers()) {
			if (p instanceof SakePower) {
				power = p;
				break;
			}
		}
		if (power == null)return;
		power.setSendCooldown(false);
		((SakePower) power).setShowCdInHand(false);
		getPowers().remove(power);
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
			if (shinjuro.alliance) {
				if (shinjuro.gameState.getOwner(Roles.Kyojuro) != null) {
					shinjuro.sendCustomActionBar(shinjuro.owner, Loc.getDirectionMate(shinjuro.owner, shinjuro.gameState.getOwner(Roles.Kyojuro), true));
				}
			}
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
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
			});
		}
	}
	private static class SakePower extends ItemPower {

		private final Shinjuro shinjuro;

		protected SakePower(@NonNull Shinjuro role) {
			super("§6Sake", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§6Sake"), role);
			this.shinjuro = role;
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				player.sendMessage("Vous venez de boire de l'alcool");
				if (!this.shinjuro.killkyojuro){
					this.shinjuro.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*90, 0, false, false), EffectWhen.NOW);
				} else {
					this.shinjuro.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*90, 1, false, false), EffectWhen.NOW);
				}
				return true;
			}
			return false;
		}
	}
	private static class SouflePower extends ItemPower implements Listener {

		private boolean use = false;
		private final PotionEffect fireResistance;
		private final PotionEffect speed;
		private final Shinjuro shinjuro;

		protected SouflePower(@NonNull Shinjuro role) {
			super("§6Soufle du Feu", new Cooldown(3), new ItemBuilder(Material.NETHER_STAR).setName("§6Soufle du Feu"), role);
			this.shinjuro = role;
			this.fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false);
			this.speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false);
			EventUtils.registerRoleEvent(this);
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				if (this.use) {
					player.sendMessage("Vous avez§c désactivé§f votre "+getName());
					getRole().getEffects().remove(this.fireResistance, EffectWhen.PERMANENT);
					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
					if (this.shinjuro.killkyojuro) {
						getRole().getEffects().remove(speed, EffectWhen.PERMANENT);
						player.removePotionEffect(PotionEffectType.SPEED);
					}
					this.use = false;
				} else {
					player.sendMessage("Vous avez§a activé§f votre "+getName());
					getRole().givePotionEffect(this.fireResistance, EffectWhen.PERMANENT);
					if (this.shinjuro.killkyojuro) {
						getRole().givePotionEffect(this.speed, EffectWhen.PERMANENT);
					}
					this.use = true;
				}
				return true;
			}
			return false;
		}
		@EventHandler
		private void UHCPlayerBattleEvent(final EntityDamageByEntityEvent event) {
			if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
			if (!(event.getEntity() instanceof Player))return;
			if (!(event.getDamager() instanceof Player))return;
			if (!checkIfPowerEnable((Player) event.getDamager()))return;
			if (!this.use)return;
			event.getEntity().setFireTicks(event.getEntity().getFireTicks()+150);
		}
	}
}