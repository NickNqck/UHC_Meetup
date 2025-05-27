package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DemonSimple extends DemonInferieurRole implements Listener {

    private boolean resistance = false;

    public DemonSimple(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Démon Simple";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Demon;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerRoleEvent(this);
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0, false, false), EffectWhen.DAY);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        new NearbyMoonRunnable(getGameState(), this).runTaskTimerAsynchronously(Main.getInstance(), 20*6, 20);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onUHCKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            addBonusforce(3.0);
            event.getKiller().sendMessage("§7Vous avez gagnez§c 3%§7 de§c force§7, ce qui vous fait monter à §c"+getBonusForce()+" de force§7.");
        }
    }
    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getUniqueId().equals(getPlayer()) && resistance) {
            event.setDamage(event.getDamage()*0.95);
        }
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7En tuant un joueur, vous recevrez§c 3%§7 de§c force§7.")
                .addCustomLine("§7Lorsque vous êtes proche de votre§c Lune Supérieur§7,\n" +
                        "§7Si c'est le§e jour§7 vous ne recevrez pas votre effet§8 Weakness§7,\n" +
                        "§7Si c'est la§c nuit§7 vous recevrez§c 5%§7 de§c dégâts§7 en moins.")
                .getText();
    }

    private static class NearbyMoonRunnable extends BukkitRunnable {

        private final GameState gameState;
        private final DemonSimple demonSimple;

        private NearbyMoonRunnable(GameState gameState, DemonSimple demonSimple) {
            this.gameState = gameState;
            this.demonSimple = demonSimple;
        }

        @Override
        public void run() {
            if (!this.gameState.getServerState().equals(GameState.ServerStates.InGame) || this.demonSimple.getLune() == null) {
                cancel();
                return;
            }
            final Player player = Bukkit.getPlayer(this.demonSimple.getPlayer());
            if (player == null)return;
            if (Loc.getNearbyGamePlayers(player.getLocation(), 20).contains(this.demonSimple.getLune().getGamePlayer())) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (!this.gameState.isNightTime()) {
                        player.removePotionEffect(PotionEffectType.WEAKNESS);
                    } else {
                        this.demonSimple.resistance = true;
                    }
                });
            } else {
                this.demonSimple.resistance = false;
            }
        }
    }
}