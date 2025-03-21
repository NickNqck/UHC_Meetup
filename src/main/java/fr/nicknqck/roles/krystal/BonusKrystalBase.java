package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class BonusKrystalBase extends KrystalBase implements IKrystalRoleBooster {

    private final KrystalBonusManager krystalBonusManager;
    private final List<String> customBonusString;

    public BonusKrystalBase(UUID player) {
        super(player);
        this.krystalBonusManager = new KrystalBonusManager(this);
        this.customBonusString = new ArrayList<>();
    }

    @Override
    public String getBonusString() {
        final StringBuilder sb = new StringBuilder("§7");
        if (!getBonus().isEmpty()) {
            for (final PotionEffect potionEffect : getBonus().keySet()) {
                sb.append("Vous obtiendrez l'effet§c ")
                        .append(AutomaticDesc.getPotionEffectNameWithRomanLevel(potionEffect))
                        .append("§7 en ayant au§c minimum§d ")
                        .append(getBonus().get(potionEffect))
                        .append(" krystaux");
            }
            assert !this.customBonusString.isEmpty();
            for (final String string : this.customBonusString) {
                sb.append(string);
            }
        }
        return sb.toString();
    }

    @Getter
    private static class KrystalBonusManager extends BukkitRunnable {

        private final GameState gameState;
        private final BonusKrystalBase role;

        private KrystalBonusManager(BonusKrystalBase role) {
            this.gameState = role.getGameState();
            this.role = role;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (role.getBonus().isEmpty())return;
            final Player owner = Bukkit.getPlayer(this.role.getPlayer());
            if (owner == null)return;
            for (final PotionEffect potionEffect : this.role.getBonus().keySet()) {
                if (this.role.getKrystalAmount() >= this.role.getBonus().get(potionEffect)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.role.givePotionEffect(potionEffect, EffectWhen.NOW));
                }
            }
        }
    }
}