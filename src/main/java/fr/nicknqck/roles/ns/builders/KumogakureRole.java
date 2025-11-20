package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public abstract class KumogakureRole extends NSSoloRoles {

    public KumogakureRole(UUID player) {
        super(player);
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Kumogakure;
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }
    public abstract void onEndKyubi();
    public static class KyubiPower extends ItemPower implements Listener {

        public KyubiPower(@NonNull RoleBase role) {
            super("Kyubi (Kumogakure)", new Cooldown(60*15), new ItemBuilder(Material.NETHER_STAR).setName("§6Kyubi"), role,
                    "§7Pendant§c 3 minutes§7 vous offre des effets, cependant ils changent chaque minutes: ",
                    "§8 - §aPremière minute§7: Vous obtenez les effets§e Speed II§7 ainsi que§c Force I§7.",
                    "§8 - §6Deuxième minute§7: Vous obtenez les effets§e Speed I§7 ainsi que§c Force I§7.",
                    "§8 - §cTroisième minute§7: Vous obtenez l'effet§e Speed I§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                new KyubiRunnable(getRole().getGameState(), getRole().getGamePlayer()).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onEffectGive(@NonNull final EffectGiveEvent event) {
            if (event.isCancelled())return;
            if (!event.getPlayer().getUniqueId().equals(this.getRole().getPlayer()))return;
            if (event.getEffectWhen().equals(EffectWhen.NOW))return;
            if (!event.getRole().getGamePlayer().getActionBarManager().containsKey("kyubi.runnable"))return;
            if (event.getPotionEffect().getType().equals(PotionEffectType.SPEED)) {
                event.setCancelled(true);
            }
        }
        private static class KyubiRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private int timeRemaining;
            private int timeRemainingEffect;
            private int stade;

            private KyubiRunnable(GameState gameState, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.timeRemaining = 60*3;
                this.timeRemainingEffect = 60;
                this.stade= 1;
                this.gamePlayer.getActionBarManager().addToActionBar("kyubi.runnable", "§bTemp avant prochain stade de§6 Kyubi§b:§c "+ StringUtils.secondsTowardsBeautiful(timeRemainingEffect));
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeRemaining <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("kyubi.runnable");
                    this.gamePlayer.sendMessage("§6Kyubi§7 s'est arrêter");
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> ((KumogakureRole) this.gamePlayer.getRole()).onEndKyubi());
                    cancel();
                    return;
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (this.stade == 1) {
                        this.gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), EffectWhen.NOW);
                    } else {
                        this.gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NOW);
                    }
                    if (this.stade < 3) {
                        this.gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW);
                    }
                });
                this.gamePlayer.getActionBarManager().updateActionBar("kyubi.runnable", "§bTemp avant prochain stade de§6 Kyubi§b:§c "+ StringUtils.secondsTowardsBeautiful(timeRemainingEffect));
                if (this.timeRemainingEffect <= 0) {
                    this.stade++;
                    this.timeRemainingEffect = 60;
                    return;
                }
                this.timeRemainingEffect--;
                this.timeRemaining--;
            }
        }
    }
}