package fr.nicknqck.roles.ds.solos.jigorov2;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.lune.KaigakuV2;
import fr.nicknqck.roles.ds.slayers.ZenItsuV2;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JigoroV2PZenItsu extends JigoroV2 implements Listener {

    private final ZenItsuV2 zenItsu;
    private boolean killKaigaku;

    public JigoroV2PZenItsu(final UUID player, final ZenItsuV2 zenItsu, final GamePlayer gamePlayer) {
        super(player);
        this.zenItsu = zenItsu;
        this.killKaigaku = false;
        setCanuseblade(true);
        setLameincassable(true);
        setGamePlayer(gamePlayer);
        addKnowedRole(ZenItsuV2.class);
        addKnowedRole(KaigakuV2.class);
        zenItsu.addKnowedRole(KaigakuV2.class);
        gamePlayer.setRole(this);
        new ZenItsuRunnable(zenItsu, this, getGameState());
        EventUtils.registerRoleEvent(this);
        setTeam(TeamList.Jigoro);
    }

    @Override
    public String[] Desc() {
        return AllDesc.JigoroV2Pacte3;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Jigoro;
    }

    @EventHandler
    private void onUHCKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
        if (event.getGamePlayerKiller().getUuid().equals(this.zenItsu.getPlayer()) || event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
            if (role instanceof KaigakuV2) {
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false), EffectWhen.MID_LIFE);
                getGamePlayer().sendMessage("§6"+event.getGamePlayerKiller().getRole().getName()+"§f à tué§c Kaigaku§f ce qui vous permet d'avoir§b Speed II§f en dessous de §c5❤");
                this.killKaigaku = true;
            }
        }
    }

    private static final class ZenItsuRunnable extends BukkitRunnable {

        private final ZenItsuV2 zenItsu;
        private final JigoroV2PZenItsu jigoro;
        private final GameState gameState;
        private int tick = 0;

        private ZenItsuRunnable(ZenItsuV2 zenItsu, JigoroV2PZenItsu jigoro, GameState gameState) {
            this.zenItsu = zenItsu;
            this.jigoro = jigoro;
            this.gameState = gameState;
            jigoro.getGamePlayer().getActionBarManager().addToActionBar("jigoro.traquezen", "§aZenItsu§r: ?");
            zenItsu.getGamePlayer().getActionBarManager().addToActionBar("jigoro.traquezen", "§6Jigoro§r: ?");
            runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!jigoro.getGamePlayer().isAlive()) {
                jigoro.getGamePlayer().getActionBarManager().removeInActionBar("jigoro.traquezen");
                zenItsu.getGamePlayer().getActionBarManager().removeInActionBar("jigoro.traquezen");
                return;
            }
            if (!zenItsu.getGamePlayer().isAlive()) {
                jigoro.getGamePlayer().getActionBarManager().removeInActionBar("jigoro.traquezen");
                zenItsu.getGamePlayer().getActionBarManager().removeInActionBar("jigoro.traquezen");
                return;
            }
            final Player owner = Bukkit.getPlayer(jigoro.getPlayer());
            final Player mate = Bukkit.getPlayer(zenItsu.getPlayer());
            if (owner == null)return;
            if (mate == null)return;
            if (!owner.getWorld().equals(mate.getWorld()))return;
            final DecimalFormat df = new DecimalFormat("0");
            jigoro.getGamePlayer().getActionBarManager().updateActionBar("jigoro.traquezen", "§aZenItsu§r: "+df.format(
                    owner.getLocation().distance(mate.getLocation())
            ) + ArrowTargetUtils.calculateArrow(owner, mate.getLocation()));
            zenItsu.getGamePlayer().getActionBarManager().updateActionBar("jigoro.traquezen", "§6Jigoro§r: "+df.format(
                    mate.getLocation().distance(owner.getLocation())
            ) + ArrowTargetUtils.calculateArrow(mate, owner.getLocation()));

            if (jigoro.killKaigaku) {
                if (owner.getHealth() <= (owner.getMaxHealth()/2)) {
                    owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false), true);
                }
            }

            final List<Player> locs = new ArrayList<>(Loc.getNearbyPlayersExcept(owner, 15));
            if (locs.isEmpty())return;
            this.tick++;
            if (tick < 20)return;
            this.tick = 0;
            if (locs.contains(mate)) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    mate.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false), true);
                    owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false), true);
                });
            }
        }
    }
}
