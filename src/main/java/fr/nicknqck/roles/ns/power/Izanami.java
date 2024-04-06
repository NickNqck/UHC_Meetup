package fr.nicknqck.roles.ns.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKill;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.NMSPacket;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;

public class Izanami implements Listener{
	
	private final UUID target;
	private final UUID user;
	private int taperCoupRemaining = 15;
	private String color;
	private int FraperCoupRemaining = 15;
	private HashMap<MissionUser, Boolean> Missions = new HashMap<>();
	private HashMap<MissionTarget, Boolean> TargetMissions = new HashMap<>();
	private final List<BukkitRunnable> runnables = new ArrayList<>();
	private int gapEatingRemaining = 5;
	public Izanami(UUID user, UUID target) {
		this.target = target;
		this.user = user;
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	private boolean isNotNull() {
		if (user == null) {
			return false;
		}
		if (color == null) {
			return false;
		}
		if (target == null) {
			return false;
		}
		return true;
	}
	private boolean isGoodMission(MissionUser m) {
		if (Missions.containsKey(m) && !Missions.get(m)) {
			return true;
		}
		return false;
	}
	private boolean isGoodMission(MissionTarget m) {
		if (TargetMissions.containsKey(m) && !TargetMissions.get(m)) {
			return true;
		}
		return false;
	}
	private void setTrueMissions(MissionTarget m) {
		if (isGoodMission(m)) {
			TargetMissions.remove(m, false);
			TargetMissions.put(m, true);
			if (Bukkit.getPlayer(user) != null && Bukkit.getPlayer(target) != null) {
				Bukkit.getPlayer(user).sendMessage("§c"+Bukkit.getPlayer(target).getDisplayName()+"§7 a terminé sa mission pour votre"+color+" Izanami§7: "+m.getMission());
			}
		}
	}
	private void setTrueMissions(MissionUser m) {
		if (isGoodMission(m)) {
			Missions.remove(m, false);
			Missions.put(m, true);
			if (Bukkit.getPlayer(user) != null) {
				Bukkit.getPlayer(user).sendMessage("§7Vous avez terminé la mission de votre"+color+" Izanami§7:§r "+m.getMission());
			}
		}
	}
	public String[] getStringsMission() {
		String l1 = "";
		String l2 = "";
		for (MissionUser mu : Missions.keySet()) {
			if (l1 == "") {
				l1 = mu.getMission();
			}
			if (l2 == "" || l2 == l1) {
				l2 = mu.getMission();
			}
		}
		return new String[] {
				"",
				"§eVos Missions: ",
				"",
				"§7 -§f "+l1,
				"",
				"§7 - §f"+l2,
				"",
				"§eMission de la cible: ",
				"",
				"§7 - §f"+TargetMissions.keySet().stream().findFirst().get().getMission()
		};
	}
	private MissionTarget getMission() {
		for (MissionTarget mt : TargetMissions.keySet()) {
			return mt;
		}
		return null;
	}
	public boolean isAllTrue() {
		for (MissionUser mu : Missions.keySet()) {
			if (!Missions.get(mu)) {
				return false;
			}
		}
		for (MissionTarget mt : TargetMissions.keySet()) {
			if (!TargetMissions.get(mt)) {
				return false;
			}
		}
		return true;
	}
	public UUID getUser() {
		return user;
	}
	public UUID getTarget() {
		return target;
	}
	private enum MissionUser {
		Taper(0, "\"Tapée la cible 15x\""),//Fait
		Lave(1, "\"Mettre de la§6 lave§f sous la cible\""),//Fait
		Fraper(2, "\"Être frappé par l'épée de la cible 15x\""),//FAIT
		Gap(3, "\"Donner une pomme d'or à la cible\""),//FAIT
		Rester(4, "\"Rester proche de la cible (§c20 blocs§f) pendant 5m\"");//FAIT
		
		private String mission;
		private int nmb;
		private MissionUser(int nmb, String string) {
			this.nmb=nmb;
			this.mission = string;
		}
		public String getMission() {
			return mission;
		}
		public int getNMB() {
			return nmb;
		}
	}
	private enum MissionTarget {
		Tuer(0, "\"Tuer un joueur\""),//FAIT
		Gap(1, "\"Manger 5§e pommes d'or\""),//FAIT
		Distance(2, "\"Rester loin de vous (§c30 blocs§f) pendant 1 minutes\"s");
		private String mission;
		private int nmb;
		private MissionTarget(int nmb, String string) {
			this.nmb=nmb;
			this.mission = string;
		}
		public String getMission() {
			return mission;
		}
		public int getNMB() {
			return nmb;
		}
	}
	public void start(String izanamiColor) {
		this.color = izanamiColor;
		int rdm1 = RandomUtils.getRandomInt(0, 4);
		int rdm2 = rdm1;
		while (rdm2 == rdm1 || rdm2 > 4) {
			rdm2 = RandomUtils.getRandomInt(0, 5);
		}
		int rdm3 = RandomUtils.getRandomInt(0, 2);
		for (MissionUser mo : MissionUser.values()) {
			if (mo.getNMB() == rdm1) {
				Missions.put(mo, false);
			}
			if (mo.getNMB() == rdm2) {
				Missions.put(mo, false);
			}
		}
		for (MissionTarget mt : MissionTarget.values()) {
			if (mt.getNMB() == rdm3) {
				TargetMissions.put(mt, false);
				break;
			}
		}
		if (isGoodMission(MissionUser.Rester)) {
			BukkitRunnable run1 = new BukkitRunnable() {
				private int timeRemaining = 60*5;
				@Override
				public void run() {
					Player user1 = Bukkit.getPlayer(user);
					Player target1 = Bukkit.getPlayer(target);
					if (user1 != null && target1 != null) {
						if (Loc.getNearbyPlayersExcept(user1, 20).contains(target1)) {
							NMSPacket.sendActionBar(user1, "§bTemp restant proche de§c "+target1.getDisplayName()+"§b:§c "+StringUtils.secondsTowardsBeautiful(timeRemaining));
							timeRemaining--;
						}
						if (timeRemaining == 0) {
							setTrueMissions(MissionUser.Rester);
							cancel();
						}
					}
					
				}
			};
			run1.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);//Le Async est censé permettre d'être plus opti
			runnables.add(run1);
		}
		if (isGoodMission(MissionTarget.Distance)) {
			BukkitRunnable run = new  BukkitRunnable() {
				private int timeRemaining = 60;
				@Override
				public void run() {
					Player user1 = Bukkit.getPlayer(user);
					Player target1 = Bukkit.getPlayer(target);
					if (user1 != null && target1 != null) {
						if (!Loc.getNearbyPlayersExcept(user1, 30).contains(target1)) {
							NMSPacket.sendActionBar(user1, "§bTemp restant loin de§c "+target1.getDisplayName()+"§b:§c "+StringUtils.secondsTowardsBeautiful(timeRemaining));
							timeRemaining--;
						}
						if (timeRemaining == 0) {
							setTrueMissions(MissionTarget.Distance);
							cancel();
						}
					}
					
				}
			};
			run.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);//Le Async est censé permettre d'être plus opti
			this.runnables.add(run);
		}
	}
	public ItemStack getResultVictimMission() {
		ItemBuilder ib = new ItemBuilder(Material.NETHER_STAR).setName("§eMission de la victim");
		ib.setLore("§eSa mission§f "+getMission().getMission()+"§e est "+(TargetMissions.get(getMission()) ? "§aTerminé" : "§cInachevé"+(getMission() == MissionTarget.Gap ? "§7 (§ePomme d'or§7 restante§c§l "+gapEatingRemaining : "")));
		return ib.toItemStack();
	}
	public ItemStack getResultUserMission() {
		ItemBuilder ib = new ItemBuilder(Material.NETHER_STAR).setName("Vos missions:");
		ib.addLoreLine("");
		for (MissionUser mu : Missions.keySet()) {
			ib.addLoreLine("§eVotre mission §f"+mu.getMission()+"§e est "+(Missions.get(mu) ? "§aTerminé" : "§cInachevé "+getOtherStrings(mu)));
		}
		return ib.toItemStack();
	}
	private String getOtherStrings(MissionUser mu) {
		return (mu == MissionUser.Taper ? "§7Coup restant à infligé:§c§l "+taperCoupRemaining : mu == MissionUser.Fraper ? "§7Coup restant à subir:§c§l "+FraperCoupRemaining : "");//Code compliquer a expliquer à l'écrit mp moi si tu comprend pas
	}
	@EventHandler
	private void onEat(PlayerItemConsumeEvent e) {
		if (isNotNull() && isGoodMission(MissionTarget.Gap)) {
			if (e.getPlayer().getUniqueId().equals(target) && e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
				gapEatingRemaining--;
				Player inf = Bukkit.getPlayer(user);
				if (inf != null) {
					inf.sendMessage("§c"+e.getPlayer().getDisplayName()+"§7 vient de manger une§e Pomme d'or§7, plus que§c§l "+gapEatingRemaining+"§7, avant la fin de sa mission.");
				}
				if (gapEatingRemaining == 0) {
					setTrueMissions(MissionTarget.Gap);
				}
			}
		}
	}
	@EventHandler
	private void onEndGame(EndGameEvent e) {
		runnables.clear();
		Missions.clear();
		TargetMissions.clear();
		gapEatingRemaining = 5;
		FraperCoupRemaining = 15;
		taperCoupRemaining = 15;
	}
	@EventHandler
	private void onKill(UHCPlayerKill e) {
		if (isNotNull()) {
			if (isGoodMission(MissionTarget.Tuer)) {
				if (e.getKiller().getUniqueId().equals(target)) {
					setTrueMissions(MissionTarget.Tuer);
				}
			}
		}
	}
	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		if (isGoodMission(MissionUser.Gap) && isNotNull()) {
			if (e.getPlayer().getUniqueId().equals(user) && e.getItemDrop().getItemStack().getType().equals(Material.GOLDEN_APPLE)) {
				e.getItemDrop().setMetadata("Izanami.Gap"+user.toString(), new FixedMetadataValue(Main.getInstance(), user));
			}
		}
	}
	@EventHandler
	private void onRecup(PlayerPickupItemEvent e) {
		if (isGoodMission(MissionUser.Gap) && isNotNull()) {
			if (e.getItem().hasMetadata("Izanami.Gap"+user.toString())) {
		//		Bukkit.getPlayer(user).sendMessage("§c"+e.getPlayer().getDisplayName()+"§7 a récupérer votre§c");
				if (e.getPlayer().getUniqueId().equals(target)) {
					setTrueMissions(MissionUser.Gap);
				}
			}
		}
	}
	@EventHandler
	private void onBattle(EntityDamageByEntityEvent e) {
		if (!isNotNull())return;
		if (isGoodMission(MissionUser.Taper)) {
			if (e.getDamager().getUniqueId().equals(user) && e.getEntity().getUniqueId().equals(target)) {
				taperCoupRemaining--;
				NMSPacket.sendActionBar(Bukkit.getPlayer(user), "§7Coup restant >>§c "+taperCoupRemaining);
				if (taperCoupRemaining == 0) {
					setTrueMissions(MissionUser.Taper);
				}
			}
		}
		if (isGoodMission(MissionUser.Fraper)) {
			if (e.getDamager().getUniqueId().equals(target) && e.getEntity().getUniqueId().equals(user) && e.getDamager() instanceof Player) {
				Player damager = Bukkit.getPlayer(target);
				if (damager != null) {
					FraperCoupRemaining--;
					NMSPacket.sendActionBar(Bukkit.getPlayer(user), "§7Coup restant >>§c "+FraperCoupRemaining);
					if (FraperCoupRemaining == 0) {
						setTrueMissions(MissionUser.Fraper);
					}
				}
			}
		}
	}
	@EventHandler
	private void onMoove(PlayerMoveEvent e) {
		if (!isNotNull())return;
		if (e.getPlayer().getUniqueId().equals(target)) {
			if (isGoodMission(MissionUser.Lave)) {
				Player user = Bukkit.getPlayer(this.user);
				if (e.getFrom().getBlock().getType().name().contains("LAVA") || e.getTo().getBlock().getType().name().contains("LAVA") && user != null) {
					if (Loc.getNearbyPlayers(user, 15).contains(e.getPlayer())) {
						setTrueMissions(MissionUser.Lave);
					}
				}
			}
		}
	}
}