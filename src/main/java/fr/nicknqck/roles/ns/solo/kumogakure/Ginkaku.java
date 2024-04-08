package fr.nicknqck.roles.ns.solo.kumogakure;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.GameState.ServerStates;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.StringUtils;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class Ginkaku extends RoleBase{
	
	private final ItemStack KyubiItem = new ItemBuilder(Material.NETHER_STAR).setName("§6§lKyubi").setLore("§7Vous permet d'obtenir des effets").toItemStack();
	private int cdKyubi = 0;
	private final ItemStack SabreItem = new ItemBuilder(Material.DIAMOND_SWORD).setName("§aSabre des Septs Étoiles").addEnchant(Enchantment.DAMAGE_ALL, 3).setUnbreakable(true).setLore("§7Après avoir infligé§c 15 coups§7 d'affilé a un joueur, lui inflige un effet de§c saignement").toItemStack();
	private final HashMap<UUID, Integer> coupInfliged = new HashMap<>();
	private int cdSabre = 0;
	private final ItemStack CordeItem = new ItemBuilder(Material.NETHER_STAR).setName("§6Corde d'or").setUnbreakable(true).setLore("§7Vous permet d'éjecter le joueur viser, puis de l'empêcher de bouger pendant§c 5s§7.").toItemStack();
	private int cdCorde = 0;
	private TargetFallChecker checker;
	private final ItemStack GourdeItem = new ItemBuilder(Material.HOPPER).setName("§bGourde écarlate").setLore("§7Vous permet de connaitre le daron a C3rv0l3nt").addEnchant(Enchantment.DAMAGE_ALL, 1).hideEnchantAttributes().toItemStack();
	private UUID GourdeTarget;
	private int cdGourde = 0;
	public Ginkaku(Player player, Roles roles, GameState gameState) {
		super(player, roles, gameState);
		setChakraType(getRandomChakras());
		owner.sendMessage(Desc());
	}
	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}
	@Override
	public String[] Desc() {
		return new String[] {
				AllDesc.bar,
				AllDesc.role+"§6Ginkaku",
				AllDesc.objectifsolo+"avec§6 Kinkaku",
				"",
				AllDesc.effet,
				"",
				AllDesc.point+"§9Résistance I§f proche de§6 Kinkaku§f et§e Speed I§f la "+AllDesc.nuit,
				"",
				AllDesc.items,
				"",
				AllDesc.point+"§6§lKyubi§f: Pendant§c 3 minutes§f vous offre des effets, cependant ils changent chaque minutes: ",
				AllDesc.tab+"§aPremière minute§f: Vous obtenez les effets§e Speed II§f ainsi que§c Force I§f.",
				AllDesc.tab+"§6Deuxième minute§f: Vous obtenez les effets§e Speed I§f ainsi que§c Force I§f.",
				AllDesc.tab+"§cTroisième minute§f: Vous obtenez l'effet§e Speed I§f.",
				"§c! Ce pouvoir est utilisable une fois toute les 12 minutes !",
				"",
				AllDesc.point+"§6Corde d'or§f: En visant un joueur, le repousse en l'air, puis, lorsqu'il attérit, l'empêche de bouger pendant§c 5s§f.§7 (1x/3m)",
				"",
				AllDesc.point+"§bGourde écarlate§f: Après avoir infligé§c 1 joueurs§f vous pourrez poser un§7 hopper§f à votre position, puis,§c 10 secondes§f plus tard, si le§7 hopper§f n'a pas été cassé, téléporte le joueur sur le§7 hopper§f et obtiendra les effets:§2 Poison I§f et§8 Wither I§f pendant§c 10s§f.§7 (1x/7m)",
				"",
				AllDesc.point+"§aSabre des septs étoiles§f: Sous forme d'une§7 Épée en diamant Tranchant III§f, infligera un effet de§c saignement§f (§c1/2"+AllDesc.coeur+" toute les§c 2 secondes§f pendant§c 8 secondes§f) au joueur sur qui vous infligerez§c 15 coups§f d'affilé.§7 (1x/30s)",
				"",
				AllDesc.particularite,
				"",
				"Vous connaissez le joueur possédant le rôle de§6 Kinkaku",
				"",
				"Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
				AllDesc.bar

		};
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(GourdeItem)){
			if (cdGourde <= 0){
				if (GourdeTarget != null){
					cdGourde = 60*7+10;
					owner.getLocation().getWorld().getBlockAt(owner.getLocation()).setType(Material.HOPPER, true);
				//	PacketDisplay display = new PacketDisplay(owner.getLocation(), "§c10s", false, true, true, true, true);
					new BukkitRunnable() {
						private final Location initLoc = owner.getLocation().clone();
						private int timeRemaining = 10;
						private final UUID uuid = GourdeTarget;
					//	private final PacketDisplay dp = display;
						@Override
						public void run() {
							if (!gameState.getServerState().equals(ServerStates.InGame) || !getIGPlayers().contains(owner)){
								cancel();
							}
							if (timeRemaining == 0){
								cdGourde = 60*7;
								Player p = Bukkit.getPlayer(uuid);
								if (p != null){
									initLoc.getWorld().getBlockAt(new Location(initLoc.getWorld(), initLoc.getX(), initLoc.getY(), initLoc.getZ(), p.getEyeLocation().getYaw(), p.getEyeLocation().getPitch())).setType(Material.AIR);
									p.teleport(new Location(initLoc.getWorld(), initLoc.getX(), initLoc.getY()+1.5, initLoc.getZ(), p.getEyeLocation().getYaw(), p.getEyeLocation().getPitch()));
									p.sendMessage("§7Vous avez été téléporter au lieu de scellement de§6 Ginkaku§7.");
									owner.sendMessage("§c"+p.getDisplayName()+"§7 à été téléporter");
									givePotionEffet(p, PotionEffectType.POISON, 20*10, 1, true);
									givePotionEffet(p, PotionEffectType.WITHER, 20*10, 1, true);
								}
								initLoc.getWorld().getBlockAt(initLoc).setType(Material.AIR, true);
								cancel();
							}
							sendCustomActionBar(owner, "§bTemp avant scellement: §c"+StringUtils.secondsTowardsBeautiful(timeRemaining));
							timeRemaining--;
						}
					}.runTaskTimer(Main.getInstance(), 0, 20);
				} else {
					owner.sendMessage("§cIl faut d'abord démarrer le scellement sur un joueur");
				}
			} else {
				sendCooldown(owner, cdGourde);
				return true;
			}
			return true;
		}
		if (item.isSimilar(KyubiItem)) {
			if (cdKyubi <= 0) {
				owner.sendMessage("§7Activation de§6 Kyubi");
				cdKyubi = 60*15;
				new BukkitRunnable() {
					private int time = 60;
					private int state = 3;
					@Override
					public void run() {
						if (owner == null) {
							cancel();
							return;
						}
						if (gameState.getServerState() != ServerStates.InGame) {
							cancel();
							return;
						}
						if (state == 0) {
							owner.sendMessage("§7L'utilisation du chakra de§6 Kyubi§7 est maintenant§c terminer§7.");
							cancel();
							return;
						}
						if (state == 3) {
							givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						}
						if (state == 2) {
							givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
							givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
						}
						if (state == 1) {
							givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
						}
						if (time == 0) {
							state--;
							time = 60;
						}
						sendCustomActionBar(owner, "Temp avant prochain stade de§6 Kyubi§f:§c "+StringUtils.secondsTowardsBeautiful(time));
						time--;
					}
				}.runTaskTimer(Main.getInstance(), 0, 20);
			} else {
				sendCooldown(owner, cdKyubi);
				return true;
			}
		}
		if (item.isSimilar(CordeItem)){
			if (cdCorde <= 0){
				Player target = getTargetPlayer(owner, 25);
				if (target != null){
					ejectPlayers(owner.getLocation(), target);
					owner.sendMessage("§7Vous éjectez§c "+target.getDisplayName()+"§7.");
					cdCorde = 120;
					if (checker == null){
						checker = new TargetFallChecker();
					}
					checker.starter(target.getUniqueId());
				} else {
					owner.sendMessage("§cIl faut viser un joueur !");
				}
			} else {
				sendCooldown(owner, cdCorde);
			}
			return true;
		}
		return super.ItemUse(item, gameState);
	}
	@Override
	public void Update(GameState gameState) {
		if (gameState.nightTime && cdKyubi < 60*12) {
			givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
		}
		if (cdKyubi >= 0) {
			cdKyubi--;
			if (cdKyubi == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser§6§l Kyubi§7.");
			}
		}
		if (cdSabre >= 0){
			cdSabre--;
			if (cdSabre == 0){
				owner.sendMessage("§7Vous pouvez à nouveau infliger un effet de§c saignement§7 à un joueur.");
			}
		}
		if (cdCorde >= 0){
			cdCorde--;
			if (cdCorde == 0){
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§6 Corde d'or§7.");
			}
		}
		if (cdGourde >= 0){
			cdGourde--;
			if (cdGourde == 0){
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§b Gourde écarlate§7.");
			}
		}
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				KyubiItem,
				SabreItem,
				CordeItem,
				GourdeItem
		};
	}
	@Override
	public void resetCooldown() {
		cdKyubi = 0;
		cdSabre = 0;
		cdCorde = 0;
		cdGourde = 0;
	}
	private void ejectPlayers(final Location location, final Player player) {
			final double distance = Math.sqrt(Math.pow(player.getLocation().getX() - location.getX(), 2) + Math.pow(player.getLocation().getY() - location.getY(), 2) + Math.pow(player.getLocation().getZ() - location.getZ(), 2));

			final double exposure = ((CraftWorld) player.getWorld()).getHandle().a(new Vec3D(location.getX(), location.getY(), location.getZ()), ((CraftEntity) player).getHandle().getBoundingBox());

			final double multiply = (1D - (distance / (8 * 2F))) * exposure;

			final Vector vector = new Vector((player.getLocation().getX() - location.getX()) * multiply, (player.getLocation().getY() + 1.62D - location.getY()) * multiply, (player.getLocation().getZ() - location.getZ()) * multiply);

			player.setVelocity(player.getVelocity().add(vector));
	}
	@Override
	public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
		super.onALLPlayerDamageByEntity(event, victim, entity);
		if (entity.getUniqueId().equals(owner.getUniqueId())) {
			if (owner.getItemInHand().isSimilar(GourdeItem)){
				if (this.GourdeTarget == null){
					GourdeTarget = victim.getUniqueId();
					owner.sendMessage("§c"+victim.getDisplayName()+"§7 est prêt à être scellé dans votre§b Gourde§7.");
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						Player p = Bukkit.getPlayer(GourdeTarget);
						if (p != null && owner != null){
							owner.sendMessage("§c"+p.getDisplayName()+"§7 ne peut plus être scellé, pour l'instant...");
						}
						GourdeTarget = null;
					} ,20*10);
				} else {
					Player p = Bukkit.getPlayer(GourdeTarget);
					if (p != null){
						owner.sendMessage("§7Vous avez déjà un joueur en cours de scellement (§c"+p.getDisplayName()+"§7), vous ne pouvez donc pas scellé§c "+victim.getDisplayName()+"§7.");
					}
				}
			}
			if (owner.getItemInHand().isSimilar(SabreItem)){
				if (coupInfliged.containsKey(victim.getUniqueId())) {
					int i = coupInfliged.get(victim.getUniqueId());
					coupInfliged.remove(victim.getUniqueId(), i);
					coupInfliged.put(victim.getUniqueId(), i+1);
					if (coupInfliged.get(victim.getUniqueId()) == 15){
						cdSabre = 30;
						coupInfliged.clear();
						new BukkitRunnable() {
							private int timeRemaining = 8;
							private final UUID gTarget = victim.getUniqueId();
							@Override
							public void run() {
								if (gameState.getServerState() != ServerStates.InGame){
									cancel();
									return;
								}
								if (timeRemaining == 0){
									cdSabre = 30;
									owner.sendMessage("§7L'effet de§c saignement§7 est terminer.");
									cancel();
									return;
								}
								Player target = Bukkit.getPlayer(gTarget);
								if (target != null){
									damage(target, 1.0, 1, owner, true);
									sendCustomActionBar(victim, "§7Vous subissez un effet de§c saignement");
								}
								timeRemaining--;

							}
						}.runTaskTimer(Main.getInstance(), 0, 20);
					}
				} else {
					if (cdSabre <= 0){
						coupInfliged.put(victim.getUniqueId(), 1);
					}
				}
				if (cdSabre <= 0){
					sendCustomActionBar(owner, "§7Coup infligé contre§c "+victim.getDisplayName()+"§7 >> "+coupInfliged.get(victim.getUniqueId())+"/15");
				}
			}
		}
	}
	private class TargetFallChecker implements Listener {
		private UUID gTarget;
		TargetFallChecker(){
			Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		}
		public void starter(UUID uuid){
			gTarget = uuid;
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
				if (gTarget != null){
					Main.getInstance().getGamePlayer().stun(gTarget, 5.0, null);
					gTarget = null;
				}
			}, 100);
		}
		@EventHandler
		private void onDamage(EntityDamageEvent event){
			if (gTarget != null && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity().getUniqueId().equals(gTarget)){
				event.setDamage(0.0);
				event.getEntity().setFallDistance(0f);
				new BukkitRunnable() {
					private final Location initLoc = event.getEntity().getLocation();
					private final UUID gT = event.getEntity().getUniqueId();
					private int time = 100;
					@Override
					public void run() {
						Player p = Bukkit.getPlayer(gT);
						if (p != null){
							p.setAllowFlight(true);
							p.setFlying(true);
							p.teleport(initLoc);
							time--;
							if (time == 0){
								cancel();
								owner.sendMessage("§c"+p.getDisplayName()+"§7 peut à nouveau bouger.");
								p.setFlying(false);
								p.setAllowFlight(false);
								p.setFallDistance(0f);
								gTarget = null;
							}
						}
					}
				}.runTaskTimer(Main.getInstance(), 0, 1);
			}
		}
	}
}