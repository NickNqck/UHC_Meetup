package fr.nicknqck.roles.ns.power;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.ns.IzanamiFinishEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IzanamiV2 implements Listener {

    private final GamePlayer gamePlayer;
    private final GamePlayer gameTarget;
    private String color = "";
    private int FraperCoupRemaining = 15;
    private final HashMap<MissionUser, Boolean> Missions = new HashMap<>();
    private final HashMap<MissionTarget, Boolean> TargetMissions = new HashMap<>();
    private final List<BukkitRunnable> runnables = new ArrayList<>();
    private int gapEatingRemaining = 5;
    private int taperCoupRemaining = 15;

    public IzanamiV2(GamePlayer gamePlayer, GamePlayer gameTarget) {
        this.gamePlayer = gamePlayer;
        this.gameTarget = gameTarget;
        EventUtils.registerRoleEvent(this);
    }
    @Getter
    private enum MissionUser {
        Taper(0, "\"Tapée la cible 15x\""),
        Lave(1, "\"Mettre de la§6 lave§f sous la cible\""),
        Fraper(2, "\"être frappé par l'épée de la cible 15x\""),
        Gap(3, "\"Donner une pomme d'or à la cible\""),
        Rester(4, "\"Rester proche de la cible (§c20 blocs§f) pendant 5m\"");

        private final String mission;
        private final int nmb;
        MissionUser(int nmb, String string) {
            this.nmb=nmb;
            this.mission = string;
        }
    }
    @Getter
    private enum MissionTarget {
        Tuer(0, "\"Tuer un joueur\""),
        Gap(1, "\"Manger 5§e pommes d'or\""),
        Distance(2, "\"Rester loin de vous (§c30 blocs§f) pendant 1 minutes\"s");
        private final String mission;
        private final int nmb;
        MissionTarget(int nmb, String string) {
            this.nmb=nmb;
            this.mission = string;
        }
    }
    private boolean isNotNull() {
        return this.gamePlayer != null && this.color != null && this.gameTarget != null;
    }
    private boolean isGoodMission(MissionUser m) {
        return Missions.containsKey(m) && !Missions.get(m);
    }
    private boolean isGoodMission(MissionTarget m) {
        return TargetMissions.containsKey(m) && !TargetMissions.get(m);
    }
    private void setTrueMissions(MissionTarget m) {
        if (isGoodMission(m)) {
            TargetMissions.remove(m, false);
            TargetMissions.put(m, true);
            this.gamePlayer.sendMessage("§c"+this.gameTarget.getPlayerName()+"§7 a terminé sa mission pour votre"+color+" Izanami§7: "+m.getMission());
        }
    }
    private void setTrueMissions(MissionUser m) {
        if (isGoodMission(m)) {
            Missions.remove(m, false);
            Missions.put(m, true);
            this.gamePlayer.sendMessage("§7Vous avez terminé la mission de votre"+color+" Izanami§7:§r "+m.getMission());
        }
    }
    public String[] getStringsMission() {
        String l1 = "";
        String l2 = "";
        for (MissionUser mu : Missions.keySet()) {
            if (l1.isEmpty()) {
                l1 = mu.getMission();
            }
            if (l2.isEmpty() || l2.equals(l1)) {
                l2 = mu.getMission();
            }
        }
        String l3 = "";
        if (TargetMissions.keySet().stream().findFirst().isPresent()){
            l3 = TargetMissions.keySet().stream().findFirst().get().getMission();
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
                "§7 - §f"+l3
        };
    }
    @NonNull
    private MissionTarget getMission() {
        for (MissionTarget mt : TargetMissions.keySet()) {
            return mt;
        }
        return MissionTarget.Gap;//Je le met comme base au cas ou
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
    public void start(String izanamiColor) {
        this.color = izanamiColor;
        int rdm1 = RandomUtils.getRandomInt(0, 4);
        int rdm2 = rdm1;
        while (rdm2 == rdm1 || rdm2 > 4) {
            rdm2 = RandomUtils.getRandomInt(0, 5);
        }
        int rdm3 = RandomUtils.getRandomInt(0, 3);
        for (MissionUser mo : MissionUser.values()) {
            if (mo.getNmb() == rdm1) {
                Missions.put(mo, false);
            }
            if (mo.getNmb() == rdm2) {
                Missions.put(mo, false);
            }
        }
        for (MissionTarget mt : MissionTarget.values()) {
            if (mt.getNmb() == rdm3) {
                TargetMissions.put(mt, false);
                break;
            }
        }
        if (isGoodMission(MissionUser.Rester)) {
            new ResterRunnable(GameState.getInstance(), this);
        }
        if (isGoodMission(MissionTarget.Distance)) {
            new DistanceRunnable(GameState.getInstance(), this);
        }
        if (!this.runnables.isEmpty()){
            for (BukkitRunnable run : this.runnables){
                run.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);//Le Async est censé permettre d'être plus opti
            }
        }
    }
    public ItemStack getResultVictimMission() {
        ItemBuilder ib = new ItemBuilder(Material.NETHER_STAR).setName("§eMission de la victim");
        ib.setLore(findVictimLore());
        return ib.toItemStack();
    }
    public ItemStack getResultUserMission() {
        ItemBuilder ib = new ItemBuilder(Material.NETHER_STAR).setName("Vos missions:");
        ib.addLoreLine("");
        for (MissionUser mu : Missions.keySet()) {
            ib.addLoreLine(findUserLore(mu));
        }
        return ib.toItemStack();
    }
    private String findUserLore(@NonNull final MissionUser mu) {
        return "§eVotre mission §f"+mu.getMission()+"§e est "+(Missions.get(mu) ? "§aTerminé" : "§cInachevé "+getOtherStrings(mu));
    }
    public List<String> findListUserLore() {
        @NonNull final List<String> list = new ArrayList<>();
        for (@NonNull final MissionUser mu : Missions.keySet()) {
            list.add(findUserLore(mu));
            list.add(" ");
        }
        return list;
    }
    public String findVictimLore() {
        return "§eSa mission§f "+getMission().getMission()+"§e est "+(TargetMissions.get(getMission()) ?
                "§aTerminé" : "§cInachevé"+(getMission() == MissionTarget.Gap ? "§7 (§ePomme d'or§7 restante§c§l "+gapEatingRemaining : ""));
    }
    private String getOtherStrings(MissionUser mu) {
        return (mu == MissionUser.Taper ?
                "§7Coup restant à infligé:§c§l "+taperCoupRemaining : mu == MissionUser.Fraper ?
                "§7Coup restant à subir:§c§l "+FraperCoupRemaining : "");
    }
    @EventHandler
    private void onEat(PlayerItemConsumeEvent e) {
        if (isNotNull() && isGoodMission(MissionTarget.Gap)) {
            if (e.getPlayer().getUniqueId().equals(this.gameTarget.getUuid()) && e.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                gapEatingRemaining--;
                Player inf = Bukkit.getPlayer(this.gamePlayer.getUuid());
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
        for (BukkitRunnable runnable : runnables){
            runnable.cancel();
        }
        runnables.clear();
        Missions.clear();
        TargetMissions.clear();
        gapEatingRemaining = 5;
        FraperCoupRemaining = 15;
        taperCoupRemaining = 15;
        this.color = null;
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent e) {
        if (isNotNull()) {
            if (isGoodMission(MissionTarget.Tuer)) {
                if (e.getKiller().getUniqueId().equals(this.gameTarget.getUuid())) {
                    setTrueMissions(MissionTarget.Tuer);
                }
            }
        }
    }
    @EventHandler
    private void onDrop(PlayerDropItemEvent e) {
        if (isGoodMission(MissionUser.Gap) && isNotNull()) {
            if (e.getPlayer().getUniqueId().equals(this.gamePlayer.getUuid()) && e.getItemDrop().getItemStack().getType().equals(Material.GOLDEN_APPLE)) {
                e.getItemDrop().setMetadata("Izanami.Gap"+ this.gamePlayer.getUuid(), new FixedMetadataValue(Main.getInstance(), this.gamePlayer.getUuid()));
            }
        }
    }
    @EventHandler
    private void onRecup(PlayerPickupItemEvent e) {
        if (isGoodMission(MissionUser.Gap) && isNotNull()) {
            if (e.getItem().hasMetadata("Izanami.Gap"+ this.gamePlayer.getUuid())) {
                //		Bukkit.getPlayer(user).sendMessage("§c"+e.getPlayer().getDisplayName()+"§7 a récupérer votre§c");
                if (e.getPlayer().getUniqueId().equals(this.gameTarget.getUuid())) {
                    setTrueMissions(MissionUser.Gap);
                }
            }
        }
    }
    @EventHandler
    private void onBattle(EntityDamageByEntityEvent e) {
        if (!isNotNull())return;
        if (isGoodMission(MissionUser.Taper)) {
            if (e.getDamager().getUniqueId().equals(this.gamePlayer.getUuid()) && e.getEntity().getUniqueId().equals(this.gameTarget.getUuid())) {
                taperCoupRemaining--;
                int coup = this.taperCoupRemaining;
                this.gamePlayer.getActionBarManager().addToActionBar("izanami.taper."+coup, "§7Coup restant >>§c "+coup);
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> this.gamePlayer.getActionBarManager().removeInActionBar("izanami.taper."+coup), 20);
                if (taperCoupRemaining == 0) {
                    setTrueMissions(MissionUser.Taper);
                }
            }
        }
        if (isGoodMission(MissionUser.Fraper)) {
            if (e.getDamager().getUniqueId().equals(this.gameTarget.getUuid()) && e.getEntity().getUniqueId().equals(this.gamePlayer.getUuid()) && e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                if (damager != null) {
                    FraperCoupRemaining--;
                    int coup = this.FraperCoupRemaining;
                    this.gamePlayer.getActionBarManager().addToActionBar("izanami.fraper."+coup, "§7Coup restant >> §c"+coup);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> this.gamePlayer.getActionBarManager().removeInActionBar("izanami.fraper."+coup), 20);
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
        if (e.getPlayer().getUniqueId().equals(this.gameTarget.getUuid())) {
            if (isGoodMission(MissionUser.Lave)) {
                Player user = Bukkit.getPlayer(this.gamePlayer.getUuid());
                if (e.getFrom().getBlock().getType().name().contains("LAVA") || e.getTo().getBlock().getType().name().contains("LAVA") && user != null) {
                    if (Loc.getNearbyPlayers(user, 15).contains(e.getPlayer())) {
                        setTrueMissions(MissionUser.Lave);
                    }
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    public boolean onSuccessfullInfection(NSRoles infecteur, RoleBase infecter) {
        Player owner = Bukkit.getPlayer(infecteur.getPlayer());
        Player toIzanami = Bukkit.getPlayer(infecter.getPlayer());
        if (owner != null && toIzanami != null) {
            owner.sendMessage("§7L'infection est terminé§c "+toIzanami.getName()+"§7 rejoint maintenant votre camp");
            infecter.setTeam(infecteur.getTeam());
            toIzanami.resetTitle();
            GamePlayer gOwner = GameState.getInstance().getGamePlayer().get(owner.getUniqueId());
            GamePlayer gTo = GameState.getInstance().getGamePlayer().get(toIzanami.getUniqueId());
            toIzanami.sendTitle("§cVous êtes sous l'effet de l'§lIzanami", "§cVous êtes maintenant dans le camp "+gOwner.getRole().getTeamColor()+ infecteur.getOriginTeam().name());
            toIzanami.sendMessage("§7Voici l'identité de votre coéquipier"+this.color+infecteur.getName()+": "+(infecteur.getPlayerFromRole(infecteur.getRoles()) != null ? infecteur.getPlayerFromRole(infecteur.getRoles()).getName() : "§cMort"));
            gTo.getRole().setSuffixString(gTo.getRole().getSuffixString()+"§7 ("+this.color+"Izanami§7)");
            final IzanamiFinishEvent izanamiFinishEvent = new IzanamiFinishEvent(infecteur, infecter, this.color, owner, toIzanami);
            return true;
        }
        return false;
    }
    private static class ResterRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final IzanamiV2 izanami;
        private int timeRemaining = 60*5;

        private ResterRunnable(GameState gameState, IzanamiV2 izanami) {
            this.gameState = gameState;
            this.izanami = izanami;
            this.izanami.gamePlayer.getActionBarManager().addToActionBar("izanami.rester", "§bTemp restant proche de §c"+this.izanami.gameTarget.getPlayerName()+"§7: "+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!this.izanami.gamePlayer.isAlive()) {
                return;
            }
            if (!this.izanami.gameTarget.isAlive()) {
                return;
            }
            this.izanami.gamePlayer.getActionBarManager().updateActionBar("izanami.rester", "§bTemp restant proche de §c"+this.izanami.gameTarget.getPlayerName()+"§b: §c"+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
            if (this.timeRemaining <= 0) {
                this.izanami.gamePlayer.getActionBarManager().removeInActionBar("izanami.rester");
                this.izanami.setTrueMissions(MissionUser.Rester);
                cancel();
                return;
            }
            if (this.izanami.gamePlayer.getLastLocation().getWorld().getName().equals(this.izanami.gameTarget.getLastLocation().getWorld().getName())){
                if (this.izanami.gamePlayer.getLastLocation().distance(this.izanami.gameTarget.getLastLocation()) <= 20){
                    this.timeRemaining--;
                }
            }
        }
    }
    private static class DistanceRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final IzanamiV2 izanami;
        private int timeRemaining = 60;

        private DistanceRunnable(GameState gameState, IzanamiV2 izanami) {
            this.gameState = gameState;
            this.izanami = izanami;
            this.izanami.gamePlayer.getActionBarManager().addToActionBar("izanami.distance", "§bTemp restant loin de §c"+this.izanami.gameTarget.getPlayerName()+"§b: §c"+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!this.izanami.gamePlayer.isAlive()) {
                return;
            }
            if (!this.izanami.gameTarget.isAlive()) {
                return;
            }
            if (this.timeRemaining <= 0) {
                this.izanami.gamePlayer.getActionBarManager().removeInActionBar("izanami.distance");
                this.izanami.setTrueMissions(MissionTarget.Distance);
                cancel();
                return;
            }
            this.izanami.gamePlayer.getActionBarManager().updateActionBar("izanami.distance", "§bTemp restant loin de §c"+this.izanami.gameTarget.getPlayerName()+"§b: §c"+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
            if (this.izanami.gamePlayer.getLastLocation().getWorld().getName().equals(this.izanami.gameTarget.getLastLocation().getWorld().getName())){
                if (this.izanami.gamePlayer.getLastLocation().distance(this.izanami.gameTarget.getLastLocation()) < 30){
                    return;
                }
            }
            this.timeRemaining--;
        }
    }
}