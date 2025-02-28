package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
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
		addPower(new RegenerationPower(this));
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
		return new ItemStack[0];
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
	private static class RegenerationPower extends Power {

		public RegenerationPower(@NonNull RoleBase role) {
			super("Régénération", null, role);
			new RegenRunnable(role.getGameState(), role.getGamePlayer());
		}

		@Override
		public boolean onUse(Player player, Map<String, Object> map) {
			return true;
		}
		private static class RegenRunnable extends BukkitRunnable {

			private final GameState gameState;
			private final GamePlayer gamePlayer;
			private int regenTimeLeft;

            private RegenRunnable(GameState gameState, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
				this.regenTimeLeft = 10;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				final Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
				if (owner == null) {
					this.regenTimeLeft = 10;
					return;
				}
				final Location location = owner.getLocation();
				final Location eye = owner.getEyeLocation();
				if (location.getBlock().getType().name().contains("LAVA") || location.getBlock().getType().name().contains("FIRE")
				|| eye.getBlock().getType().name().contains("LAVA") || eye.getBlock().getType().name().contains("FIRE")) {
					this.regenTimeLeft--;
					if (!this.gamePlayer.getActionBarManager().containsKey("shinjuro.healtime")) {
						gamePlayer.getActionBarManager().addToActionBar("shinjuro.healtime", "§bTemp avant§d régénération§b:§c "+this.regenTimeLeft+"s");
					} else {
						this.gamePlayer.getActionBarManager().updateActionBar("shinjuro.healtime", "§bTemp avant§d régénération§b:§c "+this.regenTimeLeft+"s");
					}
				} else {
					this.gamePlayer.getActionBarManager().removeInActionBar("shinjuro.healtime");
				}
				if (this.regenTimeLeft <= 0) {
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0)));
					this.regenTimeLeft = 10;
				}
			}
		}
	}
}