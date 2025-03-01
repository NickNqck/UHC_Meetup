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
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JigoroV2PKaigaku extends JigoroV2 implements Listener {

    private final KaigakuV2 kaigaku;

    public JigoroV2PKaigaku(UUID player, final KaigakuV2 kaigaku, GamePlayer gamePlayer) {
        super(player);
        this.kaigaku = kaigaku;
        setCanuseblade(true);
        setLameincassable(true);
        setGamePlayer(gamePlayer);
        addKnowedRole(KaigakuV2.class);
        gamePlayer.setRole(this);
        new ResistanceRunnable(kaigaku, this, getGameState());
        EventUtils.registerRoleEvent(this);
        getGamePlayer().startChatWith("§6Jigoro:§7", "!", KaigakuV2.class);
        kaigaku.getGamePlayer().startChatWith("§cKaigaku:§7", "!", JigoroV2PKaigaku.class);
        setTeam(TeamList.Jigoro);
    }

    @Override
    public String[] Desc() {
        return AllDesc.JigoroV2Pacte2;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Jigoro;
    }

    @EventHandler
    private void UHCKillEvent(UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getUuid().equals(this.kaigaku.getPlayer()) || event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            final String msg = "§7Vous avez reçus §c1/2❤ permanent§7 car§6 Jigoro§r ou§c Kaigaku§7 à fait un §ckill";
            getGamePlayer().sendMessage(msg);
            kaigaku.getGamePlayer().sendMessage(msg);
            kaigaku.setMaxHealth(kaigaku.getMaxHealth()+1.0);
            setMaxHealth(getMaxHealth()+1.0);
            if (!event.getGameState().hasRoleNull(event.getVictim().getUniqueId())) {
                final RoleBase role = event.getGameState().getGamePlayer().get(event.getPlayerKiller().getUniqueId()).getRole();
                if (role instanceof ZenItsuV2) {
                    givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                    getGamePlayer().sendMessage("§aZen'Itsu§7 est§c mort§7, grâce à ceci vous gagnez l'effet§c Force I§7 de manière§c permanente");
                }
            }
        }
    }
    private static final class ResistanceRunnable extends BukkitRunnable {

        private final KaigakuV2 kaigaku;
        private final JigoroV2PKaigaku jigoro;
        private final GameState gameState;

        private ResistanceRunnable(KaigakuV2 kaigaku, JigoroV2PKaigaku jigoro, GameState gameState) {
            this.kaigaku = kaigaku;
            this.jigoro = jigoro;
            this.gameState = gameState;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!jigoro.getGamePlayer().isAlive())return;
            if (!kaigaku.getGamePlayer().isAlive())return;
            final Player owner = Bukkit.getPlayer(jigoro.getPlayer());
            final Player mate = Bukkit.getPlayer(kaigaku.getPlayer());
            if (owner == null)return;
            if (mate == null)return;
            final List<Player> locs = new ArrayList<>(Loc.getNearbyPlayersExcept(owner, 50));
            if (locs.isEmpty())return;
            if (locs.contains(mate)) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    mate.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), true);
                    owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), true);
                });
            }
        }
    }
}
