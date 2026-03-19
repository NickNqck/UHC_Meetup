package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Soufle;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

public class MitsuriV2 extends PilierRoles implements Listener {

    private TextComponent desc;
    private ObanaiV2 obanai;
    private boolean loveDeath = false;

    public MitsuriV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FLAMME;
    }

    @Override
    public String getName() {
        return "Mitsuri";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Mitsuri;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                if (gamePlayer.isAlive() && gamePlayer.getRole() != null) {
                    if (gamePlayer.getRole() instanceof ObanaiV2) {
                        this.obanai = (ObanaiV2) gamePlayer.getRole();
                        break;
                    }
                }
            }
        }, 60);
        setMaxHealth(getMaxHealth()+4.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        addPower(new ParIciPower(this), true);
        new StrengthRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        this.desc = new AutomaticDesc(this)
                .addCustomLine("§7Vous possédez§c 2❤ permanents§7 supplémentaire")
                .addCustomLine("§7Vous possédez l'effet§c Force I§7 proche d'§aObanai")
                .setPowers(getPowers())
                .addParticularites(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                                new TextComponent("§7A la mort d'§aObanai§7 vous§c perdrez 3💛 permanents§7 mais vous gagnerez l'effet§9 Résistance I§7 durant le§c cycle§7 dans lequel il est mort")
                        })
                )
                .getText();
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
    @EventHandler
    private void onDie(UHCDeathEvent event) {
        if (event.getRole() instanceof ObanaiV2 && !loveDeath && getGamePlayer().isAlive()) {
            EffectWhen when = event.getGameState().isNightTime() ? EffectWhen.NIGHT : EffectWhen.DAY;
            onObanaiDeath(when);
        }
    }
    private void onObanaiDeath(EffectWhen timeDeath) {
        loveDeath = true;
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), timeDeath);
        setMaxHealth(getMaxHealth()-6.0);
        owner.setMaxHealth(getMaxHealth());
        owner.sendMessage("§aObanai§7 est mort, votre chagrin vous fait perdre l'envie d'§daimer§7 quelqu'un d'autre");
    }
    private static class ParIciPower extends ItemPower {

        private final MitsuriV2 mitsuriV2;

        protected ParIciPower(@NonNull MitsuriV2 role) {
            super("§dPar Ici !", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§dPar Ici !"), role,
                    "§7Si§a Obanai§7 est à moins de§c 50 blocs§7 de vous, vous le téléporterez dans un§c rayon§7 de§c 5 blocs§7 autours de vous",
                    "",
                    "§7Cependant s'il est plus loin il aura un§c traqueur§7 pointant vers vous pendant§c 30 secondes§7.");
            this.mitsuriV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (mitsuriV2.obanai == null) {
                player.sendMessage("§aObanai§7 n'est pas dans la partie...");
                return false;
            }
            Player obanai = Bukkit.getPlayer(mitsuriV2.obanai.getPlayer());
            if (obanai == null) {
                player.sendMessage("§aObanai§c n'est pas connecter");
                return false;
            }
            if (!player.getWorld().equals(obanai.getWorld())) {
                player.sendMessage("§aObanai§7 ne peut recevoir de message pour l'instant");
                return false;
            }
            final double distance = player.getLocation().distance(obanai.getLocation());
            if (distance <= 50.0) {
                Location loc = Loc.getRandomLocationAroundPlayer(player, 5);
                obanai.teleport(loc);
                player.sendMessage("§aVous avez téléporter votre§d amoureux§a autours de vous, n'hésitez pas à lui faire un gros §mbisou");
                obanai.sendMessage("§aVotre amour de toujours§d Mitsuri§a vous à téléportez autours d'elle.");
            } else {
                obanai.sendMessage("§dMitsuri§a vous appel, vous devriez essayez de la rejoindre, elle se situt à environ §c"+((int)distance)+"§a de vous§c "+ ArrowTargetUtils.calculateArrow(obanai,player.getLocation()));
                player.sendMessage("§7Vous avez envoyez votre position à §aObanai§7.");
                new TrouveMoi(obanai.getUniqueId(), mitsuriV2);
            }
            return true;
        }
        private static class TrouveMoi extends BukkitRunnable {

            private final UUID finders;
            private final MitsuriV2 mitsuriV2;
            private int timeRemaining = 30;

            private TrouveMoi(UUID finders, MitsuriV2 mitsuriV2) {
                this.finders = finders;
                this.mitsuriV2 = mitsuriV2;
            }

            @Override
            public void run() {
                if (timeRemaining <= 0) {
                    cancel();
                    return;
                }
                Player finder = Bukkit.getPlayer(finders);
                if (finder != null) {
                    if (finder.getLocation().distance(mitsuriV2.getGamePlayer().getLastLocation()) <= 30) {
                        finder.sendMessage("§7Vous êtes proche de§d Mitsuri§7, vous ne trouvez plus sa position exacte...");
                        cancel();
                        return;
                    }
                    NMSPacket.sendActionBar(finder, "§dJ'arrive mon amour !: §c"+ArrowTargetUtils.calculateArrow(finder, mitsuriV2.getGamePlayer().getLastLocation()));
                }
                timeRemaining--;
            }
        }
    }
    private static class StrengthRunnable extends BukkitRunnable {

        private final MitsuriV2 mitsuriV2;
        private final GameState gameState;

        private StrengthRunnable(MitsuriV2 mitsuriV2) {
            this.mitsuriV2 = mitsuriV2;
            this.gameState = mitsuriV2.getGameState();
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (mitsuriV2.obanai == null)return;
            Player obanai = Bukkit.getPlayer(mitsuriV2.obanai.getPlayer());
            if (obanai != null) {
                Player owner = Bukkit.getPlayer(mitsuriV2.getPlayer());
                if (owner != null) {
                    if (Loc.getNearbyPlayersExcept(owner, 15).contains(obanai)) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true));
                    }
                }
            }
        }
    }
}