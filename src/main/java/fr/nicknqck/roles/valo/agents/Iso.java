package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.StunManager;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.raytrace.RayTrace;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Iso extends RoleBase {
    private final ItemStack ProtectionItem = new ItemBuilder(Material.NETHER_STAR).setName("§dProtection couplé").setLore("§7Vous permet de crée un timer visant à ne pas subir de dégat").toItemStack();
    private int cdProtection = 0;
    private int stackedCoup = 0;
    private final ItemStack BarriereItem = new ItemBuilder(Material.NETHER_STAR).setName("§5Barrière").setLore("§fVous permet de tier un laser de particule, les joueurs touchés auront un effet différent en fonction du clique utiliser:",
            "§cClique gauche§f: Les jououeurs subiront§c +25%§f de dégat de votre pars pendant§c 1 minute§f",
            "§aClique droit§f: Les joueurs ne pourront plus vous tapez durant les§c 10 secondes§f qui suive."
    ).toItemStack();
    private int cdBarriereDroite = 0;
    private final List<UUID> LeftBarrieredItem = new ArrayList<>();
    private int cdBarriereGauche = 0;
    private final ItemStack UltimeItem = new ItemBuilder(Material.NETHER_STAR).setName("§r§fUltime: duel").setLore("§r§fEn visant un joueur, vous permet de vous téléportez avec lui dans un§c 1v1§f dans une arène,","§r§fle gagnant remporte le nombre de pomme d'or que possédait le perdant (avant la téléportation)").toItemStack();
    private int cdUltime = 0;
    private final Map<UUID, Integer> ultimateGap = new HashMap<>();
    private final Map<UUID, Location> ultimeLocation = new HashMap<>();
    public Iso(Player player) {
        super(player);
        owner.sendMessage(Desc());
        giveItem(owner, false, getItems());
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Iso;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§5Iso",
                AllDesc.objectifsolo+"§5Solo",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§dProtection couplé§f: A l'activation crée un timer de§c 15 secondes§f durant lesquelles tout les coups que vous infligerez au un joueur seront comptabilisé, après ce temp la vous pourrez esquiver un nombre de coup équivalant à ceux que vous aviez accumulé.§7 (1x/7min)",
                "",
                AllDesc.point+"§5Barrière§f: Vous permet de tier un laser de particule, les joueurs touchés auront un effet différent en fonction du clique utiliser: ",
                AllDesc.tab+"§cClique gauche§f: Les joueurs touchés par le laser subiront§c +25%§f de dégat de votre pars pendant§c 1 minute§f.§7 (1x/5m)",
                AllDesc.tab+"§aClique droit§f: Les joueurs touchés par le laser ne pourront plus vous tapez durant les§c 10 secondes§f qui suive.§7 (1x/7m)",
                "",
                AllDesc.point+"Ultime: duel: En visant un joueur, vous permet de vous téléportez avec lui dans un§c 1v1§f dans une arène, le gagnant remporte le nombre de pomme d'or que possédait le perdant (avant la téléportation).§7 (1x/10min)",
                "",
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                ProtectionItem,
                BarriereItem,
                UltimeItem
        };
    }

    @Override
    public void resetCooldown() {
        cdProtection = 0;
        cdBarriereDroite = 0;
        cdBarriereGauche = 0;
        cdUltime = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(ProtectionItem)){
            if (cdProtection > 0){
                sendCooldown(owner, cdProtection);
                return true;
            }
            cdProtection = 60*7+15;
            owner.sendMessage("§7Le compteur de temp de votre protection commence...");
            return true;
        }
        if (item.isSimilar(BarriereItem)){
            if (cdBarriereDroite > 0){
                sendCooldown(owner, cdBarriereDroite);
                return true;
            }
            createLaser(true);
            cdBarriereDroite = 60*7;
            return true;
        }
        if (item.isSimilar(UltimeItem)){
            if (cdUltime > 0) {
                sendCooldown(owner, cdUltime);
                return true;
            }
            useUltimate();
        }
        return super.ItemUse(item, gameState);
    }
    private void useUltimate(){
        Player target = RayTrace.getTargetPlayer(owner, 30.0, p -> owner.canSee(p));
        if (target != null){
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "nakime IenvOfezfSds48v*:!");
            if (Bukkit.getWorld("IsoUlt") == null){
                owner.sendMessage("§7Ce pouvoir est inutilisable, il manque le plugin avec les utilitaires ou ce plugin n'a pas été mis a jour par l'administrateur de votre serveur. (§6/discord§7)");
                return;
            }
            ultimeLocation.put(owner.getUniqueId(), owner.getLocation());
            ultimeLocation.put(target.getUniqueId(), target.getLocation());
            final Location oLoc = new Location(Bukkit.getWorld("IsoUlt"), 0.5, 33.1, -20, -0.1f, -0.1f);
            final Location tLoc = new Location(Bukkit.getWorld("IsoUlt"), 0.5, 33.1, 19.489, -180f, -0.4f);
            owner.teleport(oLoc);
            target.teleport(tLoc);
            ultimateGap.put(owner.getUniqueId(), GlobalUtils.getItemAmount(owner, Material.GOLDEN_APPLE));
            ultimateGap.put(target.getUniqueId(), GlobalUtils.getItemAmount(target, Material.GOLDEN_APPLE));
            StunManager.stun(target.getUniqueId(), 5.0, false);
            StunManager.stun(getUuidOwner(), 5.0, false);
            createBuildingTask();
            cdUltime = 60*10;
        } else {
            owner.sendMessage("§cIl faut viser un joueur !");
        }
    }
    private void createBuildingTask(){
        new BukkitRunnable() {
            private int time = 12;
            private int couchPlaced = 0;
            private boolean wall1 = false;
            private boolean wall2 = false;
            private boolean wall3 = false;
            private int couchRemoved = 6;
            @Override
            public void run() {
                if (time <= 0){
                    cancel();
                    return;
                }
                if (time >= 6){//lorsque les murs ce construise
                    for (int x = 8; x >= 2; x--) {
                        double z = (x > 6 || x < 4) ? -14.0 : -13.0;

                        Location location = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);
                        Location location1 = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);

                        byte data1 = (x % 2 == 0) ? (byte) 0 : (byte) 2;
                        byte data2 = (x % 2 == 0) ? (byte) 2 : (byte) 0;


                        if (wall1){
                            setBlockTypeAndData(location, data1);
                        } else {
                            setBlockTypeAndData(location1, data2);
                        }
                        if (x == 2){
                            wall1 = !wall1;
                        }
                    }
                    for (int x = -2; x >= -8; x--) {
                        double z = (x < -6 || x > -4) ? -14.0 : -13.0;

                        Location location = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);
                        Location location1 = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);

                        byte data1 = (x % 2 == 0) ? (byte) 0 : (byte) 2;
                        byte data2 = (x % 2 == 0) ? (byte) 2 : (byte) 0;
                        if (wall2){
                            setBlockTypeAndData(location, data1);
                        } else {
                            setBlockTypeAndData(location1, data2);
                        }
                        if (x == -8){
                            wall2 = !wall2;
                        }
                    }
                    for (int x = -3; x <= 3; x++){
                        double z = (x < -1 || x > 1) ? 13.0 : 14.0;

                        Location location = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);
                        Location location1 = new Location(owner.getWorld(), x, 33.0 + couchPlaced, z);

                        byte data1 = (x % 2 == 0) ? (byte) 0 : (byte) 2;
                        byte data2 = (x % 2 == 0) ? (byte) 2 : (byte) 0;
                        if (wall3) {
                            setBlockTypeAndData(location, data1);
                        } else {
                            setBlockTypeAndData(location1, data2);
                        }
                        if (x == 3){
                            wall3 = !wall3;
                        }
                    }
                    couchPlaced+=1;
                } else {//Si les murs sont en déconstruction
                    for (int x = 8; x >= 2; x--) {
                        double z = (x > 6 || x < 4) ? -14.0 : -13.0;

                        Location location = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);
                        Location location1 = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);

                        if (wall1){
                            owner.getWorld().getBlockAt(location).setType(Material.AIR);
                        } else {
                            owner.getWorld().getBlockAt(location1).setType(Material.AIR);
                        }
                        if (x == 2){
                            wall1 = !wall1;
                        }
                    }
                    for (int x = -2; x >= -8; x--) {
                        double z = (x < -6 || x > -4) ? -14.0 : -13.0;

                        Location location = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);
                        Location location1 = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);

                        if (wall2){
                            owner.getWorld().getBlockAt(location).setType(Material.AIR);
                        } else {
                            owner.getWorld().getBlockAt(location1).setType(Material.AIR);
                        }
                        if (x == -8){
                            wall2 = !wall2;
                        }
                    }
                    for (int x = -3; x <= 3; x++){
                        double z = (x < -1 || x > 1) ? 13.0 : 14.0;

                        Location location = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);
                        Location location1 = new Location(owner.getWorld(), x, 39.0 - couchRemoved, z);

                        if (wall3) {
                            owner.getWorld().getBlockAt(location).setType(Material.AIR);
                        } else {
                            owner.getWorld().getBlockAt(location1).setType(Material.AIR);
                        }
                        if (x == 3){
                            wall3 = !wall3;
                        }
                    }
                    couchRemoved-=1;
                }
                time--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
    @SuppressWarnings("deprecation")
    private void setBlockTypeAndData(Location location, byte data) {
       owner.getWorld().getBlockAt(location).setTypeIdAndData(95, data, true);
    }
    @Override
    public void onLeftClick(PlayerInteractEvent event, GameState gameState) {
        super.onLeftClick(event, gameState);
        if (!event.getAction().name().contains("LEFT"))return;
        if (event.getItem().isSimilar(BarriereItem)){

            event.setCancelled(true);
            if (cdBarriereGauche > 0){
                sendCooldown(owner, cdBarriereGauche);
                return;
            }
            createLaser(false);
            cdBarriereGauche = 60*5;
            event.setCancelled(true);
        }
    }

    private void createLaser(boolean right){
        new BukkitRunnable() {
            //  int i = 20;
            final List<UUID> damaged = new ArrayList<>();
            @Override
            public void run() {
                double particleDistance = 0.1;
                final Location location = owner.getLocation().add(0, 1, 0);
                for (double waypoint = 1; waypoint < 10; waypoint += particleDistance) {
                    Vector vector = location.getDirection().multiply(waypoint);
                    location.add(vector);
                    MathUtil.sendParticle(EnumParticle.REDSTONE, location);
                    for (Player target : location.getWorld().getPlayers()) {
                        if (target.getUniqueId() != getUuidOwner() && target.getLocation().distance(location) < 1.0) {
                            if (!damaged.contains(target.getUniqueId())){
                                if (right) {
                                    target.sendMessage("§7Vous ne pouvez plus attaqué§d Iso");
                                    AttackUtils.setCantAttack(target, getUuidOwner());
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                                        AttackUtils.getCantAttackNobody().remove(target.getUniqueId(), AttackUtils.getCantAttackNobody().get(target.getUniqueId()));
                                        if (target.isOnline()){
                                            target.sendMessage("§7Vous pouvez à nouveau attaqué§d Iso");
                                        }
                                    }, 20*10);
                                } else {
                                    LeftBarrieredItem.add(target.getUniqueId());
                                    new BukkitRunnable() {
                                        private int timeRemain = 60;
                                        @Override
                                        public void run() {
                                            if (gameState.getServerState() != GameState.ServerStates.InGame){
                                                cancel();
                                                return;
                                            }
                                            if (timeRemain == 0){
                                                LeftBarrieredItem.clear();
                                                if (Bukkit.getPlayer(getUuidOwner()) != null){
                                                    Bukkit.getPlayer(getUuidOwner()).sendMessage("§7Votre§l Barrière de Renforcement§7 ne fait plus effet...");
                                                }
                                            }
                                            timeRemain--;
                                        }
                                    }.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                                }
                                owner.sendMessage("§c"+target.getDisplayName()+"§7 a été toucher par votre laser");
                                damaged.add(target.getUniqueId());
                            }
                        }
                    }
                }
                cancel();
            }

        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
    }

    @Override
    public void onALLPlayerDamageByEntityAfterPatch(EntityDamageByEntityEvent event, Player victim, Player damager) {
        super.onALLPlayerDamageByEntityAfterPatch(event, victim, damager);
        if (event.getDamager().getUniqueId().equals(getUuidOwner())){
            if (LeftBarrieredItem.contains(event.getEntity().getUniqueId())) {
                event.setDamage(event.getDamage()*1.25);
            }
            if (cdProtection >= 60*7){
                stackedCoup++;
            }
        } else if (event.getEntity().getUniqueId().equals(getUuidOwner())){
            if (stackedCoup > 0 && cdProtection <= 60*7-1){
                event.setCancelled(true);
                stackedCoup--;
                event.getEntity().sendMessage("§7Vous avez esquivez un coup, plus que§c "+stackedCoup+"§7 esquivable");
                System.out.println("Iso has cancelled a damage from "+event.getEntity());
            }
        }
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (cdProtection >= 0){
            cdProtection--;
            if (cdProtection == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§d Protection couplé");
            }
            if (cdProtection >= 60*7){
                sendCustomActionBar(owner,"Coup accumulé:§c "+stackedCoup+"§7 |§f Temp restant d'accumulation: §c"+(cdProtection-90*7)+"s");
                if (cdProtection == 60*7-1) {
                    owner.sendMessage("Il n'est maintenant plus temp d'accumulé des coups, mais de les utilisés !");
                }
            }
        }
        if (cdBarriereDroite >= 0){
            cdBarriereDroite--;
            if (cdBarriereDroite == 0){
                owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§l Barrière protectrice");
            }
        }
        if (cdBarriereGauche >= 0){
            cdBarriereGauche--;
            if (cdBarriereGauche == 0){
                owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§l Barrière offensive");
            }
        }
        if (cdUltime >= 0){
            cdUltime--;
            if (cdUltime == 0){
                owner.sendMessage("§7Vous pouvez a nouveau utiliser votre§l Ultime");
            }
        }
    }

    @Override
    public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
        super.OnAPlayerDie(player, gameState, killer);
        if (Bukkit.getWorld("IsoUlt") != null){
            if (killer.getWorld().equals(Bukkit.getWorld("IsoUlt"))){
                if (!ultimeLocation.isEmpty() && ultimeLocation.containsKey(killer.getUniqueId())){
                    killer.sendMessage("§7Vous avez vaincu votre adversaire en§c 1v1§7 vous remportez donc§e "+ultimateGap.get(player.getUniqueId())+" pommes d'or");
                    ultimateGap.clear();
                    for (UUID uuid : ultimeLocation.keySet()){
                        if (Bukkit.getPlayer(uuid) != null){
                            Bukkit.getPlayer(uuid).teleport(ultimeLocation.get(uuid));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "§dIso";
    }
}