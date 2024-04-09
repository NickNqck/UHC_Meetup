package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKill;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Warden extends RoleBase {

    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).toItemStack();
    private final ItemStack laser = new ItemBuilder(Material.NETHER_STAR).setUnbreakable(true).setName("§bLaser").toItemStack();
    private int cdLaser = 0;
    private final ItemStack darkness = new ItemBuilder(Material.NETHER_STAR).setUnbreakable(true).setName("§9Darkness").toItemStack();
    private int cdDarkness = 0;
    private int cdCible = 0;
    public Warden(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        addBonusResi(10.0);
        owner.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> giveHealedHeartatInt(owner, 5), 20);
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
    public void fireLaser(Player shooter) {
        Location shooterLoc = shooter.getLocation();
        Vector direction = shooterLoc.getDirection().normalize();

        double range = 10;
        double step = 0.5;

        for (double i = 0; i < range; i += step) {
            Location particleLoc = shooterLoc.clone().add(direction.clone().multiply(i));
            MathUtil.sendParticle(EnumParticle.REDSTONE, particleLoc);

            for (Player target : particleLoc.getWorld().getPlayers()) {
                if (target != shooter && target.getLocation().distance(particleLoc) < 1.0) {
                    damage(target, 6.0, 1, owner, true);
                }
            }
        }
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("cible")){
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null){
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
        givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
        givePotionEffet(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false);
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
                fireLaser(owner);
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
                    givePotionEffet(p, PotionEffectType.BLINDNESS, 20*18, 1, true);
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

    @Override
    public void resetCooldown() {
        cdLaser = 0;
        cdDarkness = 0;
        cdCible = 0;
    }
    private static class WardenRunnable extends BukkitRunnable implements Listener {
    private final Warden warden;
    private final UUID target;
        public WardenRunnable(Warden warden, Player p){
            this.warden = warden;
            Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
            this.target = p.getUniqueId();
        }
        private int timeRemaining = 60*5;
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
            warden.givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
            warden.sendCustomActionBar(warden.owner, "§bTemp de traque restant:§c "+ StringUtils.secondTowardsConventional(timeRemaining));
            timeRemaining--;
        }
        @EventHandler
        private void onKill(UHCPlayerKill e){
            if (Main.getInstance().getGamePlayer().getGamePlayersRoles().containsKey(e.getKiller().getUniqueId())){
                if (Main.getInstance().getGamePlayer().getRole(e.getKiller().getUniqueId()).getClass().equals(warden.getClass())){
                    if (e.getVictim().getUniqueId().equals(target) && timeRemaining > 0){
                        if (warden.getBonusResi() < 30.0){
                            warden.addBonusResi(5.0);
                        }
                        e.getKiller().sendMessage("§7Vous avez réussi a tué§c "+e.getVictim().getDisplayName()+"§7 qui était votre cible vous obtenez donc§c 5%§7 de§9 Résistance");
                    }
                }
            }
        }
    }
}
