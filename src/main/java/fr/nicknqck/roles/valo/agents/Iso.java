package fr.nicknqck.roles.valo.agents;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Iso extends RoleBase {
    private final ItemStack ProtectionItem = new ItemBuilder(Material.NETHER_STAR).setName("§dProtection couplé").setLore("§7Vous permet de crée un timer visant à ne pas subir de dégat").toItemStack();
    private int cdProtection = 0;
    private int stackedCoup = 0;
    private final ItemStack BarriereItem = new ItemBuilder(Material.NETHER_STAR).setName("§5Barrière").setLore("§fVous permet de tier un laser de particule, les joueurs touchés auront un effet différent en fonction du clique utiliser:",
            "§cClique gauche§f: Les joueurs touchés par le laser subiront§c +25%§f de dégat de votre pars pendant§c 1 minute§f",
            "§aClique droit§f: Les joueurs touchés par le laser ne pourront plus vous tapez durant les§c 10 secondes§f qui suive."
    ).toItemStack();
    private int cdBarriereDroite = 0;
    private final List<UUID> LeftBarrieredItem = new ArrayList<>();
    private int cdBarriereGauche = 0;
    public Iso(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        owner.sendMessage(Desc());
        giveItem(owner, false, getItems());
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
                BarriereItem
        };
    }

    @Override
    public void resetCooldown() {
        cdProtection = 0;
        cdBarriereDroite = 0;
        cdBarriereGauche = 0;
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
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void onLeftClick(PlayerInteractEvent event, GameState gameState) {
        super.onLeftClick(event, gameState);
        if (event.getItem().isSimilar(BarriereItem)){
            createLaser(false);
            cdBarriereGauche = 60*5;
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
                                    AttackUtils.setCantAttack(target, getUuidOwner());
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                                        AttackUtils.getCantAttackNobody().remove(target.getUniqueId(), AttackUtils.getCantAttackNobody().get(target.getUniqueId()));
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
            if (stackedCoup > 0){
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
    }
}