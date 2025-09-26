package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class Jigoro extends DemonsSlayersRoles implements Listener {

	private boolean killzen = false;
	private boolean killkai = false;

    @Override
	public String getName() {
		return "Jigoro";
	}

	public Jigoro(UUID player) {
		super(player);
	}

	@Override
	public void RoleGiven(GameState gameState) {
		this.setCanuseblade(true);
		this.setResi(20);
		addKnowedRole(ZenItsuV2.class);
		addKnowedRole(KaigakuV2.class);
		setLameIncassable(owner, true);
		addPower(new ZoneFoudrePower(this), true);
		givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false), EffectWhen.PERMANENT);
		givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0, false, false), EffectWhen.PERMANENT);
		EventUtils.registerRoleEvent(this);
	}

	@Override
	public Soufle getSoufle() {
		return Soufle.FOUDRE;
	}

	@Override
	public @NonNull Roles getRoles() {
		return Roles.Jigoro;
	}

	@Override
	public @NonNull TeamList getOriginTeam() {
		return TeamList.Solo;
	}

    @Override
	public String[] Desc() {
		return AllDesc.Jigoro;
	}

    @EventHandler
	private void onKill(UHCPlayerKillEvent event) {
		if (event.getGamePlayerKiller() == null)return;
		if (event.getGamePlayerKiller().getRole() == null)return;
		if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
		if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
			final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
			if (role instanceof ZenItsuV2 && !killzen) {
				addPower(new SpeedTroisPower(this), true);
				event.getGamePlayerKiller().sendMessage("§7Vous venez de tuez §aZen'Itsu§7 vous obtenez donc §cforce 1§7 le §ejour§7, ainsi que l'accès au:§6 Premier Mouvement du Soufle de la Foudre§7 qui vous donnera§e Speed 3§7 pendant§c 1 minutes");
				killzen = true;
				givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.DAY);
			}
			if (role instanceof KaigakuV2 && !killkai) {
				killkai = true;
				giveItem(event.getPlayerKiller(), false, Items.getSoufleFoudre4iememouvement());
				event.getGamePlayerKiller().sendMessage("§7Vous venez de tuez§c Kaigaku§7 vous obtenez donc§c force 1 §7la §cnuit§7, ainsi que l'accès au:§6 Quatrième Mouvement du Soufle de la Foudre§7 qui vous téléportera à la personne la plus proche que vous pouvez voir dans un rayon maximum de§c 30 blocs");
				givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.NIGHT);
				addPower(new TPPower(this), true);
			}
			if (killkai && killzen) {
				getEffects().remove(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.NIGHT);
				getEffects().remove(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.DAY);
				givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0, false, false), EffectWhen.PERMANENT);
				getGamePlayer().sendMessage("§7Vous avez tué vos§c deux disciple§7 vos§c force§7 est maintenant§c permanente§7...");
			}
		}
	}

	private static class TPPower extends ItemPower {

		protected TPPower(@NonNull Jigoro role) {
			super("Soufle de la Foudre: Quatrième Mouvement", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§eSoufle de la Foudre: Quatrième Mouvement"), role);
		}

		@Override
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				final Player target = RayTrace.getTargetPlayer(player, 30, null);
				if (target == null) {
					player.sendMessage("§cIl faut viser un joueur");
					return false;
				}
				if (player.canSee(target)) {
					final Location loc = target.getLocation();
					if (Main.isDebug()){
						System.out.println(target.getEyeLocation());
					}
					loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
					loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
					loc.setPitch(0);
					if (Main.isDebug()) {
						System.out.println(loc);
					}
					target.getWorld().strikeLightning(target.getLocation());
					if (target.getHealth() > 4.0) {
						target.setHealth(target.getHealth() - 4.0);
					} else {
						target.setHealth(0.5);
					}
					player.sendMessage("§aExécution du§6 Quatrième mouvement du soufle de la foudre");
					target.sendMessage("§fVous avez été touché par un soufle de la foudre");
					player.teleport(loc);
					if (((Jigoro) getRole()).killzen) {
						Bukkit.getScheduler().runTaskLater(getPlugin(), () -> getCooldown().addSeconds(-60), 5);
					}
					return true;
				}
				return true;
			}
			return false;
		}
	}
	private static class SpeedTroisPower extends ItemPower {

		protected SpeedTroisPower(@NonNull RoleBase role) {
			super("Soufle de la foudre: Premier Mouvement", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§eSoufle de la foudre: Premier Mouvement"), role,
					"");
		}

		@Override
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				player.sendMessage("§7Vous venez d'utiliser votre §eSpeed III");
				getRole().getGameState().spawnLightningBolt(player.getWorld(), player.getLocation());
				getRole().getEffects().remove(new PotionEffect(PotionEffectType.SPEED, 80, 0, false, false), EffectWhen.PERMANENT);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 2, false, false), true);
				new RecupSpeedRunnable(this);
				return true;
			}
			return false;
		}
		private static class RecupSpeedRunnable extends BukkitRunnable {

			private final SpeedTroisPower power;
			private int timeLeft;

            private RecupSpeedRunnable(SpeedTroisPower power) {
                this.power = power;
				this.timeLeft = 0;
				runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
			public void run() {
				if (!power.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				if (this.timeLeft == 0) {
					Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.power.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0, false, false), EffectWhen.PERMANENT));
					cancel();
					return;
				}
				this.timeLeft--;
			}
		}
 	}
	private static class ZoneFoudrePower extends ItemPower {

		protected ZoneFoudrePower(@NonNull RoleBase role) {
			super("Zone de Foudre", new Cooldown(60*10+15), new ItemBuilder(Material.NETHER_STAR).setName("§6Zone de Foudre"), role,
					"§7Crée une zone circulaire de §c15 blocs§7 de§c rayon§7, dans laquelle vous recevrez l'effet§d régénération 1§7 pendant§c 15s§7.",
					"§7Toutes les §c4 secondes§7, un§e éclair§7 infligeant §c2❤§7 apparaîtra sur les joueurs présent dans la zone.");
		}

		@Override
		public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
			if (getInteractType().equals(InteractType.INTERACT)) {
				new ZoneRunnable(this);
				player.sendMessage("§7Activation de votre§6 Zone de Foudre");
				return true;
			}
			return false;
		}
		private static class ZoneRunnable extends BukkitRunnable {

			private final ZoneFoudrePower power;
			private final GameState gameState;
			private int timeLeft;
			private int cooldowndegat;

            private ZoneRunnable(ZoneFoudrePower power) {
                this.power = power;
				this.gameState = power.getRole().getGameState();
				this.timeLeft = 15;
				this.cooldowndegat = 0;
				power.getRole().getGamePlayer().getActionBarManager().addToActionBar("jigoro.zonefoudre", "§bTemp de zone restant:§c 15s");
            }

            @Override
			public void run() {
				if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
					cancel();
					return;
				}
				final Player owner = Bukkit.getPlayer(power.getRole().getPlayer());
				if (owner == null)return;
				if (power.getRole().getGamePlayer().isAlive()) {
					MathUtil.sendCircleParticle(EnumParticle.VILLAGER_ANGRY, owner.getLocation(), 10, 15);
					for (final Player p : Loc.getNearbyPlayersExcept(owner, 15)) {
						if (!gameState.hasRoleNull(p.getUniqueId())) {
							if (gameState.getInGamePlayers().contains(p.getUniqueId())) {
								if (cooldowndegat <= 0 && p != owner) {
									if (p.getHealth() > 4.0) {
										p.setHealth(p.getHealth() - 4.0);
									} else {
										p.setHealth(0.5);
									}
									owner.sendMessage("§7Vous avez foudroyé:§6 "+ p.getDisplayName());
									p.sendMessage("§6Jigoro§7 vous à fait perdre§c 2❤§7 suite à votre§e Foudroyage");
									p.getWorld().strikeLightningEffect(p.getLocation());
									Location loc = p.getLocation();
									gameState.spawnLightningBolt(loc.getWorld(), loc);
									Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> cooldowndegat = 4, 1);
								}
							}
						}
					}
				}
				this.cooldowndegat--;
				if (timeLeft == 0) {
					owner.sendMessage("§7Votre§6 Zone de Foudre§7 se termine");
					cancel();
					return;
				}
				this.timeLeft--;
			}
		}
	}
}