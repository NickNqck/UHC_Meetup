package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.UHCMcRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Warden extends UHCMcRoles {

    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setLore("§c").toItemStack();
    private final ItemStack laser = new ItemBuilder(Material.NETHER_STAR).setLore("§7").setUnbreakable(true).setName("§bLaser").toItemStack();
    private int cdLaser = 0;
    private final ItemStack darkness = new ItemBuilder(Material.NETHER_STAR).setLore("§7").setUnbreakable(true).setName("§9Darkness").toItemStack();
    private int cdDarkness = 0;
    private int cdCible = 0;
    public Warden(UUID player) {
        super(player);
        addBonusResi(10.0);
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Warden;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public String getName() {
        return "Warden";
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> giveHealedHeartatInt(owner, 5), 20);
    }
    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§9Warden",
                AllDesc.objectifsolo+"§e Seul",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§cForce I§f,§6 Résistance au feu I§f et§c 5"+AllDesc.coeur+" permanent supplémentaire",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§bLaser§f: Tire un rayon de particule qui infligera§c 3"+AllDesc.coeur+" de dégat à la personne touché.",
                "",
                AllDesc.point+"§9Darkness§f: Inflige l'effet§9 Blindness I§f pendant§c 18 secondes§f à tout les joueurs autours de vous à moins de§c 25 blocs§f.",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/mc cible <joueur>§f: Pendant§c 5 minutes§f, vous obtenez un traqueur en direction du joueur ciblé ainsi que l'effet§e Speed I§f, si vous parvenez à la tuer vous obtiendrez§c 5%§f de "+AllDesc.Resi+" (max§c 30%§f), par contre, si vous n'y arrivez pas vous perdez§c 5%§f de "+AllDesc.Resi+"§f.",
                "",
                AllDesc.bar

        };
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("cible")){
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null){
                    if (target.getUniqueId().equals(getPlayer())) {
                        owner.sendMessage("§cVous ne pouvez pas vous ciblez vous même.");
                        return;
                    }
                    if (cdCible <= 0){
                        if (getBonusResi() >= 5.0){
                            cdCible = 60*10;
                            new WardenRunnable(this, target).runTaskTimer(Main.getInstance(), 0, 20);
                        } else {
                            owner.sendMessage("§7Vous n'avez plus asser de§c pourcentage§7 pour cibler un joueur.");
                        }
                    } else {
                        sendCooldown(owner, cdCible);
                    }
                } else {
                    owner.sendMessage("§b"+args[1]+"§c n'est pas connecter !");
                }
            }
        }
    }

    @Override
    public void Update(GameState gameState) {
        OLDgivePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
        OLDgivePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
        if (cdLaser >= 0){
            cdLaser--;
            if (cdLaser == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§b laser§7.");
            }
        }
        if (cdDarkness >= 0){
            cdDarkness--;
            if (cdDarkness == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§9 Darkness§7.");
            }
        }
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                sword,
                laser,
                darkness
        };
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(laser)){
            if (cdLaser <=0){
                createLaser();
                cdLaser = 120;
                owner.sendMessage("§7Vous avez lancer votre§9 laser§7.");
            } else {
                sendCooldown(owner, cdLaser);
            }
            return true;
        }
        if (item.isSimilar(darkness)){
            if (cdDarkness <= 0){
                owner.sendMessage("§7Activation de votre pouvoir§9 Darkness");
                for (Player p : Loc.getNearbyPlayersExcept(owner, 25)){
                    OLDgivePotionEffet(p, PotionEffectType.BLINDNESS, 20*18, 1, true);
                    owner.sendMessage("§c"+p.getDisplayName()+"§7 à été toucher par votre pouvoir§9 Darkness");
                    p.sendMessage("§7Vous avez été toucher par la§9 Darkness§7 du§9 Warden");
                }
                cdDarkness = 60*7;
            } else {
                sendCooldown(owner, cdDarkness);
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }
    private void createLaser(){
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
                        if (target.getUniqueId() != owner.getUniqueId() && target.getLocation().distance(location) < 1.0) {
                            if (!damaged.contains(target.getUniqueId())){
                                damage(target, 6.0, 1, owner, true);
                                damaged.add(target.getUniqueId());
                                target.sendMessage("§7Vous subissez le§c laser§7 du§e Warden");
                            }
                        }
                    }
                }
                cancel();
            }

        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
    }

    @Override
    public void resetCooldown() {
        cdLaser = 0;
        cdDarkness = 0;
        cdCible = 0;
    }
    private static class WardenRunnable extends BukkitRunnable implements Listener {
    private final Warden warden;
    private final UUID target;
    private boolean cancel = false;
        private int timeRemaining = 60*5;
        public WardenRunnable(Warden warden, Player p){
            this.warden = warden;
            Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
            this.target = p.getUniqueId();
        }
        @Override
        public void run() {
            if (warden.owner == null || !warden.gameState.getServerState().equals(GameState.ServerStates.InGame)){
                cancel();
                return;
            }
            if (timeRemaining <= 0){
                warden.owner.sendMessage("§7La traque est maintenant terminer.");
                warden.setBonusForce(warden.getBonusResi()-5.0);
                cancel();
                return;
            }
            if (cancel){
                cancel();
            }
            warden.OLDgivePotionEffet(PotionEffectType.SPEED, 60, 1, true);
            warden.sendCustomActionBar(warden.owner, Loc.getDirectionMate(warden.owner, Bukkit.getPlayer(target), true)+"§bTemp de traque restant:§c "+ StringUtils.secondTowardsConventional(timeRemaining));
            timeRemaining--;
        }
        @EventHandler
        private void onKill(UHCPlayerKillEvent e){
                if (e.getPlayerKiller() != null && e.getGamePlayerKiller() != null){
                    if (e.getGameState().getGamePlayer().containsKey(e.getKiller().getUniqueId())){
                        if (e.getGameState().getGamePlayer().get(e.getPlayerKiller().getUniqueId()).getRole() instanceof Warden ){
                            if (e.getVictim().getUniqueId().equals(target) && timeRemaining > 0){
                                if (warden.getBonusResi() < 30.0 && !cancel){
                                    warden.addBonusResi(5.0);
                                }
                                if (!cancel){
                                    e.getKiller().sendMessage("§7Vous avez réussi a tué§c "+e.getVictim().getDisplayName()+"§7 qui était votre cible vous obtenez donc§c 5%§7 de§9 Résistance");
                                }
                                cancel = true;
                            }
                        }
                    }
                }
        }
    }
}
