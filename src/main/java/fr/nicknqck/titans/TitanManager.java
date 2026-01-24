package fr.nicknqck.titans;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.TitanForm;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareStealCommandEvent;
import fr.nicknqck.events.custom.roles.aot.PrepareTitanStealEvent;
import fr.nicknqck.events.custom.roles.aot.TitanOwnerChangeEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.AotRoles;
import fr.nicknqck.roles.aot.builders.titans.StealCommand;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TitanManager implements Listener {

    private final Map<UUID, TitanBase> titansMap;

    public TitanManager() {
        this.titansMap = new HashMap<>();
        EventUtils.registerEvents(this);
    }

    public void addTitan(final UUID uuid, final TitanBase titan){
        this.titansMap.put(uuid, titan);
    }
    public void replaceOwner(@NonNull final UUID oldUUID, @NonNull final GamePlayer gamePlayer, @NonNull final TitanBase titan) {
        @NonNull final TitanOwnerChangeEvent event = new TitanOwnerChangeEvent(oldUUID, gamePlayer, titan);
        if (event.isCancelled()) return;
        this.titansMap.remove(oldUUID, titan);
        this.addTitan(gamePlayer.getUuid(), titan);
    }
    public boolean hasTitan(@NonNull final UUID uuid) {
        return this.titansMap.containsKey(uuid);
    }
    public String[] getDescriptions(@NonNull final UUID uuid) {
        if (hasTitan(uuid)) {
            return this.titansMap.get(uuid).getDescription();
        }
        return new String[0];
    }
    public TitanBase getTitan(@NonNull final UUID uuid) {
        if (!hasTitan(uuid)) {
            return null;
        }
        return this.titansMap.get(uuid);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onDeath(@NonNull final UHCDeathEvent event) {
        if (event.isCancelled())return;
        if (event.getRole() == null)return;
        if (this.titansMap.isEmpty())return;
        if (this.titansMap.containsKey(event.getPlayer().getUniqueId())) {
            final TitanBase titan = this.titansMap.get(event.getPlayer().getUniqueId());
            final List<GamePlayer> prepaList = Loc.getNearbyGamePlayers(event.getPlayer().getLocation(), 25.0);
            prepaList.remove(event.getRole().getGamePlayer());
            if (prepaList.isEmpty())return;
            final List<GamePlayer> list = new ArrayList<>(prepaList);
            for (TitanBase base : this.titansMap.values()) {
                for (final GamePlayer player : prepaList) {
                    if (base.getStealers().contains(player)) {
                        list.remove(player);
                    }
                }
            }
            if (list.isEmpty())return;
            final PrepareTitanStealEvent prepareTitanStealEvent = new PrepareTitanStealEvent(titan.getTitanForm(), event.getGameState(), titan.getStealers(), titan.getGamePlayer());
            Bukkit.getPluginManager().callEvent(prepareTitanStealEvent);
            if (prepareTitanStealEvent.isCancelled())return;
            final TitanStealManager stealManager = new TitanStealManager(titan);
            for (final GamePlayer player : list) {
                if (player.getRole() == null)continue;
                if (!(player.getRole() instanceof AotRoles))continue;
                if (!((AotRoles) player.getRole()).isCanVoleTitan())continue;
                titan.getStealers().add(player);
                player.sendMessage("§7Un§c titan§7 est mort proche de vous, vous pouvez faire la commande§6 /aot steal§7 pour§c récupérez§7 ce§c titan");
                player.getRole().addPower(new StealCommand(player.getRole()));
                stealManager.map.put(player.getUuid(), ((AotRoles) player.getRole()).getStealPriority());
            }
            this.titansMap.remove(event.getPlayer().getUniqueId(), titan);
        }
    }
    @EventHandler
    private void onEndGame(@NonNull final GameEndEvent event) {
        this.titansMap.clear();
    }
    @EventHandler
    private void onEffectGive(@NonNull final EffectGiveEvent event) {
        if (hasTitan(event.getPlayer().getUniqueId())) {
            if (event.getEffectWhen().equals(EffectWhen.SPECIAL))return;
            final TitanBase titan = getTitan(event.getPlayer().getUniqueId());
            if (titan.isTransformed()) {
                final List<PotionEffectType> titanTypes = new ArrayList<>();
                titan.getEffects().forEach(effect -> titanTypes.add(effect.getType()));
                if (titanTypes.contains(event.getPotionEffect().getType()) || !event.getEffectWhen().equals(EffectWhen.SPECIAL)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    public boolean isTitanAttributed(@NonNull final TitanForm titanForm) {
        if (!titansMap.isEmpty()) {
            for (final TitanBase base : new ArrayList<>(this.titansMap.values())) {
                if (base.getTitanForm().equals(titanForm)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static class TitanStealManager implements Listener {

        private final TitanBase titan;
        private final Map<UUID, Integer> map;
        private StealRunnable stealRunnable = null;

        private TitanStealManager(@NonNull final TitanBase titan) {
            this.titan = titan;
            this.map = new HashMap<>();
            EventUtils.registerEvents(this);
        }
        @EventHandler
        private void StealCommandEvent(@NonNull final PrepareStealCommandEvent event) {
            if (this.map.containsKey(event.getPlayer().getUniqueId())) {
                if (this.stealRunnable == null) {
                    this.stealRunnable = new StealRunnable(event.getRole().getGameState(), this);
                }
                event.getPlayer().sendMessage("§7Vous avez été ajouter en tant que potentiel récupérateur du§c titan "+titan.getName()+"§7, vous aurez les résultas dans§c "+this.stealRunnable.timeLeft+"s");
                this.stealRunnable.addGamePlayer(event.getRole().getGamePlayer(), event.getRole().getStealPriority());
            }
        }
        private synchronized void forceSteal(@NonNull final GamePlayer gamePlayer) {
            if (this.stealRunnable != null) {
                this.stealRunnable.stop();
            }
            gamePlayer.sendMessage("§7Bravo, vous avez§c hérité§7 du§c titan "+this.titan.getName());
            this.titan.setNewOwner(gamePlayer);
        }
        private static class StealRunnable extends BukkitRunnable {

            private final GameState gameState;
            private int timeLeft = 10;
            private final Map<GamePlayer, Integer> map;
            private final TitanStealManager stealManager;

            private StealRunnable(@NonNull final GameState gameState, TitanStealManager stealManager) {
                this.gameState = gameState;
                this.stealManager = stealManager;
                this.map = new HashMap<>();
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }
            private void addGamePlayer(@NonNull final GamePlayer gamePlayer, final int stealPriority) {
                this.map.put(gamePlayer, stealPriority);
                gamePlayer.getActionBarManager().addToActionBar("stealmanager.steal", "§7Temp avant obtention du§c titan§7: §c"+this.timeLeft+"s");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                for (@NonNull final GamePlayer gamePlayer : this.map.keySet()) {
                    gamePlayer.getActionBarManager().updateActionBar("stealmanager.steal", "§7Temp avant obtention du§c titan§7: §c"+this.timeLeft+"s");
                }
                if (this.timeLeft <= 0) {
                    GamePlayer gamePlayer = null;
                    for (@NonNull final GamePlayer gP : this.map.keySet()) {
                        if (gP.getRole() == null)continue;
                        if (!gP.isOnline())continue;
                        if (!gP.isAlive())continue;
                        if (gP.getRole() instanceof AotRoles) {
                            if (gamePlayer == null) {
                                gamePlayer = gP;
                            } else {
                                if (((AotRoles) gP.getRole()).getStealPriority() > ((AotRoles)gamePlayer.getRole()).getStealPriority()) {
                                    gamePlayer = gP;
                                }
                            }
                        }
                    }
                    if (gamePlayer != null){
                        this.stealManager.forceSteal(gamePlayer);
                        return;
                    }
                }
                this.timeLeft--;
            }

            public synchronized void stop() {
                if (!this.map.isEmpty()) {
                    for (@NonNull final GamePlayer gamePlayer : this.map.keySet()) {
                        gamePlayer.getActionBarManager().removeInActionBar("stealmanager.steal");
                    }
                }
                cancel();
            }
        }
    }
}