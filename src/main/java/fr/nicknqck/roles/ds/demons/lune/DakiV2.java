package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DakiV2 extends DemonsRoles {

    public DakiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Daki§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Daki;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new ObisItems(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.NIGHT);
        addPower(new TroisiemeOeil(this), true);
        addKnowedRole(GyutaroV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static class TroisiemeOeil extends ItemPower implements Listener {

        private boolean gyutaroDead = false;

        public TroisiemeOeil(@NonNull RoleBase role) {
            super("Troisième Oeil", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Oeil"), role,
                    "§7A l'activation, durant§c 3 minutes§7 si vous êtes à moins de§c 50 blocs§7 de§c Gyutaro§7,",
                    "§7vous obtiendrez l'effet§e Speed I§7.",
                    "",
                    "§4!§c Si§4 Gyutaro§c est§4 mort§c alors vous aurez d'office§e Speed I§c pendant les§4 3 minutes§4 !");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Votre§a Troisième Oeil§7 s'éveille...");
                new SpeedRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                return true;
            }
            return false;
        }

        @EventHandler
        private void onDeath(final UHCDeathEvent event) {
            if (event.getRole() == null)return;
            if (event.getRole() instanceof GyutaroV2) {
                this.gyutaroDead = true;
                getRole().getGamePlayer().sendMessage("§cGyutaro§7 est§c mort§7, mais il vous à léguez l'un de ses yeux, n'en faite pas un mauvais usage...");
            }
        }

        private static class SpeedRunnable extends BukkitRunnable {

            private final TroisiemeOeil troisiemeOeil;
            private int timeLeft = 60*3;

            private SpeedRunnable(TroisiemeOeil troisiemeOeil) {
                this.troisiemeOeil = troisiemeOeil;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.troisiemeOeil.getRole().getGamePlayer().getActionBarManager().removeInActionBar("dakiv2.oeil");
                    this.troisiemeOeil.getRole().getGamePlayer().sendMessage("§7Les effets de votre§a Troisième Oeil§7 s'estompent...");
                    cancel();
                    return;
                }
                this.troisiemeOeil.getRole().getGamePlayer().getActionBarManager().updateActionBar("dakiv2.oeil", "§bTemps restant (§aTroisième Oeil§b):§c "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.timeLeft--;
                final Player owner = Bukkit.getPlayer(this.troisiemeOeil.getRole().getPlayer());
                if (owner == null)return;

                boolean speed = false;
                if (!troisiemeOeil.gyutaroDead) {
                    for (final Player player : owner.getWorld().getPlayers()) {
                        if (player.getUniqueId().equals(owner.getUniqueId()))continue;
                        if (this.troisiemeOeil.getRole().getGameState().hasRoleNull(player.getUniqueId()))continue;
                        if (player.getLocation().distance(owner.getLocation()) > 50)continue;
                        @NonNull final RoleBase role = this.troisiemeOeil.getRole().getGameState().getGamePlayer().get(player.getUniqueId()).getRole();
                        if (role instanceof GyutaroV2) {
                            speed = true;
                            break;
                        }
                    }
                } else {
                    speed = true;
                }
                if (speed) {
                    Bukkit.getScheduler().runTask(this.troisiemeOeil.getPlugin(), () -> this.troisiemeOeil.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NOW));
                }
            }
        }
    }
    private static class ObisItems extends ItemPower {

        public ObisItems(@NonNull RoleBase role) {
            super("Obis", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§cObis"), role,
                    "§7Pendant§c 5 secondes§7 et tant que vous avez vos§c Obis§7 dans les mains,",
                    "§7vous pouvez viser des§c joueurs§7 avec votre§c crosshair§7,",
                    "§7une fois les§c 5 secondes§7 passés, tout les joueurs qui auront été viser seront§c stun§7 pendant§c 6 secondes§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous démarrez votre visée avec des§c Obis§7.");
                new ObisRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 1);
                return true;
            }
            return false;
        }
        private synchronized void onStop(@NonNull final List<GamePlayer> toStuns) {
            if (toStuns.isEmpty()) {
                getRole().getGamePlayer().sendMessage("§7Vous avez réussi à viser personne avec vos§c Obis§7, le cooldown a été réduit.");
                return;
            }
            for (GamePlayer gamePlayer : toStuns) {
                gamePlayer.stun(20*6);
                MathUtil.sendParticleLine(getRole().getGamePlayer().getLastLocation(), gamePlayer.getLastLocation(), EnumParticle.FLAME, ((int) getRole().getGamePlayer().getLastLocation().distance(gamePlayer.getLastLocation()))+1);
                getRole().getGamePlayer().sendMessage("§c"+gamePlayer.getPlayerName()+"§7 a été§c stun§7 par vos§c Obis§7.");
            }
        }
        private static class ObisRunnable extends BukkitRunnable {

            private final ObisItems obisItems;
            private final List<GamePlayer> toStuns = new ArrayList<>();
            private int ticks = 0;

            private ObisRunnable(ObisItems obisItems) {
                this.obisItems = obisItems;
            }

            @Override
            public void run() {
                if (!obisItems.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (ticks > 100) {
                    cancel();
                    return;
                }
                ticks++;
                this.obisItems.getRole().getGamePlayer().getActionBarManager().updateActionBar("daki.a" ,"§bTimeLeft =§c "+ StringUtils.secondsTowardsBeautiful(ticks));
                final Player owner = Bukkit.getPlayer(this.obisItems.getRole().getPlayer());
                if (owner == null) return;
                if (owner.getItemInHand() == null || !owner.getItemInHand().isSimilar(this.obisItems.getItem())) {
                    this.obisItems.onStop(this.toStuns);
                    cancel();
                    return;
                }
                final Player target = RayTrace.getTargetPlayer(owner, 30, null);
                if (target == null)return;
                final GamePlayer gameTarget = this.obisItems.getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                if (gameTarget == null)return;
                if (gameTarget.getRole() != null) {
                    if (gameTarget.getRole() instanceof GyutaroV2 || gameTarget.getRole() instanceof MuzanV2 || gameTarget.getRole() instanceof DakiV2) {
                        return;
                    }
                }
                if (!this.toStuns.contains(gameTarget)) {
                    this.toStuns.add(gameTarget);
                    owner.sendMessage("§c"+target.getDisplayName()+"§7 sera pris pour cible par vos§c Obis§7.");
                    target.sendMessage("§7Vous vous sentez observez...");
                }
            }
        }
    }

}