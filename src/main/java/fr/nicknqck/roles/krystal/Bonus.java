package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class Bonus {

    private final String bonusName;
    private final int amountToHaveBonus;
    private final KrystalBase role;
    private final BonusRunnable bonusRunnable;
    private boolean alreadyActivate = false;
    private final String[] descriptions;

    protected Bonus(@NonNull String bonusName, @NonNull KrystalBase role, int amountToHaveBonus,String... descriptions) {
        this.bonusName = bonusName;
        this.amountToHaveBonus = amountToHaveBonus;
        this.role = role;
        this.bonusRunnable = new BonusRunnable(this);
        this.descriptions = descriptions;
    }

    public abstract boolean onActivate(@NonNull final Player player);
    public abstract boolean onDisable(@NonNull final Player player);
    public boolean checkIfCanActivate() {
        if (role.getKrystalAmount() >= amountToHaveBonus) {
            if (alreadyActivate) {
                return false;
            }
            final Player player = Bukkit.getPlayer(role.getPlayer());
            if (player == null) {
                return false;
            }
            if (onActivate(player)){
                alreadyActivate = true;
                player.sendMessage("§bLe bonus \""+getBonusName()+"§b\" a été§a activer§b !");
                return true;
            }
        } else {
            if (!alreadyActivate) {
                return false;
            }
            final Player player = Bukkit.getPlayer(role.getPlayer());
            if (player == null) {
                return false;
            }
            if (onDisable(player)) {
                alreadyActivate = false;
                player.sendMessage("§bLe bonus \""+getBonusName()+"§b\" a été§c désactiver§b !");
                return true;
            }
        }
        return false;
    }
    private static class BonusRunnable extends BukkitRunnable {

        private final Bonus bonus;

        private BonusRunnable(Bonus bonus) {
            this.bonus = bonus;
            runTaskTimerAsynchronously(Main.getInstance(), 5, 20);
        }

        @Override
        public void run() {
            if (!GameState.inGame()) {
                cancel();
                return;
            }
            final KrystalBase role = bonus.getRole();
            if (role.getGamePlayer() == null)return;
            final GamePlayer gamePlayer = role.getGamePlayer();
            if (!gamePlayer.isOnline() || !gamePlayer.isAlive() || gamePlayer.getRole() == null) {
                return;
            }
            if (role.getKrystalAmount() >= bonus.getAmountToHaveBonus()) {
                final String playerName = gamePlayer.getPlayerName();
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (bonus.checkIfCanActivate()) {
                        Main.getInstance().getLogger().info("Le bonus "+bonus.getBonusName()+" a été activer par "+playerName);
                    }
                });
            } else {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    if (bonus.checkIfCanActivate()) {
                        Main.getInstance().getLogger().info("Le bonus "+bonus.getBonusName()+" a été désactiver");
                    }
                });
            }
        }
    }
}